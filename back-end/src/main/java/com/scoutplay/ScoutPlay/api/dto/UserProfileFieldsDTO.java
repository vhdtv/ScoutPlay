package com.scoutplay.ScoutPlay.api.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.scoutplay.ScoutPlay.models.DetalhePerfil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login
 * Retorna o token JWT e informações do usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileFieldsDTO {
    private Optional<String> nome;
    private Optional<String> sobrenome;
    private Optional<String> username;
    private Optional<String> fotoPerfil;
    private Optional<Map<String, Object>> config;
}
