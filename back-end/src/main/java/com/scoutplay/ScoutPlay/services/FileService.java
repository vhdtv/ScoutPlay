package com.scoutplay.ScoutPlay.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class FileService {

    private static final long MAX_AVATAR_BYTES = 5L * 1024 * 1024;
    private static final long MAX_MEDIA_BYTES = 10L * 1024 * 1024;
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> MEDIA_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "mp4", "mov");

    private FileService() {
    }

    public static String saveAvatar(MultipartFile file, String saveDirectory) throws IOException {
        return save(file, saveDirectory, IMAGE_EXTENSIONS, MAX_AVATAR_BYTES);
    }

    public static String saveMedia(MultipartFile file, String saveDirectory) throws IOException {
        return save(file, saveDirectory, MEDIA_EXTENSIONS, MAX_MEDIA_BYTES);
    }

    private static String save(MultipartFile file, String saveDirectory, Set<String> allowedExtensions, long maxBytes)
            throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo é obrigatório");
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Arquivo excede o tamanho permitido");
        }

        String extension = getFileExtension(file);
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido");
        }

        byte[] header;
        try (InputStream input = file.getInputStream()) {
            header = input.readNBytes(16);
        }
        if (!matchesSignature(extension, header)) {
            throw new IllegalArgumentException("Conteúdo do arquivo não corresponde ao formato informado");
        }

        Path root = Paths.get(saveDirectory).toAbsolutePath().normalize();
        Files.createDirectories(root);
        String filename = UUID.randomUUID() + "." + extension;
        Path destination = root.resolve(filename).normalize();
        if (!destination.startsWith(root)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido");
        }
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, destination);
        }
        return filename;
    }

    static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean matchesSignature(String extension, byte[] bytes) {
        if (bytes.length < 4) return false;
        return switch (extension) {
            case "jpg", "jpeg" -> unsigned(bytes[0]) == 0xFF && unsigned(bytes[1]) == 0xD8 && unsigned(bytes[2]) == 0xFF;
            case "png" -> unsigned(bytes[0]) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G';
            case "webp" -> bytes.length >= 12 && ascii(bytes, 0, "RIFF") && ascii(bytes, 8, "WEBP");
            case "mp4", "mov" -> bytes.length >= 8 && ascii(bytes, 4, "ftyp");
            default -> false;
        };
    }

    private static boolean ascii(byte[] bytes, int offset, String expected) {
        if (bytes.length < offset + expected.length()) return false;
        for (int i = 0; i < expected.length(); i++) {
            if (bytes[offset + i] != (byte) expected.charAt(i)) return false;
        }
        return true;
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }
}
