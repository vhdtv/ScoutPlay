# Roadmap consolidado

Este documento é a fonte resumida para organizar Issues e a aba GitHub Projects. A ordem combina risco técnico, dependências e valor funcional.

## Agora — revisão privada antes da publicação

A auditoria encontrou itens de segurança que devem bloquear um deploy público. Como este repositório é aberto, detalhes, evidências operacionais e cenários de abuso não são reproduzidos neste documento. Os mantenedores devem tratá-los em canal privado, executar rotação preventiva de credenciais e publicar somente as correções e seus testes.

## Próximo — estabilizar a base

| Prioridade | Trabalho | Resultado esperado |
| --- | --- | --- |
| P1 | [revisar tipos de ID dos repositórios e restrições de unicidade](https://github.com/vhdtv/ScoutPlay/issues/125) | contratos JPA coerentes e concorrência protegida no banco |
| P1 | adotar migrations versionadas | schema reproduzível sem depender de `ddl-auto=update` |
| P1 | eliminar N+1 no feed, busca e contexto da IA | consultas paginadas e orçamento de queries testado |
| P1 | validar uploads por tamanho, tipo real e caminho normalizado | arquivos não autorizados rejeitados e mídia servida de diretório permitido |
| P1 | fechar regras de olheiro, atleta e responsável | avaliações e shortlist autorizadas por papel no back-end |
| P1 | [tornar builds reproduzíveis](https://github.com/vhdtv/ScoutPlay/issues/124) | lockfile versionado, versões fixas e Maven Wrapper executável |
| P1 | criar CI para Java 21 e front-end | build, testes, lint e análise de dependências em cada pull request |

## Depois — evoluir o produto

1. implementar responsável e consentimento para menores;
2. criar convites de contato com aceite, validade, revogação e auditoria;
3. adicionar verificação de olheiros e moderação;
4. ampliar busca com localização, idade e métricas esportivas;
5. criar controles de privacidade, exportação e exclusão de dados;
6. formalizar consentimento e minimização de dados no copiloto de IA.

## Critério de pronto

Cada item deve incluir:

- regra e ameaça ou necessidade que motivaram a mudança;
- testes automatizados do caminho feliz e das negações relevantes;
- documentação e variáveis de ambiente atualizadas;
- ausência de segredo ou dado pessoal em código, logs e fixtures;
- evidência de build limpo em CI;
- estratégia de rollback quando houver migration ou mudança de contrato.

## Organização sugerida no GitHub Projects

| Campo | Valores sugeridos |
| --- | --- |
| Status | Backlog, Pronto, Em andamento, Em revisão, Concluído |
| Prioridade | P0, P1, P2 |
| Área | Segurança, Back-end, Front-end, Dados, IA, Produto, Documentação |
| Esforço | P, M, G |
| Entrega | Hardening, Estabilização, Evolução funcional |

Os itens de segurança privados devem vir antes do backlog público. Entre as Issues públicas, persistência/desempenho e qualidade/CI são as primeiras dependências técnicas.
