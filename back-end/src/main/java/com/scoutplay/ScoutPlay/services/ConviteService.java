package com.scoutplay.ScoutPlay.services;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.api.dto.ConviteInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ConviteOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.models.Convite;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.ConviteRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConviteService {
    private final ConviteRepository conviteRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ConviteOutputDTO> findAllByDestinatarioAndAtivoTrue(UUID usuarioLogado) {
        return conviteRepository.findConvitesRecebidosComRemetente(usuarioLogado)
            .stream()
            .map(item -> build(item))
            .collect(Collectors.toList());
    }

    public ConviteOutputDTO criar(UUID idRemetente, String usernameDestinatario, String mensagem) throws Exception {
        Convite convite = new Convite();
        Usuario remetente = usuarioRepository.findByAliasId(idRemetente).orElseThrow(() -> new Exception("Remetente não encontrado"));
        Usuario destinatario = usuarioRepository.findByUsernameIgnoreCase(usernameDestinatario);
        convite.setRemetente(remetente);
        convite.setDestinatario(destinatario);
        convite.setMensagem(mensagem);
        Convite linha = conviteRepository.save(convite);
        return build(linha);
    }
    

    private ConviteOutputDTO build(Convite item) {
        return ConviteOutputDTO.builder()
            .id(item.getAliasId())
            .remetente(UserSummaryDTO.builder()
                .nome(item.getRemetente().getNome())
                .sobrenome(item.getRemetente().getSobrenome())
                .username(item.getRemetente().getUsername())
                .iniciais(item.getRemetente().getIniciais())
                .fotoPerfil(item.getRemetente().getFotoPerfil())
                .build())
            .destinatario(UserSummaryDTO.builder()
                .nome(item.getDestinatario().getNome())
                .sobrenome(item.getDestinatario().getSobrenome())
                .username(item.getDestinatario().getUsername())
                .iniciais(item.getDestinatario().getIniciais())
                .fotoPerfil(item.getDestinatario().getFotoPerfil())
                .build())
            .aceito(item.isAceito())
            .mensagem(item.getMensagem())
            .build();
    }

    @Transactional
    public void aceitarConvite(UUID idConvite, UUID usuarioLogado) throws Exception {
        Convite convite = conviteRepository.findByAliasId(idConvite).orElseThrow(() -> new Exception("Convite não encontrado"));
        if(!convite.getDestinatario().getAliasId().equals(usuarioLogado)) throw new Exception("Usuario não pode executar esta ação");
        convite.setAceito(true);
    }

    @Transactional
    public void rejeitarConvite(UUID idConvite, UUID usuarioLogado) throws Exception {
        Convite convite = conviteRepository.findByAliasId(idConvite).orElseThrow(() -> new Exception("Convite não encontrado"));
        if(!convite.getDestinatario().getAliasId().equals(usuarioLogado)) throw new Exception("Usuario não pode executar esta ação");
        convite.setAtivo(false);
    }
}