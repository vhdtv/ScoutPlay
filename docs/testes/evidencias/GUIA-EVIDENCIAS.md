# Guia de Coleta de Evidências e Conteúdo para Slides — ScoutPlay

Este guia tem dois objetivos:
1. **Evidências:** passo a passo para rodar a aplicação e tirar os prints dos testes
2. **Slides:** todo o conteúdo necessário para montar a apresentação em PowerPoint

---

## PARTE 1 — Conteúdo para o Slide PowerPoint

Use esta seção para montar os slides. Copie os textos, adapte o visual e inclua os prints que você tirar nos testes.

---

### Slide 1 — Capa

**Título:** ScoutPlay  
**Subtítulo:** A rede social do futebol de base  
**Disciplina:** Gestão e Qualidade de Software  

**Integrantes:**
| Nome | RA |
|------|----|
| Victor Henrique Dias | 4231920004 |
| Paulo Vitor Amorim de Oliveira | 42322453 |
| Maria Clara Marques Lino | 4231924407 |
| Lucas Ferreira Andrade | 4231921505 |
| Cesar Augusto Ferreira Martins | 4231921453 |
| Caio Alves Fernandes | 4231925609 |

---

### Slide 2 — O Problema

**Título:** Por que o ScoutPlay existe?

O processo de descoberta de talentos no futebol brasileiro é desorganizado e inacessível:

- Jovens atletas não têm visibilidade sem acesso a peneiras e clubes grandes
- Olheiros perdem tempo visitando treinos sem dados prévios dos atletas
- Não existe uma plataforma centralizada conectando **atleta → olheiro → clube**
- Informações ficam dispersas em grupos de WhatsApp, planilhas e e-mails

**Público-alvo:**
| Perfil | O que precisa |
|--------|---------------|
| Atleta (14–25 anos) | Criar perfil, publicar vídeos/fotos, ser encontrado |
| Olheiro / Scout | Buscar atletas por posição, avaliar e acompanhar |
| Responsável legal | Gerenciar conta de atleta menor de idade |

---

### Slide 3 — A Solução

**Título:** ScoutPlay — o que a plataforma faz

> Uma plataforma que centraliza o scouting, conectando jovens talentos a oportunidades reais.

**12 funcionalidades implementadas:**

| # | Funcionalidade | Descrição resumida |
|---|----------------|--------------------|
| 1 | Cadastro de Atleta | Perfil com nome, CPF, posição, pé dominante, data de nascimento |
| 2 | Cadastro de Olheiro | Conta de profissional de scouting com acesso a busca de atletas |
| 3 | Cadastro de Responsável | Conta para responsáveis legais de atletas menores |
| 4 | Autenticação (Login/Logout) | Login seguro com JWT armazenado em cookie HttpOnly |
| 5 | Feed Paginado de Posts | Timeline de publicações com filtros por posição e tipo de conta |
| 6 | Criação e Exclusão de Post | Upload de imagem/vídeo com título; exclusão apenas pelo autor |
| 7 | Like, Dislike e Comentário | Interações em posts com contador em tempo real |
| 8 | Seguir / Deixar de Seguir | Sistema de seguidores sincronizado entre cards e perfil |
| 9 | Visualização e Edição de Perfil | Perfil público com dados esportivos, posts e contadores |
| 10 | Upload de Foto de Perfil | Avatar atualizado e refletido em toda a aplicação |
| 11 | Copiloto de IA (Chatbot RAG) | Assistente que responde perguntas sobre atletas cadastrados |
| 12 | Recuperação de Senha | Redefinição de senha por e-mail com token seguro |

**Regras de negócio não-triviais (diferencial técnico):**
- CPF e e-mail únicos por sistema (retorna erro 409 se duplicado)
- Ownership via JWT: só o dono edita/exclui seu conteúdo
- Senha sempre codificada com bcrypt antes de salvar
- IA nunca expõe CPF, e-mail ou telefone dos atletas
- JWT em cookie HttpOnly: protegido contra roubo via JavaScript (XSS)

---

### Slide 4 — Arquitetura

**Título:** Como o sistema foi construído

**Stack tecnológico:**
| Camada | Tecnologia |
|--------|-----------|
| Back-end | Java 21 + Spring Boot 3.3.4 |
| Banco de dados | PostgreSQL |
| Segurança | Spring Security + JWT (cookie HttpOnly) |
| Front-end | React 19 + Vite + TanStack Router |
| Estilos | TailwindCSS 4 |
| Inteligência Artificial | Python FastAPI + RAG + Claude API |
| Testes | JUnit 5 + Mockito + Cucumber 7 (BDD) |
| Build | Maven (back-end) · npm (front-end) |

