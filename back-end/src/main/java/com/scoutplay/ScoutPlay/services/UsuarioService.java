package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutplay.ScoutPlay.api.dto.UserProfileFieldsDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.enums.TipoContaEnum;
import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;


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

    private String gerarUsername(String nomeUsuario) {
        String enderecoGerado = "";
        boolean enderecoGeradoJaExisteNoBanco = true;
        do {
            enderecoGerado = nomeUsuario.toLowerCase().replaceAll(" ", "_");
            Random rand = new Random();
            final int MIN_VALUE = 1000;
            final int MAX_VALUE = 99999;
            enderecoGerado += rand.nextInt(MAX_VALUE - MIN_VALUE) + MIN_VALUE;
            enderecoGeradoJaExisteNoBanco = usuarioRepository.findByUsernameIgnoreCase(enderecoGerado) != null;
        } while(enderecoGeradoJaExisteNoBanco);
        
        return enderecoGerado;
    }
    
    @Transactional
    public Usuario cadastrarAtleta(Usuario usuario) {
        if(usuario.getSenha() == null || usuario.getSenha().isBlank()) throw new IllegalArgumentException("Senha é obrigatória");
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um atleta com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        if(usuario.getUsername().isBlank()) usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoAtleta(usuario.getAliasId());
        if(usuario.obterIdade() >= 18) this.vincularResponsavel(usuario.getUsername(), usuario.getAliasId());
        return usuario;
    }
    
    @Transactional
    public Usuario cadastrarResponsavel(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoResponsavel(usuario.getAliasId());
        return usuario;
    }

    
    @Transactional
    public DetalhePerfil vincularResponsavel(String username, UUID aliasId) {
        Usuario usuarioLogado = usuarioRepository.findByAliasId(aliasId).get();
        Usuario outro = usuarioRepository.findByUsernameIgnoreCase(username);
        Usuario responsavel, atleta;
        boolean usuarioLogadoEUmAtleta = tipoContaService.verificarTipoConta(usuarioLogado, TipoContaEnum.ATLETA);
        if(usuarioLogadoEUmAtleta) {
            responsavel = outro;
            atleta = usuarioLogado;
        }
        else {
            responsavel = usuarioLogado;
            atleta = outro;
        }

        Map<String, Object> info = new HashMap<>();
        Object data = responsavel.getAliasId();
        info.put("RESPONSAVEL", data);
        return detalhePerfilService.adicionarInformacao("RESPONSAVEL", responsavel.getAliasId(), atleta);
    }
    
    @Transactional
    public void desvincularResponsavel(String username, UUID aliasId) throws Exception {
        Usuario outro = usuarioRepository.findByUsernameIgnoreCase(username);
        Usuario usuarioLogado = usuarioRepository.findByAliasId(aliasId).get();
        Usuario responsavel, atleta;
        boolean usuarioLogadoEUmAtleta = tipoContaService.verificarTipoConta(usuarioLogado, TipoContaEnum.ATLETA);
        if(usuarioLogadoEUmAtleta) {
            responsavel = outro;
            atleta = usuarioLogado;
            if(atleta.obterIdade() < 18) throw new Exception("Ação proibida");
        }
        else {
            responsavel = usuarioLogado;
            atleta = outro;
        }
        
        Map<String, Object> detalhePerfilDoAtleta = detalhePerfilRepository.getByUsuario(atleta).getData();
        // valida se esse responsavel realmente é o RESPONSAVEL de atleta
        if(detalhePerfilDoAtleta.get("RESPONSAVEL").equals(responsavel.getAliasId().toString()) == false) throw new ConflictException("Usuario não autorizado a realizar esta ação");
        detalhePerfilDoAtleta.remove("RESPONSAVEL");
    }
    
    @Transactional
    public Usuario cadastrarOlheiro(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoOlheiro(usuario.getAliasId());
        return usuario;
    }
    
    @Transactional
    public Usuario buscarPor(UUID aliasId) {
        return this.usuarioRepository.findByAliasId(aliasId).orElse(null);
    }

    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar, UUID idDoUsuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByUsernameWithPoderesIgnoreCase(idDoUsuarioAProcurar);
        String[] poderesConta = usuario.getPoderesConta().stream().map(poder -> poder.getNome()).toArray(String[]::new);
        boolean souEu = false;
        if(usuario.getAliasId().equals(idDoUsuarioAutenticado)) souEu = true;

        return UserProfileSummary.builder()
            .nome(usuario.getNome())
            .username(usuario.getUsername())
            .sobrenome(usuario.getSobrenome())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(poderesConta)
            .souEu(souEu)
            .idade(usuario.obterIdade())
            .build();
    }

    @Transactional
    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(idDoUsuarioAProcurar);
        if(usuario == null) throw new Error("Usuario não encontrado");
        String[] poderesConta = usuario.getPoderesConta().stream().map(poder -> poder.getNome()).distinct().toArray(String[]::new);

        return UserProfileSummary.builder()
            .nome(usuario.getNome())
            .username(usuario.getUsername())
            .sobrenome(usuario.getSobrenome())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(poderesConta)
            .idade(usuario.obterIdade())
            .build();
    }

    @Transactional
    public DetalhePerfil atualizarConfiguracoesPerfilParcialmente(Map<String, Object> config, Usuario usuario) {
        DetalhePerfil informacoesNoBanco = detalhePerfilRepository.getByUsuario(usuario);
        informacoesNoBanco.getData().putAll(config);
        return informacoesNoBanco;
    }   

    @Transactional
    public UserProfileSummary atualizarPerfilParcialmente(UserProfileFieldsDTO novaInformacao, Usuario usuario) {
        if(novaInformacao.getNome().isPresent()) usuario.setNome(novaInformacao.getNome().get());
        String[] poderesConta = usuario.getPoderesConta().stream().map(poder -> poder.getNome()).distinct().toArray(String[]::new);
        
        return UserProfileSummary.builder()
            .nome(usuario.getNome())
            .username(usuario.getUsername())
            .sobrenome(usuario.getSobrenome())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(poderesConta)
            .idade(usuario.obterIdade())
            .build();
    }

    @Transactional
    public DetalhePerfil adicionarInformacao(Map <String, Object> info, Usuario usuario) {
        return detalhePerfilService.adicionarInformacao(info, usuario);
    }

    @Transactional
    public DetalhePerfil adicionarInformacao(String chave, Object dado, Usuario usuario) {
        return detalhePerfilService.adicionarInformacao(chave, dado, usuario);
    }

    @Transactional
    public DetalhePerfil removerInformacao(String chave, Usuario usuario) {
        return detalhePerfilService.removerInformacao(chave, usuario);
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

    @Transactional
    public Usuario buscarPorComLike(UUID aliasId) {
        return this.usuarioRepository.findByIdWithPostsCurtidos(aliasId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return this.usuarioRepository.findByUsernameIgnoreCase(username);
    }
    
    @Transactional
    public boolean seguir(String username, UUID aliasIdDoUsuarioLogado) {
        try {
            Usuario usuarioLogado = this.usuarioRepository.findByAliasId(aliasIdDoUsuarioLogado).get();
            Usuario contaASeguir = this.buscarPorUsername(username);
    
            usuarioLogado.seguir(contaASeguir);
            contaASeguir.getContasQueMeSeguem().add(usuarioLogado);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
    
    @Transactional
    public boolean deixarDeSeguir(String username, UUID aliasIdDoUsuarioLogado) {
        try {
            Usuario usuarioLogado = this.usuarioRepository.findByAliasId(aliasIdDoUsuarioLogado).get();
            Usuario contaASeguir = this.buscarPorUsername(username);
    
            usuarioLogado.pararDeSeguir(contaASeguir);
            contaASeguir.getContasQueMeSeguem().remove(usuarioLogado);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

}