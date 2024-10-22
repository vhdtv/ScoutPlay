package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Olheiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OlheiroRepository extends JpaRepository<Olheiro, String> {
    Optional<Olheiro> findByEmail(String email);
}
