package com.scoutplay.ScoutPlay.bdd;

import com.scoutplay.ScoutPlay.api.dto.PostDataOutputDTO;
import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.services.ComentarioService;
import com.scoutplay.ScoutPlay.services.DetalhePerfilService;
import com.scoutplay.ScoutPlay.services.InteractionsService;
import com.scoutplay.ScoutPlay.services.PostService;
import com.scoutplay.ScoutPlay.services.TipoMidiaService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PostsPerfilStepDefinitions {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private InteractionsService interactionsService;

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private DetalhePerfilService detalhePerfilService;

    @Autowired
    private TipoMidiaService tipoMidiaService;

    // Estado do cenário
    private Usuario usuarioAtual;
    private Post postAtual;
    private boolean likeRegistrado;
    private boolean dislikeRegistrado;
    private Comentario comentarioCriado;
    private ArrayList<Comentario> comentariosDoPost;
    private Object dadosPerfil;
    private Object detalhePerfilSalvo;
    private Object detalhePerfilRemovido;

    @Before
    public void limparEstadoPosts() {
        usuarioAtual = null;
        postAtual = null;
        likeRegistrado = false;
        dislikeRegistrado = false;
        comentarioCriado = null;
        comentariosDoPost = null;
        dadosPerfil = null;
        detalhePerfilSalvo = null;
        detalhePerfilRemovido = null;
    }

    // ========== STEPS COMPARTILHADOS ==========

    @Dado("que o atleta {string} com senha {string} está autenticado")
    public void queOAtletaEstaAutenticado(String email, String senha) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario atleta = new Usuario("Fabio", "Braga", email, "999888777", senha, LocalDate.of(2000, 1, 1));
            atleta.setUsername("fabin_123");
            usuarioService.cadastrarAtleta(atleta);
        }
        usuarioAtual = usuarioRepository.findByEmail(email);
        assertNotNull(usuarioAtual, "Usuário não encontrado: " + email);
    }

    // ========== STEPS DE POSTS ==========

    @Dado("que existe um post cadastrado")
    public void queExisteUmPostCadastrado() {
        Post post = new Post("Post de Teste", "Descricao do post", "video.mp4",
                tipoMidiaService.categorizarComoVideo(), usuarioAtual);
        PostDataOutputDTO dto = postService.criar(post);
        assertNotNull(dto);
        postAtual = postService.buscarPor(dto.getUrl()).orElseThrow();
    }

    @Quando("o atleta dá like no post")
    public void oAtletaDaLikeNoPost() {
        Usuario comLike = usuarioService.buscarPorComLike(usuarioAtual.getAliasId());
        interactionsService.darLike(postAtual.getAliasId(), comLike);
        likeRegistrado = true;
    }

    @Quando("o atleta dá dislike no post")
    public void oAtletaDaDislikeNoPost() {
        Usuario comLike = usuarioService.buscarPorComLike(usuarioAtual.getAliasId());
        interactionsService.darDislike(postAtual.getAliasId(), comLike);
        dislikeRegistrado = true;
    }

    @Entao("o like deve ser registrado com sucesso")
    public void oLikeDeveSerRegistradoComSucesso() {
        assertTrue(likeRegistrado);
        Post postAtualizado = postService.buscarPorComLike(postAtual.getAliasId()).orElseThrow();
        assertTrue(postAtualizado.getUsuariosQueCurtiram().size() >= 1);
    }

    @Entao("o dislike deve ser registrado com sucesso")
    public void oDislikeDeveSerRegistradoComSucesso() {
        assertTrue(dislikeRegistrado);
    }

    @Quando("o atleta comenta {string} no post")
    public void oAtletaComentaNoPost(String texto) {
        Comentario comentario = new Comentario(texto, postAtual, usuarioAtual);
        comentarioCriado = comentarioService.criar(comentario);
    }

    @Entao("o comentário deve ser salvo com sucesso")
    public void oComentarioDeveSerSalvoComSucesso() {
        assertNotNull(comentarioCriado);
        assertNotNull(comentarioCriado.getAliasId());
    }

    @E("busco os comentários do post")
    public void buscoOsComentariosDoPost() {
        comentariosDoPost = comentarioService.buscarTodosPorPost(postAtual);
    }

    @Entao("a lista de comentários não deve estar vazia")
    public void aListaDeComentariosNaoDeveEstarVazia() {
        assertNotNull(comentariosDoPost);
        assertFalse(comentariosDoPost.isEmpty());
    }

    // ========== STEPS DE PERFIL ==========

    @Quando("busco os dados do perfil do username {string}")
    public void buscoOsDadosDoPerfil(String username) {
        dadosPerfil = usuarioService.buscarDadosPerfil(username, usuarioAtual.getAliasId());
        assertNotNull(dadosPerfil);
    }

    @Quando("busco os dados do perfil do username {string} sem autenticação")
    public void buscoOsDadosDoPerfilSemAutenticacao(String username) {
        dadosPerfil = usuarioService.buscarDadosPerfil(username);
        assertNotNull(dadosPerfil);
    }

    @Entao("os dados do perfil devem ser retornados com sucesso")
    public void osDadosDoPerfilDevemSerRetornadosComSucesso() {
        assertNotNull(dadosPerfil);
    }

    @Quando("adiciono o detalhe de perfil com chave {string} e valor {string}")
    public void adicionoODetalheDePerfil(String chave, String valor) {
        detalhePerfilSalvo = usuarioService.adicionarInformacao(chave, valor, usuarioAtual);
        assertNotNull(detalhePerfilSalvo);
    }

    @Entao("o detalhe de perfil deve ser salvo com sucesso")
    public void oDetalheDePerfilDeveSerSalvoComSucesso() {
        assertNotNull(detalhePerfilSalvo);
    }

    @Dado("que o detalhe de perfil com chave {string} e valor {string} existe")
    public void queODetalheDePerfilExiste(String chave, String valor) {
        usuarioService.adicionarInformacao(chave, valor, usuarioAtual);
    }

    @Quando("removo o detalhe de perfil com chave {string}")
    public void removoODetalheDePerfil(String chave) {
        detalhePerfilRemovido = usuarioService.removerInformacao(chave, usuarioAtual);
    }

    @Entao("o detalhe de perfil deve ser removido com sucesso")
    public void oDetalheDePerfilDeveSerRemovidoComSucesso() {
        assertNotNull(detalhePerfilRemovido);
    }
}
