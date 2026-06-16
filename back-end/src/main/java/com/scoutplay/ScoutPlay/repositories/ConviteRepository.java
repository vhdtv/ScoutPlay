package com.scoutplay.ScoutPlay.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scoutplay.ScoutPlay.models.Convite;

@Repository
public interface ConviteRepository extends JpaRepository<Convite, UUID> {
    @Query("SELECT c FROM Convite c JOIN FETCH c.remetente WHERE c.destinatario.aliasId = :aliasId AND c.ativo = true")
    List<Convite> findConvitesRecebidosComRemetente(@Param("aliasId") UUID aliasId);

    Optional<Convite> findByAliasId(UUID aliasId);
}