**Arquitetura em 4 camadas (back-end Java):**
```
controllers/   →  Camada UI      — 5 controllers, 24 endpoints REST
services/      →  Camada Service — 13 serviços, regras de negócio
repositories/  →  Camada Infra   — 12 repositórios JPA
models/        →  Camada Domain  — 14 entidades JPA (tabelas do banco)
```

**Números do projeto:**
| Item | Quantidade |
|------|-----------|
| Classes Java autorais | 73 |
| Camadas de arquitetura | 4 (+ config, security, exceptions) |
| Endpoints REST | 24+ |
| Telas no front-end | 11 rotas |

---

### Slide 5 — Testes

**Título:** Qualidade garantida por 3 tipos de teste

**Testes Unitários — JUnit 5 + Mockito (11 testes)**

Todos os 11 testes passando na classe `UsuarioService`:
- Cadastrar atleta com dados válidos ✅
- Lançar exceção para senha nula ✅
- Lançar exceção para senha em branco ✅
- ConflictException para CPF duplicado ✅
- ConflictException para e-mail duplicado ✅
- Codificar senha antes de salvar (bcrypt) ✅
- Retornar usuário por ID ✅
- Retornar null quando não encontrado ✅
- Buscar perfil por username ✅
- Delegar adição de detalhe de perfil ✅
- Delegar remoção de detalhe de perfil ✅

**Testes BDD — Cucumber 7 (17 cenários em português)**

| Feature | Cenários | Resultado |
|---------|----------|-----------|
| autenticacao.feature | 5 | ✅ Todos passando |
| cadastro_atleta.feature | 4 | ✅ Todos passando |
| posts.feature | 4 | ✅ Todos passando |
| perfil.feature | 4 | ✅ Todos passando |

**Testes Manuais — 10 Roteiros Executados**

RT-01 Login com credenciais válidas · RT-02 Cadastro de atleta · RT-03 Login com senha errada · RT-04 CPF duplicado · RT-05 Criar e excluir post · RT-06 Like e unlike · RT-07 Seguir/deixar de seguir · RT-08 Consultar perfil alheio · RT-09 Copiloto IA · RT-10 Upload de avatar

**Teste de Usabilidade — 3 Participantes**

| Participante | Perfil | Nota média (1–5) |
|-------------|--------|-----------------|
| P1 | Estudante, 19 anos | 4,2 |
| P2 | Atleta amador, 22 anos | 3,8 |
| P3 | Técnico juvenil, 34 anos | 3,0 |
| **Média geral** | | **3,7 / 5** |

Ponto mais crítico: publicação de post (média 2,3/5) → melhoria identificada para próxima Sprint.

---

### Slide 6 — Lições Aprendidas (Retrospectiva)

**Título:** O que aprendemos ao longo do projeto

**O que mantemos:**
- TanStack Router com `loader` para prefetch: eliminou loading states desnecessários
- JWT via cookie HttpOnly em vez de localStorage: mais seguro contra XSS
- Estado compartilhado (lifted state) para sincronizar o botão "Seguir" entre cards
- Commits frequentes com PRs e revisão: rastreabilidade garantida via Issues
- Cucumber em português: cenários legíveis por todo o time

**O que paramos:**
- Retornar dados sensíveis (CPF, e-mail) nos endpoints públicos de perfil
- Testar com mocks de banco (BDD passava mas falhas de migração surgiam em produção)
- Guardar arquivos em `static/` (conflito com build do Vite)

**O que começamos:**
- RAG com Claude API: contexto montado dinamicamente sem expor dados privados
- JPQL customizado para evitar N+1 queries no feed
- Documentação de testes completa e rastreável por requisito

---

### Slide 7 — Checklist de Entrega

| Requisito | Mínimo exigido | Entregue |
|-----------|---------------|----------|
| Board GitHub (Kanban + Issues) | Colunas mínimas | ✅ 4 colunas + 20 issues |
| Classes autorais | 10 | ✅ 73 classes |
| Camadas de arquitetura | 4 | ✅ 4 camadas principais |
| Funcionalidades | 6 | ✅ 12 funcionalidades |
| Testes unitários | ≥80% cobertura serviços | ✅ 11 testes JUnit 5 |
| Cenários BDD | 5 | ✅ 17 cenários Cucumber |
| Plano de teste | 1 documento completo | ✅ docs/testes/plano-de-teste.md |
| Roteiros de teste | 5 com evidências | ✅ 10 roteiros executados |
| Teste de usabilidade | 3 participantes | ✅ 3 participantes, média 3,7/5 |
| Slides | Contexto, solução, arquitetura, demo, testes, retro | ✅ |
| README | Todos os campos obrigatórios | ✅ 611 linhas |

