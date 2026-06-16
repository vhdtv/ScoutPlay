package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;
    private final AIContextService aiContextService;

    @Value("${app.ia.url}")
    private String iaUrl;

    @Value("${app.anthropic.api-key:}")
    private String anthropicApiKey;

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_MODEL = "claude-haiku-4-5-20251001";

    private static final String SYSTEM_PROMPT =
        "Você é o Copiloto ScoutPlay, assistente especialista em futebol e scouting de atletas.\n\n" +
        "Regras importantes:\n" +
        "- Responda apenas com base nos dados fornecidos sobre os atletas cadastrados.\n" +
        "- NUNCA revele informações sigilosas como CPF, e-mail, telefone ou endereço.\n" +
        "- Você pode falar sobre: nome, idade, posição, altura, peso, pé dominante, clubes anteriores e notas.\n" +
        "- Se não encontrar a resposta nos dados, diga claramente que não encontrou.\n" +
        "- Responda em português, de forma objetiva e natural.\n\n" +
        "Atletas cadastrados na plataforma:\n%s";

    public String perguntar(String pergunta) {
        String contexto = aiContextService.montarContexto();

        if (anthropicApiKey != null && !anthropicApiKey.isBlank()) {
            return perguntarAnthropicAPI(pergunta, contexto);
        }

        return perguntarServicoPython(pergunta, contexto);
    }

    private String perguntarAnthropicAPI(String pergunta, String contexto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");

        String systemPrompt = String.format(SYSTEM_PROMPT, contexto);

        Map<String, Object> body = Map.of(
            "model", ANTHROPIC_MODEL,
            "max_tokens", 512,
            "system", systemPrompt,
            "messages", List.of(Map.of("role", "user", "content", pergunta))
        );

        try {
            Map<?, ?> resposta = restTemplate.postForObject(
                ANTHROPIC_URL,
                new HttpEntity<>(body, headers),
                Map.class
            );
            if (resposta == null) throw new ServiceUnavailableException("Anthropic retornou resposta vazia");

            List<?> content = (List<?>) resposta.get("content");
            if (content == null || content.isEmpty()) throw new ServiceUnavailableException("Anthropic: campo 'content' vazio");

            Map<?, ?> firstBlock = (Map<?, ?>) content.get(0);
            String texto = (String) firstBlock.get("text");
            if (texto == null || texto.isBlank()) throw new ServiceUnavailableException("Anthropic: texto vazio");

            return texto.trim();
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Não foi possível conectar à API de IA: " + e.getMessage());
        } catch (ServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Erro ao processar resposta da IA: " + e.getMessage());
        }
    }

    private String perguntarServicoPython(String pergunta, String contexto) {
        Map<String, String> body = Map.of("pergunta", pergunta, "contexto", contexto);
        try {
            Map<?, ?> resposta = restTemplate.postForObject(iaUrl + "/perguntar", body, Map.class);
            if (resposta == null || resposta.get("resposta") == null) {
                throw new ServiceUnavailableException("Serviço de IA retornou resposta inválida");
            }
            return (String) resposta.get("resposta");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(
                "Serviço de IA indisponível. Configure ANTHROPIC_API_KEY ou inicie o servidor Python em " + iaUrl);
        }
    }
}
