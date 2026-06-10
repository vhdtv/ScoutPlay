package com.scoutplay.ScoutPlay.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.api.dto.UserProfileFieldsDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DetalhePerfilRepository detalhePerfilRepository;
    @Autowired
    private XUsuarioTipoContaRepository xUsuarioTipoContaRepository;
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
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um atleta com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoAtleta(usuario);
        if(usuario.obterIdade() >= 18) this.vincularResponsavel(usuario, usuario);
        return usuario;
    }
    
    @Transactional
    public Usuario cadastrarResponsavel(Usuario usuario) {
        if(usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoResponsavel(usuario);
        return usuario;
    }
    
    @Transactional
    public DetalhePerfil vincularResponsavel(Usuario atleta, Usuario responsavel) {
        Map<String, Object> info = new HashMap<>();
        Object data = responsavel.getAliasId();
        info.put("RESPONSAVEL", data);
        return detalhePerfilService.adicionarInformacao("RESPONSAVEL", responsavel.getAliasId(), atleta);
    }
    
    @Transactional
    public void desvincularResponsavel(Usuario responsavel, Usuario atleta) {
        if(!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) throw new ConflictException("A conta informada não é do tipo válido (Tipo A)");
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
        tipoContaService.categorizarContaComoOlheiro(usuario);
        return usuario;
    }
    
    @Transactional
    public Usuario buscarPor(UUID aliasId) {
        return this.usuarioRepository.findByAliasId(aliasId).orElse(null);
    }

    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar, UUID idDoUsuarioAutenticado) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(idDoUsuarioAProcurar);
        Set<XUsuarioTipoConta> poderesQueContaPossui = this.xUsuarioTipoContaRepository.getByUsuario(usuario);
        String[] poderesConta = poderesQueContaPossui.stream().map(poder -> resolverTipo(poder)).distinct().toArray(String[]::new);

        return UserProfileSummary.builder()
            .nome(usuario.getNome())
            .username(usuario.getUsername())
            .sobrenome(usuario.getSobrenome())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(poderesConta)
            .idade(usuario.obterIdade())
            .build();
    }

    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(idDoUsuarioAProcurar);
        if(usuario == null) throw new Error("Usuario não encontrado");
        Set<XUsuarioTipoConta> poderesQueContaPossui = this.xUsuarioTipoContaRepository.getByUsuario(usuario);
        String[] poderesConta = poderesQueContaPossui.stream().map(poder -> resolverTipo(poder)).distinct().toArray(String[]::new);

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
        Set<XUsuarioTipoConta> poderesQueContaPossui = this.xUsuarioTipoContaRepository.getByUsuario(usuario);
        String[] poderesConta = poderesQueContaPossui.stream().map(poder -> resolverTipo(poder)).distinct().toArray(String[]::new);
        
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

    static String resolverTipo(XUsuarioTipoConta relacao) {
        if (relacao == null) return "USUARIO";
        return switch (relacao.getTipoConta().getId()) {
            case TipoConta.ATLETA -> "ATLETA";
            case TipoConta.OLHEIRO -> "OLHEIRO";
            case TipoConta.RESPONSAVEL -> "RESPONSAVEL";
            case TipoConta.REPRESENTANTE_CLUBE -> "REPRESENTANTE_CLUBE";
            default -> "USUARIO";
        };
    }

}