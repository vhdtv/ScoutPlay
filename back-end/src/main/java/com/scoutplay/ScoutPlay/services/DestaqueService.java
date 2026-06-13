package com.scoutplay.ScoutPlay.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutplay.ScoutPlay.api.dto.PostHighlightDTO;
import com.scoutplay.ScoutPlay.models.Destaque;
import com.scoutplay.ScoutPlay.repositories.DestaqueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DestaqueService {
    private final DestaqueRepository destaqueRepository;

    @Transactional
    public Destaque criar(PostHighlightDTO dto) {
        Destaque destaque = new Destaque();
        destaque.setNome(dto.getTexto());
        Destaque result = destaqueRepository.save(destaque);
        return result;
    }

    @Transactional
    public Destaque buscarPor(UUID aliasId) {
        return destaqueRepository.findById(aliasId).get();
    }
}
