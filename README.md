# Scout Play Application

Bem-vindo ao **Scout Play Application** 🎯 - uma plataforma inovadora que conecta jovens talentos de futebol a olheiros e clubes profissionais. Esta aplicação utiliza **Spring Boot 3**, **PostgreSQL** e oferece uma API RESTful completa para gerenciar perfis de atletas, olheiros e autenticação de usuários.
O Scoutplay (Scope) resolve a desorganização na captação e análise de atletas ⚽📊. Centralizamos players CRZ, cadastro de atletas e upload de vídeos em um só lugar, facilitando a busca por oportunidades e visibilidade em mercados e clubes.
Com login dedicado para players, o Scope permite encontrar talentos de forma rápida, estruturada e estratégica 🚀, transformando dados e vídeos em oportunidades reais.


## Integrantes

| | Nome | RA |
| --- | --- | --- |
| <img src="https://avatars.githubusercontent.com/u/101878978?v=4" style="display: inline-block; width: 64px"> | Victor Henrique Dias | 4231920004
| <img src="https://avatars.githubusercontent.com/u/130322697?v=4" style="display: inline-block; width: 64px"> | Paulo Vitor Amorim de Oliveira | 42322453
| <img src="https://avatars.githubusercontent.com/u/129996963?v=4" style="display: inline-block; width: 64px"> | Maria Clara Marques Lino | 4231924407
| <img src="https://avatars.githubusercontent.com/u/129918519?v=4" style="display: inline-block; width: 64px"> | Lucas Ferreira Andrade | 4231921505
| <img src="https://avatars.githubusercontent.com/u/134884115?v=4" style="display: inline-block; width: 64px"> | Cesar Augusto Ferreira Martins | 4231921453
| <img src="https://avatars.githubusercontent.com/u/35999467?v=4" style="display: inline-block; width: 64px"> | Caio Alves Fernandes | 4231925609
---

