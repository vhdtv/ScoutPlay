package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.api.dto.AvaliacaoDTO;
import com.scoutplay.ScoutPlay.exceptions.ConflictException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        atletaRepository.findById(atletaId)
            .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + atletaId));
        Olheiro olheiro = olheiroRepository.findById(olheiroId)
            .orElseThrow(() -> new ResourceNotFoundException("Olheiro não encontrado com ID " + olheiroId));

        if (avaliacaoRepository.existsByAtletaIdAndOlheiroId(atletaId, olheiroId)) {
            throw new ConflictException("Este olheiro já avaliou este atleta.");
        }

        Atleta atleta = atletaRepository.findById(atletaId).get();

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

    public Page<Avaliacao> listarPorAtleta(String atletaId, Pageable pageable) {
        return avaliacaoRepository.findByAtletaIdOrderByDataCriacaoDesc(atletaId, pageable);
    }

    public Avaliacao buscarPorId(UUID id) {
        return avaliacaoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID " + id));
    }

    public Avaliacao atualizar(UUID id, AvaliacaoDTO dto) {
        Avaliacao avaliacao = buscarPorId(id);
        avaliacao.setNota(dto.getNota());
        if (dto.getComentario() != null) avaliacao.setComentario(dto.getComentario());
        if (dto.getVideoId() != null) {
            VideoAtleta video = videoAtletaRepository.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));
            if (!video.getAtleta().getId().equals(avaliacao.getAtleta().getId())) {
                throw new IllegalArgumentException("O vídeo informado não pertence ao atleta desta avaliação.");
            }
            avaliacao.setVideo(video);
        }
        return avaliacaoRepository.save(avaliacao);
    }

    public void deletar(UUID id) {
        if (!avaliacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avaliação não encontrada com ID " + id);
        }
        avaliacaoRepository.deleteById(id);
    }
}
