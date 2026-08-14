package com.scoutplay.ScoutPlay.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.CacheControl;
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
        if (filename == null || !filename.matches("^[a-zA-Z0-9_-]+\\.(?i:jpg|jpeg|png|webp|mp4|mov)$")) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Path root = dir.toAbsolutePath().normalize();
            Path resolved = root.resolve(filename).normalize();
            if (!resolved.startsWith(root) || !Files.isRegularFile(resolved)) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(resolved);
            String contentType = Files.probeContentType(resolved);
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
