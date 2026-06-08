package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
public interface XUsuarioTipoContaRepository extends JpaRepository<XUsuarioTipoConta, Integer> {
    Set<XUsuarioTipoConta> getByTipoConta(TipoConta tipoConta);
    Set<XUsuarioTipoConta> getByUsuario(Usuario usuario);
    Set<XUsuarioTipoConta> getByAliasId(UUID alias_id);
}
