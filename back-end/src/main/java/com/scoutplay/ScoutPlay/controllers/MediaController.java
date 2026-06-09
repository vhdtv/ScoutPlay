package com.scoutplay.ScoutPlay.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MediaController {

    @GetMapping("/media/{filename}")
    public ResponseEntity<Resource> criar(@PathVariable String filename) {
        try {
            Path caminhoArquivo = Paths.get("uploads/media").resolve(filename);
            File arquivo = caminhoArquivo.toFile();
            
            if (!arquivo.exists()) return ResponseEntity.notFound().build();
            Resource resource = new FileSystemResource(arquivo);
            String contentType = Files.probeContentType(caminhoArquivo);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
