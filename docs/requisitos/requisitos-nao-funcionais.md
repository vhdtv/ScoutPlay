# Requisitos Não Funcionais — ScoutPlay

**Atualizado em:** 2026-06-16

## RNF01 — Segurança

- Senhas armazenadas com BCrypt (Spring Security, fator de custo 10)
- Autenticacao via JWT armazenado em cookie HttpOnly (`access_token`) — token expira em 24 horas
- Cookie configurado com `SameSite=Lax` (bloqueia CSRF em requisicoes cross-site POST/PUT/DELETE)
- Flag `secure` do cookie configuravel via variavel de ambiente `COOKIE_SECURE` (default `false` em dev, `true` em producao com HTTPS)
- JWT secret validado na inicializacao: obrigatorio e minimo de 32 caracteres (`@PostConstruct` em `JwtProperties`)
- JWT secret de producao nunca commitado — carregado via variavel de ambiente `JWT_SECRET`
- URL do backend configuravel via `VITE_BACKEND_URL` no frontend (nunca hardcoded)
- Recuperacao de senha: nova senha temporaria gerada com `SecureRandom` (10 chars alfanumericos) e enviada por email
- Rotas publicas: `/api/login`, `/api/logout`, `/api/signup`, `/api/forgot-password`, `/api/media/*`, `/api/avatar/*`
- Todas as demais rotas exigem token JWT valido no cookie `access_token`

## RNF02 — Desempenho

- Feed de posts retornado de forma paginada (`page` + `size`, max recomendado 10 por pagina)
- Paginacao executada no banco de dados via `PageRequest` do Spring Data (nao em memoria)
- Imagens servidas diretamente via `MediaController` sem buffering em memoria desnecessario

## RNF03 — Escalabilidade

- Arquitetura em camadas: `controllers` → `services` → `repositories` → `models`
- Banco de dados PostgreSQL em producao
- Modulo de IA como microsservico independente (FastAPI, porta 8081) — pode ser escalado separadamente
- Configuracao por perfis Spring: `dev` e `prod` (via `application-{profile}.properties`)

## RNF04 — Portabilidade

- Backend executavel via Maven Wrapper (`./mvnw`) sem instalacao local do Maven
- Frontend executavel via npm (`npm run dev`) na porta 3000
- Servico de IA executavel via `python -m uvicorn api:app --host 0.0.0.0 --port 8081`
- Variaveis de ambiente documentadas em `front-end/.env.example`

## RNF05 — Manutenibilidade

- Codigo organizado em 4 camadas: `models` (14 classes de dominio), `services` (16 classes de regra de negocio), `repositories` (persistencia JPA), `controllers` (5 classes de API REST)
- Respostas padronizadas via `ApiResponse<T>` com campos `success`, `data`, `message`, `timestamp`, `errorCode`
- IDs publicos expostos pela API sao sempre UUIDs (`aliasId`) — o `id` interno (INT) nunca e exposto

## RNF06 — Usabilidade da API

- API RESTful com verbos HTTP semanticos (GET, POST, PUT, DELETE, PATCH)
- Respostas em JSON com estrutura consistente
- Mensagens de erro descritivas e codigos HTTP adequados (400, 401, 404, 409, 500)
- CORS configurado para aceitar requisicoes de `http://localhost:3000` (dev) e dominio de producao via `CORS_ORIGINS`

## RNF07 — Qualidade de Codigo

- Cobertura minima de 80% nas classes de servico (verificada via JaCoCo)
- Testes unitarios com JUnit 5 + Mockito
- Testes BDD com Cucumber

## RNF08 — Tamanho de Arquivos

- Upload de imagens e videos limitado a 10 MB por arquivo
- Formatos de imagem aceitos: JPG, JPEG, PNG, WEBP
- Formatos de video aceitos: MP4, MOV
- Arquivos salvos localmente em `uploads/media/` (posts) e `uploads/avatars/` (fotos de perfil)

## RNF09 — Disponibilidade do Modulo IA

- Copiloto ScoutPlay requer Ollama rodando localmente com os modelos `phi3:mini` e `embeddinggemma:latest`
- FastAPI sobe na porta 8081 e carrega 395 chunks de RAG a partir do PDF de fundamentos taticos na inicializacao
- Se o servico de IA estiver indisponivel, o restante da aplicacao continua funcionando normalmente
