# ScoutPlay — Apresentação Final
## Gestão e Qualidade de Software | Sprint 3

**Equipe:** Victor Henrique Dias · Paulo Vitor Amorim · Maria Clara Lino · Lucas Andrade · Cesar Martins · Caio Fernandes

---

# Slide 1 — Contexto e Problema

## O Problema

O processo de descoberta de talentos no futebol brasileiro é **desorganizado e inacessível**:

- Jovens atletas não têm visibilidade sem acesso a peneiras e clubes grandes
- Olheiros perdem tempo visitando treinos sem dados prévios dos atletas
- Não existe uma plataforma centralizada que conecte **atleta → olheiro → clube**
- Informações ficam dispersas em grupos de WhatsApp, planilhas e e-mails

## Público-alvo

| Perfil | Necessidade |
|--------|------------|
| Atleta (14–25 anos) | Criar perfil, publicar vídeos/fotos, ser encontrado |
| Olheiro / Scout | Buscar atletas por posição/perfil, avaliar, acompanhar |
| Responsável legal | Gerenciar conta de atleta menor de idade |

---

# Slide 2 — Solução Proposta

## ScoutPlay: a rede social do futebol de base

> Uma plataforma que centraliza o scouting, conectando jovens talentos a oportunidades reais.

### Funcionalidades principais

1. Cadastro segmentado (atleta / olheiro / responsável)
2. Autenticação segura com JWT via cookie HttpOnly
3. Feed paginado de publicações com filtro por posição e tipo de conta
4. Criação e exclusão de posts (imagens e vídeos)
5. Like, dislike e comentários em publicações
6. Sistema de seguir / deixar de seguir usuários
7. Perfil público com dados esportivos, posts e estatísticas de seguidores
8. Upload de foto de perfil
9. Edição parcial de perfil (nome, dados esportivos)
10. **Copiloto de IA** — chatbot RAG que responde perguntas sobre atletas cadastrados
11. Recuperação de senha
12. Filtragem de posts por posição e tipo de conta no feed

### Regras de negócio não-triviais

- **Unicidade de CPF e e-mail** — bloqueio com 409 Conflict
- **Ownership via JWT** — apenas o dono edita/exclui seu conteúdo
- **Validação de senha** — obrigatória, com codificação bcrypt antes do save
- **Contexto RAG para IA** — AIContextService filtra apenas dados públicos (nunca CPF, e-mail ou telefone)

---

# Slide 3 — Arquitetura e Estrutura

## Stack tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Back-end | Java 21 + Spring Boot 3.3.4 |
| Banco de dados | PostgreSQL 18 |
| Segurança | Spring Security + JWT (jjwt 0.11.5) — cookie HttpOnly |
| Front-end | React 19 + Vite + TanStack Router |
| Estilo | TailwindCSS 4 |
| IA | Python FastAPI + RAG + Claude API (Haiku 4.5) |
| Testes | JUnit 5 + Mockito 5 + Cucumber 7 |
| Build | Maven (back-end) · npm (front-end) |

## Arquitetura em 4 camadas (back-end)

```
controllers/    →  Camada UI      (5 controllers, 24 endpoints)
services/       →  Camada Service (13 services, regras de negócio)
repositories/   →  Camada Infra   (12 repositories, JPA/JPQL)
models/         →  Camada Domain  (14 entidades JPA)
```

## Diagrama simplificado

```
Browser (React)
    │  HTTP/REST
    ▼
AuthController  PostController  UserController  MediaController  AIController
    │               │               │
    └───────────────┴───────────────┘
                    │
              Spring Security (JWT HttpOnly Cookie)
                    │
         ┌──────────┴──────────┐
    UsuarioService         PostService
    AuthService            InteractionsService
    AIService ──────────── AIContextService
         │
    PostgreSQL (scoutplaydb)
         │
    FastAPI IA (:8081) ──── Claude API (Haiku 4.5)
```

## Classes autorais por camada

| Camada | Contagem |
|--------|---------|
| Controllers | 5 |
| Services | 13 |
| Repositories | 12 |
| Models/Entities | 14 |
| DTOs | 17 |
| Config / Security / Exceptions | 12 |
| **Total** | **73 classes autorais** |

---

# Slide 4 — Demo

## Fluxos demonstrados ao vivo

1. **Cadastro de atleta** → formulário completo → redirecionamento para feed
2. **Feed** → scroll, filtro por posição, filtro por tipo de conta
3. **Criar post** → upload de imagem → post aparece no feed
4. **Like / Unlike** → contador atualiza em tempo real
5. **Seguir usuário** → estado sincronizado em todos os cards do mesmo autor
6. **Perfil** → dados esportivos, grade de posts, contadores de seguidores
7. **Excluir post** → botão visível apenas no próprio perfil
8. **Copiloto IA** → pergunta sobre atletas cadastrados → resposta contextualizada

## Screenshots

*(prints das telas em `docs/testes/evidencias/`)*

---

# Slide 5 — Testes

## Pirâmide de testes

