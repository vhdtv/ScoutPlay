package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Atleta;
import com.scoutplay.ScoutPlay.repositorys.AtletaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtletaServiceTest {

    @Mock
    private AtletaRepository atletaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AtletaService atletaService;

    private Atleta atleta;

    @BeforeEach
    void setUp() {
        atleta = new Atleta();
        atleta.setId("ATL-001");
        atleta.setNome("João Silva");
        atleta.setEmail("joao@email.com");
        atleta.setCpf("123.456.789-00");
        atleta.setSenha("senha123");
    }

    @Test
    void deveCriarAtletaComSucesso() {
        when(atletaRepository.existsByCpf(anyString())).thenReturn(false);
        when(atletaRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash-senha123");
        when(atletaRepository.save(any(Atleta.class))).thenReturn(atleta);

        Atleta resultado = atletaService.criarAtleta(atleta);

        assertNotNull(resultado);
        assertEquals("ATL-001", resultado.getId());
        verify(atletaRepository).save(any(Atleta.class));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNula() {
        atleta.setSenha(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> atletaService.criarAtleta(atleta));

        assertEquals("Senha é obrigatória para cadastro.", ex.getMessage());
        verify(atletaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForVazia() {
        atleta.setSenha("  ");

        assertThrows(IllegalArgumentException.class,
                () -> atletaService.criarAtleta(atleta));
        verify(atletaRepository, never()).save(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoCpfJaExiste() {
        when(atletaRepository.existsByCpf(atleta.getCpf())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> atletaService.criarAtleta(atleta));

        assertTrue(ex.getMessage().contains("CPF"));
        verify(atletaRepository, never()).save(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoEmailJaExiste() {
        when(atletaRepository.existsByCpf(anyString())).thenReturn(false);
        when(atletaRepository.existsByEmail(atleta.getEmail())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> atletaService.criarAtleta(atleta));

        assertTrue(ex.getMessage().contains("e-mail"));
        verify(atletaRepository, never()).save(any());
    }

    @Test
    void deveBuscarAtletaPorIdExistente() {
        when(atletaRepository.findById("ATL-001")).thenReturn(Optional.of(atleta));

        Optional<Atleta> resultado = atletaService.buscarAtletaPorId("ATL-001");

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
    }

    @Test
    void deveRetornarVazioParaIdInexistente() {
        when(atletaRepository.findById("NAO-EXISTE")).thenReturn(Optional.empty());

        Optional<Atleta> resultado = atletaService.buscarAtletaPorId("NAO-EXISTE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveDeletarAtletaExistente() {
        when(atletaRepository.existsById("ATL-001")).thenReturn(true);
        doNothing().when(atletaRepository).deleteById("ATL-001");

        assertDoesNotThrow(() -> atletaService.deletarAtletaPorId("ATL-001"));
        verify(atletaRepository).deleteById("ATL-001");
    }

    @Test
    void deveLancarExcecaoAoDeletarAtletaInexistente() {
        when(atletaRepository.existsById("NAO-EXISTE")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> atletaService.deletarAtletaPorId("NAO-EXISTE"));
        verify(atletaRepository, never()).deleteById(anyString());
    }

    @Test
    void deveAtualizarAtletaSemAlterarSenhaQuandoSenhaForNula() {
        Atleta atletaAtualizado = new Atleta();
        atletaAtualizado.setNome("João Atualizado");
        atletaAtualizado.setSenha(null);

        when(atletaRepository.findById("ATL-001")).thenReturn(Optional.of(atleta));
        when(atletaRepository.save(any(Atleta.class))).thenAnswer(inv -> inv.getArgument(0));

        Atleta resultado = atletaService.atualizarAtleta("ATL-001", atletaAtualizado);

        assertEquals("João Atualizado", resultado.getNome());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveAtualizarSenhaQuandoNovaSenhaForFornecida() {
        Atleta atletaAtualizado = new Atleta();
        atletaAtualizado.setNome("João Atualizado");
        atletaAtualizado.setSenha("novaSenha456");

        when(atletaRepository.findById("ATL-001")).thenReturn(Optional.of(atleta));
        when(passwordEncoder.encode("novaSenha456")).thenReturn("hash-novaSenha456");
        when(atletaRepository.save(any(Atleta.class))).thenAnswer(inv -> inv.getArgument(0));

        Atleta resultado = atletaService.atualizarAtleta("ATL-001", atletaAtualizado);

        verify(passwordEncoder).encode("novaSenha456");
        assertEquals("hash-novaSenha456", resultado.getSenha());
    }
}
