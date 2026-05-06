package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.TipoConta;

import org.springframework.data.jpa.repository.JpaRepository;
public interface TipoContaRepository extends JpaRepository<TipoConta, Integer> {
    TipoConta getByNome(String string);
}
