package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.api.dto.PostHighlight;
import com.scoutplay.ScoutPlay.models.Destaque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestaqueRepository extends JpaRepository<Destaque, UUID> {
    Optional<Destaque> findByAliasId(UUID destaqueId);

    @Query(value = "SELECT d.alias_id AS aliasId, " +
                "       d.nome AS nome, " +
                "       COUNT(x.id) AS quantidadeMarcada, " +
                "       BOOL_OR(u.alias_id = :usuarioId) AS marcadoPeloUsuario " +
                "FROM t_destaques_em_post x " +
                "JOIN t_destaque d ON x.destaque_id = d.id " +
                "JOIN t_usuario u ON x.usuario_id = u.id " +
                "JOIN t_post p ON x.post_id = p.id " +
                "WHERE p.alias_id = :postId " +
                "GROUP BY d.alias_id, d.nome", 
        nativeQuery = true)
    List<PostHighlight> findAllByPostWithUserContext(@Param("postId") UUID postId, @Param("usuarioId") UUID usuarioId);

    @Query(value = "SELECT d.alias_id AS aliasId, " +
                "       d.nome AS nome, " +
                "       COUNT(x.id) AS quantidadeMarcada " +
                "FROM t_destaques_em_post x " +
                "JOIN t_destaque d ON x.destaque_id = d.id " +
                "JOIN t_post p ON x.post_id = p.id " +
                "WHERE p.alias_id = :postId " +
                "GROUP BY d.alias_id, d.nome", 
        nativeQuery = true)
    List<PostHighlight> findAllByPost(@Param("postId") UUID postId);

    @Query(value = "SELECT d.alias_id AS aliasId, " +
               "       d.nome AS nome, " +
               "       COUNT(x.id) AS quantidadeMarcada, " +
               "       COALESCE(BOOL_OR(u.alias_id = :usuarioId), false) AS marcadoPeloUsuario " +
               "FROM t_destaque d " +
               "LEFT JOIN t_destaques_em_post x ON x.destaque_id = d.id " +
               "LEFT JOIN t_post p ON x.post_id = p.id AND p.alias_id = :postId " +
               "LEFT JOIN t_usuario u ON x.usuario_id = u.id " +
               "GROUP BY d.alias_id, d.nome", 
       nativeQuery = true)
    List<PostHighlight> findAllWithUserContext(@Param("postId") UUID postId, @Param("usuarioId") UUID usuarioId);

}