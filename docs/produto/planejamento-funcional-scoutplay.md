# ScoutPlay — Planejamento Funcional Estruturado

## 1) Visão do Produto

**Objetivo central (MVP):** permitir que atletas sejam encontrados por olheiros com segurança, priorizando vídeos e contato direto (WhatsApp/telefone) com rastreabilidade de convites.

**Problema que resolve:** dificuldade de visibilidade de atletas e falta de canal organizado, auditável e seguro para aproximação entre atleta e olheiro.

**Público-alvo inicial:** atletas, responsáveis (menores), olheiros e admin.

**Módulo futuro (fora do MVP):** clubes divulgando peneiras/eventos.

---

## 2) Perfis e Permissões

## 2.1 Atleta (maior)
- Cadastra perfil e vídeos
- Define privacidade de contato
- Recebe e responde convites
- Visualiza destaques/tags recebidas

## 2.2 Responsável (menor)
- Gerencia perfil do menor
- Recebe notificações e responde convites
- Controla visibilidade de contato

## 2.3 Olheiro
- Busca atletas por filtros
- Marca destaques por vídeo (tags)
- Favorita atletas
- Envia convites

## 2.4 Admin
- Moderação de conteúdo
- Verificação de perfis
- Auditoria de convites e ações críticas

---

## 3) Escopo: MVP x Fase 2

## 3.1 MVP (obrigatório)
1. Cadastro/login por perfil
2. Perfil do atleta com vídeos
3. Busca de atletas com filtros
4. Convites (enviar, receber, aceitar/recusar)
5. Contato via WhatsApp (deep link)
6. Destaques por tags pré-definidas
7. Favoritos do olheiro
8. Logs básicos de auditoria

## 3.2 Fase 2 (evolução)
- Mensageria interna
- Divulgação de peneiras por Clube
- Recomendação avançada por IA (ranking semântico/multimodal)
- Dashboard de métricas para olheiro/admin

---

## 4) Fluxos Funcionais (detalhados)

## 4.1 Convite/Contato Olheiro -> Atleta
1. Olheiro acessa perfil do atleta
2. Vê CTA: **Chamar no WhatsApp** e **Enviar Convite**
3. Ao enviar convite:
   - sistema registra convite
   - dispara notificação (email; WhatsApp/SMS opcional)
4. Atleta/Responsável abre convite e decide:
   - **Aceitar**: registra aceite + abre WhatsApp do olheiro (MVP)
   - **Recusar**: registra recusa
5. Tudo gera evento de auditoria

**Regra para menores:** interação de convite apenas via responsável.

## 4.2 Tela “Convites” (atleta/responsável)
- Lista por cards com status: **Novo, Aceito, Recusado, Expirado**
- Detalhes do convite:
  - nome do olheiro, clube, cargo
  - objetivo, local/data (se informado)
  - instruções
- CTA: Aceitar/Recusar
- Banner de segurança: “não pagar taxas; validar dados do clube”

## 4.3 Destaques por Tags
- Taxonomia fixa de habilidades (MVP), ex.:
  - Bom passe, Visão de jogo, Defesa, Finalização, Velocidade
- Olheiro marca tags em vídeos
- Atleta visualiza feedback qualitativo
- Busca permite filtrar por tags
- “Favoritos” separado de “Destaques” (não é avaliação oficial)

---

## 5) Busca Otimizada por IA (estratégia prática)

## 5.1 Fase MVP (rápida e robusta)
- Indexação por metadados:
  - posição, idade/faixa etária, pé dominante, cidade/UF, tags, clube atual
- Busca híbrida:
  - filtros estruturados + relevância textual simples
- Ordenação inicial por:
  1) aderência aos filtros
  2) completude do perfil
  3) recência de atualização

## 5.2 Fase Evolutiva (IA)
- Embeddings de texto (bio, descrição de vídeos, observações)
- Ranking semântico por intenção do olheiro
- Sugestões automáticas:
  - “atletas semelhantes”
  - “tags prováveis” (assistida, com revisão humana)

**Nota:** manter transparência: IA recomenda, decisão final é humana.

---

## 6) Regras de Negócio Prioritárias

