package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.VideoDTO;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.VideoAtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    @Autowired
    private VideoAtletaRepository videoAtletaRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    public Page<VideoAtleta> listarPorAtleta(String atletaId, Pageable pageable) {
        return videoAtletaRepository.findByAtletaId(atletaId, pageable);
    }

    public VideoAtleta buscarPorId(java.util.UUID videoId) {
        return videoAtletaRepository.findById(videoId)
            .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));
    }

    public VideoAtleta adicionarVideo(String atletaId, VideoDTO videoDTO) {
        Atleta atleta = atletaRepository.findById(atletaId)
            .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + atletaId));

        VideoAtleta video = new VideoAtleta();
        video.setTitulo(videoDTO.getTitulo());
        video.setUrlVideo(videoDTO.getUrlVideo());
        video.setAtleta(atleta);
        return videoAtletaRepository.save(video);
    }

    public VideoAtleta atualizarVideo(java.util.UUID videoId, VideoDTO videoDTO) {
        VideoAtleta video = buscarPorId(videoId);
        video.setTitulo(videoDTO.getTitulo());
        video.setUrlVideo(videoDTO.getUrlVideo());
        return videoAtletaRepository.save(video);
    }

    public void deletarVideo(java.util.UUID videoId) {
        videoAtletaRepository.delete(buscarPorId(videoId));
    }
}