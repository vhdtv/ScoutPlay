package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.exceptions.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;
    private final AIContextService aiContextService;

    @Value("${app.ia.url}")
    private String iaUrl;

    public String perguntar(String pergunta) {
        String contexto = aiContextService.montarContexto();

        Map<String, String> body = Map.of(
                "pergunta", pergunta,
                "contexto", contexto
        );

        try {
            Map<?, ?> resposta = restTemplate.postForObject(iaUrl + "/perguntar", body, Map.class);
            if (resposta == null || resposta.get("resposta") == null) {
                throw new ServiceUnavailableException("Serviço de IA retornou resposta inválida");
            }
            return (String) resposta.get("resposta");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException(
                    "Serviço de IA indisponível. Verifique se o servidor Python está rodando em " + iaUrl);
        }
    }
}
