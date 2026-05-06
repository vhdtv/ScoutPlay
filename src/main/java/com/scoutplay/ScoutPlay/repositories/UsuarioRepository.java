package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Usuario;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Usuario findByEnderecoUnico(String enderecoGerado);
}
