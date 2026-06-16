package com.scoutplay.ScoutPlay.api.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileFieldsDTO {
    private String nome;
    private String sobrenome;
    private String username;
    private MultipartFile fotoPerfil;
    private String config;
}
