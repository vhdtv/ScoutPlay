package com.scoutplay.ScoutPlay.config;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.scoutplay.ScoutPlay.services.TipoContaService;
import com.scoutplay.ScoutPlay.services.TipoDetalhePerfilService;
import com.scoutplay.ScoutPlay.services.DetalhePerfilService;
import com.scoutplay.ScoutPlay.services.PostService;
import com.scoutplay.ScoutPlay.services.TipoMidiaService;
import com.scoutplay.ScoutPlay.services.UsuarioService;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.Usuario;


@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(UsuarioService usuarioService, TipoContaService tipoContaService, TipoMidiaService tipoMidiaService, TipoDetalhePerfilService tipoDetalhePerfil, DetalhePerfilService detalhePerfilService, PostService postService) {
        return args -> {
            tipoContaService.injetarValores();
            tipoMidiaService.injetarValores();
            tipoDetalhePerfil.injetarValores();
            
            try {
                // Ações de Atleta
                Usuario atletaFabio = new Usuario("Fabio", "Braga", "fabio@atleta.com", "12345678910", "12345", LocalDate.of(2006, 12, 4));
                atletaFabio.setUsername("fabin_123");
                atletaFabio.setAliasId(UUID.fromString("296e861f-6697-4f74-99a1-f016e6b3de1e"));
                usuarioService.cadastrarAtleta(atletaFabio);
                Usuario atletaRoberto = new Usuario("Roberto", "Mendes", "roberto@atleta.com", "12345678911", "asd", LocalDate.of(2006, 12, 4));
                atletaRoberto.setUsername("__robertoo");
                atletaRoberto.setAliasId(UUID.fromString("ebc1c3ad-6924-4c0e-b63c-c787ce7f1bda"));
                usuarioService.cadastrarAtleta(atletaRoberto);
                usuarioService.seguir(atletaRoberto.getUsername(), atletaFabio.getAliasId());
                Map<String, Object> info = new HashMap<>();
                info.put("PE_DOMINANTE", "Direito");
                info.put("PE_DOMINANTEE", "Direito");
                detalhePerfilService.adicionarInformacao(info, atletaFabio);
                detalhePerfilService.adicionarInformacao("CLUBE_QUE_PARTICIPOU", "CRA", atletaFabio);
                detalhePerfilService.removerInformacao("PE_DOMINANTEE", atletaFabio);
                
                // Ações de Responsavel
                Usuario responsavelArthur = new Usuario("Arthur", "Braga", "arthur@responsavel.com", "124567", "12345", LocalDate.of(1980, 6, 26));
                responsavelArthur.setUsername("o_arthur_olha");
                responsavelArthur.setAliasId(UUID.fromString("c0b5889c-dc37-47e6-85fd-7ac3f89c7df7"));;
                usuarioService.cadastrarResponsavel(responsavelArthur);
                usuarioService.vincularResponsavel(atletaFabio.getUsername(), responsavelArthur.getAliasId());
                usuarioService.vincularResponsavel(responsavelArthur.getUsername(), atletaFabio.getAliasId());
                
                // Ações de Olheiro
                Usuario olheiroRamilson = new Usuario("Ramilson", "Neto", "ramilson@olheiro.com", "1245678", "12345", LocalDate.of(1978, 07, 8));
                usuarioService.cadastrarOlheiro(olheiroRamilson);

                // Criação de Post
                Post post1 = new Post("Titulo", "descricao", "3560017-uhd_3840_2160_25fps.mp4", tipoMidiaService.categorizarComoVideo(), atletaRoberto);
                post1.setAliasId(UUID.fromString("ec51b202-c0f0-43ec-9f39-0cc86d48d6ee"));
                postService.criar(post1);
            }
            catch (Throwable t) {
                System.err.println("Erro fatal: " + t.getClass().getName());
                t.printStackTrace();
            }
            
            System.out.println("Banco de dados sincronizado");
        };
    }
}