package com.scoutplay.ScoutPlay.bdd;

import com.scoutplay.ScoutPlay.api.dto.ClientLoginInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ClientLoginOutputDTO;
import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;
import com.scoutplay.ScoutPlay.services.AuthService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ScoutPlayStepDefinitions {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private XUsuarioTipoContaRepository xUsuarioTipoContaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Estado do cenário
    private ClientLoginOutputDTO resultadoLogin;
    private String tipoUsuarioRetornado;
    private String mensagemErroLogin;
    private boolean loginFalhouPorDadosInvalidos;
    private Usuario usuarioCadastrado;
    private Exception excecaoCadastro;

    @Before
    public void limparEstado() {
        resultadoLogin = null;
        tipoUsuarioRetornado = null;
        mensagemErroLogin = null;
        loginFalhouPorDadosInvalidos = false;
        usuarioCadastrado = null;
        excecaoCadastro = null;
    }

    // ========== STEPS DE AUTENTICAÇÃO ==========

    @Dado("que existe um atleta com e-mail {string} e senha {string}")
    public void queExisteAtletaComEmailESenha(String email, String senha) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario atleta = new Usuario("Atleta", "Teste", email, "000000001", senha, LocalDate.of(2000, 1, 1));
            atleta.setUsername("atletateste_" + Math.abs(email.hashCode() % 10000));
            usuarioService.cadastrarAtleta(atleta);
        }
    }

    @Dado("que existe um olheiro com e-mail {string} e senha {string}")
    public void queExisteOlheiroComEmailESenha(String email, String senha) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario olheiro = new Usuario("Olheiro", "Teste", email, "000000002", senha, LocalDate.of(1980, 1, 1));
            usuarioService.cadastrarOlheiro(olheiro);
        }
    }

    @Quando("eu realizo login com e-mail {string} e senha {string}")
    public void euRealizoLoginComEmailESenha(String email, String senha) {
        if (email.isBlank() || senha.isBlank()) {
            loginFalhouPorDadosInvalidos = true;
            return;
        }
        try {
            resultadoLogin = authService.autenticarUsuario(new ClientLoginInputDTO(email, senha));
            Usuario usuario = usuarioRepository.findByEmail(email);
            String tipo = xUsuarioTipoContaRepository.getByUsuario(usuario)
                    .stream()
                    .map(x -> switch (x.getTipoConta().getId()) {
                        case 2 -> "ATLETA";
                        case 1 -> "OLHEIRO";
                        case 3 -> "RESPONSAVEL";
                        default -> "DESCONHECIDO";
                    })
                    .filter(t -> t.equals("ATLETA") || t.equals("OLHEIRO"))
                    .findFirst()
                    .orElse("DESCONHECIDO");
            tipoUsuarioRetornado = tipo;
        } catch (IllegalArgumentException e) {
            mensagemErroLogin = e.getMessage();
        }
    }

    @Entao("o login deve ser realizado com sucesso")
    public void oLoginDeveSerRealizadoComSucesso() {
        assertNotNull(resultadoLogin, "Esperava login bem-sucedido, mas autenticação falhou: " + mensagemErroLogin);
        assertNotNull(resultadoLogin.getTokenAcesso(), "Token de acesso não deve ser nulo");
    }

    @E("o tipo de usuário retornado deve ser {string}")
    public void oTipoDeUsuarioRetornadoDeveSer(String tipoEsperado) {
        assertEquals(tipoEsperado, tipoUsuarioRetornado);
    }

    @Entao("o login deve falhar com mensagem {string}")
    public void oLoginDeveFalharComMensagem(String mensagemEsperada) {
        assertNull(resultadoLogin, "Esperava falha de login, mas autenticação foi bem-sucedida");
        assertEquals(mensagemEsperada, mensagemErroLogin);
    }

    @Entao("o login deve falhar por dados inválidos")
    public void oLoginDeveFalharPorDadosInvalidos() {
        assertTrue(loginFalhouPorDadosInvalidos);
    }

    // ========== STEPS DE CADASTRO ==========

    @Dado("que não existe atleta com CPF {string}")
    public void queNaoExisteAtletaComCpf(String cpf) {
        usuarioRepository.findByCpf(cpf).ifPresent(u -> usuarioRepository.delete(u));
    }

    @Dado("que não existe atleta com e-mail {string}")
    public void queNaoExisteAtletaComEmail(String email) {
        Usuario u = usuarioRepository.findByEmail(email);
        if (u != null) usuarioRepository.delete(u);
    }

    @Dado("que já existe um atleta com CPF {string}")
    public void queJaExisteAtletaComCpf(String cpf) {
        if (!usuarioRepository.existsByCpf(cpf)) {
            Usuario a = new Usuario("Atleta", "Existente", "existente.cpf." + Math.abs(cpf.hashCode()) + "@email.com", cpf, "senha123", LocalDate.of(2000, 1, 1));
            a.setUsername("atletaexist_" + Math.abs(cpf.hashCode() % 10000));
            usuarioService.cadastrarAtleta(a);
        }
    }

    @Dado("que já existe um atleta com e-mail {string}")
    public void queJaExisteAtletaComEmail(String email) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario a = new Usuario("Atleta", "Email", email, "777777" + Math.abs(email.hashCode() % 100), "senha123", LocalDate.of(2000, 1, 1));
            a.setUsername("atletaemail_" + Math.abs(email.hashCode() % 10000));
            usuarioService.cadastrarAtleta(a);
        }
    }

    @Quando("eu cadastro um atleta com nome {string}, CPF {string}, e-mail {string} e senha {string}")
    public void euCadastroAtletaComDados(String nome, String cpf, String email, String senha) {
        Usuario novoAtleta = new Usuario(nome, "Sobrenome", email, cpf, senha, LocalDate.of(2000, 1, 1));
        novoAtleta.setUsername("bdd_" + Math.abs(email.hashCode() % 100000));
        try {
            usuarioCadastrado = usuarioService.cadastrarAtleta(novoAtleta);
        } catch (Exception e) {
            excecaoCadastro = e;
        }
    }

    @Quando("eu cadastro um atleta sem senha")
    public void euCadastroAtletaSemSenha() {
        Usuario novoAtleta = new Usuario("Sem", "Senha", "semsena@email.com", "555444333", null, LocalDate.of(2000, 1, 1));
        novoAtleta.setUsername("semsenhbdd");
        try {
            usuarioCadastrado = usuarioService.cadastrarAtleta(novoAtleta);
        } catch (Exception e) {
            excecaoCadastro = e;
        }
    }

    @Entao("o atleta deve ser cadastrado com sucesso")
    public void oAtletaDeveSerCadastradoComSucesso() {
        assertNull(excecaoCadastro, "Nao esperava excecao: " + (excecaoCadastro != null ? excecaoCadastro.getMessage() : ""));
        assertNotNull(usuarioCadastrado);
    }

    @Entao("o cadastro deve falhar com conflito de CPF")
    public void oCadastroDeveFalharComConflitoDeCpf() {
        assertNotNull(excecaoCadastro);
        assertInstanceOf(ConflictException.class, excecaoCadastro);
        assertTrue(excecaoCadastro.getMessage().contains("CPF"));
    }

    @Entao("o cadastro deve falhar com conflito de e-mail")
    public void oCadastroDeveFalharComConflitoDeEmail() {
        assertNotNull(excecaoCadastro);
        assertInstanceOf(ConflictException.class, excecaoCadastro);
        assertTrue(excecaoCadastro.getMessage().contains("e-mail"));
    }

    @Entao("o cadastro deve falhar por senha obrigatória")
    public void oCadastroDeveFalharPorSenhaObrigatoria() {
        assertNotNull(excecaoCadastro);
        assertInstanceOf(IllegalArgumentException.class, excecaoCadastro);
        assertTrue(excecaoCadastro.getMessage().contains("Senha"));
    }
}
