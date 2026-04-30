package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.models.Responsavel;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AtletaRepository atletaRepository;

    @Mock
    private OlheiroRepository olheiroRepository;

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private Atleta atleta;
    private Olheiro olheiro;
    private Responsavel responsavel;

    @BeforeEach
    void setUp() {
        atleta = new Atleta();
        atleta.setId("ATL-001");
        atleta.setEmail("atleta@email.com");
        atleta.setSenha("$2a$hash-atleta"); // senha encodada

        olheiro = new Olheiro();
        olheiro.setId("OLH-001");
        olheiro.setEmail("olheiro@email.com");
        olheiro.setSenha("$2a$hash-olheiro");

        responsavel = new Responsavel();
        responsavel.setId("RES-001");
        responsavel.setEmail("resp@email.com");
        responsavel.setSenha("$2a$hash-responsavel");
    }

    @Test
    void deveAutenticarAtletaComSucesso() {
        when(atletaRepository.findByEmail("atleta@email.com")).thenReturn(Optional.of(atleta));
        when(passwordEncoder.matches("senha123", "$2a$hash-atleta")).thenReturn(true);

        Optional<Usuario> resultado = loginService.autenticar("atleta@email.com", "senha123");

        assertTrue(resultado.isPresent());
        assertEquals("ATL-001", resultado.get().getId());
        assertInstanceOf(Atleta.class, resultado.get());
    }

    @Test
    void deveAutenticarOlheiroComSucesso() {
        when(atletaRepository.findByEmail("olheiro@email.com")).thenReturn(Optional.empty());
        when(olheiroRepository.findByEmail("olheiro@email.com")).thenReturn(Optional.of(olheiro));
        when(passwordEncoder.matches("senha456", "$2a$hash-olheiro")).thenReturn(true);

        Optional<Usuario> resultado = loginService.autenticar("olheiro@email.com", "senha456");

        assertTrue(resultado.isPresent());
        assertInstanceOf(Olheiro.class, resultado.get());
    }

    @Test
    void deveAutenticarResponsavelComSucesso() {
        when(atletaRepository.findByEmail("resp@email.com")).thenReturn(Optional.empty());
        when(olheiroRepository.findByEmail("resp@email.com")).thenReturn(Optional.empty());
        when(responsavelRepository.findByEmail("resp@email.com")).thenReturn(Optional.of(responsavel));
        when(passwordEncoder.matches("senhaResp", "$2a$hash-responsavel")).thenReturn(true);

        Optional<Usuario> resultado = loginService.autenticar("resp@email.com", "senhaResp");

        assertTrue(resultado.isPresent());
        assertInstanceOf(Responsavel.class, resultado.get());
    }

    @Test
    void deveRetornarVazioQuandoSenhaForIncorreta() {
        when(atletaRepository.findByEmail("atleta@email.com")).thenReturn(Optional.of(atleta));
        when(passwordEncoder.matches("senhaErrada", "$2a$hash-atleta")).thenReturn(false);
        when(olheiroRepository.findByEmail("atleta@email.com")).thenReturn(Optional.empty());
        when(responsavelRepository.findByEmail("atleta@email.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = loginService.autenticar("atleta@email.com", "senhaErrada");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExiste() {
        when(atletaRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());
        when(olheiroRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());
        when(responsavelRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = loginService.autenticar("inexistente@email.com", "qualquerSenha");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRetornarVazioQuandoSenhaArmazenadaForNula() {
        atleta.setSenha(null);
        when(atletaRepository.findByEmail("atleta@email.com")).thenReturn(Optional.of(atleta));
        when(olheiroRepository.findByEmail("atleta@email.com")).thenReturn(Optional.empty());
        when(responsavelRepository.findByEmail("atleta@email.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = loginService.autenticar("atleta@email.com", "qualquerSenha");

        assertTrue(resultado.isEmpty());
    }
}
