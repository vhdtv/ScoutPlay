package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.Usuario;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    Usuario findByEmail(String email);
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contasQueSegue WHERE LOWER(u.username) = LOWER(:username)")
    Usuario findByUsernameIgnoreCase(@Param("username") String username);
    Optional<Usuario> findByAliasId(UUID aliasId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contasQueSegue WHERE u.aliasId = :aliasId")
    Optional<Usuario> findByAliasIdWithContasQueSegue(@Param("aliasId") UUID aliasId);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.postsCurtidos WHERE u.aliasId = :aliasId")
    Optional<Usuario> findByIdWithPostsCurtidos(@Param("aliasId") UUID aliasId);

    @Query("SELECT x.usuario FROM XUsuarioTipoConta x WHERE x.tipoConta.id = :tipoContaId")
    Page<Usuario> findAllByTipoContaId(int tipoContaId, Pageable pageable);

    @Query("SELECT x.usuario FROM XUsuarioTipoConta x WHERE x.tipoConta.id = :tipoContaId AND x.usuario.ativo = true")
    Page<Usuario> findAllAtivosByTipoContaId(int tipoContaId, Pageable pageable);
}
