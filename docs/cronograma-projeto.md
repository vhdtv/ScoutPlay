# Cronograma do Projeto - ScoutPlay

**Atualizado em:** 2026-05-20

## 1. Equipe e papéis

- **Backend (BE):** arquitetura, regras de negócio, services, persistência
- **Frontend (FE):** telas, fluxos, integração com API
- **IA:** testes BDD (Gherkin/Cucumber), plano de testes, usabilidade

---

## 2. Estado atual por Sprint

| Sprint | Status | Entregas |
|--------|--------|----------|
| **Sprint 0 (Setup)** | Concluido | Repo, arquitetura 4 camadas, Maven, estrutura base |
| **Sprint 1 (MVP)** | Concluido | JWT auth, registro de atleta/olheiro/responsavel, login |
| **Sprint 2 (Incremento)** | Concluido (backend) | 18 endpoints CRUD, upload de foto, videos, avaliacoes, remodel de banco |
| **Sprint 3 (Estabilizacao)** | Em andamento | Testes unitarios, BDD, integracao frontend, modulo IA |

---

## 3. Distribuicao de trabalho — Sprint 3 (atual)

### Backend
- [x] Todos os 18 endpoints implementados
- [x] Seguranca JWT configurada (SecurityConfig, JwtAuthenticationFilter)
- [x] Remodel de banco: modelo flat com `t_usuario`, `t_detalhes_perfil` (JSONB), `t_video_atleta`, `t_avaliacao`
- [x] Dados de teste (DataInitializer) — 3 usuarios seed
- [ ] Testes unitarios (meta >=80% cobertura em services)
- [ ] Integracao do modulo IA como microservico (ver `docs/ia-integracao.md`)

### Frontend
- [ ] Conectar LoginForm ao `POST /api/login` e salvar token no localStorage
- [ ] Implementar registro de atleta (usar `POST /api/atletas/registro`)
- [ ] Feed de atletas (`GET /api/atletas`) com paginacao
- [ ] Perfil de atleta (`GET /api/atletas/{id}`)
- [ ] Upload de foto de perfil (`POST /api/atletas/{id}/foto`)
- [ ] Adicionar e listar videos (`POST/GET /api/atletas/{id}/videos`)
- [ ] Perfil de olheiro (`GET /api/olheiros/{id}`)
- [ ] Criar avaliacao (`POST /api/avaliacoes`)
- [ ] Implementar client HTTP com `Authorization: Bearer <token>` em todas as chamadas

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

- [ ] Board GitHub com colunas minimas e issues por sprint
- [x] Minimo de 10 classes autorais + 4 camadas/pacotes
- [x] Minimo de 6 funcionalidades (18 endpoints implementados)
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
| `docs/SEMANA2_JWT.md` | Como JWT foi implementado |
| `docs/ia-integracao.md` | Plano de integracao do modulo RAG |
| `docs/ScoutPlay.postman_collection.json` | Colecao Postman com todos os endpoints |
