package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.PeDominante;
import com.scoutplay.ScoutPlay.models.VideoAtleta;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existeEmail(String email) {
        return atletaRepository.existsByEmail(email);
    }

    public Atleta criarAtleta(Atleta novoAtleta) {
        // Gera um ID personalizado se não existir
        if (novoAtleta.getId() == null || novoAtleta.getId().isEmpty()) {
            novoAtleta.setId(novoAtleta.gerarIdPersonalizado());
        }

        // Valida que senha foi fornecida no cadastro
        if (novoAtleta.getSenha() == null || novoAtleta.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória para cadastro.");
        }

        // Verificação de duplicidade de CPF
        if (atletaRepository.existsByCpf(novoAtleta.getCpf())) {
            throw new ConflictException("Um atleta com este CPF já existe.");
        }

        // Verificação de duplicidade de e-mail
        if (existeEmail(novoAtleta.getEmail())) {
            throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        }

        novoAtleta.setSenha(passwordEncoder.encode(novoAtleta.getSenha()));

        // Configure o relacionamento dos vídeos com o Atleta
        if (novoAtleta.getVideos() != null) {
            for (VideoAtleta video : novoAtleta.getVideos()) {
                video.setAtleta(novoAtleta);
            }
        }

        // Salve o atleta e os vídeos associados
        return atletaRepository.save(novoAtleta);
    }

    // Método para buscar todos atletas
    public List<Atleta> buscarTodosAtletas() {
        return atletaRepository.findAll();
    }

    public List<Atleta> buscarAtletasComFiltro(String nome, Integer anoNascimento, Double peso, Double altura, String posicao, PeDominante peDominante) {
        return atletaRepository.findByFilters(nome, anoNascimento, peso, altura, posicao, peDominante);
    }

    public Page<Atleta> buscarAtletasComFiltro(String nome, Integer anoNascimento, Double peso, Double altura, String posicao, PeDominante peDominante, Pageable pageable) {
        return atletaRepository.findByFilters(nome, anoNascimento, peso, altura, posicao, peDominante, pageable);
    }

    // Método para buscar atleta por ID
    public Optional<Atleta> buscarAtletaPorId(String id) {
        return atletaRepository.findById(id);
    }

    // Método para deletar atleta pelo ID
    public void deletarAtletaPorId(String id) {
        if (!atletaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Atleta não encontrado com ID " + id);
        }
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
            atleta.setNome(atletaAtualizado.getNome());
            atleta.setEmail(atletaAtualizado.getEmail());
            atleta.setCep(atletaAtualizado.getCep());
            atleta.setCpf(atletaAtualizado.getCpf());
            atleta.setDataNascimento(atletaAtualizado.getDataNascimento());
            if (atletaAtualizado.getSenha() != null && !atletaAtualizado.getSenha().isBlank()) {
                atleta.setSenha(passwordEncoder.encode(atletaAtualizado.getSenha()));
            }
            atleta.setVideos(atletaAtualizado.getVideos());
            if (atleta.getVideos() != null) {
                for (VideoAtleta video : atleta.getVideos()) {
                    video.setAtleta(atleta);
                }
            }
            return atletaRepository.save(atleta);
        }).orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + id));
    }

    // Método para salvar foto de perfil no sistema de arquivos
    public String salvarFotoPerfil(MultipartFile fotoPerfil) throws IOException {
        String originalFilename = fotoPerfil.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase()
            : ".jpg";

        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
            throw new IllegalArgumentException("Formato de imagem não suportado. Use JPG, PNG ou WEBP.");
        }

        String nomeArquivo = UUID.randomUUID() + extension;
        Path caminhoArquivo = Paths.get("uploads/fotos_perfil/" + nomeArquivo);
        Files.createDirectories(caminhoArquivo.getParent());

        BufferedImage imagem = ImageIO.read(fotoPerfil.getInputStream());
        if (imagem == null) {
            Files.copy(fotoPerfil.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
        } else {
            BufferedImage redimensionada = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            redimensionada.getGraphics().drawImage(imagem, 0, 0, 500, 500, null);
            ImageIO.write(redimensionada, extension.replace(".", "").replace("jpeg", "jpg"), caminhoArquivo.toFile());
        }

        return "uploads/fotos_perfil/" + nomeArquivo;
    }

    public Atleta atualizarFotoPerfil(String id, MultipartFile fotoPerfil) throws IOException {
        Atleta atleta = atletaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Atleta não encontrado com ID " + id));
        atleta.setFotoPerfil(salvarFotoPerfil(fotoPerfil));
        return atletaRepository.save(atleta);
    }
}
