package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.services.OlheiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/olheiros")
public class OlheiroController {

    @Autowired
    private OlheiroService olheiroService;

    //Endpoint para criar um olheiro
    @PostMapping
    public ResponseEntity<Olheiro> criarOlheiro(@RequestBody Olheiro novoOlheiro) {
        try{
            Olheiro olheiroCriado = olheiroService.criarOlheiro(novoOlheiro);
            return new ResponseEntity<>(olheiroCriado, HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    //Endpoint para buscar todos olheiros
    @GetMapping
    public List<Olheiro> listarTodosOlheiros() {
        return olheiroService.buscarTodosOlheiros();
    }

    //Endpoint para buscar olheiro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Olheiro> buscarOlheiroPorId(@PathVariable String id) {
        Optional<Olheiro> olheiro = olheiroService.buscarOlheiroPorId(id);
        return olheiro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    //Endpoint para atualizar informações de um olheiro existente
    @PutMapping("/{id}")
    public ResponseEntity<Olheiro> atualizarInformacoesDoOlheiro(@PathVariable String id, @RequestBody Olheiro atualizarInformacoesDoOlheiro) {
        try{
            Olheiro olheiro = olheiroService.atualizarOlheiro(id, atualizarInformacoesDoOlheiro);
            return ResponseEntity.ok(olheiro);
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    //Endpoint para deletar um atleta por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Olheiro> deletarOlheiro(@PathVariable String id) {
        olheiroService.deletarOlheiroPorId(id);
        return ResponseEntity.noContent().build();
    }
}
