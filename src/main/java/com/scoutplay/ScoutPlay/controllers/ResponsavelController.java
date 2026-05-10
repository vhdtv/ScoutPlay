package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.ResponsavelDTO;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.ResponsavelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrarResponsavel(@Valid @RequestBody ResponsavelDTO dto) {
        Responsavel criado = responsavelService.save(toResponsavel(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(buildLoginResponse(criado), "Responsável cadastrado com sucesso"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Responsavel>> salvarResponsavel(@Valid @RequestBody ResponsavelDTO dto) {
        Responsavel criado = responsavelService.save(toResponsavel(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(criado, "Responsável criado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Responsavel>>> listarResponsaveis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Responsavel> responsavelPage = responsavelService.findAllPaginated(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(responsavelPage), "Responsáveis listados com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Responsavel>> buscarResponsavelPorId(@PathVariable String id) {
        Responsavel responsavel = responsavelService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com ID " + id));
        return ResponseEntity.ok(ApiResponse.success(responsavel, "Responsável encontrado com sucesso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Responsavel>> atualizarResponsavel(
            @PathVariable String id,
            @Valid @RequestBody ResponsavelDTO dto) {

        assertOwner(id);
        Responsavel atualizado = responsavelService.atualizar(id, toResponsavel(dto));
        return ResponseEntity.ok(ApiResponse.success(atualizado, "Responsável atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removerResponsavel(@PathVariable String id) {
        assertOwner(id);
        responsavelService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Responsável removido com sucesso"));
    }

    private void assertOwner(String id) {
        if (!SecurityUtils.isOwner(id)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este responsável.");
        }
    }

    private Responsavel toResponsavel(ResponsavelDTO dto) {
        Responsavel responsavel = new Responsavel();
        responsavel.setId(dto.getId());
        responsavel.setNome(dto.getNome());
        responsavel.setTelefone(dto.getTelefone());
        responsavel.setCpf(dto.getCpf());
        responsavel.setCep(dto.getCep());
        responsavel.setEmail(dto.getEmail());
        responsavel.setSenha(dto.getSenha());
        return responsavel;
    }

    private LoginResponse buildLoginResponse(Responsavel responsavel) {
        return LoginResponse.builder()
            .token(jwtTokenProvider.generateToken(responsavel.getId(), "RESPONSAVEL"))
            .userId(responsavel.getId())
            .userType("RESPONSAVEL")
            .nome(responsavel.getNome())
            .email(responsavel.getEmail())
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .build();
    }
}
