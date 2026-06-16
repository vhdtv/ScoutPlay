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

    private static final String MEDIA_DIR  = "uploads/media/";
    private static final String AVATAR_DIR = "uploads/avatars/";

    // Imagens de posts — ação de futebol (Pexels)
    private static final String[][] POSTS_FOTOS = {
        {"seed_post_0.jpeg", "https://images.pexels.com/photos/159594/soccer-football-player-sport-159594.jpeg"},
        {"seed_post_1.jpeg", "https://images.pexels.com/photos/5246965/pexels-photo-5246965.jpeg"},
        {"seed_post_2.jpeg", "https://images.pexels.com/photos/7187827/pexels-photo-7187827.jpeg"},
        {"seed_post_3.jpeg", "https://images.pexels.com/photos/7187841/pexels-photo-7187841.jpeg"},
        {"seed_post_4.jpeg", "https://images.pexels.com/photos/6507967/pexels-photo-6507967.jpeg"},
        {"seed_post_5.jpeg", "https://images.pexels.com/photos/46798/the-ball-stadion-football-the-pitch-46798.jpeg"},
        {"seed_post_6.jpeg", "https://images.pexels.com/photos/3361471/pexels-photo-3361471.jpeg"},
        {"seed_post_7.jpeg", "https://images.pexels.com/photos/6658150/pexels-photo-6658150.jpeg"},
        {"seed_post_8.jpeg", "https://images.pexels.com/photos/6409107/pexels-photo-6409107.jpeg"},
        {"seed_post_9.jpeg", "https://images.pexels.com/photos/12659345/pexels-photo-12659345.jpeg"},
    };

    // Fotos de perfil — atletas
    private static final String[][] ATLETA_AVATARS = {
        {"avatar_atleta_0.jpeg", "https://images.pexels.com/photos/1681010/pexels-photo-1681010.jpeg"},
        {"avatar_atleta_1.jpeg", "https://images.pexels.com/photos/1222271/pexels-photo-1222271.jpeg"},
        {"avatar_atleta_2.jpeg", "https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg"},
        {"avatar_atleta_3.jpeg", "https://images.pexels.com/photos/1484794/pexels-photo-1484794.jpeg"},
        {"avatar_atleta_4.jpeg", "https://images.pexels.com/photos/1239288/pexels-photo-1239288.jpeg"},
    };

    // Fotos de perfil — olheiros
    private static final String[][] OLHEIRO_AVATARS = {
        {"avatar_olheiro_0.jpeg", "https://images.pexels.com/photos/2182970/pexels-photo-2182970.jpeg"},
        {"avatar_olheiro_1.jpeg", "https://images.pexels.com/photos/3779760/pexels-photo-3779760.jpeg"},
        {"avatar_olheiro_2.jpeg", "https://images.pexels.com/photos/1310522/pexels-photo-1310522.jpeg"},
        {"avatar_olheiro_3.jpeg", "https://images.pexels.com/photos/1036623/pexels-photo-1036623.jpeg"},
        {"avatar_olheiro_4.jpeg", "https://images.pexels.com/photos/3785079/pexels-photo-3785079.jpeg"},
    };

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (usuarioRepository.findByUsernameIgnoreCase("lucas_striker") != null) {
            log.info("DataSeeder: dados já existem, pulando.");
            return;
        }
        log.info("DataSeeder: criando dados de demonstração...");

        Files.createDirectories(Paths.get(MEDIA_DIR));
        Files.createDirectories(Paths.get(AVATAR_DIR));

        // ── Baixar imagens ──────────────────────────────────────────────────────
        String[] postFotos     = baixarLote(POSTS_FOTOS, MEDIA_DIR);
        String[] atletaAvatars = baixarLote(ATLETA_AVATARS, AVATAR_DIR);
        String[] olheiroAvatars = baixarLote(OLHEIRO_AVATARS, AVATAR_DIR);

        // ── 5 Atletas ───────────────────────────────────────────────────────────
        Usuario lucas   = criarAtleta("Lucas",   "Almeida",  "lucas@scoutplay.com",   "111.111.111-11", LocalDate.of(2005, 3, 15),  "11999990001");
        Usuario rafael  = criarAtleta("Rafael",  "Costa",    "rafael@scoutplay.com",  "222.222.222-22", LocalDate.of(2003, 7, 22),  "21988880002");
        Usuario diego   = criarAtleta("Diego",   "Ferreira", "diego@scoutplay.com",   "333.333.333-33", LocalDate.of(2001, 11, 8),  "51977770003");
        Usuario mateus  = criarAtleta("Mateus",  "Silva",    "mateus@scoutplay.com",  "444.444.444-44", LocalDate.of(2004, 1, 30),  "31966660004");
        Usuario vinicius = criarAtleta("Vinicius","Nunes",   "vinicius@scoutplay.com","555.555.555-55", LocalDate.of(2006, 8, 12),  "71955550005");

        setUsername(lucas,    "lucas_striker");
        setUsername(rafael,   "rafael_lateral");
        setUsername(diego,    "diego_keeper");
        setUsername(mateus,   "mateus_meia");
        setUsername(vinicius, "vinicius_ponta");

        setAvatar(lucas,    atletaAvatars[0]);
        setAvatar(rafael,   atletaAvatars[1]);
        setAvatar(diego,    atletaAvatars[2]);
        setAvatar(mateus,   atletaAvatars[3]);
        setAvatar(vinicius, atletaAvatars[4]);

        usuarioRepository.saveAllAndFlush(List.of(lucas, rafael, diego, mateus, vinicius));

        perfilAtleta(lucas,    "Centroavante",         "DIREITO",   "1.82", "78",  "Flamengo Sub-17, Vasco Sub-20",      "01001-000");
        perfilAtleta(rafael,   "Lateral Direito",      "DIREITO",   "1.76", "72",  "Vasco da Gama Sub-20",               "20040-020");
        perfilAtleta(diego,    "Goleiro",              "DIREITO",   "1.90", "85",  "Botafogo Sub-20, Carioca FC",        "90010-000");
        perfilAtleta(mateus,   "Meio-campista Central","ESQUERDO",  "1.78", "74",  "América MG Sub-17, Guarani",         "30130-010");
        perfilAtleta(vinicius, "Ponta Esquerda",       "ESQUERDO",  "1.73", "68",  "Bahia Sub-17",                       "40020-010");

        // ── 5 Olheiros ──────────────────────────────────────────────────────────
        Usuario paulo    = criarOlheiro("Paulo",    "Mendes",    "paulo@scoutplay.com",    "666.666.666-66", LocalDate.of(1985, 6, 10),  "21977770006");
        Usuario ana      = criarOlheiro("Ana",      "Rodrigues", "ana@scoutplay.com",      "777.777.777-77", LocalDate.of(1990, 9, 5),   "13010-010");
        Usuario carlos   = criarOlheiro("Carlos",   "Souza",     "carlos@scoutplay.com",   "888.888.888-88", LocalDate.of(1982, 3, 20),  "90050-170");
        Usuario fernanda = criarOlheiro("Fernanda", "Lima",      "fernanda@scoutplay.com", "999.999.999-99", LocalDate.of(1993, 12, 1),  "01310-100");
        Usuario roberto  = criarOlheiro("Roberto",  "Matos",     "roberto@scoutplay.com",  "123.456.789-00", LocalDate.of(1980, 5, 17),  "05425-070");

        setUsername(paulo,    "paulo_olheiro");
        setUsername(ana,      "ana_olheira");
        setUsername(carlos,   "carlos_gremio");
        setUsername(fernanda, "fernanda_spfc");
        setUsername(roberto,  "roberto_palestra");

        setAvatar(paulo,    olheiroAvatars[0]);
        setAvatar(ana,      olheiroAvatars[1]);
        setAvatar(carlos,   olheiroAvatars[2]);
        setAvatar(fernanda, olheiroAvatars[3]);
        setAvatar(roberto,  olheiroAvatars[4]);

        usuarioRepository.saveAllAndFlush(List.of(paulo, ana, carlos, fernanda, roberto));

        perfilOlheiro(paulo,    "Flamengo",   "Rio de Janeiro - RJ", "15 anos de experiência em scouting. Especialista em atacantes e meias ofensivos.");
        perfilOlheiro(ana,      "Santos FC",  "Litoral Paulista - SP", "Ex-jogadora profissional, hoje formando a base do Santos. Foco em revelações sub-20.");
        perfilOlheiro(carlos,   "Grêmio",     "Porto Alegre - RS", "Olheiro do Grêmio há 10 anos. Especialista em jogadores de meio e defesa.");
        perfilOlheiro(fernanda, "São Paulo FC","São Paulo - SP", "Descobri vários talentos que hoje jogam no exterior. Visão apurada para o jogo coletivo.");
        perfilOlheiro(roberto,  "Palmeiras",  "São Paulo - SP", "Trabalho na base do Palmeiras buscando os próximos grandes nomes do futebol brasileiro.");

        // ── 1 Post por atleta ───────────────────────────────────────────────────
        TipoMidia img = tipoMidiaService.categorizarComoImagem();

        Post p1 = criarPost(lucas,    "Treino de finalização ⚽",
            "Cada gol é um passo mais perto do sonho! Finalização trabalhada hoje com o professor. Foco total no campeonato estadual sub-20. #Centroavante #Futebol",
            postFotos[0], img);

        Post p2 = criarPost(rafael,   "Cruzamentos no treino 🎯",
            "Lateral moderno precisa saber cruzar e chegar ao ataque. Trabalhando os cruzamentos e a sobreposição. Evolução constante! #LateralDireito #Futebol",
            postFotos[1], img);

        Post p3 = criarPost(diego,    "Treinamento de reflexos 🧤",
            "Nada passa por mim. Sessão intensa de trabalho com bolas rebatidas, cruzamentos e chutes de longa distância. Goleiro raçudo! #Goleiro #DefesaEpica",
            postFotos[2], img);

        Post p4 = criarPost(mateus,   "Ditando o ritmo do jogo 💪",
            "Futebol é leitura. A antecipação faz toda a diferença no meio-campo. Treino de posse e transição hoje. Sentindo evolução a cada sessão! #MeioCampo",
            postFotos[3], img);

        Post p5 = criarPost(vinicius, "Velocidade e drible na ponta 🔥",
            "Ponta rápido que o adversário não consegue marcar. Trabalhei hoje os dribles em velocidade e a finalização depois do 1x1. Sonho grande! #PontaEsquerda",
            postFotos[4], img);

        // ── 1 Post por olheiro ──────────────────────────────────────────────────
        Post p6 = criarPost(paulo,    "Dia de observação no sub-20 👀",
            "Mais um dia de trabalho percorrendo o Brasil em busca de novos talentos. Muito potencial nos garotos que vi hoje. O futuro do futebol brasileiro está vivo! #Scouting #Flamengo",
            postFotos[5], img);

        Post p7 = criarPost(ana,      "Revelando talentos pelo litoral 🌊⚽",
            "Ex-atleta, agora minha missão é abrir portas para quem tem talento mas falta oportunidade. Sessão de avaliação intensa hoje. Santos FC de olhos bem abertos! #ScoutingFeminino",
            postFotos[6], img);

        Post p8 = criarPost(carlos,   "Gaúcho na caça aos craques 🦁",
            "A grama do sul é berço de grandes jogadores. Acompanhando o campeonato gaúcho sub-17 hoje. Grêmio sempre de olho nos melhores! #Gremio #Scouting",
            postFotos[7], img);

        Post p9 = criarPost(fernanda, "Inteligência tática em campo 🧠",
            "Não basta ter velocidade e técnica. Busco jogadores com leitura de jogo acima da média. São Paulo FC precisa de jogadores que pensem o futebol. #SPFC #Scouting",
            postFotos[8], img);

        Post p10 = criarPost(roberto, "Palmeiras na base, campeão no futuro 🏆",
            "A base forte garante o futuro. Acompanhando promessas do interior de SP hoje. Palmeiras investe pesado em revelar os próximos ídolos! #Palmeiras #Revelacao",
            postFotos[9], img);

        // ── Curtidas (scouts curtem posts de atletas + atletas entre si) ────────
        darLike(paulo,    p1); darLike(ana,      p1); darLike(carlos,   p1);
        darLike(fernanda, p1); darLike(roberto,  p1); darLike(rafael,   p1); darLike(mateus, p1);

        darLike(paulo,    p2); darLike(ana,      p2); darLike(fernanda, p2);
        darLike(roberto,  p2); darLike(lucas,    p2); darLike(diego,    p2);

        darLike(carlos,   p3); darLike(paulo,    p3); darLike(ana,      p3);
        darLike(roberto,  p3); darLike(lucas,    p3); darLike(vinicius, p3);

        darLike(fernanda, p4); darLike(paulo,    p4); darLike(carlos,   p4);
        darLike(ana,      p4); darLike(rafael,   p4); darLike(diego,    p4);

        darLike(roberto,  p5); darLike(fernanda, p5); darLike(paulo,    p5);
        darLike(ana,      p5); darLike(lucas,    p5); darLike(mateus,   p5);

        // Atletas curtem posts dos scouts
        darLike(lucas,   p6); darLike(rafael,  p6); darLike(diego,    p6); darLike(mateus, p6);
        darLike(vinicius,p7); darLike(lucas,   p7); darLike(rafael,   p7);
        darLike(diego,   p8); darLike(mateus,  p8); darLike(vinicius, p8); darLike(lucas,  p8);
        darLike(rafael,  p9); darLike(diego,   p9); darLike(mateus,   p9);
        darLike(vinicius,p10);darLike(lucas,   p10);darLike(rafael,   p10);darLike(mateus, p10);

        // ── Comentários (scouts expressando interesse + atletas interagindo) ────
        comentar(paulo,    p1, "Que finalização precisa! Esse centroavante tem tudo para jogar no profissional. Já estou de olho, Lucas! 👏");
        comentar(ana,      p1, "Impressionante a velocidade de reação e posicionamento. Ótimo candidato para a base do Santos.");
        comentar(carlos,   p1, "Técnica apurada e frieza na hora de finalizar. Perfil que o Grêmio está procurando!");
        comentar(rafael,   p1, "Irmão, você é foda! Continua assim que o gol vai sair sempre 🔥");
        comentar(mateus,   p1, "Quando a bola sobra pra você já é gol garantido! 💪");

        comentar(paulo,    p2, "Lateral com qualidade ofensiva e boa leitura de jogo. Rafael, meu contato fica aqui caso queira conversar.");
        comentar(fernanda, p2, "Cruzamentos precisos e capacidade de chegar ao ataque. Perfil moderno! São Paulo acompanha de perto.");
        comentar(lucas,    p2, "Irmão, seu cruzamento me deixou na cara do gol hoje! Parceria que funciona 🎯");
        comentar(diego,    p2, "Quando você cruza eu já sei que vai dar gol, continua assim!");

        comentar(carlos,   p3, "Presença no gol incrível. Diego tem futuro garantido no futebol gaúcho — e quem sabe além. Grêmio de olho! 👀");
        comentar(paulo,    p3, "Defesa impressionante! Goleiro difícil de passar. Anote o nome: Diego Ferreira.");
        comentar(ana,      p3, "Esse goleiro tem um potencial enorme. Contato feito, Diego!");
        comentar(lucas,    p3, "Cara... não consigo marcar contra você nem em treino 😅 Boa demais!");
        comentar(vinicius, p3, "Quebrou o meu drible aí e ainda saiu jogando limpo. Monstro! 🧤");

        comentar(fernanda, p4, "Visão de jogo e técnica de alto nível. Mateus é exatamente o tipo de meia que o São Paulo busca para a base.");
        comentar(paulo,    p4, "Jogador completo — distribui, pressiona e finaliza. Esse meia é especial.");
        comentar(carlos,   p4, "Inteligência tática acima da média para a idade. Impressionante!");
        comentar(diego,    p4, "Minha defesa agradece quando você rouba a bola no meio antes de chegarem a mim 🤝");
        comentar(rafael,   p4, "Ele me pede a bola e já sei que vai virar jogada 🔥 Parceria top!");

        comentar(roberto,  p5, "Velocidade e drible são armas que poucos têm. Vinicius, o Palmeiras quer saber mais sobre você. 🌴");
        comentar(fernanda, p5, "Jovem com muita qualidade! Futuro promissor, continuando assim vai longe.");
        comentar(paulo,    p5, "Que ponta explosivo! Difícil de marcar e ainda finaliza bem. Perfeito para o futebol moderno.");
        comentar(lucas,    p5, "Parceiro, sua velocidade é absurda! Fico feliz de treinar contigo 🔥");
        comentar(mateus,   p5, "Quando você arranca pela esquerda já sei que é assistência certa pra mim 😂");

        // Atletas comentando nos posts dos scouts
        comentar(lucas,   p6, "Obrigado pelo trabalho que voces fazem, Paulo! E uma motivacao enorme saber que olheiros assim acompanham os jogadores.");
        comentar(rafael,  p6, "Que honra! Foco total para mostrar meu melhor quando tiver essa oportunidade.");
        comentar(diego,   p6, "Goleiros tambem estao atentos, Paulo! Conte comigo quando precisar.");
        comentar(mateus,  p6, "Motivacao extra para treinar ainda mais. Obrigado, Paulo!");

        comentar(vinicius,p7, "Ana, seu trabalho e inspirador! Fico ainda mais motivado sabendo que profissionais como voce acompanham os atletas.");
        comentar(lucas,   p7, "Ex-atleta que virou olheira e o melhor tipo de avaliadora. Obrigado, Ana!");
        comentar(rafael,  p7, "Que orgulho ter alguem com essa experiencia de olho nos jogadores!");

        comentar(diego,   p8, "Gremio e uma escola, Carlos. Seria uma honra fazer parte disso!");
        comentar(mateus,  p8, "O futebol do sul tem muito talento mesmo. Otimo trabalho, Carlos!");
        comentar(vinicius,p8, "Mesmo sendo baiano, quero mostrar meu futebol por ai tambem!");
        comentar(lucas,   p8, "Gremio formou muitos idolos. Seria incrivel fazer parte dessa historia!");

        comentar(rafael,  p9, "Fernanda, leitura de jogo e o que mais trabalho nos treinos. Espero estar no radar do Sao Paulo!");
        comentar(diego,   p9, "Goleiro inteligente e diferencial. Leio o jogo antes do chute vir. Conte comigo, Fernanda.");
        comentar(mateus,  p9, "Inteligencia tatica e meu forte. Sao Paulo e eu temos tudo a ver.");

        comentar(vinicius,p10,"Roberto, sou exatamente o que o Palmeiras precisa na ponta! Velocidade, drible e gol.");
        comentar(lucas,   p10,"Base forte e tudo! Aprendi isso desde cedo. Palmeiras fez historia revelando craques.");
        comentar(rafael,  p10,"Lateral jovem aqui, Roberto! Pode colocar meu nome na lista.");
        comentar(mateus,  p10,"Interior de SP tem muito talento sim! Guarani me revelou e hoje estou crescendo cada vez mais.");

        log.info("DataSeeder: concluido. 5 atletas, 5 olheiros, 10 posts, curtidas e comentarios criados.");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    @Transactional
    private Usuario criarAtleta(String nome, String sobrenome, String email, String cpf, LocalDate nasc, String tel) {
        Usuario u = new Usuario(nome, sobrenome, email, cpf, "Senha@123", nasc);
        u.setTelefone(tel);
        return usuarioService.cadastrarAtleta(u);
    }

    @Transactional
    private Usuario criarOlheiro(String nome, String sobrenome, String email, String cpf, LocalDate nasc, String tel) {
        Usuario u = new Usuario(nome, sobrenome, email, cpf, "Senha@123", nasc);
        u.setTelefone(tel);
        return usuarioService.cadastrarOlheiro(u);
    }

    private void setUsername(Usuario u, String username) { u.setUsername(username); }
    private void setAvatar(Usuario u, String filename)   { u.setFotoPerfil(filename); }

    @Transactional
    private void perfilAtleta(Usuario u, String posicao, String pe, String altura, String peso, String clubes, String cep) {
        Map<String, Object> info = new HashMap<>();
        info.put("posicao",          posicao);
        info.put("peDominante",      pe);
        info.put("PE_DOMINANTE",     pe);
        info.put("altura",           altura);
        info.put("peso",             peso);
        info.put("clubesAnteriores", clubes);
        info.put("cep",              cep);
        usuarioService.adicionarInformacao(info, u);
    }

    @Transactional
    private void perfilOlheiro(Usuario u, String clube, String regiao, String bio) {
        Map<String, Object> info = new HashMap<>();
        info.put("empresa", clube);
        info.put("regiao",  regiao);
        info.put("bio",     bio);
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

    private String[] baixarLote(String[][] fotos, String dir) {
        String[] nomes = new String[fotos.length];
        for (int i = 0; i < fotos.length; i++) {
            nomes[i] = baixarImagem(fotos[i][1], fotos[i][0], dir);
        }
        return nomes;
    }

    private String baixarImagem(String url, String nomeArquivo, String dir) {
        Path destino = Paths.get(dir + nomeArquivo);
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
            log.info("DataSeeder: {} baixada.", nomeArquivo);
        } catch (Exception e) {
            log.warn("DataSeeder: falha ao baixar {}: {}", nomeArquivo, e.getMessage());
        }
        return nomeArquivo;
    }
}
