package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByAtivoTrue(Pageable pageable);
    Page<Post> findByAutorAndAtivoTrue(Usuario autor, Pageable pageable);
    Page<Post> findAllByAutorAndAtivoTrue(Usuario autor, Pageable pageable);
    Optional<Post> findByAliasIdAndAtivoTrue(UUID aliasId);
    
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.usuariosQueCurtiram WHERE p.autor IN :autores AND p.ativo = true")
    Page<Post> findAllByAutorInAndAtivoTrue(@Param("autores") Set<Usuario> autores, Pageable pageable);
}