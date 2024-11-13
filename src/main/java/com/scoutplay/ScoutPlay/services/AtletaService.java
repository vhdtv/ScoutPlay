package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
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
import java.util.stream.Collectors;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    public boolean existeEmail(String email) {
        return atletaRepository.existsByEmail(email);
    }

    public Atleta criarAtleta(Atleta novoAtleta) {
        // Gera um ID personalizado se não existir
        if (novoAtleta.getId() == null || novoAtleta.getId().isEmpty()) {
            novoAtleta.setId(novoAtleta.gerarIdPersonalizado());
        }

        // Verificação de duplicidade de CPF
        if (atletaRepository.existsByCpf(novoAtleta.getCpf())) {
            throw new IllegalArgumentException("Um atleta com este CPF já existe.");
        }

        // Verificação de duplicidade de e-mail
        if (existeEmail(novoAtleta.getEmail())) {
            throw new IllegalArgumentException("Este e-mail já está em uso. Por favor, utilize outro.");
        }

        // Configure o relacionamento dos vídeos com o Atleta
        for (VideoAtleta video : novoAtleta.getVideos()) {
            video.setAtleta(novoAtleta);
        }

        // Salve o atleta e os vídeos associados
        return atletaRepository.save(novoAtleta);
    }

    // Método para buscar todos atletas
    public List<Atleta> buscarTodosAtletas() {
        return atletaRepository.findAll();
    }
    public List<Atleta> buscarAtletasComFiltro(String nome, Integer anoNascimento, Double peso, Double altura, String posicao) {
        return atletaRepository.findByFilters(nome, anoNascimento, peso, altura, posicao);
    }

    // Método para buscar atleta por ID
    public Optional<Atleta> buscarAtletaPorId(String id) {
        return atletaRepository.findById(id);
    }

    // Método para deletar atleta pelo ID
    public void deletarAtletaPorId(String id) {
        atletaRepository.deleteById(id);
    }

    // Método para atualizar informações de um atleta
    public Atleta atualizarAtleta(String id, Atleta atletaAtualizado) {
        return atletaRepository.findById(id).map(atleta -> {
            atleta.setPosicao(atletaAtualizado.getPosicao());
            atleta.setPeso(atletaAtualizado.getPeso());
            atleta.setAltura(atletaAtualizado.getAltura());
            atleta.setClubesAnteriores(atletaAtualizado.getClubesAnteriores());
            atleta.setFotoPerfil(atletaAtualizado.getFotoPerfil());
            atleta.setPeDominante(atletaAtualizado.getPeDominante());
            atleta.setTelefone(atletaAtualizado.getTelefone());
            atleta.setVideos(atletaAtualizado.getVideos()); // Atualiza os vídeos
            for (VideoAtleta video : atleta.getVideos()) {
                video.setAtleta(atleta); // Certifique-se de associar cada vídeo ao atleta
            }
            return atletaRepository.save(atleta);
        }).orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID " + id));
    }

    // Método para salvar foto de perfil no sistema de arquivos
    public String salvarFotoPerfil(MultipartFile fotoPerfil) throws IOException {
        // Gera um nome único para o arquivo
        String nomeArquivo = UUID.randomUUID().toString() + "_" + fotoPerfil.getOriginalFilename();
        // Define o caminho relativo para armazenar o arquivo
        Path caminhoArquivo = Paths.get("uploads/fotos_perfil/" + nomeArquivo);

        // Cria o diretório, se não existir
        Files.createDirectories(caminhoArquivo.getParent());

        // Salva o arquivo no sistema de arquivos
        Files.copy(fotoPerfil.getInputStream(), caminhoArquivo);

        // Retorna o caminho relativo do arquivo para ser salvo no banco de dados
        return "uploads/fotos_perfil/" + nomeArquivo;
    }
}
