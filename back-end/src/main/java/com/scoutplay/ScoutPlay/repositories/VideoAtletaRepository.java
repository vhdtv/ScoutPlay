package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VideoAtletaRepository extends JpaRepository<VideoAtleta, Integer> {
    Page<VideoAtleta> findByUsuario(Usuario usuario, Pageable pageable);
    Optional<VideoAtleta> findByAliasId(UUID aliasId);
}
