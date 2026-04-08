package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.ResponsavelDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.services.ResponsavelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @PostMapping
    public ResponseEntity<ApiResponse<Responsavel>> salvarResponsavel(@Valid @RequestBody ResponsavelDTO responsavelDTO) {
        Responsavel savedResponsavel = responsavelService.save(toResponsavel(responsavelDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedResponsavel, "Responsável criado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Responsavel>>> listarResponsaveis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Responsavel> responsaveis = responsavelService.findAll();
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), responsaveis.size());
        int end = Math.min(start + pageable.getPageSize(), responsaveis.size());
        Page<Responsavel> responsavelPage = new PageImpl<>(responsaveis.subList(start, end), pageable, responsaveis.size());

        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(responsavelPage), "Responsáveis listados com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Responsavel>> buscarResponsavelPorId(@PathVariable String id) {
        Optional<Responsavel> responsavel = responsavelService.findById(id);
        return responsavel
                .map(value -> ResponseEntity.ok(ApiResponse.success(value, "Responsável encontrado com sucesso")))
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com ID " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removerResponsavel(@PathVariable String id) {
        responsavelService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Responsável removido com sucesso"));
    }

    private Responsavel toResponsavel(ResponsavelDTO responsavelDTO) {
        Responsavel responsavel = new Responsavel();
        responsavel.setId(responsavelDTO.getId());
        responsavel.setNome(responsavelDTO.getNome());
        responsavel.setTelefone(responsavelDTO.getTelefone());
        responsavel.setCpf(responsavelDTO.getCpf());
        responsavel.setCep(responsavelDTO.getCep());
        responsavel.setEmail(responsavelDTO.getEmail());
        responsavel.setSenha(responsavelDTO.getSenha());
        return responsavel;
    }
}