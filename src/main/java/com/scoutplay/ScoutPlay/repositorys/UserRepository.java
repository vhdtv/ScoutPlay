package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, String> {
    public Optional<Usuario> findByEmail(String email);
}
