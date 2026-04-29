package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    List<Avaliacao> findByAtletaIdOrderByDataCriacaoDesc(String atletaId);
}