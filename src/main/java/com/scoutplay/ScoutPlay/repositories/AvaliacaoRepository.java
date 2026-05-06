package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {}