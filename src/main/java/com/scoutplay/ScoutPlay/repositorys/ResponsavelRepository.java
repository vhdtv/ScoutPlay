package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, String> {
    Optional<Responsavel> findByCpf(String cpf);
}
