package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSignupInputDTO {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
    
    private String username;
    
    @NotNull(message = "Data de nascimento precisa ser informado")
    private LocalDate dataNascimento;

    @NotBlank(message = "Defina seu CPF")
    private String cpf;
    
    @NotBlank(message = "Nome precisa ser informado")
    private String nome;
    
    @NotBlank(message = "Sobrenome precisa ser informado")
    private String sobrenome;

    @NotBlank(message = "O tipo da conta precisa ser definido")
    String tipoConta;
}
