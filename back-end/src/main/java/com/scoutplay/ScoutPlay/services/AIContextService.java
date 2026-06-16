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
            System.out.println(">>> [AVISO] Lista de atletas veio VAZIA do banco de dados!");
            return "Nenhum atleta cadastrado na plataforma.";
        }

        System.out.println(">>> QUANTIDADE DE ATLETAS ENCONTRADOS: " + atletas.size());
        StringBuilder sb = new StringBuilder();

        for (Usuario atleta : atletas) {
            System.out.println(">>> Montando contexto do atleta: " + atleta.getNome());

            sb.append("--- ATLETA DO SISTEMA ---\n");
            sb.append("ID: ").append(atleta.getAliasId()).append("\n");
            sb.append("Nome Completo: ").append(atleta.getNome());
            if (atleta.getSobrenome() != null) {
                sb.append(" ").append(atleta.getSobrenome());
            }
            if (atleta.obterIdade() != null) {
                sb.append(" (").append(atleta.obterIdade()).append(" anos)");
            }
            sb.append("\n");

            DetalhePerfil detalhe = detalhePerfilRepository.getByUsuario(atleta);
            if (detalhe != null && detalhe.getData() != null) {
                Map<String, Object> data = detalhe.getData();
                System.out.println("  -> JSON lido para " + atleta.getNome() + ": " + data);

                Object posicao = data.get("posicao") != null ? data.get("posicao") : data.get("posiçao");
                Object altura = data.get("altura");
                Object peso = data.get("peso");
                Object peDominante = data.get("pe_dominante") != null ? data.get("pe_dominante")
                        : data.get("peDominante");
                Object clubes = data.get("clubes_anteriores") != null ? data.get("clubes_anteriores")
                        : data.get("clubesAnteriores");

                if (posicao != null)
                    sb.append("Posição: ").append(posicao).append("\n");
                if (altura != null)
                    sb.append("Altura: ").append(altura).append(" metros\n");
                if (peso != null)
                    sb.append("Peso: ").append(peso).append(" kg\n");
                if (peDominante != null)
                    sb.append("Pé Dominante: ").append(peDominante).append("\n");
                if (clubes != null)
                    sb.append("Clubes Anteriores: ").append(clubes).append("\n");
            } else {
                System.out
                        .println("  -> [AVISO] Detalhe de perfil ou JSON veio NULO para o atleta: " + atleta.getNome());
            }

            List<Avaliacao> avaliacoes = avaliacaoRepository.findByAtleta(atleta);
            if (!avaliacoes.isEmpty()) {
                OptionalDouble media = avaliacoes.stream().mapToDouble(Avaliacao::getNota).average();
                media.ifPresent(
                        m -> sb.append(String.format("Nota Média: %.1f (%d avaliações)%n", m, avaliacoes.size())));
            }
            sb.append("\n");
        }

        System.out.println("=== CONTEXTO COMPLETO ENVIADO À IA ===\n" + sb.toString());

        return sb.toString().trim();
    }
}