**Repositório:** github.com/vhdtv/ScoutPlay  
**Branch:** ScoutPlay2.0

---

### Dicas para o PowerPoint

- **Cores sugeridas:** verde escuro `#1a6b3c` e branco (identidade de futebol) ou azul `#1e3a5f`
- **Fonte:** use uma sem serifa limpa (Calibri, Arial, Montserrat)
- **Onde colocar os prints:** nos slides 5 (testes) e em um slide extra de "Demo" — use os prints que você tirar nos roteiros
- **Slide de Demo:** pode ser um slide só com capturas de tela lado a lado mostrando: feed, perfil, criação de post e copiloto IA
- **Tempo sugerido de apresentação:** ~15 min — 2 min por slide

---

## PARTE 2 — Como rodar a aplicação e tirar os prints

Esta guia é para a pessoa responsável por executar os roteiros de teste e salvar os prints como evidências.

---

## 1. O que você vai precisar instalar

Baixe e instale na ordem abaixo:

### Java 21
- Download: https://www.oracle.com/java/technologies/downloads/#java21
- Escolha: **Windows x64 Installer**
- Durante a instalação, marque a opção "Set JAVA_HOME variable"
- Verificar: abra o Prompt de Comando e rode `java -version` — deve aparecer `java version "21..."`

### PostgreSQL 16
- Download: https://www.postgresql.org/download/windows/
- Durante a instalação:
  - Senha do superusuário (`postgres`): **postgres**
  - Porta: **5432** (padrão, não mude)
  - Locale: deixe o padrão
- Verificar: abra o **pgAdmin 4** (instalado junto) e veja se conecta

### Node.js 20
- Download: https://nodejs.org/en — escolha a versão **LTS**
- Instale com as opções padrão
- Verificar: `node -v` deve mostrar `v20...`

### Git
- Download: https://git-scm.com/download/win
- Instale com as opções padrão
- Verificar: `git --version`

---

## 2. Baixar o projeto

Abra o **Prompt de Comando** (ou PowerShell) e rode:

```bash
git clone https://github.com/vhdtv/ScoutPlay.git
cd ScoutPlay
git checkout ScoutPlay2.0
```

Agora você tem o projeto na pasta `ScoutPlay/` na sua máquina.

---

## 3. Configurar o banco de dados

### 3.1 — Abrir o pgAdmin 4
- Procure "pgAdmin 4" no menu Iniciar e abra
- Conecte no servidor local com a senha **postgres**

### 3.2 — Criar o banco de dados
- No painel esquerdo, clique com o botão direito em **Databases → Create → Database**
- Nome: `scoutplaydb`
- Clique em **Save**

### 3.3 — Verificar as credenciais
O projeto já vem configurado para usar:
- **Usuário:** `postgres`
- **Senha:** `postgres`
- **Banco:** `scoutplaydb`
- **Porta:** `5432`

Se a sua instalação do PostgreSQL usou uma senha diferente para o usuário `postgres`, edite o arquivo:

```
ScoutPlay/back-end/src/main/resources/application-dev.properties
```

Linha a alterar:
```properties
spring.datasource.password=postgres   ← troque pelo que você definiu
```

---

## 4. Rodar o back-end (API)

Abra o **Prompt de Comando**, navegue até a pasta do projeto e rode:

```bash
cd ScoutPlay/back-end
.\mvnw.cmd spring-boot:run
```

> Na primeira vez vai demorar alguns minutos pois o Maven vai baixar as dependências.

**Como saber que funcionou:**  
Aguarde aparecer no terminal:

```
Started ScoutPlayApplication in X.XXX seconds
```

A API estará disponível em `http://localhost:8080`

> **Atenção:** mantenha esta janela do terminal aberta durante todos os testes. Fechar o terminal encerra a API.

---

## 5. Rodar o front-end (interface visual)

Abra **outra janela** do Prompt de Comando e rode:

```bash
cd ScoutPlay/front-end
npm install
npm run dev
```

