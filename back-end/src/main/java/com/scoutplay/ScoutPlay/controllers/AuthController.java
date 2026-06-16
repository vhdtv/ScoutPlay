package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.services.AuthService;
import com.scoutplay.ScoutPlay.services.PasswordResetService;
import com.scoutplay.ScoutPlay.services.UsuarioService;
import java.util.HashMap;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService loginService;
    private final PasswordResetService passwordResetService;
    private final UsuarioService usuarioService;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> login(@Valid @RequestBody ClientLoginInputDTO request) {
        ClientLoginOutputDTO loginResponse = loginService.autenticarUsuario(request);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(loginResponse.getExpiraEm())
            .sameSite("Lax")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout() {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", null)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(0) // pede ao cliente para remover o cookie de autenticação
            .sameSite("Lax")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(true));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> signup(@RequestBody SignupRequest request) {
        LocalDate dataNascimento = null;
        if (request.getDataNascimento() != null && !request.getDataNascimento().isEmpty()) {
            dataNascimento = LocalDate.parse(request.getDataNascimento());
        }
        String sobrenome = request.getSobrenome() != null ? request.getSobrenome() : "";
        Usuario usuario = new Usuario(request.getNome(), sobrenome, request.getEmail(), request.getSenha(), dataNascimento);
        if (request.getCpf() != null && !request.getCpf().isBlank()) usuario.setCpf(request.getCpf());
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) usuario.setTelefone(request.getTelefone());

        Usuario savedUser;
        if ("OLHEIRO".equalsIgnoreCase(request.getTipoConta())) {
            savedUser = usuarioService.cadastrarOlheiro(usuario);
        } else {
            savedUser = usuarioService.cadastrarAtleta(usuario);
        }

        HashMap<String, Object> profileData = new HashMap<>();
        if (request.getPeso() != null) profileData.put("peso", request.getPeso());
        if (request.getAltura() != null) profileData.put("altura", request.getAltura());
        if (request.getPosicao() != null && !request.getPosicao().isBlank()) profileData.put("posicao", request.getPosicao());
        if (request.getPeDominante() != null && !request.getPeDominante().isBlank()) profileData.put("peDominante", request.getPeDominante());
        if (request.getClubesAnteriores() != null && !request.getClubesAnteriores().isBlank()) profileData.put("clubesAnteriores", request.getClubesAnteriores());
        if (request.getCep() != null && !request.getCep().isBlank()) profileData.put("cep", request.getCep());
        if (!profileData.isEmpty()) usuarioService.adicionarInformacao(profileData, savedUser);

        ClientLoginInputDTO loginInput = ClientLoginInputDTO.builder()
            .email(request.getEmail())
            .senha(request.getSenha())
            .build();
        ClientLoginOutputDTO loginResponse = loginService.autenticarUsuario(loginInput);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true).secure(cookieSecure).path("/").maxAge(loginResponse.getExpiraEm()).sameSite("Lax").build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Conta criada com sucesso"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.enviarSenhaTemporaria(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null,
                "Se o e-mail estiver cadastrado, uma nova senha foi enviada."));
    }

    @Data
    static class SignupRequest {
        private String nome;
        private String sobrenome;
        private String email;
        private String senha;
        private String dataNascimento;
        private String tipoConta;
        private String cpf;
        private String telefone;
        private String cep;
        private Double peso;
        private Double altura;
        private String posicao;
        private String peDominante;
        private String clubesAnteriores;
    }

    @Data
    static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;
    }

}
