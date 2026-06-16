package com.scoutplay.ScoutPlay.services;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scoutplay.ScoutPlay.exceptions.ConflictException;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.models.XUsuarioTipoConta;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.SeguidorRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.repositories.XUsuarioTipoContaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DetalhePerfilRepository detalhePerfilRepository;

    @Mock
    private XUsuarioTipoContaRepository xUsuarioTipoContaRepository;

    @Mock
    private TipoContaService tipoContaService;

    @Mock
    private DetalhePerfilService detalhePerfilService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SeguidorRepository seguidorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario atletaMock;

    @BeforeEach
    void setUp() {
        atletaMock = new Usuario("Fabiano", "Moreira", "fabiano@atleta.com", "12345678900", "senha123", LocalDate.of(2004, 12, 4));
        atletaMock.setUsername("fabiano_teste");
    }

    // ===== cadastrarAtleta =====

    @Test
    @DisplayName("Deve cadastrar atleta com dados válidos")
    void deveCadastrarAtletaComSucesso() {
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncoded");
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(atletaMock);
        when(detalhePerfilService.adicionarInformacao(anyString(), any(), any(Usuario.class)))
                .thenReturn(new DetalhePerfil());

        Usuario resultado = usuarioService.cadastrarAtleta(atletaMock);

        assertNotNull(resultado);
        verify(usuarioRepository).saveAndFlush(any(Usuario.class));
        verify(tipoContaService).categorizarContaComoAtleta(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar atleta sem senha")
    void deveLancarExcecaoQuandoSenhaNula() {
        Usuario semSenha = new Usuario("Teste", "Silva", "teste@email.com", "99988877700", null, LocalDate.of(2000, 1, 1));
        semSenha.setUsername("teste_usuario");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarAtleta(semSenha));

        assertEquals("Senha é obrigatória", ex.getMessage());
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar atleta com senha em branco")
    void deveLancarExcecaoQuandoSenhaEmBranco() {
        Usuario semSenha = new Usuario("Teste", "Silva", "teste@email.com", "99988877700", "   ", LocalDate.of(2000, 1, 1));
        semSenha.setUsername("teste_usuario");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarAtleta(semSenha));

        assertEquals("Senha é obrigatória", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando CPF já existe")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        when(usuarioRepository.existsByCpf(atletaMock.getCpf())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> usuarioService.cadastrarAtleta(atletaMock));

        assertTrue(ex.getMessage().contains("CPF"));
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando e-mail já está em uso")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(atletaMock.getEmail())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> usuarioService.cadastrarAtleta(atletaMock));

        assertTrue(ex.getMessage().contains("e-mail"));
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve codificar a senha antes de salvar")
    void deveCodificarSenhaAntesDoSalvar() {
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(atletaMock);
        when(detalhePerfilService.adicionarInformacao(anyString(), any(), any(Usuario.class)))
                .thenReturn(new DetalhePerfil());

        usuarioService.cadastrarAtleta(atletaMock);

        verify(passwordEncoder).encode("senha123");
    }

    // ===== buscarPor =====

    @Test
    @DisplayName("Deve retornar usuário ao buscar por aliasId válido")
    void deveRetornarUsuarioQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findByAliasId(id)).thenReturn(Optional.of(atletaMock));

        Usuario resultado = usuarioService.buscarPor(id);

        assertNotNull(resultado);
        assertEquals("fabiano@atleta.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve retornar null quando usuário não encontrado")
    void deveRetornarNullQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findByAliasId(id)).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.buscarPor(id);

        assertNull(resultado);
    }

    // ===== buscarDadosPerfil =====

    @Test
    @DisplayName("Deve buscar dados do perfil por username")
    void deveBuscarDadosDoPerfil() {
        XUsuarioTipoConta relacao = mock(XUsuarioTipoConta.class);
        TipoConta tipoConta = mock(TipoConta.class);
        when(tipoConta.getId()).thenReturn(TipoConta.ATLETA);
        when(relacao.getTipoConta()).thenReturn(tipoConta);

        Page<Post> paginaVazia = new PageImpl<>(Collections.<Post>emptyList());
        when(usuarioRepository.findByUsernameIgnoreCase("fabiano_teste")).thenReturn(atletaMock);
        when(xUsuarioTipoContaRepository.getByUsuario(atletaMock)).thenReturn(Set.of(relacao));
        when(postRepository.findByAutorAndAtivoTrue(any(Usuario.class), any(Pageable.class))).thenReturn(paginaVazia);
        when(seguidorRepository.countBySeguidor(any(Usuario.class))).thenReturn(0L);
        when(seguidorRepository.countBySeguido(any(Usuario.class))).thenReturn(0L);

        var perfil = usuarioService.buscarDadosPerfil("fabiano_teste", UUID.randomUUID());

        assertNotNull(perfil);
        assertEquals("Fabiano", perfil.getNome());
        assertEquals("fabiano_teste", perfil.getUsername());
    }

    // ===== adicionarInformacao =====

    @Test
    @DisplayName("Deve delegar adição de informação ao DetalhePerfilService")
    void deveDelegarAdicaoDeInformacao() {
        DetalhePerfil detalhe = new DetalhePerfil();
        when(detalhePerfilService.adicionarInformacao("POSICAO", "Atacante", atletaMock)).thenReturn(detalhe);

        DetalhePerfil resultado = usuarioService.adicionarInformacao("POSICAO", "Atacante", atletaMock);

        assertNotNull(resultado);
        verify(detalhePerfilService).adicionarInformacao("POSICAO", "Atacante", atletaMock);
    }

    // ===== removerInformacao =====

    @Test
    @DisplayName("Deve delegar remoção de informação ao DetalhePerfilService")
    void deveDelegarRemocaoDeInformacao() {
        DetalhePerfil detalhe = new DetalhePerfil();
        when(detalhePerfilService.removerInformacao("POSICAO", atletaMock)).thenReturn(detalhe);

        DetalhePerfil resultado = usuarioService.removerInformacao("POSICAO", atletaMock);

        assertNotNull(resultado);
        verify(detalhePerfilService).removerInformacao("POSICAO", atletaMock);
    }
}
