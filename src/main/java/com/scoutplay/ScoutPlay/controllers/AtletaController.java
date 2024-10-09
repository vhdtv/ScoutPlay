package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.services.AtletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/atletas")
public class AtletaController {

    @Autowired
    private AtletaService atletaService;

    //Endpoint para criar um novo atleta
    @PostMapping
    public ResponseEntity<Atleta> criarAtleta(@RequestBody Atleta novoAtleta) {
        try{
            Atleta atletaCriado = atletaService.criarAtleta(novoAtleta);
            return new ResponseEntity<>(atletaCriado, HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Endpoint para buscar atleta todos atletas
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

    //Endpoint para atualizar um atleta existente
    @PutMapping("/{id}")
    public ResponseEntity<Atleta> atualizarAtleta(@PathVariable String id, @RequestBody Atleta atletaAtualizado){
        try{
            Atleta atleta = atletaService.atualizarAtleta(id, atletaAtualizado);
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
