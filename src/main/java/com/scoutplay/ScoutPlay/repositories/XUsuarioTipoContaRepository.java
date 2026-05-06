package com.scoutplay.ScoutPlay.repositories;

import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
public interface XUsuarioTipoContaRepository extends JpaRepository<XUsuarioTipoConta, Integer> {
    XUsuarioTipoConta getByTipoConta(TipoConta tipoConta);
    XUsuarioTipoConta getByUsuario(Usuario usuario);
    XUsuarioTipoConta getByAliasId(UUID alias_id);
}
