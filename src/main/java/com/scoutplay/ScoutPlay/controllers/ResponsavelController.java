package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.services.ResponsavelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @PostMapping
    public Responsavel salvarResponsavel(@RequestBody Responsavel responsavel) {
        return responsavelService.save(responsavel);
    }
}