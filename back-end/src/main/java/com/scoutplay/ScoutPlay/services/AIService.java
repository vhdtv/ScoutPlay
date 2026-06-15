package com.scoutplay.ScoutPlay.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIContextService aiContextService;

    private final RestTemplate restTemplate = new RestTemplate();

    public String perguntar(String pergunta) {

        String dadosDosAtletas = aiContextService.montarContexto();

        String systemPrompt = "Você é o Copiloto ScoutPlay, um assistente inteligente especialista em análise de dados de futebol.\n"
                + "Abaixo estão as informações reais extraídas do nosso banco de dados em tempo real.\n"
                + "REGRAS CRUTIAIS:\n"
                + "1. Responda à pergunta do usuário baseando-se APENAS e EXCLUSIVAMENTE nos dados fornecidos abaixo.\n"
                + "2. Se você não encontrar nenhum jogador correspondente aos critérios, responda textualmente: 'Não encontrei nenhum atleta com essas características nos registros do sistema.'\n"
                + "3. NUNCA invente ou alucine nomes de jogadores ou dados que não estejam explicitamente listados abaixo.\n\n"
                + "=== DADOS DOS ATLETAS (BANCO DE DADOS) ===\n"
                + dadosDosAtletas
                + "\n=========================================\n\n";

        Map<String, Object> requestBody = Map.of(
                "model", "llama3.2",
                "prompt", systemPrompt + "Usuário: " + pergunta,
                "stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = "http://localhost:11434/api/generate";
            Map<?, ?> response = restTemplate.postForObject(url, entity, Map.class);

            if (response != null && response.containsKey("response")) {
                return response.get("response").toString();
            }
        } catch (Exception e) {
            return "Erro ao comunicar com o modelo de IA: " + e.getMessage();
        }

        return "Não foi possível obter uma resposta da IA.";
    }
}