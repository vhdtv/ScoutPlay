package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    Page<Avaliacao> findByAtleta(Usuario atleta, Pageable pageable);
    List<Avaliacao> findByAtleta(Usuario atleta);
    Optional<Avaliacao> findByAliasId(UUID aliasId);
}