## 📋 Sumário
- [Integrantes](#integrantes)
- [Visão Geral](#visão-geral)
- [Pré-requisitos](#pré-requisitos)
- [Configuração Rápida](#configuração-rápida)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints Disponíveis](#endpoints-disponíveis)
- [Testando a Aplicação](#testando-a-aplicação)
- [Troubleshooting](#troubleshooting)
- [Contribuição](#contribuição)

---

## 👀 Visão Geral

A plataforma Scout Play funciona em três pilares:

- **Atletas**: Jovens jogadores podem criar perfis, fazer upload de fotos e serem descobertos por olheiros
- **Olheiros**: Profissionais que avaliam e monitoram talentos
- **Responsáveis**: Gerenciam menores de idade na plataforma

**Stack Tecnológico:**
- Backend: Java 21 + Spring Boot 3.3.4
- Banco de Dados: PostgreSQL
- Segurança: Spring Security + JWT (jjwt 0.11.5)
- Frontend: HTML5 + CSS3 + JavaScript (Vanilla) + React
- Build Tool: Maven
- Documentação: Swagger UI (springdoc-openapi 2.6.0)
- Testes: JUnit 5 + Mockito + Cucumber 7 (BDD) + JaCoCo
- IA: Python + Streamlit + Ollama (RAG + Classificação de craques)

---

## ✨ Funcionalidades

| # | Funcionalidade | Descrição |
|---|---|---|
| 1 | **Cadastro de atleta** | Jovens jogadores criam perfil com dados pessoais, posição, pé dominante e senha |
| 2 | **Cadastro de olheiro** | Profissionais de scouting se registram com clube e localização |
| 3 | **Cadastro de responsável** | Responsáveis legais de atletas menores de idade |
| 4 | **Autenticação (login)** | Login por e-mail/senha com retorno de token JWT; válido para 3 tipos de usuário |
| 5 | **Busca e filtro de atletas** | Olheiros filtram atletas por nome, posição, peso, altura, pé dominante; resultado paginado |
| 6 | **Upload de foto de perfil** | Atleta envia foto; sistema valida formato (JPG/PNG/WEBP) e redimensiona para 500×500 |
| 7 | **Gestão de vídeos** | Atleta associa, atualiza e remove vídeos do perfil |
| 8 | **Avaliações de atletas** | Olheiro registra avaliação (nota + comentário) sobre atleta |
| 9 | **Consultar perfil autenticado** | Endpoint `GET /api/me` retorna dados do usuário logado via JWT |
| 10 | **Atualização e exclusão de perfil** | Usuário edita ou remove o próprio perfil (somente o próprio — verificação de ownership) |
| 11 | **Copiloto de futebol (IA)** | Chatbot RAG com base em fundamentos técnicos e táticos do futebol, respondendo perguntas contextualizadas |
| 12 | **Classificação de craques (IA)** | Modelo que classifica potencial de atletas com base em stats de jogadores reais (Brasileirão, SofaScore, Transfermarkt) |

**Regra de negócio não-trivial:** Validação de unicidade de CPF e e-mail por tipo de usuário + validação de formato/tamanho de imagem no upload de foto de perfil + controle de ownership via JWT para PUT/DELETE.

---

## 🔧 Pré-requisitos

Certifique-se de ter os seguintes softwares instalados:

| Requisito | Versão | Download |
|-----------|--------|----------|
| **Java JDK** | 21+ | [java.com](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| **PostgreSQL** | 12+ | [postgresql.org](https://www.postgresql.org/download/) |
| **Git** | Latest | [git-scm.com](https://git-scm.com/) |
| **IDE** | Qualquer | IntelliJ IDEA, Eclipse, VS Code |

**Verificar instalação:**
```bash
java -version
mvn -version
psql --version
```

---

## ⚡ Configuração Rápida

### 1️⃣ Clone o Repositório
```bash
git clone https://github.com/vhdtv/ScoutPlay.git
cd ScoutPlay
```

### 2️⃣ Configurar Banco de Dados PostgreSQL

**Windows (PowerShell):**
```powershell
# Inicie o PostgreSQL (já deve estar em execução)
# Conecte como superusuário
psql -U postgres

# No prompt do PostgreSQL, execute:
CREATE DATABASE scoutplaydb;
CREATE USER scout_user WITH PASSWORD 'senha_segura_123';
ALTER ROLE scout_user SET client_encoding TO 'utf8';
ALTER ROLE scout_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE scout_user SET default_transaction_deferrable TO on;
ALTER ROLE scout_user SET default_time_zone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE scoutplaydb TO scout_user;
\q
```

**macOS/Linux:**
```bash
sudo -u postgres psql

# No prompt do PostgreSQL:
CREATE DATABASE scoutplaydb;
CREATE USER scout_user WITH PASSWORD 'senha_segura_123';
ALTER ROLE scout_user SET client_encoding TO 'utf8';
GRANT ALL PRIVILEGES ON DATABASE scoutplaydb TO scout_user;
\q
```

### 3️⃣ Configurar Credenciais da Aplicação

Abra `src/main/resources/application.properties` e configure:

```properties
# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/scoutplaydb
spring.datasource.username=scout_user
spring.datasource.password=senha_segura_123
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false

# Aplicação
spring.application.name=Scout Play Application
server.port=8080
```

### 4️⃣ Estrutura de Pastas Necessárias

```bash
# No diretório raiz do projeto, crie:
mkdir -p uploads/fotos_perfil

# Verifique se a pasta foi criada
ls -la uploads/
```

### 5️⃣ Build e Execução

**Primeira execução - Build completo:**
```bash
# Limpa build anterior, instala dependências e compila
mvn clean install
```

**Iniciar a aplicação:**

**Via Maven:**
```bash
mvn spring-boot:run
```

**Via IDE (IntelliJ IDEA):**
- Clique com botão direito em `ScoutPlayApplication.java`
- Selecione "Run 'ScoutPlayApplication.main()'"

**Verificar se está rodando:**
- Acesse: http://localhost:8080
- Você deve ver a página de login

---

## 📁 Estrutura do Projeto

```
ScoutPlay/
├── src/
│   ├── main/
│   │   ├── java/com/scoutplay/ScoutPlay/
│   │   │   ├── ScoutPlayApplication.java    (Classe principal)
│   │   │   ├── controllers/                 (APIs REST — camada UI)
│   │   │   ├── models/                      (Entidades JPA — camada domain)
│   │   │   ├── repositorys/                 (Acesso a dados — camada infra)
│   │   │   ├── services/                    (Lógica de negócio — camada service)
│   │   │   ├── api/dto/                     (Data Transfer Objects)
│   │   │   ├── api/response/                (ApiResponse padrão)
│   │   │   ├── config/                      (CORS, Security)
│   │   │   ├── exceptions/                  (ConflictException, ResourceNotFoundException)
│   │   │   └── security/                    (JWT Filter, Provider, SecurityUtils)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/                      (Frontend HTML/CSS/JS)
│   │       └── ia/
│   │           ├── app.py                   (Copiloto RAG — Streamlit + Ollama)
│   │           ├── arquivos/                (Base de conhecimento em PDF)
│   │           ├── brasileirao/             (Dataset Brasileirão)
│   │           ├── safa e transfermarkt/    (Datasets SofaScore + Transfermarkt)
│   │           └── notebook/               (Notebooks de análise e modelo pkl)
│   └── test/
│       ├── java/com/scoutplay/ScoutPlay/
│       │   ├── services/                    (Testes unitários — JUnit 5 + Mockito)
│       │   ├── controllers/                 (Testes de integração — MockMvc)
│       │   └── bdd/                         (Cucumber runner + step definitions)
│       └── resources/
│           ├── features/                    (Arquivos .feature Gherkin)
│           └── application-test.properties  (Config de teste com H2)
├── docs/
│   ├── requisitos/
│   │   ├── requisitos-funcionais.md
│   │   └── requisitos-nao-funcionais.md
│   └── testes/
│       ├── plano-de-teste.md
│       ├── roteiros-de-teste.md
│       ├── usabilidade.md                   (a preencher)
│       └── evidencias/                      (prints dos testes)
├── uploads/
│   └── fotos_perfil/
├── pom.xml
└── README.md
```

---

## 🔌 Endpoints Disponíveis

> A documentação interativa completa está disponível em **http://localhost:8080/swagger-ui.html** após iniciar a aplicação.

### 🏃 Atletas (`/api/atletas`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `POST` | `/registro` | ❌ | Cadastra atleta e retorna token JWT |
| `POST` | `/` | ❌ | Cria atleta com foto de perfil (multipart) |
| `GET` | `/` | ✅ | Lista atletas com filtros e paginação |
| `GET` | `/{id}` | ✅ | Busca atleta por ID |
| `PUT` | `/{id}` | ✅ Owner | Atualiza dados do atleta |
| `DELETE` | `/{id}` | ✅ Owner | Remove atleta |
| `POST` | `/{id}/foto` | ✅ Owner | Atualiza foto de perfil (multipart) |
| `GET` | `/fotos/{filename}` | ❌ | Download de foto de perfil |

**Filtros disponíveis em `GET /api/atletas`:** `nome`, `posicao`, `peso`, `altura`, `peDominante`, `anoNascimento` (paginado)

### 👁️ Olheiros (`/api/olheiros`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `POST` | `/registro` | ❌ | Cadastra olheiro e retorna token JWT |
| `POST` | `/` | ❌ | Cria olheiro |
| `GET` | `/` | ✅ | Lista olheiros (paginado) |
| `GET` | `/{id}` | ✅ | Busca olheiro por ID |
| `PUT` | `/{id}` | ✅ Owner | Atualiza dados do olheiro |
| `DELETE` | `/{id}` | ✅ Owner | Remove olheiro |

### 👪 Responsáveis (`/api/responsaveis`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `POST` | `/registro` | ❌ | Cadastra responsável e retorna token JWT |
| `POST` | `/` | ❌ | Cria responsável |
| `GET` | `/` | ✅ | Lista responsáveis (paginado) |
| `GET` | `/{id}` | ✅ | Busca responsável por ID |
| `DELETE` | `/{id}` | ✅ Owner | Remove responsável |

### 🎬 Vídeos (`/api/atletas/{atletaId}/videos`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `GET` | `/api/atletas/{atletaId}/videos` | ✅ | Lista vídeos do atleta (paginado) |
| `POST` | `/api/atletas/{atletaId}/videos` | ✅ Owner | Adiciona vídeo |
| `PUT` | `/api/videos/{videoId}` | ✅ Owner | Atualiza vídeo |
| `DELETE` | `/api/videos/{videoId}` | ✅ Owner | Remove vídeo |

### ⭐ Avaliações (`/api/atletas/{atletaId}/avaliacoes`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `GET` | `/api/atletas/{atletaId}/avaliacoes` | ✅ | Lista avaliações do atleta |
| `POST` | `/api/atletas/{atletaId}/avaliacoes` | ✅ OLHEIRO | Cria avaliação (nota + comentário + videoId opcional) |

### 👤 Usuário Genérico (`/api/usuarios`)

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `PUT` | `/api/usuarios/{id}` | ✅ Owner | Atualiza nome, telefone ou CEP |
| `DELETE` | `/api/usuarios/{id}` | ✅ Owner | Remove usuário |

### 🔐 Autenticação

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| `POST` | `/api/login` | ❌ | Autentica usuário (retorna JWT 24h) |
| `GET` | `/api/me` | ✅ | Retorna dados do usuário logado |

**Resposta do login:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "ATL-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "userType": "ATLETA",
  "nome": "João Silva",
  "email": "joao@example.com",
  "expiresIn": 86400000
}
```

---

## 🧪 Testando a Aplicação

### ▶️ Rodar todos os testes automatizados

```bash
# Rodar testes unitários (JUnit 5 + Mockito)
./mvnw test

# Rodar apenas um arquivo de teste específico
./mvnw test -Dtest=AtletaServiceTest

# Gerar relatório de cobertura JaCoCo
./mvnw test
# Abrir: target/site/jacoco/index.html

# Relatório Cucumber (BDD)
# Abrir: target/cucumber-reports/report.html
```

**Classes testadas e cobertura:**

| Classe | Tipo | Testes |
|---|---|---|
| `AtletaService` | Unitário | 10 cenários (criação, CPF/e-mail duplicado, senha, update, delete) |
| `OlheiroService` | Unitário | 8 cenários (criação, duplicidade, update, delete) |
| `LoginService` | Unitário | 6 cenários (atleta, olheiro, responsável, senha inválida, e-mail inexistente) |
| `LoginController` | Integração (MockMvc) | 3 cenários (atleta, olheiro, credenciais inválidas) |
| Autenticação | BDD (Cucumber) | 5 cenários Gherkin |
| Cadastro de atleta | BDD (Cucumber) | 4 cenários Gherkin |

### Opção 2: Usando Postman

1. Baixe [Postman](https://www.postman.com/)
2. Importe as requisições da API
3. Configure a URL base: `http://localhost:8080/api`
4. Teste os endpoints

### Opção 2: Usando cURL (Terminal)

```bash
# Listar atletas
curl http://localhost:8080/api/atletas

# Criar atleta
curl -X POST http://localhost:8080/api/atletas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste","email":"teste@test.com","idade":20}'

# Buscar atleta específico
curl http://localhost:8080/api/atletas/1
```

### Opção 3: Acessar pelo Frontend

- Navegue para: http://localhost:8080
- Acesse as páginas HTML disponíveis no diretório `static/`

### Opção 4: Validar Banco de Dados

```bash
# Conectar ao PostgreSQL
psql -U scout_user -d scoutplaydb

# Listar tabelas
\dt

# Consultar atletas
SELECT * FROM atleta;

# Sair
\q
```

---

## ⚠️ Troubleshooting

### ❌ Erro: "Connection to localhost:5432 refused"

**Solução:**
```bash
# Verificar se PostgreSQL está rodando (Windows)
Get-Service postgresql-x64-*

# Iniciar PostgreSQL (se não estiver rodando)
# Windows: Services > PostgreSQL Database Server > Iniciar
# macOS: brew services start postgresql
# Linux: sudo systemctl start postgresql
```

### ❌ Erro: "Permission denied" na pasta uploads

**Solução:**
```bash
# Dar permissão de leitura/escrita
chmod -R 755 uploads/

# Ou no Windows PowerShell:
icacls uploads /grant:r Everyone:(OI)(CI)F /t
```

### ❌ Erro: "Failed to bind to port 8080"

**Solução:**
```bash
# Matar processo na porta 8080 (Windows PowerShell)
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force

# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Ou configure outra porta em application.properties:
# server.port=8081
```

### ❌ Erro: Maven não encontrado

**Solução:**
```bash
# Instalar Maven via package manager
# Windows (Chocolatey): choco install maven
# macOS (Homebrew): brew install maven
# Linux (apt): sudo apt install maven

# Verificar instalação
mvn -version
```

---

## 📊 Variáveis de Ambiente

### Desenvolvimento (opcionais)

**Windows PowerShell:**
```powershell
[Environment]::SetEnvironmentVariable("JWT_SECRET", "seu_secret_aqui", "User")
[Environment]::SetEnvironmentVariable("JWT_EXPIRATION", "86400000", "User")
```

**macOS/Linux:**
```bash
export JWT_SECRET=seu_secret_aqui
export JWT_EXPIRATION=86400000
```

### Produção (obrigatórias — `application-prod.properties`)

| Variável | Descrição | Exemplo |
|----------|-----------|--------|
| `DB_URL` | URL JDBC do banco | `jdbc:postgresql://host:5432/scoutplaydb` |
| `DB_USERNAME` | Usuário do banco | `scout_user` |
| `DB_PASSWORD` | Senha do banco | — |
| `JWT_SECRET` | Chave HMAC-SHA256 para JWT (**obrigatória**) | — |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h) |
| `CORS_ORIGINS` | Origens permitidas | `https://scoutplay.com` |

> **Atenção:** Em produção, `JWT_SECRET` não possui valor padrão — a aplicação não sobe sem ela. Use variáveis de ambiente ou secrets do seu provedor de nuvem.

---

## 🖥️ Páginas Frontend

O frontend estático está disponível em `src/main/resources/static/` e pode ser acessado em `http://localhost:8080`:

| Página | URL | Descrição |
|--------|-----|-----------|
| `index.html` | `/` | Home da plataforma |
| `login.html` | `/login.html` | Login |
| `esqueceuSenha.html` | `/esqueceuSenha.html` | Recuperação de senha |
| `formularioAtletaMaiorDeIdade.html` | `/formularioAtletaMaiorDeIdade.html` | Cadastro de atleta (18+) |
| `formularioAtletaMenorDeIdade.html` | `/formularioAtletaMenorDeIdade.html` | Cadastro de atleta menor |
| `formularioOlheiro.html` | `/formularioOlheiro.html` | Cadastro de olheiro |
| `feedAtleta.html` | `/feedAtleta.html` | Feed do atleta |
| `feedOlheiro.html` | `/feedOlheiro.html` | Feed do olheiro (busca e filtros) |
| `editarPerfilAtleta.html` | `/editarPerfilAtleta.html` | Edição de perfil do atleta |
| `editarPerfilOlheiro.html` | `/editarPerfilOlheiro.html` | Edição de perfil do olheiro |

---

## 🤖 Módulo de Inteligência Artificial

O ScoutPlay conta com dois recursos de IA, localizados em `src/main/resources/ia/`:

### Copiloto de Futebol (RAG)

Chatbot baseado em Retrieval-Augmented Generation que responde perguntas sobre fundamentos técnicos e táticos do futebol com base em material especializado.

**Tecnologias:** Python, Streamlit, Ollama (`embeddinggemma`, `phi3:mini`)

**Para executar:**
```bash
# Instale as dependências Python
pip install streamlit ollama numpy

# Certifique-se de ter o Ollama rodando localmente
ollama serve

# Inicie a aplicação
streamlit run src/main/resources/ia/app.py
```

> **Atenção:** Antes de rodar, atualize o caminho `BASE_RAG` em `app.py` para o caminho local do arquivo `notebook/base_rag.pkl`.

### Classificação de Craques

Modelo de ML que classifica o potencial de atletas com base em estatísticas reais de jogadores do Brasileirão, SofaScore e Transfermarkt.

**Dados utilizados:**
- `brasileirao/database.csv` — estatísticas do Brasileirão
- `safa e transfermarkt/partidas_sofascore.csv` — dados de partidas
- `safa e transfermarkt/players_tm.csv` — perfis Transfermarkt
- `safa e transfermarkt/market_value.csv` — valores de mercado

**Exploração:** Ver notebooks em `ia/notebook/analise_bases.ipynb` e `ia/notebook/nb.ipynb`.

---

## �🤝 Contribuição

Contribuições são bem-vindas! Siga os passos:

1. **Fork** o repositório
2. **Crie uma branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. **Abra um Pull Request**

**Guidelines:**
- Mantenha o código limpo e bem documentado
- Adicione testes para novas funcionalidades
- Siga o padrão de código existente

---

## 📝 Licença

Este projeto está licenciado sob os termos da [MIT License](LICENSE).

---

## 📞 Suporte

Encontrou um problema? 
- Abra uma **Issue** no repositório
- Verifique a seção [Troubleshooting](#troubleshooting)
- Consulte a documentação do [Spring Boot](https://spring.io/projects/spring-boot)

---

**Última atualização:** Abril de 2026  
**Versão:** 1.2.0
