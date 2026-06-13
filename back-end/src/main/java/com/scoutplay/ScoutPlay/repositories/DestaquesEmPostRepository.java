package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Destaque;
import com.scoutplay.ScoutPlay.models.DestaquesEmPost;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface DestaquesEmPostRepository extends JpaRepository<DestaquesEmPost, UUID> {
    boolean existsByPostAndUsuarioAndDestaque(Post post, Usuario usuario, Destaque destaque);
    @Query("SELECT d.id, d.nome, COUNT(pdu) " +
           "FROM DestaquesEmPost pdu " +
           "JOIN pdu.destaque d " + 
           "WHERE pdu.post.id = :postId " +
           "GROUP BY d.id, d.nome")
    List<Object[]> countDestaquesByPostId(@Param("postId") UUID postId);
    DestaquesEmPost findByPostAndUsuarioAndDestaque(Post post, Usuario usuario, Destaque destaque);
}