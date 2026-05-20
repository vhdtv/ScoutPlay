package com.scoutplay.ScoutPlay.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DetalhePerfilRepository detalhePerfilRepository;
    @Autowired
    private TipoContaService tipoContaService;
    @Autowired
    private DetalhePerfilService detalhePerfilService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String gerarEnderecoUnico(String nomeUsuario) {
        String enderecoGerado = "";
        boolean enderecoGeradoJaExisteNoBanco = true;
        do {
            enderecoGerado = nomeUsuario.toLowerCase().replaceAll(" ", "_");
            Random rand = new Random();
            final int MIN_VALUE = 1000;
            final int MAX_VALUE = 99999;
            enderecoGerado += rand.nextInt(MAX_VALUE - MIN_VALUE) + MIN_VALUE;
            enderecoGeradoJaExisteNoBanco = usuarioRepository.findByEnderecoUnico(enderecoGerado) != null;
        } while(enderecoGeradoJaExisteNoBanco);
        
        return enderecoGerado;
    }
    
    @Transactional
    public Usuario cadastrarAtleta(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um atleta com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setEnderecoUnico(gerarEnderecoUnico(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoAtleta(usuario);
        return usuario;
    }
    
    @Transactional
    public Usuario cadastrarResponsavel(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setEnderecoUnico(gerarEnderecoUnico(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoResponsavel(usuario);
        return usuario;
    }
    
    @Transactional
    public DetalhePerfil vincularResponsavel(Usuario atleta, Usuario responsavel) {
        if(!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) throw new ConflictException("A conta informada não é do tipo válido (Tipo A)");
        Map<String, Object> info = new HashMap<>();
        Object data = responsavel.getAliasId();
        info.put("RESPONSAVEL", data);
        return detalhePerfilService.adicionarInformacao(info, atleta);
    }
    
    @Transactional
    public void desvincularResponsavel(Usuario responsavel, Usuario atleta) {
        if(!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) throw new ConflictException("A conta informada não é do tipo válido (Tipo A)");
        // quem faz a requisicao é o responsavel?
            // JWT validation aqui
        Map<String, Object> info = detalhePerfilRepository.getByUsuario(atleta).getData();
        // valida se esse responsavel realmente é o RESPONSAVEL de atleta
        System.out.println(info.get("RESPONSAVEL"));
        System.out.println(responsavel.getAliasId());
        if(info.get("RESPONSAVEL").equals(responsavel.getAliasId().toString()) == false) throw new ConflictException("Usuario não autorizado a realizar esta ação");
        // remove
        info.remove("RESPONSAVEL");
    }
    
    @Transactional
    public Usuario cadastrarOlheiro(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setEnderecoUnico(gerarEnderecoUnico(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoOlheiro(usuario);
        return usuario;
    }
    @Transactional
    public void buscarPor() {}

    @Transactional
    public void atualizarPerfil() {}

    @Transactional
    public DetalhePerfil adicionarInformacao(Map <String, Object> info, Usuario usuario) {
        return detalhePerfilService.adicionarInformacao(info, usuario);
    }

    @Transactional
    public DetalhePerfil adicionarInformacao(String chave, Object dado, Usuario usuario) {
        return detalhePerfilService.adicionarInformacao(chave, dado, usuario);
    }

    @Transactional
    public void removerInformacao(String chave, Usuario usuario) {
        detalhePerfilService.removerInformacao(chave, usuario);
    }

    @Transactional
    public void banir() {}

    @Transactional
    public void removerBanimento() {}

    @Transactional
    public void deletar() {}

    @Transactional
    public void deletarPermanentemente() {}

    @Transactional
    public void mudarFotoPerfil() {}

    @Transactional
    public void removerFotoPerfil() {}

    @Transactional
    public void buscarSeguidores() {}

    @Transactional
    public void buscarQuemSegue() {}

}