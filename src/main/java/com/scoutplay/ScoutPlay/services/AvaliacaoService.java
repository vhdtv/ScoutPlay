package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.AvaliacaoDTO;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.repositorys.AvaliacaoRepository;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.repositorys.VideoAtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private OlheiroRepository olheiroRepository;

    @Autowired
    private VideoAtletaRepository videoAtletaRepository;

    public Avaliacao criar(String atletaId, String olheiroId, AvaliacaoDTO dto) {
        Atleta atleta = atletaRepository.findById(atletaId)
            .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + atletaId));
        Olheiro olheiro = olheiroRepository.findById(olheiroId)
            .orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado com ID " + olheiroId));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAtleta(atleta);
        avaliacao.setOlheiro(olheiro);
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        if (dto.getVideoId() != null) {
            VideoAtleta video = videoAtletaRepository.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));
            if (!video.getAtleta().getId().equals(atletaId)) {
                throw new IllegalArgumentException("O vídeo informado não pertence ao atleta selecionado.");
            }
            avaliacao.setVideo(video);
        }

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarPorAtleta(String atletaId) {
        return avaliacaoRepository.findByAtletaIdOrderByDataCriacaoDesc(atletaId);
    }
}