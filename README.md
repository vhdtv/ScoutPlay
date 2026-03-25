# Scout Play Application

Bem-vindo ao **Scout Play Application** 🎯 - uma plataforma inovadora que conecta jovens talentos de futebol a olheiros e clubes profissionais. Esta aplicação utiliza **Spring Boot 3**, **PostgreSQL** e oferece uma API RESTful completa para gerenciar perfis de atletas, olheiros e autenticação de usuários.
O Scoutplay (Scope) resolve a desorganização na captação e análise de atletas ⚽📊. Centralizamos players CRZ, cadastro de atletas e upload de vídeos em um só lugar, facilitando a busca por oportunidades e visibilidade em mercados e clubes.
Com login dedicado para players, o Scope permite encontrar talentos de forma rápida, estruturada e estratégica 🚀, transformando dados e vídeos em oportunidades reais.


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
- Banco de Dados: PostgreSQL 42.7.4
- Frontend: HTML5 + CSS3 + JavaScript (Vanilla)
- Build Tool: Maven

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
│   │   │   ├── controllers/                 (APIs REST)
│   │   │   │   ├── AtletaController.java
│   │   │   │   ├── OlheiroController.java
│   │   │   │   ├── ResponsavelController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── LoginController.java
│   │   │   ├── models/                      (Entidades JPA)
│   │   │   │   ├── Atleta.java
│   │   │   │   ├── Olheiro.java
│   │   │   │   └── ...
│   │   │   ├── repositorys/                 (Acesso a dados)
│   │   │   ├── services/                    (Lógica de negócio)
│   │   │   └── DTO/                         (Data Transfer Objects)
│   │   └── resources/
│   │       ├── application.properties       (Configurações)
│   │       └── static/                      (Frontend - HTML/CSS/JS)
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── style.css
│   │           └── script*.js
│   └── test/                                (Testes unitários)
├── uploads/
│   └── fotos_perfil/                        (Fotos de perfil dos usuários)
├── pom.xml                                  (Dependências Maven)
└── README.md                                (Este arquivo)
```

---

## 🔌 Endpoints Disponíveis

### 🏃 Atletas (`/api/atletas`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/` | Cria novo atleta com foto de perfil |
| `GET` | `/` | Lista todos atletas (com filtros) |
| `GET` | `/{id}` | Busca atleta por ID |
| `PUT` | `/{id}` | Atualiza dados do atleta |
| `DELETE` | `/{id}` | Remove atleta |
| `GET` | `/fotos/{filename}` | Download de foto de perfil |

**Exemplo de requisição (POST):**
```bash
curl -X POST http://localhost:8080/api/atletas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "idade": 18,
    "posicao": "Atacante",
    "clube": "FC Example"
  }'
```

### 👁️ Olheiros (`/api/olheiros`)
*Funcionalidades do sistema*
O Scoutplay é uma plataforma completa para gestão de olheiros e atletas ⚽📊, trazendo organização, controle e escalabilidade para sua operação.

Funcionalidades principais:
✨ Cadastro de novos olheiros
📋 Listagem completa e organizada
🔍 Busca rápida por ID
✏️ Atualização de dados em tempo real
🗑️ Remoção de registros

Além disso, o sistema garante mais agilidade na gestão, centralização das informações e suporte ao crescimento da operação 🚀

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/` | Cria novo olheiro |
| `GET` | `/` | Lista todos olheiros |
| `GET` | `/{id}` | Busca olheiro por ID |
| `PUT` | `/{id}` | Atualiza dados do olheiro |
| `DELETE` | `/{id}` | Remove olheiro |

### 🔐 Autenticação (`/api/auth` ou `/login`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/login` | Autentica usuário |
| `GET` | `/` | Página de login (HTML) |

---

## 🧪 Testando a Aplicação

### Opção 1: Usando Postman

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

## 📊 Variáveis de Ambiente (Opcional)

Para melhor segurança, configure variáveis de ambiente:

**Windows PowerShell:**
```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/scoutplaydb", "User")
[Environment]::SetEnvironmentVariable("DB_USER", "scout_user", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "senha_segura_123", "User")
```

**macOS/Linux:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/scoutplaydb
export DB_USER=scout_user
export DB_PASSWORD=senha_segura_123
```

---

## 🤝 Contribuição

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

**Última atualização:** Fevereiro de 2026  
**Versão:** 1.0.0