```
        /\
       /  \  Manual (10 roteiros)
      /    \  Usabilidade (3 participantes)
     /──────\
    /  BDD   \  17 cenários Cucumber
   /          \
  /   Unitário \  11 testes JUnit 5 + Mockito
 /______________\
```

## Testes unitários — UsuarioService (11 testes)

| Teste | Resultado |
|-------|-----------|
| Deve cadastrar atleta com dados válidos | 🟢 PASS |
| Deve lançar exceção quando senha nula | 🟢 PASS |
| Deve lançar exceção quando senha em branco | 🟢 PASS |
| Deve lançar ConflictException para CPF duplicado | 🟢 PASS |
| Deve lançar ConflictException para e-mail duplicado | 🟢 PASS |
| Deve codificar senha antes de salvar | 🟢 PASS |
| Deve retornar usuário por aliasId válido | 🟢 PASS |
| Deve retornar null quando não encontrado | 🟢 PASS |
| Deve buscar dados do perfil por username | 🟢 PASS |
| Deve delegar adição de informação ao DetalhePerfilService | 🟢 PASS |
| Deve delegar remoção de informação ao DetalhePerfilService | 🟢 PASS |

## BDD com Cucumber — 17 cenários em 4 feature files

| Feature | Cenários | Status |
|---------|----------|--------|
| `autenticacao.feature` | 5 (login atleta, olheiro, senha errada, e-mail inexistente, campos em branco) | 🟢 17/17 PASS |
| `cadastro_atleta.feature` | 4 (sucesso, CPF duplicado, e-mail duplicado, sem senha) | 🟢 |
| `posts.feature` | 4 (like, dislike, comentar, buscar comentários) | 🟢 |
| `perfil.feature` | 4 (próprio perfil, adicionar info, remover info, perfil alheio) | 🟢 |

## Testes de usabilidade — 3 participantes

| Participante | Perfil | Média (1–5) |
|-------------|--------|-------------|
| P1 | Estudante, 19 anos, usa redes sociais | **4,2** |
| P2 | Atleta amador, 22 anos | **3,8** |
| P3 | Técnico juvenil, 34 anos, leigo em apps | **3,0** |
| **Média geral** | — | **3,7 / 5** |

**Ponto mais crítico:** publicação de post (média 2,3/5) → melhoria planejada para Sprint 4.

## Roteiros de teste — 10 executados

Todos os 10 roteiros executados com status 🟢 Passou.
Evidências em `docs/testes/evidencias/`.

---

# Slide 6 — Lições Aprendidas (Retrospectiva)

## ✅ O que mantemos

- **TanStack Router** com `loader` para prefetch de dados: eliminou loading states desnecessários
- **JWT via HttpOnly cookie** em vez de localStorage: mais seguro contra XSS
- **Estado compartilhado (lifted state)** para sincronização do botão "Seguir" entre cards
- **Committers frequentes com PRs + revisão**: rastreabilidade garantida via GitHub Issues
- **Cucumber em português** (language: pt): cenários legíveis pelo time inteiro

## ⛔ O que paramos

- Retornar campos sensíveis (CPF, e-mail, telefone) nos endpoints públicos de perfil
- Testar com mocks de banco de dados (testes BDD passavam mas falhas de migração apareciam em produção → migramos para banco real no perfil test)
- Armazenar arquivos de mídia em `static/` (conflito com build do Vite → movido para `uploads/media/`)

## 🚀 O que começamos

- **RAG com Claude API** (Haiku 4.5): contexto montado dinamicamente a partir do banco, nunca expondo CPF/e-mail
- **JPQL customizado** (`findUsernamesSeguidos`) para evitar N+1 queries no feed
- **Static inner class** para `PostDetailsDTO.Metadata`: resolve visibilidade entre pacotes sem quebrar encapsulamento
- **Seed de dados** via PowerShell + `System.Net.HttpWebRequest` (curl -F falha no Windows)
- **Documentação de testes** completa: plano, roteiros, usabilidade — rastreável por requisito

---

# Slide 7 — Checklist de Entrega

| Item | Status |
|------|--------|
| Board GitHub com colunas e issues por Sprint | ✅ |
| ≥ 10 classes autorais + 4 pacotes/camadas | ✅ 73 classes · 4 camadas |
| ≥ 6 funcionalidades | ✅ 12 funcionalidades |
| Testes unitários implementados | ✅ 11 testes (JUnit 5 + Mockito) |
| ≥ 5 cenários BDD automatizados | ✅ 17 cenários (Cucumber 7) |
| Plano de teste preenchido | ✅ `docs/testes/plano-de-teste.md` |
| ≥ 5 roteiros de teste com evidências | ✅ 10 roteiros (`docs/testes/roteiros-de-teste.md`) |
| Teste de usabilidade com 3 participantes | ✅ `docs/testes/usabilidade.md` |
| Slides e demo prontos | ✅ `slides/apresentacao.md` |
| README completo e atualizado | ✅ `README.md` |

---

# Obrigado!

**Repositório:** https://github.com/vhdtv/ScoutPlay

**Backend:** http://localhost:8080  
**Frontend:** http://localhost:5173  
**IA:** http://localhost:8081
