package com.scoutplay.ScoutPlay.config;

import com.scoutplay.ScoutPlay.models.Comentario;
import com.scoutplay.ScoutPlay.models.Post;
import com.scoutplay.ScoutPlay.models.TipoMidia;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.ComentarioRepository;
import com.scoutplay.ScoutPlay.repositories.PostRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import com.scoutplay.ScoutPlay.services.TipoMidiaService;
import com.scoutplay.ScoutPlay.services.UsuarioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PostRepository postRepository;
    private final ComentarioRepository comentarioRepository;
    private final TipoMidiaService tipoMidiaService;

    private static final String UPLOAD_DIR = "uploads/media/";

    // URLs diretas do Pexels — imagens reais de futebol, sem redirecionamento
    private static final String[][] FOTOS = {
        {"seed_0.jpeg", "https://images.pexels.com/photos/159594/soccer-football-player-sport-159594.jpeg"},
        {"seed_1.jpeg", "https://images.pexels.com/photos/5246965/pexels-photo-5246965.jpeg"},
        {"seed_2.jpeg", "https://images.pexels.com/photos/7187827/pexels-photo-7187827.jpeg"},
        {"seed_3.jpeg", "https://images.pexels.com/photos/7187841/pexels-photo-7187841.jpeg"},
        {"seed_4.jpeg", "https://images.pexels.com/photos/6507967/pexels-photo-6507967.jpeg"},
        {"seed_5.jpeg", "https://images.pexels.com/photos/12659345/pexels-photo-12659345.jpeg"},
        {"seed_6.jpeg", "https://images.pexels.com/photos/3361471/pexels-photo-3361471.jpeg"},
        {"seed_7.jpeg", "https://images.pexels.com/photos/46798/the-ball-stadion-football-the-pitch-46798.jpeg"},
        {"seed_8.jpeg", "https://images.pexels.com/photos/6658150/pexels-photo-6658150.jpeg"},
        {"seed_9.jpeg", "https://images.pexels.com/photos/6409107/pexels-photo-6409107.jpeg"},
    };

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (usuarioRepository.findByUsernameIgnoreCase("lucas_striker") != null) {
            log.info("DataSeeder: dados já existem, pulando.");
            return;
        }
        log.info("DataSeeder: criando dados de demonstração...");

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        // ── Baixar imagens ──────────────────────────────────────────────────────
        String[] arquivos = new String[FOTOS.length];
        for (int i = 0; i < FOTOS.length; i++) {
            arquivos[i] = baixarImagem(FOTOS[i][1], FOTOS[i][0]);
        }

        // ── Criar atletas ───────────────────────────────────────────────────────
        Usuario lucas = criarAtleta("Lucas", "Almeida",  "lucas@scoutplay.com",  "111.111.111-11", LocalDate.of(2005, 3, 15));
        Usuario rafael = criarAtleta("Rafael", "Costa",  "rafael@scoutplay.com", "222.222.222-22", LocalDate.of(2003, 7, 22));
        Usuario diego  = criarAtleta("Diego",  "Ferreira","diego@scoutplay.com", "333.333.333-33", LocalDate.of(2001, 11, 8));
        Usuario mateus = criarAtleta("Mateus", "Silva",  "mateus@scoutplay.com", "444.444.444-44", LocalDate.of(2004, 1, 30));

        lucas.setUsername("lucas_striker");
        rafael.setUsername("rafael_lateral");
        diego.setUsername("diego_keeper");
        mateus.setUsername("mateus_meia");
        usuarioRepository.saveAllAndFlush(List.of(lucas, rafael, diego, mateus));

        // Perfis esportivos
        perfil(lucas,  "Centroavante",        "DIREITO",   "1.82", "78",  "Flamengo, Vasco");
        perfil(rafael, "Lateral Direito",      "DIREITO",   "1.76", "72",  "Vasco da Gama");
        perfil(diego,  "Goleiro",              "DIREITO",   "1.90", "85",  "Botafogo");
        perfil(mateus, "Meio-campista Central","ESQUERDO",  "1.78", "74",  "São Paulo, Guarani");

        // ── Criar olheiros ──────────────────────────────────────────────────────
        Usuario paulo = criarOlheiro("Paulo",  "Mendes",    "paulo@scoutplay.com",  "555.555.555-55", LocalDate.of(1985, 6, 10));
        Usuario ana   = criarOlheiro("Ana",    "Rodrigues", "ana@scoutplay.com",    "666.666.666-66", LocalDate.of(1990, 9, 5));

        paulo.setUsername("paulo_olheiro");
        ana.setUsername("ana_olheira");
        usuarioRepository.saveAllAndFlush(List.of(paulo, ana));

        perfilOlheiro(paulo, "Flamengo",  "Rio de Janeiro");
        perfilOlheiro(ana,   "Santos FC", "São Paulo");

        // ── Criar posts ─────────────────────────────────────────────────────────
        TipoMidia img = tipoMidiaService.categorizarComoImagem();

        Post p1 = criarPost(lucas,  "Treino de finalização",        "Cada gol é um passo mais perto do sonho! 🔥",                                   arquivos[0], img);
        Post p2 = criarPost(lucas,  "Pré-jogo pelo sub-20",         "Concentração total antes do clássico. Vamos com tudo!",                          arquivos[1], img);
        Post p3 = criarPost(rafael, "Cruzamentos no treino",        "Trabalhando os cruzamentos com o professor. Evolução constante.",                 arquivos[2], img);
        Post p4 = criarPost(rafael, "Treino físico intenso",        "Pré-temporada puxada mas necessária. Foco no campeonato!",                       arquivos[3], img);
        Post p5 = criarPost(diego,  "Treinamento de reflexos",      "Nada passa por mim. Goleiro raçudo! 🧤",                                         arquivos[4], img);
        Post p6 = criarPost(diego,  "Defesa incrível no treino",    "Aquele moment de pura concentração. Todo dia melhorando.",                       arquivos[5], img);
        Post p7 = criarPost(mateus, "Meia-campo em ação",           "Ditando o ritmo do jogo do meio para frente. 💪",                               arquivos[6], img);
        Post p8 = criarPost(mateus, "Visão de jogo",                "Futebol é leitura. Antecipação faz a diferença.",                                arquivos[7], img);
        Post p9 = criarPost(paulo,  "Observando talentos",          "Mais um dia de trabalho descobrindo craques pelo Brasil. 👀",                    arquivos[8], img);
        Post p10 = criarPost(ana,   "Scouting no campo",            "Analisando jogadores promissores hoje. Muita qualidade aqui! ⚽",               arquivos[9], img);

        // ── Adicionar curtidas ──────────────────────────────────────────────────
        darLike(paulo,  p1); darLike(ana, p1); darLike(rafael, p1); darLike(diego, p1);
        darLike(paulo,  p3); darLike(lucas, p3); darLike(mateus, p3);
        darLike(ana,    p5); darLike(paulo, p5); darLike(lucas, p5);
        darLike(paulo,  p7); darLike(ana, p7); darLike(rafael, p7);
        darLike(lucas,  p9); darLike(rafael, p9);
        darLike(diego,  p10); darLike(mateus, p10);

        // ── Adicionar comentários ───────────────────────────────────────────────
        comentar(paulo,  p1, "Que finalização! Esse menino tem futuro no Flamengo. Já estou de olho! 👏");
        comentar(ana,    p1, "Impressionante a velocidade de reação. Ótimo candidato para o Santos.");
        comentar(rafael, p1, "Parceiro, você é foda! Continua assim 🔥");
        comentar(diego,  p1, "Hahaha, eu que sofro no treino com esses chutes seus...");
        comentar(mateus, p1, "Gol certo quando a bola sobra pra você!");

        comentar(paulo,  p3, "Cruzamentos precisos! Lateral moderno, pode criar muito pela direita.");
        comentar(ana,    p3, "Qualidade técnica excelente. Esse vai longe.");
        comentar(lucas,  p3, "Irmão, esse cruzamento me deixou na cara do gol hoje!");
        comentar(mateus, p3, "Parceria garantida no meio! Passa pra mim que eu mando no gol 😂");

        comentar(paulo,  p5, "Defesa impressionante! Goleiro difícil de passar. Anote o nome: Diego Ferreira.");
        comentar(ana,    p5, "Esse goleiro tem um futuro enorme! Presença no gol incrível.");
        comentar(lucas,  p5, "Cara... não consigo marcar contra você nem em treino 😅");
        comentar(mateus, p5, "A defesa está em boas mãos enquanto Diego tiver lá!");

        comentar(paulo,  p7, "Jogador completo, visão e técnica de alto nível. Esse meia é especial.");
        comentar(ana,    p7, "Tomei nota! Meio-campo inteligente, distribui bem a bola.");
        comentar(diego,  p7, "Minha defesa agradece quando você rouba a bola no meio 🤝");
        comentar(rafael, p7, "Ele me pede a bola e já sei que vira jogada 🔥");

        comentar(lucas,  p9, "Obrigado pelo olhar positivo, Paulo! É uma motivação enorme.");
        comentar(rafael, p9, "Que honra ser avaliado por você, Paulo! Foco total.");
        comentar(diego,  p10, "Ana, qualquer feedback seu é muito bem-vindo! Obrigado.");
        comentar(mateus, p10, "Que trabalho incrível o que vocês fazem pelo futebol brasileiro!");

        log.info("DataSeeder: concluído com sucesso. 4 atletas, 2 olheiros, 10 posts, comentários e curtidas criados.");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    @Transactional
    private Usuario criarAtleta(String nome, String sobrenome, String email, String cpf, LocalDate nascimento) {
        Usuario u = new Usuario(nome, sobrenome, email, cpf, "Senha@123", nascimento);
        return usuarioService.cadastrarAtleta(u);
    }

    @Transactional
    private Usuario criarOlheiro(String nome, String sobrenome, String email, String cpf, LocalDate nascimento) {
        Usuario u = new Usuario(nome, sobrenome, email, cpf, "Senha@123", nascimento);
        return usuarioService.cadastrarOlheiro(u);
    }

    @Transactional
    private void perfil(Usuario u, String posicao, String pe, String altura, String peso, String clubes) {
        Map<String, Object> info = new HashMap<>();
        info.put("posicao", posicao);
        info.put("PE_DOMINANTE", pe);
        info.put("altura", altura);
        info.put("peso", peso);
        info.put("clubesAnteriores", clubes);
        usuarioService.adicionarInformacao(info, u);
    }

    @Transactional
    private void perfilOlheiro(Usuario u, String clube, String regiao) {
        Map<String, Object> info = new HashMap<>();
        info.put("empresa", clube);
        info.put("regiao", regiao);
        usuarioService.adicionarInformacao(info, u);
    }

    @Transactional
    private Post criarPost(Usuario autor, String titulo, String descricao, String arquivo, TipoMidia tipo) {
        Post p = new Post(titulo, descricao, arquivo, tipo, autor);
        return postRepository.saveAndFlush(p);
    }

    @Transactional
    private void comentar(Usuario autor, Post post, String texto) {
        comentarioRepository.saveAndFlush(new Comentario(texto, post, autor));
    }

    @Transactional
    private void darLike(Usuario usuario, Post post) {
        try {
            usuario.curtirPost(post);
            usuarioRepository.saveAndFlush(usuario);
        } catch (Exception ignored) {}
    }

    private String baixarImagem(String url, String nomeArquivo) {
        Path destino = Paths.get(UPLOAD_DIR + nomeArquivo);
        if (Files.exists(destino)) return nomeArquivo;
        try {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .build();
            HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = resp.body(); FileOutputStream out = new FileOutputStream(destino.toFile())) {
                in.transferTo(out);
            }
            log.info("DataSeeder: imagem {} baixada.", nomeArquivo);
        } catch (Exception e) {
            log.warn("DataSeeder: falha ao baixar imagem {}: {}", nomeArquivo, e.getMessage());
        }
        return nomeArquivo;
    }
}
