package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.services.AtletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/atletas")
public class AtletaController {

    @Autowired
    private AtletaService atletaService;

    //Endpoint para criar um novo atleta
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Atleta> criarAtleta(
            @RequestPart("atleta") Atleta novoAtleta,
            @RequestPart("fotoPerfil") MultipartFile fotoPerfil) {
        try {
            // Salvar a foto no sistema de arquivos ou em um serviço de armazenamento
            if (!fotoPerfil.isEmpty()) {
                String fotoCaminho = atletaService.salvarFotoPerfil(fotoPerfil);
                novoAtleta.setFotoPerfil(fotoCaminho);
            }

            Atleta atletaCriado = atletaService.criarAtleta(novoAtleta);
            return new ResponseEntity<>(atletaCriado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    //Endpoint para buscar todos atletas
    @GetMapping
    public List<Atleta> listarTodosAtletas(){
        return atletaService.buscarTodosAtletas();
    }

    //Endpoint para buscar atleta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Atleta> buscarAtletaPorId(@PathVariable String id){
        Optional<Atleta> atleta = atletaService.buscarAtletaPorId(id);
        return atleta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    //Endpoint para atualizar informações de um atleta existente
    @PutMapping("/{id}")
    public ResponseEntity<Atleta> atualizarInformacoesAtleta(@PathVariable String id, @RequestBody Atleta atualizarInformacoesAtleta){
        try{
            Atleta atleta = atletaService.atualizarAtleta(id, atualizarInformacoesAtleta);
            return ResponseEntity.ok(atleta);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    //Endpoint para deletar um atleta por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Atleta> deletarAtleta(@PathVariable String id){
        atletaService.deletarAtletaPorId(id);
        return ResponseEntity.noContent().build();
    }




}
