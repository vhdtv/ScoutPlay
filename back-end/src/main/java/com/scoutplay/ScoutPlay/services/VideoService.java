package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.VideoDTO;
import com.scoutplay.ScoutPlay.api.response.PageResponse;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
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
public class VideoService {

    private final UsuarioRepository usuarioRepository;
    private final VideoAtletaRepository videoAtletaRepository;
    private final TipoContaService tipoContaService;

    @Transactional
    public VideoDTO adicionar(UUID atletaAliasId, VideoDTO dto) {
        Usuario atleta = resolverAtleta(atletaAliasId);

        if (!SecurityUtils.isOwner(atletaAliasId.toString())) {
            throw new AccessDeniedException("Você não tem permissão para adicionar vídeos neste perfil");
        }

        VideoAtleta video = new VideoAtleta(atleta, dto.getUrlVideo(), dto.getTitulo());
        VideoAtleta salvo = videoAtletaRepository.save(video);
        return toDTO(salvo);
    }

    public PageResponse<VideoDTO> listar(UUID atletaAliasId, int page, int size) {
        Usuario atleta = resolverAtleta(atletaAliasId);
        Pageable pageable = PageRequest.of(page, size);
        Page<VideoDTO> resultado = videoAtletaRepository.findByUsuario(atleta, pageable).map(this::toDTO);
        return PageResponse.fromPage(resultado);
    }

    @Transactional
    public VideoDTO atualizar(UUID videoAliasId, VideoDTO dto) {
        VideoAtleta video = videoAtletaRepository.findByAliasId(videoAliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));

        if (!SecurityUtils.isOwner(video.getUsuario().getAliasId().toString())) {
            throw new AccessDeniedException("Você não tem permissão para editar este vídeo");
        }

        if (dto.getUrlVideo() != null) video.setUrlVideo(dto.getUrlVideo());
        if (dto.getTitulo() != null)   video.setTitulo(dto.getTitulo());
        return toDTO(videoAtletaRepository.save(video));
    }

    @Transactional
    public void deletar(UUID videoAliasId) {
        VideoAtleta video = videoAtletaRepository.findByAliasId(videoAliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));

        if (!SecurityUtils.isOwner(video.getUsuario().getAliasId().toString())) {
            throw new AccessDeniedException("Você não tem permissão para deletar este vídeo");
        }

        videoAtletaRepository.delete(video);
    }

    // -------------------------------------------------------------------------

    private Usuario resolverAtleta(UUID aliasId) {
        Usuario usuario = usuarioRepository.findByAliasId(aliasId)
                .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado"));
        if (!tipoContaService.verificarTipoConta(usuario, TipoConta.ATLETA)) {
            throw new ResourceNotFoundException("Atleta não encontrado");
        }
        return usuario;
    }

    private VideoDTO toDTO(VideoAtleta v) {
        return VideoDTO.builder()
                .id(v.getAliasId())
                .urlVideo(v.getUrlVideo())
                .titulo(v.getTitulo())
                .dataCriacao(v.getDataCriacao())
                .build();
    }
}