1. **Menor de idade**: contato/convite somente com responsável
2. **Privacidade de contato**:
   - telefone oculto por padrão para menor
   - atleta maior pode habilitar/ocultar
3. **Convite com validade** (expiração configurável)
4. **Auditoria obrigatória** de ações críticas:
   - envio, visualização, aceite, recusa
5. **Moderação**: admin pode ocultar conteúdo inadequado

---

## 7) Backlog Estruturado (épicos e histórias)

## Épico A — Identidade e Perfis
- US-A1: Como atleta, quero criar conta e perfil para ser encontrado.
- US-A2: Como responsável, quero gerenciar perfil de menor para segurança.
- US-A3: Como olheiro, quero perfil verificado para confiança.

## Épico B — Vídeos e Destaques
- US-B1: Como atleta, quero publicar vídeos por categoria.
- US-B2: Como olheiro, quero marcar tags de habilidade por vídeo.
- US-B3: Como olheiro, quero favoritar atletas para acompanhar depois.

## Épico C — Busca e Descoberta
- US-C1: Como olheiro, quero filtrar atletas por critérios objetivos.
- US-C2: Como olheiro, quero busca inteligente por texto/tags.
- US-C3: Como atleta, quero aparecer melhor quando meu perfil estiver completo.

## Épico D — Convites e Contato
- US-D1: Como olheiro, quero enviar convite com contexto.
- US-D2: Como atleta/responsável, quero aceitar/recusar com um clique.
- US-D3: Como usuário, quero abrir contato via WhatsApp com mensagem padrão.

## Épico E — Segurança e Auditoria
- US-E1: Como admin, quero logs de ações críticas.
- US-E2: Como usuário, quero alertas anti-golpe na tela de convite.
- US-E3: Como sistema, quero restringir ações de menores por responsável.

---

## 8) Critérios de Aceite (exemplos objetivos)

## CA-Convite
- Dado um olheiro autenticado, quando envia convite válido, então o convite fica com status “Novo” e log registrado.

## CA-Aceite
- Dado um convite “Novo”, quando atleta/responsável aceita, então status muda para “Aceito” e WhatsApp abre com mensagem padrão.

## CA-Menor
- Dado atleta menor, quando olheiro tenta contato direto, então sistema direciona interação ao responsável.

## CA-Destaques
- Dado vídeo publicado, quando olheiro marca tag, então atleta visualiza tag no detalhe do vídeo.

## CA-Busca
- Dado filtros aplicados, quando olheiro pesquisar, então resultados devem respeitar filtros e ordenação definida.

---

## 9) Plano de Execução (6 integrantes)

## Sprint 0 (setup)
- BE1/BE2: arquitetura base + autenticação + modelos
- FE1/FE2: layout base + navegação + tela de perfil inicial
- IA1: setup BDD
- IA2: plano de testes e estrutura de evidências

## Sprint 1 (MVP parcial)
- Perfis + upload/lista de vídeos + busca básica
- 2 cenários BDD + testes unitários iniciais
- primeira versão de convite (criar/listar)

## Sprint 2 (MVP completo)
- aceite/recusa + WhatsApp deep link + destaques + favoritos
- regra de menor/responsável fechada
- completar 5 cenários BDD

## Sprint 3 (estabilização)
- cobertura >= 80% (service/domain)
- roteiros manuais + usabilidade (3 participantes)
- documentação final + demo + slides

---

## 10) Pendente de Decisão (abrir issue para cada item)

1. Canal de notificação no MVP: apenas e-mail ou e-mail + WhatsApp/SMS?
2. Política de verificação de olheiro (documento, domínio corporativo, manual?)
3. Tabela final de tags de habilidade (quem aprova?)
4. Critério oficial de expiração de convite
5. Regras de moderação (SLA e fluxo de denúncia)

---

## 11) Métricas de Sucesso (MVP)

- % de perfis completos
- tempo médio da busca até primeiro contato
- taxa de convite aceito
- taxa de resposta por perfil (maior x menor/responsável)
- quantidade de eventos auditados sem falha

---

## 12) Mensagem padrão WhatsApp (MVP)

“Olá, [NOME DO ATLETA]. Sou [NOME], olheiro do [CLUBE]. Vi seu vídeo no ScoutPlay e gostaria de conversar sobre uma oportunidade. Você topa?”
