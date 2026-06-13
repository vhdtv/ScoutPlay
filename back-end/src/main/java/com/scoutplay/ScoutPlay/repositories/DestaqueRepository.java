package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Destaque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestaqueRepository extends JpaRepository<Destaque, UUID> {

    Optional<Destaque> findByAliasId(UUID destaqueId);
}