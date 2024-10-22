package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, String> {

}
