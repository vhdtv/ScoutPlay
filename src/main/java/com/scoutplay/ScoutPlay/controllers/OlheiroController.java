package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.OlheiroDTO;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.security.JwtTokenProvider;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.OlheiroService;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/olheiros")
public class OlheiroController {

    @Autowired
    private OlheiroService olheiroService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<LoginResponse>> registrarOlheiro(@Valid @RequestBody OlheiroDTO olheiroDTO) {
        Olheiro olheiroCriado = olheiroService.criarOlheiro(toOlheiro(olheiroDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(buildLoginResponse(olheiroCriado), "Olheiro cadastrado com sucesso"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Olheiro>> criarOlheiro(@Valid @RequestBody OlheiroDTO olheiroDTO) {
        Olheiro olheiroCriado = olheiroService.criarOlheiro(toOlheiro(olheiroDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(olheiroCriado, "Olheiro criado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Olheiro>>> listarTodosOlheiros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Olheiro> olheiroPage = olheiroService.buscarOlheirosPaginado(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(olheiroPage), "Olheiros listados com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Olheiro>> buscarOlheiroPorId(@PathVariable String id) {
        Optional<Olheiro> olheiro = olheiroService.buscarOlheiroPorId(id);
        return olheiro
                .map(value -> ResponseEntity.ok(ApiResponse.success(value, "Olheiro encontrado com sucesso")))
                .orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado com ID " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Olheiro>> atualizarInformacoesDoOlheiro(
            @PathVariable String id,
            @Valid @RequestBody OlheiroDTO olheiroDTO) {

        assertOlheiroOwner(id);

        Olheiro olheiro = olheiroService.atualizarOlheiro(id, toOlheiro(olheiroDTO));
        return ResponseEntity.ok(ApiResponse.success(olheiro, "Olheiro atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletarOlheiro(@PathVariable String id) {
        assertOlheiroOwner(id);
        olheiroService.deletarOlheiroPorId(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Olheiro removido com sucesso"));
    }

    private Olheiro toOlheiro(OlheiroDTO olheiroDTO) {
        Olheiro olheiro = new Olheiro();
        olheiro.setId(olheiroDTO.getId());
        olheiro.setNome(olheiroDTO.getNome());
        olheiro.setTelefone(olheiroDTO.getTelefone());
        olheiro.setCpf(olheiroDTO.getCpf());
        olheiro.setDataNascimento(olheiroDTO.getDataNascimento());
        olheiro.setCep(olheiroDTO.getCep());
        olheiro.setEmail(olheiroDTO.getEmail());
        olheiro.setSenha(olheiroDTO.getSenha());
        olheiro.setClube(olheiroDTO.getClube());
        olheiro.setLocal(olheiroDTO.getLocal());
        return olheiro;
    }

    private void assertOlheiroOwner(String olheiroId) {
        if (!SecurityUtils.isOwner(olheiroId)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este olheiro.");
        }
    }

    private LoginResponse buildLoginResponse(Olheiro olheiro) {
        return LoginResponse.builder()
            .token(jwtTokenProvider.generateToken(olheiro.getId(), "OLHEIRO"))
            .userId(olheiro.getId())
            .userType("OLHEIRO")
            .nome(olheiro.getNome())
            .email(olheiro.getEmail())
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .build();
    }
}