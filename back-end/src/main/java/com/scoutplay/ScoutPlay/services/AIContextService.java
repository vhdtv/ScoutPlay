package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.AvaliacaoRepository;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class AIContextService {

    private final UsuarioRepository usuarioRepository;
    private final DetalhePerfilRepository detalhePerfilRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Value("${app.ia.max-athletes:100}")
    private int maxAthletes;

    @Transactional(readOnly = true)
    public String montarContexto() {
        List<Usuario> atletas = usuarioRepository
                .findAllAtivosByTipoContaId(TipoConta.ATLETA, PageRequest.of(0, Math.max(1, Math.min(maxAthletes, 200))))
                .getContent();

        if (atletas.isEmpty()) {
            return "Nenhum atleta cadastrado na plataforma.";
        }

        StringBuilder sb = new StringBuilder();

        for (Usuario atleta : atletas) {
            sb.append("Atleta: ").append(safe(atleta.getNome()));
            if (atleta.getSobrenome() != null) sb.append(" ").append(safe(atleta.getSobrenome()));
            if (atleta.obterIdade() != null) sb.append(", ").append(atleta.obterIdade()).append(" anos");
            sb.append("\n");

            DetalhePerfil detalhe = detalhePerfilRepository.getByUsuario(atleta);
            if (detalhe != null && detalhe.getData() != null) {
                Map<String, Object> data = detalhe.getData();
                append(sb, "  Posição", data.get("posicao"));
                append(sb, "  Altura", data.get("altura"), "m");
                append(sb, "  Peso", data.get("peso"), "kg");
                append(sb, "  Pé dominante", data.get("peDominante"));
                append(sb, "  Clubes anteriores", data.get("clubesAnteriores"));
            }

            List<Avaliacao> avaliacoes = avaliacaoRepository.findByAtleta(atleta);
            if (!avaliacoes.isEmpty()) {
                OptionalDouble media = avaliacoes.stream().mapToDouble(Avaliacao::getNota).average();
                media.ifPresent(m ->
                    sb.append(String.format("  Nota média: %.1f (%d avaliações)%n", m, avaliacoes.size()))
                );
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private void append(StringBuilder sb, String label, Object value) {
        if (value != null) sb.append(label).append(": ").append(safe(value)).append("\n");
    }

    private void append(StringBuilder sb, String label, Object value, String unidade) {
        if (value != null) sb.append(label).append(": ").append(safe(value)).append(unidade).append("\n");
    }

    private String safe(Object value) {
        String normalized = String.valueOf(value)
            .replaceAll("[\\r\\n\\t]+", " ")
            .replaceAll("[<>]", "")
            .trim();
        return normalized.length() > 120 ? normalized.substring(0, 120) : normalized;
    }
}
