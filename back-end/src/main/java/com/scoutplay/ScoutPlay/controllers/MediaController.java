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
    public ResponseEntity<Resource> servirMidia(@PathVariable String filename) {
        return servirArquivo(Paths.get("uploads/media"), filename);
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> servirAvatar(@PathVariable String filename) {
        return servirArquivo(Paths.get("uploads/avatars"), filename);
    }

    private ResponseEntity<Resource> servirArquivo(Path dir, String filename) {
        try {
            File arquivo = dir.resolve(filename).toFile();
            if (!arquivo.exists()) {
                try (var paths = Files.newDirectoryStream(dir, filename + ".*")) {
                    for (Path p : paths) { arquivo = p.toFile(); break; }
                } catch (IOException ignored) {}
            }
            if (!arquivo.exists()) return ResponseEntity.notFound().build();
            Resource resource = new FileSystemResource(arquivo);
            String contentType = Files.probeContentType(arquivo.toPath());
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
