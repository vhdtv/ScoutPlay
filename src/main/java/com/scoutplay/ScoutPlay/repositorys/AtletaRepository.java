package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.PeDominante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, String> {

    Optional<Atleta> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    @Query("SELECT a FROM Atleta a WHERE " +
            "(?1 IS NULL OR a.nome LIKE %?1%) AND " +
            "(?2 IS NULL OR YEAR(a.dataNascimento) = ?2) AND " +
            "(?3 IS NULL OR a.peso = ?3) AND " +
            "(?4 IS NULL OR a.altura = ?4) AND " +
            "(?5 IS NULL OR a.posicao LIKE %?5%) AND " +
            "(?6 IS NULL OR a.peDominante = ?6)")
    List<Atleta> findByFilters(String nome, Integer anoNascimento, Double peso, Double altura, String posicao, PeDominante peDominante);
}