**Como saber que funcionou:**  
Deve aparecer:

```
  ➜  Local:   http://localhost:5173/
```

Abra o navegador e acesse `http://localhost:5173`

> **Atenção:** mantenha esta janela aberta também durante os testes.

---

## 6. Criar um usuário de teste

Antes de executar os roteiros você precisa ter um usuário cadastrado no sistema.

1. Acesse `http://localhost:5173/signup` no navegador
2. Preencha o formulário:
   - **Tipo de conta:** Atleta
   - **Nome:** João Teste
   - **E-mail:** joao.teste@email.com
   - **CPF:** 111.222.333-44
   - **Senha:** Teste@123
   - **Data de nascimento:** 15/03/2003
3. Clique em **Cadastrar**
4. Se funcionar, você será redirecionado para o feed — anote o e-mail e senha usados

> Crie um segundo usuário também (para os testes de seguir e visualizar perfil alheio):
> - **E-mail:** atleta@scoutplay.com · **Senha:** atleta@scoutplay.com · demais dados quaisquer

---

## 7. Executar os roteiros e tirar os prints

Para cada roteiro abaixo, siga os passos e ao final **tire um print da tela** mostrando o resultado.

**Como tirar print no Windows:**
- Tecla `Print Screen` (copia para área de transferência) → cole no Paint → salve como PNG
- Ou use `Windows + Shift + S` para selecionar a área e salvar diretamente

---

### RT-01 — Login com credenciais válidas

1. Acesse `http://localhost:5173/login`
2. Preencha com e-mail e senha do usuário que você criou no passo 6
3. Clique em **Entrar**
4. **Print:** tire o print mostrando o feed aberto com o nome/avatar do usuário no canto superior direito

**Salvar como:** `roteiro-01-login-sucesso.png`

---

### RT-02 — Cadastro de atleta com dados válidos

1. Acesse `http://localhost:5173/signup`
2. Selecione tipo **Atleta** e preencha todos os campos com dados diferentes do usuário que você já criou (use um e-mail e CPF novos)
3. Clique em **Cadastrar**
4. **Print:** tire o print mostrando o feed aberto após o cadastro bem-sucedido

**Salvar como:** `roteiro-02-cadastro-atleta.png`

---

### RT-03 — Login com senha incorreta

1. Acesse `http://localhost:5173/login`
2. Digite o e-mail de um usuário existente (ex.: `joao.teste@email.com`)
3. Digite uma senha **errada** (ex.: `SenhaErrada999`)
4. Clique em **Entrar**
5. **Print:** tire o print mostrando a mensagem de erro na tela (usuário não é redirecionado)

**Salvar como:** `roteiro-03-login-senha-errada.png`

---

### RT-04 — Cadastro com CPF já existente

1. Acesse `http://localhost:5173/signup`
2. Preencha com um CPF que **já está cadastrado** (use o CPF do usuário criado no passo 6: `111.222.333-44`)
3. Use um e-mail diferente do já cadastrado
4. Clique em **Cadastrar**
5. **Print:** tire o print mostrando a mensagem de erro/conflito na tela

**Salvar como:** `roteiro-04-cpf-duplicado.png`

---

### RT-05 — Criar e excluir post

1. Faça login e acesse o feed (`http://localhost:5173/feed`)
2. Clique no botão **Criar Post**
3. Selecione uma imagem (JPG ou PNG qualquer, máximo 10 MB)
4. Preencha um título (ex.: "Treino de terça") e clique em **Postar**
5. **Print:** tire o print mostrando o post recém-criado no topo do feed
6. Acesse seu perfil (`http://localhost:5173/user/seuusername`)
7. Clique no ícone de exclusão do post
8. **Print:** tire outro print mostrando o perfil após a exclusão (post sumiu)

**Salvar como:** `roteiro-05-criar-excluir-post.png`  
*(pode juntar os dois prints numa imagem ou salvar como dois arquivos: `-05a` e `-05b`)*

---

### RT-06 — Dar like e unlike em post

1. Acesse o feed (`http://localhost:5173/feed`)
2. Anote o número de likes de algum post visível
3. Clique no botão de **like** nesse post
4. **Print:** tire o print mostrando o contador incrementado e o botão ativo (azul/preenchido)
5. Clique novamente no botão (unlike)
6. **Print:** print mostrando o contador voltando ao valor original e o botão inativo

**Salvar como:** `roteiro-06-like-unlike.png`

---

