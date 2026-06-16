# Cronograma do Projeto - ScoutPlay

**Atualizado em:** 2026-06-16

## 1. Equipe e papéis

- **Backend (BE):** arquitetura, regras de negócio, services, persistência
- **Frontend (FE):** telas, fluxos, integração com API
- **IA:** testes BDD (Gherkin/Cucumber), plano de testes, usabilidade

---

## 2. Estado atual por Sprint

| Sprint | Status | Entregas |
|--------|--------|----------|
| **Sprint 0 (Setup)** | Concluido | Repo, arquitetura 4 camadas, Maven, estrutura base |
| **Sprint 1 (MVP)** | Concluido | JWT auth via cookie HttpOnly, cadastro de atleta/olheiro, login/logout |
| **Sprint 2 (Incremento)** | Concluido | 28+ endpoints, upload de foto/midia, feed de posts, likes, comentarios, seguidores, avaliacoes, shortlist, modulo IA (FastAPI + RAG) |
| **Sprint 3 (Estabilizacao)** | Concluido | Frontend completo (11 rotas), recuperacao de senha por email, seed de dados (10 usuarios), guards de autenticacao, melhorias visuais (ocai0) |
| **Sprint 4 (Seguranca)** | Em andamento | Correcao de vulnerabilidades criticas, parametrizacao de URL e cookies |

---

## 3. Distribuicao de trabalho — Sprint 4 (atual)

### Backend
- [x] JWT em cookie HttpOnly (nao mais Bearer header)
- [x] Cookie `secure` configuravel via env var `COOKIE_SECURE`
- [x] Validacao de startup do JWT secret (minimo 32 chars, obrigatorio)
- [x] Recuperacao de senha via email (Gmail SMTP, App Password)
- [x] Seed automatico de 5 atletas + 5 olheiros com posts, fotos e interacoes
- [x] Modulo IA integrado: FastAPI + RAG (Ollama phi3:mini + embeddinggemma)
- [ ] Rate limiting no /login (protecao contra brute force)
- [ ] Validacao de CPF (algoritmo mod 11)
- [ ] Fix IDOR no endpoint /user/avatar
- [ ] Indices de banco em email, username, criadoEm
- [ ] Corrigir N+1 queries em PostService.listar()

### Frontend
- [x] 11 rotas implementadas e integradas com a API
- [x] Guards de autenticacao (beforeLoad + redirect)
- [x] Feed com filtro por tipo de conta (Atleta / Olheiro)
- [x] Perfil de usuario com foto, posts, seguidores
- [x] Tela de busca de atletas com filtros
- [x] Minha Lista (shortlist para olheiros)
- [x] Configuracoes de conta
- [x] Cadastro com dados especificos por tipo de conta
- [x] Recuperacao de senha
- [x] URL do backend via `VITE_BACKEND_URL` (env var — nao hardcoded)
- [ ] Timeout no fetch (AbortController 30s)
- [ ] Desabilitar botoes durante loading (evitar double-submit)
- [ ] Error boundary nas rotas principais

### IA / Testes
- [ ] Minimo de 5 cenarios BDD automatizados (Cucumber)
- [ ] `/docs/testes/plano-de-teste.md` preenchido
- [ ] `/docs/testes/roteiros-de-teste.md` com 5 roteiros executados
- [ ] `/docs/testes/usabilidade.md` com 3 participantes
- [ ] Evidencias em `/docs/testes/evidencias`

---

## 4. Cerimônias Scrum

- **Planning:** definicao da meta da sprint + selecao de issues
- **Daily (assincrona):** atualizacao curta no board
- **Review:** validacao do que foi entregue
- **Retrospectiva:** manter / parar / comecar

---

## 5. Definition of Done (DoD)

Uma issue so vai para **Concluido** quando tiver:

1. Codigo implementado
2. Testes criados/atualizados (quando aplicavel)
3. Documentacao atualizada se houver mudanca de comportamento
4. Evidencia vinculada (issue/PR/print/log)

---

## 6. Checklist final de entrega

- [x] Board GitHub com colunas minimas e issues por sprint
- [x] Minimo de 10 classes autorais + 4 camadas/pacotes (14 models + 16 services + 5 controllers + config)
- [x] Minimo de 6 funcionalidades (28+ endpoints implementados)
- [ ] Testes unitarios (meta >=80% em regras/services)
- [ ] Minimo de 5 cenarios BDD automatizados
- [ ] `/docs/testes/plano-de-teste.md` preenchido
- [ ] `/docs/testes/roteiros-de-teste.md` com 5 roteiros executados
- [ ] `/docs/testes/usabilidade.md` com 3 participantes
- [ ] Evidencias em `/docs/testes/evidencias`
- [ ] `/slides` com apresentacao final e demo
- [ ] README completo e atualizado

---

## 7. Documentacao de referencia

| Documento | Conteudo |
|-----------|----------|
| `docs/modelagem-banco.md` | Esquema completo do banco + todos os endpoints |
| `docs/ia-integracao.md` | Arquitetura e estado atual do modulo RAG |
| `docs/requisitos/requisitos-nao-funcionais.md` | RNFs de seguranca, desempenho, escalabilidade |
