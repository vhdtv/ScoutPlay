package com.scoutplay.ScoutPlay.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    static public String saveFileInFolder(MultipartFile file, String saveDirectory) throws IOException {
        InputStream stream = file.getInputStream();
        String filetype = FileService.getFileExtension(file);
        String filename = UUID.randomUUID().toString();
        Path path = Paths.get(saveDirectory);
        Path completePath = path.resolve(String.format("%s.%s", filename, filetype));
        Files.copy(stream, completePath);

        return String.format("%s.%s", filename, filetype);
    }

    static public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
