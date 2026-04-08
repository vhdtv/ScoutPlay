package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para Atleta
 * Usado para transferir dados do backend para o frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtletaDTO {
    private String id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$", message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    private String telefone;

    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @Positive(message = "Altura deve ser um valor positivo")
    private Double altura;

    @Positive(message = "Peso deve ser um valor positivo")
    private Double peso;

    @NotBlank(message = "Posição é obrigatória")
    private String posicao;

    @NotBlank(message = "Pé dominante é obrigatório")
    private String peDominante;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato XXXXX-XXX")
    private String cep;

    private String fotoPerfil;
    private String clubesAnteriores;
    private Integer idade;

    private List<@Pattern(regexp = "^(https?://).+", message = "Cada vídeo deve ser uma URL válida") String> videos;
}
