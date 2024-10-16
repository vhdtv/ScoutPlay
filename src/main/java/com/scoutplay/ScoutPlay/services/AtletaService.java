package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    //Metódo para criar um novo atleta
    public Atleta criarAtleta(Atleta novoAtleta) {
        if(novoAtleta.getId() == null || novoAtleta.getId().isEmpty()){
            novoAtleta.setId(novoAtleta.gerarIdPersonalizado());
        }
        return atletaRepository.save(novoAtleta);
    }

    //Metódo para buscar todos atletas
    public List<Atleta> buscarTodosAtletas() {
        return atletaRepository.findAll();
    }

    //Metódo para buscar atleta por ID
    public Optional<Atleta> buscarAtletaPorId(String id) {
        return atletaRepository.findById(id);
    }

    //Metódo para deletar atleta pelo ID
    public void deletarAtletaPorId(String id) {
        atletaRepository.deleteById(id);
    }

    //Metódo para atualizar informações de um atleta
    public Atleta atualizarAtleta(String id, Atleta atletaAtualizado) {
        return atletaRepository.findById(id).map(atleta -> {
            atleta.setPosicao(atletaAtualizado.getPosicao());
            atleta.setPeso(atletaAtualizado.getPeso());
            atleta.setAltura(atletaAtualizado.getAltura());
            atleta.setClubesAnteriores(atletaAtualizado.getClubesAnteriores());
            atleta.setFotoPerfil(atletaAtualizado.getFotoPerfil());
            atleta.setPeDominante(atletaAtualizado.getPeDominante());
            atleta.setTelefone(atletaAtualizado.getTelefone());
            return atletaRepository.save(atleta);
        }).orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID" + id));
        }
    // Método para salvar foto de perfil no sistema de arquivos
    public String salvarFotoPerfil(MultipartFile fotoPerfil) throws IOException {
        String nomeArquivo = UUID.randomUUID().toString() + "_" + fotoPerfil.getOriginalFilename();
        Path caminhoArquivo = Paths.get("uploads/fotos_perfil/" + nomeArquivo);

        // Cria os diretórios se não existirem
        Files.createDirectories(caminhoArquivo.getParent());

        // Salva o arquivo no sistema de arquivos
        Files.copy(fotoPerfil.getInputStream(), caminhoArquivo);

        return caminhoArquivo.toString(); // Retorna o caminho completo para salvar no banco
    }


}
