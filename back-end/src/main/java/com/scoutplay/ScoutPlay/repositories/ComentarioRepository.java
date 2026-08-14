package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.UUID;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    ArrayList<Comentario> findAllByPost(Post post);
    ArrayList<Comentario> findAllByPostAndParentComentarioIsNull(Post post);
    ArrayList<Comentario> findAllByParentComentario(Comentario parent);
    java.util.Optional<Comentario> findByAliasId(UUID aliasId);
}
