package com.scoutplay.ScoutPlay.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scoutplay.ScoutPlay.api.dto.AtletaCardDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileFieldsDTO;
import com.scoutplay.ScoutPlay.api.dto.UserProfileSummary;
import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.models.Seguidor;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.SeguidorRepository;
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
    private PostRepository postRepository;
    @Autowired
    private SeguidorRepository seguidorRepository;
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
        validarCredenciaisBasicas(usuario);
        if(usuario.getCpf() != null && usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um atleta com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        if(usuario.getUsername() == null || usuario.getUsername().isBlank()) usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoAtleta(usuario);
        Integer idade = usuario.obterIdade();
        if (idade != null && idade < 18) {
            detalhePerfilService.adicionarInformacao("RESPONSAVEL_PENDENTE", true, usuario);
        }
        return usuario;
    }
    
    @Transactional
    public Usuario cadastrarResponsavel(Usuario usuario) {
        validarCredenciaisBasicas(usuario);
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
        if (!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) {
            throw new ConflictException("A conta informada não é de atleta");
        }
        if (!tipoContaService.verificarTipoConta(responsavel, TipoConta.RESPONSAVEL)) {
            throw new ConflictException("A conta autenticada não é de responsável");
        }
        Integer idade = atleta.obterIdade();
        if (idade == null || idade >= 18) {
            throw new ConflictException("Vínculo de responsável é permitido somente para atleta menor de idade");
        }
        DetalhePerfil detalhe = detalhePerfilRepository.getByUsuario(atleta);
        if (detalhe != null && detalhe.getData() != null) {
            Object existente = detalhe.getData().get("RESPONSAVEL");
            if (existente != null && !existente.toString().equals(responsavel.getAliasId().toString())) {
                throw new ConflictException("O atleta já possui responsável vinculado");
            }
        }
        Map<String, Object> info = new HashMap<>();
        info.put("RESPONSAVEL", responsavel.getAliasId().toString());
        info.put("RESPONSAVEL_PENDENTE", false);
        return detalhePerfilService.adicionarInformacao(info, atleta);
    }

    @Transactional
    public DetalhePerfil vincularResponsavel(UUID responsavelId, String atletaUsername) {
        Usuario responsavel = buscarPor(responsavelId);
        Usuario atleta = usuarioRepository.findByUsernameIgnoreCase(atletaUsername);
        if (responsavel == null || atleta == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        return vincularResponsavel(atleta, responsavel);
    }
    
    @Transactional
    public void desvincularResponsavel(Usuario responsavel, Usuario atleta) {
        if(!tipoContaService.verificarTipoConta(atleta, TipoConta.ATLETA)) throw new ConflictException("A conta informada não é do tipo válido (Tipo A)");
        Map<String, Object> detalhePerfilDoAtleta = detalhePerfilRepository.getByUsuario(atleta).getData();
        // valida se esse responsavel realmente é o RESPONSAVEL de atleta
        if (!Objects.equals(String.valueOf(detalhePerfilDoAtleta.get("RESPONSAVEL")), responsavel.getAliasId().toString())) {
            throw new ConflictException("Usuário não autorizado a realizar esta ação");
        }
        detalhePerfilDoAtleta.remove("RESPONSAVEL");
        detalhePerfilDoAtleta.put("RESPONSAVEL_PENDENTE", true);
    }
    
    @Transactional
    public Usuario cadastrarOlheiro(Usuario usuario) {
        validarCredenciaisBasicas(usuario);
        if(usuario.getCpf() != null && usuarioRepository.existsByCpf(usuario.getCpf())) throw new ConflictException("Um cadastro com este CPF já existe.");
        if(usuarioRepository.existsByEmail(usuario.getEmail())) throw new ConflictException("Este e-mail já está em uso. Por favor, utilize outro.");
        usuario.setUsername(gerarUsername(usuario.getNome()));
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.saveAndFlush(usuario);
        tipoContaService.categorizarContaComoOlheiro(usuario);
        return usuario;
    }

    private void validarCredenciaisBasicas(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (usuario.getSenha().length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
        }
        usuario.setEmail(usuario.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
    }
    
    @Transactional
    public Usuario buscarPor(UUID aliasId) {
        return this.usuarioRepository.findByAliasId(aliasId).orElse(null);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.saveAndFlush(usuario);
    }

    @Transactional
    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar, UUID idDoUsuarioAutenticado) {
        Usuario solicitante = idDoUsuarioAutenticado != null ? usuarioRepository.findByAliasId(idDoUsuarioAutenticado).orElse(null) : null;
        return buscarDadosPerfilInterno(idDoUsuarioAProcurar, solicitante);
    }

    @Transactional
    public UserProfileSummary buscarDadosPerfil(String idDoUsuarioAProcurar) {
        return buscarDadosPerfilInterno(idDoUsuarioAProcurar, null);
    }

    private UserProfileSummary buscarDadosPerfilInterno(String username, Usuario solicitante) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username);
        if (usuario == null) throw new ResourceNotFoundException("Usuário não encontrado");

        Set<XUsuarioTipoConta> poderes = xUsuarioTipoContaRepository.getByUsuario(usuario);
        String[] tipoConta = poderes.stream().map(this::resolverTipoInstance).distinct().toArray(String[]::new);

        Map<String, Object> detalhesPerfil = null;
        try {
            DetalhePerfil dp = detalhePerfilRepository.getByUsuario(usuario);
            if (dp != null && dp.getData() != null) {
                detalhesPerfil = new HashMap<>(dp.getData());
                boolean possuiResponsavel = detalhesPerfil.get("RESPONSAVEL") != null;
                detalhesPerfil.remove("RESPONSAVEL");
                detalhesPerfil.put("possuiResponsavel", possuiResponsavel);
            }
        } catch (Exception ignored) {}

        List<UserProfileSummary.PostResume> posts = postRepository
            .findByAutorAndAtivoTrue(usuario, Pageable.unpaged())
            .getContent()
            .stream()
            .map(post -> UserProfileSummary.PostResume.builder()
                .url(post.getAliasId().toString())
                .titulo(post.getTitulo())
                .src(post.getCaminhoArquivo())
                .mimeType(post.obterMimeType())
                .build())
            .collect(Collectors.toList());

        long seguidores = seguidorRepository.countBySeguido(usuario);
        long seguindo = seguidorRepository.countBySeguidor(usuario);
        boolean souSeguidor = solicitante != null && seguidorRepository.existsBySeguidorAndSeguido(solicitante, usuario);

        return UserProfileSummary.builder()
            .nome(usuario.getNome())
            .username(usuario.getUsername())
            .sobrenome(usuario.getSobrenome())
            .iniciais(usuario.getIniciais())
            .fotoPerfil(usuario.getFotoPerfil())
            .tipoConta(tipoConta)
            .idade(usuario.obterIdade())
            .detalhesPerfil(detalhesPerfil)
            .posts(posts)
            .seguidores(seguidores)
            .seguindo(seguindo)
            .souSeguidor(souSeguidor)
            .build();
    }

    @Transactional
    public boolean seguir(UUID seguidorId, String usernameAlvo) {
        Usuario seguidor = usuarioRepository.findByAliasId(seguidorId).orElse(null);
        Usuario seguido = usuarioRepository.findByUsernameIgnoreCase(usernameAlvo);
        if (seguidor == null || seguido == null || seguidor.getAliasId().equals(seguido.getAliasId())) return false;
        if (seguidorRepository.existsBySeguidorAndSeguido(seguidor, seguido)) return true;
        seguidorRepository.save(new Seguidor(seguidor, seguido));
        return true;
    }

    @Transactional
    public boolean pararDeSeguir(UUID seguidorId, String usernameAlvo) {
        Usuario seguidor = usuarioRepository.findByAliasId(seguidorId).orElse(null);
        Usuario seguido = usuarioRepository.findByUsernameIgnoreCase(usernameAlvo);
        if (seguidor == null || seguido == null) return false;
        seguidorRepository.findBySeguidorAndSeguido(seguidor, seguido).ifPresent(seguidorRepository::delete);
        return true;
    }

    private String resolverTipoInstance(XUsuarioTipoConta relacao) {
        return resolverTipo(relacao);
    }

    @Transactional
    public DetalhePerfil atualizarConfiguracoesPerfilParcialmente(Map<String, Object> config, Usuario usuario) {
        DetalhePerfil informacoesNoBanco = detalhePerfilRepository.getByUsuario(usuario);
        informacoesNoBanco.getData().putAll(config);
        return informacoesNoBanco;
    }   

    @Transactional
    public UserProfileSummary atualizarPerfilParcialmente(UserProfileFieldsDTO novaInformacao, Usuario usuario) {
        if (novaInformacao.getNome() != null && novaInformacao.getNome().isPresent())
            usuario.setNome(novaInformacao.getNome().get());
        if (novaInformacao.getSobrenome() != null && novaInformacao.getSobrenome().isPresent())
            usuario.setSobrenome(novaInformacao.getSobrenome().get());
        if (novaInformacao.getUsername() != null && novaInformacao.getUsername().isPresent()) {
            String novoUsername = novaInformacao.getUsername().get().trim().toLowerCase().replaceAll("\\s+", "_");
            Usuario existente = usuarioRepository.findByUsernameIgnoreCase(novoUsername);
            if (existente != null && !existente.getAliasId().equals(usuario.getAliasId())) {
                throw new ConflictException("Este nome de usuário já está em uso.");
            }
            if (!novoUsername.isEmpty()) usuario.setUsername(novoUsername);
        }
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

    @Transactional
    public Usuario buscarPorComLike(UUID aliasId) {
        return this.usuarioRepository.findByIdWithPostsCurtidos(aliasId).orElse(null);
    }

    // ── Busca de atletas ──────────────────────────────────────────────────────

    public List<AtletaCardDTO> buscarAtletas(String posicao, String peDominante, String nome, int page) {
        int pageSize = 12;
        List<Usuario> todos = usuarioRepository
            .findAllAtivosByTipoContaId(TipoConta.ATLETA,
                org.springframework.data.domain.PageRequest.of(0, 500))
            .getContent();

        List<AtletaCardDTO> cards = todos.stream()
            .map(this::toAtletaCard)
            .collect(Collectors.toList());

        if (nome != null && !nome.isBlank()) {
            String lower = nome.toLowerCase();
            cards = cards.stream()
                .filter(a -> (a.getNome() + " " + (a.getSobrenome() != null ? a.getSobrenome() : ""))
                    .toLowerCase().contains(lower))
                .collect(Collectors.toList());
        }
        if (posicao != null && !posicao.isBlank()) {
            cards = cards.stream().filter(a -> posicao.equals(a.getPosicao())).collect(Collectors.toList());
        }
        if (peDominante != null && !peDominante.isBlank()) {
            cards = cards.stream().filter(a -> peDominante.equals(a.getPeDominante())).collect(Collectors.toList());
        }

        int start = page * pageSize;
        if (start >= cards.size()) return Collections.emptyList();
        return cards.subList(start, Math.min(start + pageSize, cards.size()));
    }

    private AtletaCardDTO toAtletaCard(Usuario atleta) {
        String posicao = null, peDominante = null, clubes = null;
        try {
            DetalhePerfil dp = detalhePerfilRepository.getByUsuario(atleta);
            if (dp != null && dp.getData() != null) {
                Map<String, Object> d = dp.getData();
                posicao     = strVal(d, "posicao", "POSICAO");
                peDominante = strVal(d, "peDominante", "PE_DOMINANTE");
                clubes      = strVal(d, "clubesAnteriores", "CLUBE_QUE_PARTICIPOU");
            }
        } catch (Exception ignored) {}
        return AtletaCardDTO.builder()
            .username(atleta.getUsername())
            .nome(atleta.getNome())
            .sobrenome(atleta.getSobrenome())
            .iniciais(atleta.getIniciais())
            .fotoPerfil(atleta.getFotoPerfil())
            .idade(atleta.obterIdade())
            .posicao(posicao)
            .peDominante(peDominante)
            .clubesAnteriores(clubes)
            .seguidores(seguidorRepository.countBySeguido(atleta))
            .build();
    }

    private String strVal(Map<String, Object> data, String... keys) {
        for (String k : keys) {
            Object v = data.get(k);
            if (v != null) return v.toString();
        }
        return null;
    }

    // ── Shortlist (lista de observação do olheiro) ────────────────────────────

    @Transactional
    public void adicionarAShortlist(UUID olheiroId, String atletaUsername) {
        Usuario olheiro = buscarPor(olheiroId);
        DetalhePerfil dp = detalhePerfilRepository.getByUsuario(olheiro);
        List<String> lista;
        if (dp == null || dp.getData() == null) {
            lista = new ArrayList<>();
        } else {
            lista = shortlistDe(dp);
        }
        if (!lista.contains(atletaUsername)) lista.add(atletaUsername);
        Map<String, Object> update = new HashMap<>();
        update.put("shortlist", lista);
        adicionarInformacao(update, olheiro);
    }

    @Transactional
    public void removerDaShortlist(UUID olheiroId, String atletaUsername) {
        Usuario olheiro = buscarPor(olheiroId);
        DetalhePerfil dp = detalhePerfilRepository.getByUsuario(olheiro);
        if (dp == null || dp.getData() == null) return;
        List<String> lista = shortlistDe(dp);
        lista.remove(atletaUsername);
        Map<String, Object> update = new HashMap<>();
        update.put("shortlist", lista);
        adicionarInformacao(update, olheiro);
    }

    public List<AtletaCardDTO> obterShortlist(UUID olheiroId) {
        Usuario olheiro = buscarPor(olheiroId);
        DetalhePerfil dp = detalhePerfilRepository.getByUsuario(olheiro);
        if (dp == null || dp.getData() == null) return Collections.emptyList();
        return shortlistDe(dp).stream()
            .map(usuarioRepository::findByUsernameIgnoreCase)
            .filter(Objects::nonNull)
            .map(this::toAtletaCard)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<String> shortlistDe(DetalhePerfil dp) {
        Object raw = dp.getData().get("shortlist");
        if (raw instanceof List) return new ArrayList<>((List<String>) raw);
        return new ArrayList<>();
    }

}
