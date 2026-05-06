package com.scoutplay.ScoutPlay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutplay.ScoutPlay.models.Post;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {}