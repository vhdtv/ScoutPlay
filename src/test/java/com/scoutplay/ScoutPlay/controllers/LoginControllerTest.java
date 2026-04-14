package com.scoutplay.ScoutPlay.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import com.scoutplay.ScoutPlay.services.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.scoutplay.ScoutPlay.security.JwtTokenProvider;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
import com.scoutplay.ScoutPlay.repositorys.ResponsavelRepository;
import com.scoutplay.ScoutPlay.security.SecurityUtils;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AtletaRepository atletaRepository;

    @MockBean
    private OlheiroRepository olheiroRepository;

    @MockBean
    private ResponsavelRepository responsavelRepository;

    @Test
    void deveRealizarLoginDeAtletaComSucesso() throws Exception {
        Atleta atleta = new Atleta();
        atleta.setId("ATL-123");
        atleta.setNome("Joao Atleta");
        atleta.setEmail("atleta@scoutplay.com");

        when(loginService.autenticar("atleta@scoutplay.com", "senha123")).thenReturn(Optional.of(atleta));
        when(jwtTokenProvider.generateToken("ATL-123", "ATLETA")).thenReturn("jwt-atleta");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginPayload("atleta@scoutplay.com", "senha123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login realizado com sucesso"))
            .andExpect(jsonPath("$.data.userType").value("ATLETA"))
            .andExpect(jsonPath("$.data.userId").value("ATL-123"))
            .andExpect(jsonPath("$.data.nome").value("Joao Atleta"))
            .andExpect(jsonPath("$.data.email").value("atleta@scoutplay.com"))
            .andExpect(jsonPath("$.data.token").value("jwt-atleta"));
    }

    @Test
    void deveRealizarLoginDeOlheiroComSucesso() throws Exception {
        Olheiro olheiro = new Olheiro();
        olheiro.setId("OLH-456");
        olheiro.setNome("Maria Olheira");
        olheiro.setEmail("olheiro@scoutplay.com");

        when(loginService.autenticar("olheiro@scoutplay.com", "1234")).thenReturn(Optional.of(olheiro));
        when(jwtTokenProvider.generateToken("OLH-456", "OLHEIRO")).thenReturn("jwt-olheiro");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginPayload("olheiro@scoutplay.com", "1234"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login realizado com sucesso"))
            .andExpect(jsonPath("$.data.userType").value("OLHEIRO"))
            .andExpect(jsonPath("$.data.userId").value("OLH-456"))
            .andExpect(jsonPath("$.data.nome").value("Maria Olheira"))
            .andExpect(jsonPath("$.data.email").value("olheiro@scoutplay.com"))
            .andExpect(jsonPath("$.data.token").value("jwt-olheiro"));
    }

    @Test
    void deveRetornarUnauthorizedQuandoCredenciaisForemInvalidas() throws Exception {
        when(loginService.autenticar(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginPayload("invalido@scoutplay.com", "senha123"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
    }

    private record LoginPayload(String email, String senha) {
    }
}