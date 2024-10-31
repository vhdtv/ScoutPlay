package com.scoutplay.ScoutPlay.repositorys;

// outras importações permanecem iguais

import com.scoutplay.ScoutPlay.models.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, String> {

    Optional<Atleta> findByEmail(String email);
    boolean existsByCpf(String cpf);
}