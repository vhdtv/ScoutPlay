package com.scoutplay.ScoutPlay.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoutPlayFunctionalTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void rotasPrivadasDevemNegarUsuarioAnonimo() throws Exception {
        mvc.perform(get("/api/session"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false));

        mvc.perform(post("/api/ia/prompt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pergunta\":\"liste atletas\"}"))
            .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/post/"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroLoginSessaoEPerfilDevemFuncionar() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "atleta." + suffix + "@scoutplay.test";
        MvcResult cadastro = cadastrar(email, "ATLETA", "2000-01-10", "123" + digits(suffix));
        Cookie cookieAcesso = cadastro.getResponse().getCookie("access_token");

        mvc.perform(get("/api/session").cookie(cookieAcesso))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(true));

        String username = objectMapper.readTree(cadastro.getResponse().getContentAsString())
            .path("data").path("username").asText();

        mvc.perform(get("/api/user").param("user", username).cookie(cookieAcesso))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.username").value(username))
            .andExpect(jsonPath("$.data.tipoConta[0]").value("ATLETA"));

        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("email", email, "senha", "Senha123!")))
            .andExpect(status().isOk())
            .andExpect(cookie().httpOnly("access_token", true));
    }

    @Test
    void somenteOlheiroPodeAvaliarEUsarShortlist() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        MvcResult atletaAlvo = cadastrar(
            "alvo." + suffix + "@scoutplay.test", "ATLETA", "2002-02-02", "223" + digits(suffix));
        String alvoUsername = objectMapper.readTree(atletaAlvo.getResponse().getContentAsString())
            .path("data").path("username").asText();

        Cookie atletaCookie = cadastrar(
            "ator." + suffix + "@scoutplay.test", "ATLETA", "2001-01-01", "323" + digits(suffix))
            .getResponse().getCookie("access_token");

        mvc.perform(post("/api/user/{username}/avaliar", alvoUsername)
                .cookie(atletaCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nota\":8,\"comentario\":\"boa leitura de jogo\"}"))
            .andExpect(status().isForbidden());

        Cookie olheiroCookie = cadastrar(
            "olheiro." + suffix + "@scoutplay.test", "OLHEIRO", "1985-03-03", "423" + digits(suffix))
            .getResponse().getCookie("access_token");

        mvc.perform(post("/api/user/{username}/avaliar", alvoUsername)
                .cookie(olheiroCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nota\":8,\"comentario\":\"boa leitura de jogo\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.nota").value(8));

        mvc.perform(post("/api/user/{username}/shortlist", alvoUsername).cookie(olheiroCookie))
            .andExpect(status().isOk());

        mvc.perform(get("/api/shortlist").cookie(olheiroCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].username").value(alvoUsername));
    }

    @Test
    void atletaPodePublicarImagemEFeedPublicoPodeListaLa() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Cookie atletaCookie = cadastrar(
            "post." + suffix + "@scoutplay.test", "ATLETA", "2000-05-05", "523" + digits(suffix))
            .getResponse().getCookie("access_token");

        byte[] png = new byte[] {(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10};
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "jogada.png", "image/png", png);
        String filename = null;
        try {
            MvcResult resultado = mvc.perform(multipart("/api/post/")
                    .file(arquivo)
                    .param("titulo", "Treino funcional")
                    .param("descricao", "Finalização e movimentação")
                    .cookie(atletaCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

            JsonNode body = objectMapper.readTree(resultado.getResponse().getContentAsString());
            filename = body.path("data").path("src").asText();

            mvc.perform(get("/api/media/{filename}", filename))
                .andExpect(status().isOk());

            mvc.perform(get("/api/post/").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
        } finally {
            if (filename != null && !filename.isBlank()) {
                Files.deleteIfExists(Path.of("uploads/media").resolve(filename));
            }
        }
    }

    @Test
    void recuperacaoDeSenhaDeveUsarTokenUnico() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "reset." + suffix + "@scoutplay.test";
        cadastrar(email, "ATLETA", "2000-06-06", "623" + digits(suffix));

        mvc.perform(post("/api/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("email", email)))
            .andExpect(status().isOk());

        org.mockito.ArgumentCaptor<SimpleMailMessage> captor =
            org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String texto = captor.getValue().getText();
        String token = texto.substring(texto.indexOf("token=") + 6).split("\\s")[0];

        mvc.perform(post("/api/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("token", token, "novaSenha", "NovaSenha123!")))
            .andExpect(status().isOk());

        mvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("email", email, "senha", "NovaSenha123!")))
            .andExpect(status().isOk());

        mvc.perform(post("/api/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("token", token, "novaSenha", "OutraSenha123!")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void responsavelPodeVincularAtletaMenorSemExporIdentificadorPrivado() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        MvcResult menor = cadastrar(
            "menor." + suffix + "@scoutplay.test", "ATLETA", "2012-07-07", "723" + digits(suffix));
        String username = objectMapper.readTree(menor.getResponse().getContentAsString())
            .path("data").path("username").asText();
        Cookie responsavelCookie = cadastrar(
            "responsavel." + suffix + "@scoutplay.test", "RESPONSAVEL", "1980-08-08", "823" + digits(suffix))
            .getResponse().getCookie("access_token");

        mvc.perform(post("/api/user/{username}/responsavel", username).cookie(responsavelCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.data.RESPONSAVEL_PENDENTE").value(false));

        mvc.perform(get("/api/user").param("user", username))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.detalhesPerfil.RESPONSAVEL").doesNotExist())
            .andExpect(jsonPath("$.data.detalhesPerfil.possuiResponsavel").value(true));
    }

    private MvcResult cadastrar(String email, String tipo, String nascimento, String cpfPrefix) throws Exception {
        String cpf = (cpfPrefix + "00000000000").substring(0, 11);
        return mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome":"Teste",
                      "sobrenome":"Funcional",
                      "email":"%s",
                      "senha":"Senha123!",
                      "dataNascimento":"%s",
                      "tipoConta":"%s",
                      "cpf":"%s"
                    }
                    """.formatted(email, nascimento, tipo, cpf)))
            .andExpect(status().isOk())
            .andExpect(cookie().exists("access_token"))
            .andReturn();
    }

    private String json(String... values) throws Exception {
        java.util.Map<String, String> body = new java.util.LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) body.put(values[i], values[i + 1]);
        return objectMapper.writeValueAsString(body);
    }

    private String digits(String value) {
        String digits = value.replaceAll("[^0-9]", "");
        return (digits + "00000000").substring(0, 8);
    }
}
