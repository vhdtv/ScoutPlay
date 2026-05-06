package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalhePerfilRepository extends JpaRepository<DetalhePerfil, Integer> {

    DetalhePerfil getByUsuario(Usuario usuario);
}