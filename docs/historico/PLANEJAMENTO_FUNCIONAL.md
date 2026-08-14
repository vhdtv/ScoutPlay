# Planejamento funcional histórico

> Documento de contexto. Ele resume a visão da primeira versão e não declara que todas as funções estão implementadas na `ScoutPlay2.0`.

## Visão do produto

O ScoutPlay foi idealizado para tornar a descoberta de jovens atletas mais estruturada e segura. Atletas publicariam perfil e material esportivo; olheiros encontrariam talentos por critérios objetivos; responsáveis intermediariam o contato quando o atleta fosse menor de idade.

## Atores planejados

| Papel | Responsabilidade imaginada |
| --- | --- |
| Atleta | manter perfil esportivo, destaques e visibilidade |
| Responsável | autorizar e intermediar a participação de menores |
| Olheiro | buscar, favoritar, avaliar e solicitar contato |
| Administração | moderar conteúdo, verificar contas e auditar eventos |

Na entrega 2.0, atleta e olheiro são tipos de uma entidade genérica `Usuario`. Responsável e administração permanecem como visão de produto, não como fluxos completos.

## Planejado versus entregue

| Épico da visão original | Estado na `ScoutPlay2.0` | Observação |
| --- | --- | --- |
| Identidade e perfis | parcial | cadastro de atleta/olheiro, login e perfis existem; autorização por papel precisa ser concluída |
| Vídeos e destaques | parcial | feed aceita mídia, mas não há curadoria completa de melhores lances |
| Busca e descoberta | parcial | filtros por nome, posição e pé dominante e shortlist estão disponíveis |
| Convites e contato | não implementado | não há convite com validade nem liberação controlada de contato |
| Responsável por menor | não implementado | regra não está aplicada no modelo ou nos fluxos atuais |
| Tags e métricas esportivas | parcial | detalhes flexíveis existem em JSONB, sem taxonomia e validação completas |
| Favoritos | parcial | shortlist existe para usuários autenticados |
| Auditoria e administração | não implementado | faltam trilha de eventos, verificação e painel de moderação |

## Regras de negócio que continuam importantes

1. Menores de idade devem participar somente com vínculo e autorização de responsável.
2. Dados de contato e documentos pessoais não devem ser públicos nem enviados ao modelo de IA.
3. Solicitações de contato devem ter consentimento, estado, validade e registro de quem realizou cada ação.
4. Conteúdo e contas precisam de denúncia, moderação e resposta administrativa.
5. Avaliações esportivas devem ser atribuídas a olheiros autorizados, com rastreabilidade.
6. Perfis devem controlar visibilidade e exclusão de dados em conformidade com a LGPD.

## Backlog funcional herdado

### Identidade e confiança

- vínculo entre atleta menor e responsável;
- verificação de olheiros e clubes;
- consentimento e preferências de privacidade;
- papéis e permissões aplicados no back-end.

### Descoberta esportiva

- filtros por localização, idade e métricas esportivas;
- tags padronizadas de posição, categoria e pé dominante;
- destaques e ordenação sem depender de carregamento integral em memória;
- shortlist com notas privadas do olheiro.

### Contato e oportunidades

- convite formal do olheiro;
- aceite do atleta ou responsável;
- expiração e revogação;
- liberação de contato somente após consentimento;
- histórico auditável da oportunidade.

### Segurança e operação

- denúncia e moderação de perfis, posts e avaliações;
- trilha de auditoria para ações sensíveis;
- exclusão e exportação de dados;
- limites de uso e observabilidade.

## Decisões ainda abertas

- idade mínima e comprovação do responsável;
- critérios de verificação de olheiros;
- política de retenção de vídeos, avaliações e convites;
- visibilidade padrão do perfil e das avaliações;
- modelo de consentimento para uso de dados pela IA;
- responsabilidades de moderação da instituição ou dos autores.

## Proveniência

Resumo produzido a partir do [planejamento funcional da branch `main`](https://github.com/vhdtv/ScoutPlay/blob/main/docs/produto/planejamento-funcional-scoutplay.md) e confrontado com o código e a documentação da `ScoutPlay2.0`.
