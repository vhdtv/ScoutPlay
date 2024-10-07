package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.services.OlheiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/olheiros")
public class OlheiroController {

    @Autowired
    private OlheiroService olheiroService;

    @GetMapping
    public List<Olheiro> listarTodosAtletas() {
        return olheiroService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Olheiro> buscarPorId(@PathVariable String id) {
        Optional<Olheiro> olheiro = olheiroService.findById(id);
        return olheiro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Olheiro criarOlheiro(@RequestBody Olheiro olheiro) {
        return olheiroService.save(olheiro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Olheiro> atualizarInformacoesDoOlheiro(@PathVariable String id, @RequestBody Olheiro atualizarInformacoesDoOlheiro) {
        Optional<Olheiro> AtletaExistente = olheiroService.findById(id);
        if (AtletaExistente.isPresent()) {
            atualizarInformacoesDoOlheiro.setId(id);
            return ResponseEntity.ok(olheiroService.save(atualizarInformacoesDoOlheiro));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOlheiro(@PathVariable String id) {
        olheiroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
