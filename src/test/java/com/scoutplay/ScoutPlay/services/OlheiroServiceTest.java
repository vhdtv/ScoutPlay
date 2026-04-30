package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.exceptions.ResourceNotFoundException;
import com.scoutplay.ScoutPlay.models.Olheiro;
import com.scoutplay.ScoutPlay.repositorys.OlheiroRepository;
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
class OlheiroServiceTest {

    @Mock
    private OlheiroRepository olheiroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OlheiroService olheiroService;

    private Olheiro olheiro;

    @BeforeEach
    void setUp() {
        olheiro = new Olheiro();
        olheiro.setId("OLH-001");
        olheiro.setNome("Maria Scout");
        olheiro.setEmail("maria@email.com");
        olheiro.setCpf("987.654.321-00");
        olheiro.setSenha("senha456");
        olheiro.setClube("Clube Exemplo");
        olheiro.setLocal("São Paulo");
    }

    @Test
    void deveCriarOlheiroComSucesso() {
        when(olheiroRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(olheiroRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash-senha456");
        when(olheiroRepository.save(any(Olheiro.class))).thenReturn(olheiro);

        Olheiro resultado = olheiroService.criarOlheiro(olheiro);

        assertNotNull(resultado);
        assertEquals("OLH-001", resultado.getId());
        verify(olheiroRepository).save(any(Olheiro.class));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNula() {
        olheiro.setSenha(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> olheiroService.criarOlheiro(olheiro));

        assertEquals("Senha é obrigatória para cadastro.", ex.getMessage());
        verify(olheiroRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForVazia() {
        olheiro.setSenha("   ");

        assertThrows(IllegalArgumentException.class,
                () -> olheiroService.criarOlheiro(olheiro));
        verify(olheiroRepository, never()).save(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoCpfJaExiste() {
        when(olheiroRepository.findByCpf(olheiro.getCpf())).thenReturn(Optional.of(new Olheiro()));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> olheiroService.criarOlheiro(olheiro));

        assertTrue(ex.getMessage().contains("CPF"));
        verify(olheiroRepository, never()).save(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoEmailJaExiste() {
        when(olheiroRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(olheiroRepository.findByEmail(olheiro.getEmail())).thenReturn(Optional.of(new Olheiro()));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> olheiroService.criarOlheiro(olheiro));

        assertTrue(ex.getMessage().contains("e-mail"));
        verify(olheiroRepository, never()).save(any());
    }

    @Test
    void deveDeletarOlheiroExistente() {
        when(olheiroRepository.existsById("OLH-001")).thenReturn(true);
        doNothing().when(olheiroRepository).deleteById("OLH-001");

        assertDoesNotThrow(() -> olheiroService.deletarOlheiroPorId("OLH-001"));
        verify(olheiroRepository).deleteById("OLH-001");
    }

    @Test
    void deveLancarExcecaoAoDeletarOlheiroInexistente() {
        when(olheiroRepository.existsById("NAO-EXISTE")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> olheiroService.deletarOlheiroPorId("NAO-EXISTE"));
        verify(olheiroRepository, never()).deleteById(anyString());
    }

    @Test
    void deveAtualizarOlheiroSemAlterarSenhaQuandoNula() {
        Olheiro atualizado = new Olheiro();
        atualizado.setNome("Maria Atualizada");
        atualizado.setSenha(null);

        when(olheiroRepository.findById("OLH-001")).thenReturn(Optional.of(olheiro));
        when(olheiroRepository.save(any(Olheiro.class))).thenAnswer(inv -> inv.getArgument(0));

        Olheiro resultado = olheiroService.atualizarOlheiro("OLH-001", atualizado);

        assertEquals("Maria Atualizada", resultado.getNome());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
