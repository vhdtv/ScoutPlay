package com.scoutplay.ScoutPlay.repositorys;

import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Usuario, String> {
}
