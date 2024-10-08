package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, UUID> {

}
