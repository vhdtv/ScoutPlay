package com.scoutplay.ScoutPlay.Repository;

import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Usuario, String> {
}
