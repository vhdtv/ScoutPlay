package com.scoutplay.ScoutPlay.services;

import com.scoutplay.ScoutPlay.enums.TipoContaEnum;
import com.scoutplay.ScoutPlay.models.Avaliacao;
import com.scoutplay.ScoutPlay.models.DetalhePerfil;
import com.scoutplay.ScoutPlay.models.TipoConta;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.repositories.AvaliacaoRepository;
import com.scoutplay.ScoutPlay.repositories.DetalhePerfilRepository;
import com.scoutplay.ScoutPlay.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public String montarContexto() {
        Page<Usuario> atletas = usuarioRepository.findByTipoConta(TipoContaEnum.ATLETA, Pageable.unpaged());

        if (atletas.isEmpty()) {
            return "Nenhum atleta cadastrado na plataforma.";
        }

        StringBuilder sb = new StringBuilder();

        for (Usuario atleta : atletas) {
            sb.append("Atleta: ").append(atleta.getNome());
            if (atleta.getSobrenome() != null) sb.append(" ").append(atleta.getSobrenome());
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
        if (value != null) sb.append(label).append(": ").append(value).append("\n");
    }

    private void append(StringBuilder sb, String label, Object value, String unidade) {
        if (value != null) sb.append(label).append(": ").append(value).append(unidade).append("\n");
    }
}
