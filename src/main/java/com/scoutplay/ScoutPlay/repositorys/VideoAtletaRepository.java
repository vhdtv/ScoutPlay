package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.VideoAtleta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoAtletaRepository extends JpaRepository<VideoAtleta, UUID> {
    Page<VideoAtleta> findByAtletaId(String atletaId, Pageable pageable);
}