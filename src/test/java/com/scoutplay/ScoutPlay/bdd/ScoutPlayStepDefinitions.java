package com.scoutplay.ScoutPlay.bdd;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.services.AtletaService;
import com.scoutplay.ScoutPlay.services.LoginService;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ScoutPlayStepDefinitions {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AtletaService atletaService;

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private OlheiroRepository olheiroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Estado do cenário
    private Optional<Usuario> resultadoLogin;
    private String tipoUsuarioRetornado;
    private String mensagemErroLogin;
    private boolean loginFalhouPorDadosInvalidos;
    private Atleta atletaCadastrado;
    private Exception excecaoCadastro;

    @Before
    void limparEstado() {
        resultadoLogin = null;
        tipoUsuarioRetornado = null;
        mensagemErroLogin = null;
        loginFalhouPorDadosInvalidos = false;
        atletaCadastrado = null;
        excecaoCadastro = null;
    }

    // ========== STEPS DE AUTENTICAÇÃO ==========

    @Dado("que existe um atleta com e-mail {string} e senha {string}")
    public void queExisteAtletaComEmailESenha(String email, String senha) {
        if (atletaRepository.findByEmail(email).isEmpty()) {
            Atleta atleta = new Atleta();
            atleta.setNome("Atleta Teste");
            atleta.setEmail(email);
            atleta.setCpf("000.000.000-" + email.hashCode() % 100);
            atleta.setSenha(senha);
            atletaService.criarAtleta(atleta);
        }
    }

    @Dado("que existe um olheiro com e-mail {string} e senha {string}")
    public void queExisteOlheiroComEmailESenha(String email, String senha) {
        if (olheiroRepository.findByEmail(email).isEmpty()) {
            Olheiro olheiro = new Olheiro();
            olheiro.setNome("Olheiro Teste");
            olheiro.setEmail(email);
            olheiro.setCpf("111.111.111-" + email.hashCode() % 100);
            olheiro.setSenha(passwordEncoder.encode(senha));
            olheiroRepository.save(olheiro);
        }
    }

    @Quando("eu realizo login com e-mail {string} e senha {string}")
    public void euRealizoLoginComEmailESenha(String email, String senha) {
        if (email.isBlank() || senha.isBlank()) {
            loginFalhouPorDadosInvalidos = true;
            return;
        }
        resultadoLogin = loginService.autenticar(email, senha);
        if (resultadoLogin.isEmpty()) {
            mensagemErroLogin = "Email ou senha inválidos";
        } else {
            tipoUsuarioRetornado = resultadoLogin.get() instanceof Atleta ? "ATLETA" : "OLHEIRO";
        }
    }

    @Entao("o login deve ser realizado com sucesso")
    public void oLoginDeveSerRealizadoComSucesso() {
        assertNotNull(resultadoLogin);
        assertTrue(resultadoLogin.isPresent(), "Esperava login bem-sucedido, mas autenticação falhou");
    }

    @E("o tipo de usuário retornado deve ser {string}")
    public void oTipoDeUsuarioRetornadoDeveSer(String tipoEsperado) {
        assertEquals(tipoEsperado, tipoUsuarioRetornado);
    }

    @Entao("o login deve falhar com mensagem {string}")
    public void oLoginDeveFalharComMensagem(String mensagemEsperada) {
        assertTrue(resultadoLogin == null || resultadoLogin.isEmpty(),
                "Esperava falha de login, mas autenticação foi bem-sucedida");
        assertEquals(mensagemEsperada, mensagemErroLogin);
    }

    @Entao("o login deve falhar por dados inválidos")
    public void oLoginDeveFalharPorDadosInvalidos() {
        assertTrue(loginFalhouPorDadosInvalidos);
    }

    // ========== STEPS DE CADASTRO ==========

    @Dado("que não existe atleta com CPF {string}")
    public void queNaoExisteAtletaComCpf(String cpf) {
        atletaRepository.findByCpf(cpf).ifPresent(a -> atletaRepository.delete(a));
    }

    @Dado("que não existe atleta com e-mail {string}")
    public void queNaoExisteAtletaComEmail(String email) {
        atletaRepository.findByEmail(email).ifPresent(a -> atletaRepository.delete(a));
    }

    @Dado("que já existe um atleta com CPF {string}")
    public void queJaExisteAtletaComCpf(String cpf) {
        if (!atletaRepository.existsByCpf(cpf)) {
            Atleta a = new Atleta();
            a.setNome("Atleta Existente");
            a.setEmail("existente.cpf." + cpf.hashCode() + "@email.com");
            a.setCpf(cpf);
            a.setSenha("senha");
            atletaService.criarAtleta(a);
        }
    }

    @Dado("que já existe um atleta com e-mail {string}")
    public void queJaExisteAtletaComEmail(String email) {
        if (atletaRepository.findByEmail(email).isEmpty()) {
            Atleta a = new Atleta();
            a.setNome("Atleta Existente Email");
            a.setEmail(email);
            a.setCpf("777.777.777-" + email.hashCode() % 100);
            a.setSenha("senha");
            atletaService.criarAtleta(a);
        }
    }

    @Quando("eu cadastro um atleta com nome {string}, CPF {string}, e-mail {string} e senha {string}")
    public void euCadastroAtletaComDados(String nome, String cpf, String email, String senha) {
        Atleta novoAtleta = new Atleta();
        novoAtleta.setNome(nome);
        novoAtleta.setCpf(cpf);
        novoAtleta.setEmail(email);
        novoAtleta.setSenha(senha);
        try {
            atletaCadastrado = atletaService.criarAtleta(novoAtleta);
        } catch (Exception e) {
            excecaoCadastro = e;
        }
    }

    @Quando("eu cadastro um atleta sem senha")
    public void euCadastroAtletaSemSenha() {
        Atleta novoAtleta = new Atleta();
        novoAtleta.setNome("Sem Senha");
        novoAtleta.setCpf("555.444.333-22");
        novoAtleta.setEmail("semsena@email.com");
        novoAtleta.setSenha(null);
        try {
            atletaCadastrado = atletaService.criarAtleta(novoAtleta);
        } catch (Exception e) {
            excecaoCadastro = e;
        }
    }

    @Entao("o atleta deve ser cadastrado com sucesso")
    public void oAtletaDeveSerCadastradoComSucesso() {
        assertNull(excecaoCadastro, "Não esperava exceção, mas ocorreu: " + (excecaoCadastro != null ? excecaoCadastro.getMessage() : ""));
        assertNotNull(atletaCadastrado);
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
