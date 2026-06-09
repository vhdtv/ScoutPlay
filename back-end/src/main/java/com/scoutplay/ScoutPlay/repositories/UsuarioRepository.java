package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Usuario;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);
    Usuario findByUsername(String enderecoGerado);
    Optional<Usuario> findByAliasId(UUID aliasId);

    @Query("SELECT x.usuario FROM XUsuarioTipoConta x WHERE x.tipoConta.id = :tipoContaId")
    Page<Usuario> findAllByTipoContaId(int tipoContaId, Pageable pageable);

    @Query("SELECT x.usuario FROM XUsuarioTipoConta x WHERE x.tipoConta.id = :tipoContaId AND x.usuario.ativo = true")
    Page<Usuario> findAllAtivosByTipoContaId(int tipoContaId, Pageable pageable);
}
