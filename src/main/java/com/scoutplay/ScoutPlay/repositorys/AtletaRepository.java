package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AtletaRepository extends JpaRepository<Atleta, String> {

}
