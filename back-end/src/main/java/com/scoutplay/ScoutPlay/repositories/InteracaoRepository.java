package com.scoutplay.ScoutPlay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutplay.ScoutPlay.models.Interacao;

import java.util.UUID;

public interface InteracaoRepository extends JpaRepository<Interacao, UUID> {}