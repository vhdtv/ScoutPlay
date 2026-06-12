package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.enums.TipoContaEnum;
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
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.poderesConta WHERE LOWER(u.username) = LOWER(:username)")
    Usuario findByUsernameWithPoderesIgnoreCase(@Param("username") String username);
    Optional<Usuario> findByAliasId(UUID aliasId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contasQueSegue WHERE u.aliasId = :aliasId")
    Optional<Usuario> findByAliasIdWithContasQueSegue(@Param("aliasId") UUID aliasId);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.postsCurtidos WHERE u.aliasId = :aliasId")
    Optional<Usuario> findByIdWithPostsCurtidos(@Param("aliasId") UUID aliasId);

    @Query("SELECT u FROM Usuario u JOIN u.poderesConta p WHERE p.id = :#{#tipoEnum.id}")
    Page<Usuario> findByTipoConta(@Param("tipoEnum") TipoContaEnum tipoEnum, Pageable pageable);
}
