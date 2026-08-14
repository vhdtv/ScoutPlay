# ScoutPlay 2.0

Plataforma web para aproximar atletas de futebol, olheiros e responsáveis. A versão ativa está na branch `ScoutPlay2.0`; materiais e decisões antigas preservadas ficam na branch `historico`.

> Estado atual: a aplicação possui uma base funcional para cadastro, autenticação, perfis, feed, interações, busca de atletas, shortlist, avaliações, vínculo de responsável e recuperação de senha. Antes de receber usuários reais ainda é necessário concluir a infraestrutura de produção, as migrações de banco, o armazenamento externo de mídia, observabilidade e as políticas de privacidade/moderação.

## Funcionalidades

- Cadastro e login de atleta, olheiro e responsável.
- Sessão JWT em cookie `HttpOnly`, autorização por papel e logout.
- Perfil público, edição do próprio perfil, avatar e vínculo de responsável para atleta menor.
- Feed com imagens/vídeos, comentários, respostas e curtidas.
- Busca e filtros de atletas.
- Avaliação e shortlist exclusivas para olheiros.
- Recuperação de senha por link temporário, de uso único.
- Assistente de IA com contexto limitado de atletas, mediante autenticação.
- Swagger/OpenAPI em `/swagger-ui.html`.

## Tecnologias

| Camada | Tecnologias |
|---|---|
| Frontend | React 19, TypeScript, TanStack Router, Vite 7, Tailwind CSS 4 |
| Backend | Java 17, Spring Boot 3.5, Spring Security, JPA/Hibernate |
| Dados | PostgreSQL em execução normal; H2 nos testes |
| Qualidade | JUnit 5, MockMvc, Cucumber, Vitest, Biome e GitHub Actions |

## Executar localmente

Pré-requisitos: Java 17, Node.js 22, npm e PostgreSQL.

### 1. Banco e backend

Crie um banco vazio e defina as variáveis abaixo. `JWT_SECRET` deve ser uma chave aleatória com pelo menos 32 bytes.

```bash
export DB_URL='jdbc:postgresql://localhost:5432/scoutplaydb'
export DB_USERNAME='postgres'
export DB_PASSWORD='sua-senha-local'
export JWT_SECRET='substitua-por-uma-chave-aleatoria-de-32-bytes-ou-mais'
export CORS_ALLOWED_ORIGINS='http://localhost:3000'

cd back-end
./mvnw spring-boot:run
```

O backend inicia em `http://localhost:8080`. Os diretórios `uploads/media` e `uploads/avatars` são criados automaticamente no primeiro envio.

### 2. Frontend

```bash
cd front-end
cp .env.example .env
npm ci
npm run dev
```

O frontend inicia em `http://localhost:3000`. Para apontar para outra API, altere `VITE_BACKEND_URL` no arquivo `.env`.

### 3. E-mail de recuperação

Em desenvolvimento, use um SMTP local como Mailpit na porta 1025 ou configure:

```bash
export MAIL_HOST='smtp.exemplo.com'
export MAIL_PORT='587'
export MAIL_USERNAME='conta@exemplo.com'
export MAIL_PASSWORD='senha-do-provedor'
export MAIL_SMTP_AUTH='true'
export MAIL_STARTTLS='true'
export FRONTEND_URL='http://localhost:3000'
```

Nunca registre senhas, tokens ou segredos em arquivos versionados.

### Dados demonstrativos opcionais

O seeder não roda normalmente. Para carregar a demonstração, habilite explicitamente o perfil `demo` e forneça uma senha forte para todas as contas de exemplo:

```bash
export SPRING_PROFILES_ACTIVE='demo'
export SEED_PASSWORD='uma-senha-de-demonstracao-com-12-caracteres-ou-mais'
./mvnw spring-boot:run
```

Não use o perfil `demo` em produção.

## Testes e build

```bash
# Backend: testes unitários, BDD, funcionais e cobertura
cd back-end
./mvnw verify

# Frontend: qualidade, testes e build de produção
cd ../front-end
npm ci
npm run check
npm test
npm run build
```

O workflow `.github/workflows/ci.yml` executa essas verificações em pushes para `ScoutPlay2.0` e em pull requests.

Os testes funcionais do backend cobrem:

- bloqueio de rotas privadas para visitantes;
- cadastro, login, cookie e sessão;
- autorização de avaliação/shortlist para olheiro;
- upload e leitura de mídia;
- recuperação de senha por token de uso único;
- vínculo de responsável com atleta menor sem exposição do identificador interno.

## Configuração de produção

Use o perfil `prod` e forneça, no mínimo:

| Variável | Finalidade |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL de produção |
| `JWT_SECRET` | Assinatura dos tokens |
| `CORS_ALLOWED_ORIGINS` | Domínio HTTPS do frontend |
| `FRONTEND_URL` | Links enviados por e-mail |
| `MAIL_*` | Serviço SMTP |
| `ANTHROPIC_API_KEY` ou `IA_URL` | Provedor opcional da IA |

Ative com `SPRING_PROFILES_ACTIVE=prod`. Esse perfil força cookie seguro, respeita cabeçalhos do proxy e reduz informações de erro. Use HTTPS no proxy/reverse proxy.

## API principal

| Área | Rotas principais | Acesso |
|---|---|---|
| Autenticação | `POST /api/signup`, `/api/login`, `/api/logout` | público |
| Recuperação | `POST /api/forgot-password`, `/api/reset-password` | público |
| Sessão | `GET /api/session` | autenticado |
| Perfis | `GET /api/user`, `GET /api/atletas` | público |
| Edição e vínculo | `/api/user/**`, `/api/profile-detail` | autenticado/papel específico |
| Feed e mídia | `GET /api/post/`, `/api/post/{id}`, `/api/media/**`, `/api/avatar/**` | público |
| Publicação e interações | mutações em `/api/post/**` | autenticado |
| Avaliações e shortlist | `/api/user/{username}/avaliar`, `/shortlist` | olheiro |
| IA | `POST /api/ia/prompt` | autenticado |

Consulte o contrato completo no Swagger da instância em execução.

## Organização do repositório

```text
ScoutPlay/
├── back-end/       API Spring Boot, testes e recursos da IA
├── front-end/      aplicação React/Vite
├── docs/           documentação acadêmica e integrações
├── slides/         material da apresentação
└── .github/        integração contínua
```

## Branches e histórico

- `ScoutPlay2.0`: produto atual e branch entregue no trabalho.
- `historico`: documentação consolidada e commits antigos que ainda têm valor de consulta.
- `main`: versão acadêmica original e branch padrão do repositório.

Branches experimentais incorporadas ao histórico não devem ser usadas como base de desenvolvimento.

## Planejamento

O trabalho pendente é acompanhado nas [Issues do repositório](https://github.com/vhdtv/ScoutPlay/issues). Consulte especialmente os itens de [build e qualidade](https://github.com/vhdtv/ScoutPlay/issues/124) e [persistência/performance](https://github.com/vhdtv/ScoutPlay/issues/125). A ordem recomendada para disponibilização pública está documentada na branch [`historico`](https://github.com/vhdtv/ScoutPlay/tree/historico/docs/historico).

## Equipe acadêmica

Victor Henrique Dias, Paulo Vitor Amorim de Oliveira, Maria Clara Marques Lino, Lucas Ferreira Andrade, Cesar Augusto Ferreira Martins e Caio Alves Fernandes.

## Licença e uso

Este repositório nasceu como projeto acadêmico. Antes de uso comercial ou abertura ao público, defina formalmente a licença, os Termos de Uso, a Política de Privacidade e os procedimentos de moderação e atendimento a titulares de dados.
