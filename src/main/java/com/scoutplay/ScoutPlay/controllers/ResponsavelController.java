package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.services.ResponsavelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @PostMapping
    public ResponseEntity<?> salvarResponsavel(@RequestBody Responsavel responsavel) {
        try {
            Responsavel savedResponsavel = responsavelService.save(responsavel);
            return ResponseEntity.ok(savedResponsavel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}