### RT-07 — Seguir e deixar de seguir usuário

1. No feed, encontre um post de outro usuário (não o seu)
2. Clique no botão **Seguir** no card do post
3. **Print:** print mostrando o botão mudando para "Seguindo"
4. Clique no avatar ou nome do autor para ir ao perfil dele
5. **Print:** print do perfil mostrando o contador de seguidores atualizado
6. Clique em **Seguindo** para deixar de seguir
7. **Print:** print mostrando botão voltando para "Seguir"

**Salvar como:** `roteiro-07-seguir-usuario.png`

---

### RT-08 — Consultar perfil de outro usuário

1. No feed, clique no nome ou avatar de um post de outro usuário
2. Você será redirecionado para `/user/{username}` do autor
3. Verifique que aparecem: foto, nome, @username, tipo de conta, posts
4. Verifique que **não aparece** o ícone de lixeira (exclusão) nos posts (pois é perfil alheio)
5. **Print:** tire o print mostrando o perfil completo de outro usuário sem o botão de excluir

**Salvar como:** `roteiro-08-perfil-usuario.png`

---

### RT-09 — Copiloto IA responde pergunta

> **Pré-requisito:** o módulo de IA precisa estar rodando. Se não estiver configurado, pule este roteiro e anote "Bloqueado — serviço IA não disponível no ambiente".

Se o módulo de IA estiver rodando em `http://localhost:8081`:

1. Acesse o feed (`http://localhost:5173/feed`)
2. Localize o painel "O Especialista" no canto inferior direito
3. Digite a pergunta: `Quais atletas estão cadastrados como centroavante?`
4. Clique em enviar e aguarde a resposta
5. **Print:** tire o print mostrando a resposta do copiloto no painel

**Salvar como:** `roteiro-09-copiloto-ia.png`

---

### RT-10 — Upload de avatar pelo usuário

1. Acesse `http://localhost:5173/settings` (esteja logado)
2. Localize o campo de upload de foto de perfil
3. Clique e selecione um arquivo PNG ou JPG (qualquer foto, máximo 10 MB)
4. Confirme/salve
5. **Print:** print mostrando o novo avatar exibido no header da página

**Salvar como:** `roteiro-10-upload-avatar.png`

---

## 8. Onde salvar os prints

Todos os arquivos de print devem ser salvos em:

```
ScoutPlay/docs/testes/evidencias/
```

Nomes exatos dos arquivos (conforme cada roteiro acima):

```
evidencias/
├── roteiro-01-login-sucesso.png
├── roteiro-02-cadastro-atleta.png
├── roteiro-03-login-senha-errada.png
├── roteiro-04-cpf-duplicado.png
├── roteiro-05-criar-excluir-post.png
├── roteiro-06-like-unlike.png
├── roteiro-07-seguir-usuario.png
├── roteiro-08-perfil-usuario.png
├── roteiro-09-copiloto-ia.png
└── roteiro-10-upload-avatar.png
```

---

## 9. Após salvar os prints — enviar para o repositório

Abra o Prompt de Comando na pasta `ScoutPlay/` e rode:

```bash
git checkout ScoutPlay2.0
git add docs/testes/evidencias/
git commit -m "test: adiciona evidencias dos roteiros RT-01 a RT-10"
git push origin ScoutPlay2.0
```

---

## Problemas comuns

**"A API não responde / erro ao fazer login"**
- Verifique se o terminal do back-end ainda está aberto e mostrando "Started ScoutPlayApplication"
- Se tiver fechado, rode `.\mvnw.cmd spring-boot:run` novamente dentro de `ScoutPlay/back-end`

**"Erro de conexão com banco de dados"**
- Abra o pgAdmin e verifique se o servidor PostgreSQL está conectado (ícone verde)
- Se não estiver, clique com o botão direito → Connect Server
- Verifique se o banco `scoutplaydb` existe

**"npm: command not found" ou similar**
- Feche e reabra o Prompt de Comando após instalar o Node.js
- Se ainda não funcionar: procure "Node.js command prompt" no menu Iniciar e use esse terminal

**"Porta 8080 em uso"**
- Feche outros programas que possam estar usando essa porta
- Ou abra o Gerenciador de Tarefas → procure processos Java → finalize-os

**O front-end abre mas não carrega dados**
- Confirme que o back-end está rodando na porta 8080
- Verifique se não há bloqueio de firewall (desative temporariamente o Windows Defender Firewall para redes privadas se necessário)
