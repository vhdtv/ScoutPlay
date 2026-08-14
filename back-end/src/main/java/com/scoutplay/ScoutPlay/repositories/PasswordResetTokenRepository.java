package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.PasswordResetToken;
import com.scoutplay.ScoutPlay.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndUsadoEmIsNull(String tokenHash);
    boolean existsByUsuarioAndCriadoEmAfter(Usuario usuario, Instant criadoDepoisDe);
    long deleteByExpiraEmBefore(Instant instante);
}
