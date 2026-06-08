package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.repositories.AvaliacaoRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.VideoAtletaRepository;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteractionsService {

    // private final AvaliacaoRepository avaliacaoRepository;
    // private final UsuarioRepository usuarioRepository;
    // private final VideoAtletaRepository videoAtletaRepository;
    // private final TipoContaService tipoContaService;

    // @Transactional
    // public AvaliacaoDTO criar(AvaliacaoDTO dto) {
    //     // Valida que quem está avaliando é olheiro
    //     String olheiroAliasIdStr = SecurityUtils.currentUserId();
    //     if (olheiroAliasIdStr == null) throw new AccessDeniedException("Autenticação necessária");
    //     if (!SecurityUtils.hasUserType("OLHEIRO")) {
    //         throw new AccessDeniedException("Apenas olheiros podem criar avaliações");
    //     }

    //     Usuario olheiro = usuarioRepository.findByAliasId(UUID.fromString(olheiroAliasIdStr))
    //             .orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado"));

    //     Usuario atleta = usuarioRepository.findByAliasId(dto.getAtletaId())
    //             .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado"));
    //     if (!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) {
    //         throw new ResourceNotFoundException("Atleta não encontrado");
    //     }

    //     VideoAtleta video = null;
    //     if (dto.getVideoId() != null) {
    //         video = videoAtletaRepository.findByAliasId(dto.getVideoId())
    //                 .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));
    //     }

    //     Avaliacao avaliacao = new Avaliacao(atleta, olheiro, dto.getNota(), dto.getComentario(), video);
    //     Avaliacao salva = avaliacaoRepository.save(avaliacao);
    //     return toDTO(salva);
    // }

    // public PageResponse<AvaliacaoDTO> listarPorAtleta(UUID atletaAliasId, int page, int size) {
    //     Usuario atleta = usuarioRepository.findByAliasId(atletaAliasId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado"));

    //     Pageable pageable = PageRequest.of(page, size);
    //     Page<AvaliacaoDTO> resultado = avaliacaoRepository.findByAtleta(atleta, pageable).map(this::toDTO);
    //     return PageResponse.fromPage(resultado);
    // }

    // // -------------------------------------------------------------------------

    // private AvaliacaoDTO toDTO(Avaliacao a) {
    //     return AvaliacaoDTO.builder()
    //             .id(a.getAliasId())
    //             .atletaId(a.getAtleta().getAliasId())
    //             .nomeAtleta(a.getAtleta().getNome())
    //             .olheiroId(a.getOlheiro().getAliasId())
    //             .nomeOlheiro(a.getOlheiro().getNome())
    //             .nota(a.getNota())
    //             .comentario(a.getComentario())
    //             .videoId(a.getVideo() != null ? a.getVideo().getAliasId() : null)
    //             .build();
    // }
}
