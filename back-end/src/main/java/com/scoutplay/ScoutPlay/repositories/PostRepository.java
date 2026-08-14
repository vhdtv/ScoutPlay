package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByAtivoTrue(Pageable pageable);
    Page<Post> findByAutorAndAtivoTrue(Usuario autor, Pageable pageable);
    Optional<Post> findByAliasIdAndAtivoTrue(UUID aliasId);
}
