package com.scoutplay.ScoutPlay.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutplay.ScoutPlay.models.Seguidor;
import com.scoutplay.ScoutPlay.models.Usuario;

public interface SeguidorRepository extends JpaRepository<Seguidor, Integer> {
    long countBySeguido(Usuario seguido);
    long countBySeguidor(Usuario seguidor);
    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);
    Optional<Seguidor> findBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    @Query("SELECT s.seguido.username FROM Seguidor s WHERE s.seguidor = :seguidor")
    List<String> findUsernamesSeguidos(@Param("seguidor") Usuario seguidor);
}
