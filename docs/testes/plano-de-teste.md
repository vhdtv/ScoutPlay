# Plano de Teste — ScoutPlay

## 📋 Convenções e status

| Item | Padrão |
|------|--------|
| 🏷️ ID do teste | UT-XX · BDD-XX · RT-XX |
| 📌 Status | 🟡 Planejado · 🔵 Em execução · 🟢 Passou · 🔴 Falhou · ⚫ Bloqueado |
| ⭐ Prioridade | 🔥 Alta · ⚠️ Média · 🟦 Baixa |
| 📎 Evidência | Print / log / link do PR ou Issue |

---

## 🆔 Identificação e contexto

| Campo | Valor |
|-------|-------|
| 🧩 Nome do projeto | ScoutPlay |
| 📝 Objetivo do sistema | Plataforma que conecta atletas jovens a olheiros e clubes de futebol, permitindo cadastro, publicação de mídia, interações e busca por IA |
| 🎯 Público-alvo | Atletas em formação (14–25 anos), olheiros, responsáveis legais e representantes de clubes |
| 💻 Plataforma/Tipo | API REST (Spring Boot) + SPA (React/Vite) |
| 🔗 Repositório | https://github.com/vhdtv/ScoutPlay |
| 👥 Time/Grupo | Victor Dias · Paulo Vitor · Maria Clara · Lucas Andrade · Cesar Martins · Caio Fernandes |

---

## 🎯 Objetivo do teste

| Item | Descrição |
|------|-----------|
| ✅ Objetivo geral | Garantir que as funcionalidades críticas do ScoutPlay atendam aos requisitos funcionais definidos, com foco em regras de negócio (testes unitários), fluxos completos de usuário (BDD) e validações de interface (roteiros manuais + usabilidade) |
| 📊 Metas de cobertura | ≥ 80% nas classes de serviço (UsuarioService, PostService, AuthService) · ≥ 17 cenários BDD · ≥ 6 roteiros manuais executados · 3 participantes em teste de usabilidade |

---

## 📦 Escopo

| Categoria | ✅ Em escopo | 🚫 Fora de escopo |
|-----------|-------------|-----------------|
| 🧩 Funcionalidades | Cadastro (atleta/olheiro), autenticação JWT, feed de posts, criação/exclusão de posts, like/dislike, comentários, seguir/deixar de seguir, upload de avatar, edição de perfil, copiloto IA | Gestão de avaliações de atletas (endpoint existe mas sem UI completa), recuperação de senha por e-mail real |
| 🧠 Regras de negócio | Unicidade de CPF/e-mail, senha obrigatória, ownership de post, controle de seguidos via JWT | Regras de cotas de upload, limites de tamanho de vídeo |
| 🔌 Integrações | API REST interna, PostgreSQL, FastAPI IA local | Envio de e-mail externo, CDN de arquivos, OAuth externo |
| 🗃️ Dados | Seed de 5 usuários (3 atletas, 2 olheiros) + posts gerados para testes | Dados reais de produção |
| 🧑‍💻 Não-funcionais | Usabilidade (3 participantes), resposta < 3 s nos endpoints principais | Carga (k6/Gatling), pentest de infraestrutura |

---

## 🧰 Ambiente e ferramentas

| Item | Especificação |
|------|--------------|
| 🖥️ SO | Windows 11 Enterprise 10.0.26100 |
| ☕ Linguagem/Runtime | Java 21 · Node 20 · Python 3.11 |
| 🧑‍💻 IDE | VS Code + extensão Spring Boot |
| 🧱 Build | Maven 3 (back-end/mvnw) · npm (front-end) |
| ✅ Framework de testes unitários | JUnit 5 + Mockito 5 |
| 🥒 BDD | Cucumber 7 (JVM) com step definitions em Java |
| 🤖 CI | Nenhum CI automatizado configurado |
| 🗄️ Banco/Dados | PostgreSQL 18 · banco `scoutplaydb` · schema gerado via `spring.jpa.hibernate.ddl-auto=update` |

---

## 🧪 Estratégia de testes (por tipo)

| Tipo de teste | 🎯 Objetivo | 📌 Escopo | 🛠️ Ferramenta | 👤 Responsável | 📎 Saída/Evidência |
|--------------|------------|----------|--------------|--------------|-----------------|
| ✅ Unitário | Validar regras de negócio em isolamento via mocks | UsuarioService (11 testes) | JUnit 5 + Mockito | Equipe back-end | `back-end/target/site/jacoco/index.html` |
| 🥒 BDD | Validar fluxos completos descritos em linguagem natural | Autenticação, cadastro, posts, perfil (17 cenários) | Cucumber 7 + SpringBootTest | Equipe back-end | `back-end/target/cucumber-reports/report.html` |
| 📝 Manual | Validar fluxos pela UI como usuário real | Login, cadastro, feed, like, seguir, upload | Roteiros executados manualmente | Toda a equipe | `/docs/testes/evidencias/*.png` |
| 🧑‍💻 Usabilidade | Avaliar facilidade de uso com participantes reais | Cadastro, login, perfil, publicação de post, like | Sessão presencial com observação direta | Victor / Maria Clara | `/docs/testes/usabilidade.md` |

---

## 🧷 Rastreabilidade (Requisitos x Testes)

| ID Req | Requisito/Funcionalidade | ⭐ Prioridade | 🔗 Fonte | 🧪 IDs de testes | 📌 Status |
|--------|------------------------|-------------|---------|----------------|---------|
| RF-01 | Cadastro de atleta | 🔥 Alta | Issue #1 | UT-01, UT-02, UT-03, UT-04, UT-05, UT-06, BDD-06, BDD-07, BDD-08, BDD-09, RT-02 | 🟢 Executado |
| RF-02 | Autenticação (login/logout) | 🔥 Alta | Issue #2 | UT-07, UT-08, BDD-01, BDD-02, BDD-03, BDD-04, BDD-05, RT-01, RT-03 | 🟢 Executado |
| RF-03 | Visualização de perfil | 🔥 Alta | Issue #3 | UT-09, BDD-14, BDD-17, RT-04 | 🟢 Executado |
| RF-04 | Criação e exclusão de post | 🔥 Alta | Issue #4 | BDD-10, RT-05 | 🟢 Executado |
| RF-05 | Feed paginado de posts | ⚠️ Média | Issue #5 | BDD-11, RT-06 | 🟢 Executado |
| RF-06 | Interações (like/dislike/comentário) | ⚠️ Média | Issue #6 | BDD-12, BDD-13, RT-07 | 🟢 Executado |
| RF-07 | Seguir / deixar de seguir usuário | ⚠️ Média | Issue #7 | BDD-15, RT-08 | 🟢 Executado |
| RF-08 | Upload de foto de perfil | ⚠️ Média | Issue #8 | UT-10, UT-11, RT-09 | 🟢 Executado |
| RF-09 | Copiloto IA (chatbot RAG) | 🟦 Baixa | Issue #9 | RT-10 | 🟢 Executado |
| RF-10 | Edição parcial de perfil (settings) | ⚠️ Média | Issue #10 | BDD-16, RT-11 | 🟡 Planejado |

---

## 🧾 Casos de teste planejados (resumo)

| ID | 🧪 Tipo | 🏷️ Título | 🔐 Pré-condição | 📥 Entrada | ✅ Resultado esperado | ⭐ Prioridade | 🤖 Automatizado? |
|----|--------|----------|----------------|-----------|----------------------|-------------|----------------|
| UT-01 | ✅ Unitário | Cadastrar atleta com dados válidos | Nenhum usuário com mesmo CPF/e-mail | Usuário completo com senha | Usuario salvo, tipoContaService chamado | 🔥 Alta | Sim |
| UT-02 | ✅ Unitário | Lançar exceção quando senha nula | — | senha = null | IllegalArgumentException "Senha é obrigatória" | 🔥 Alta | Sim |
| UT-03 | ✅ Unitário | Lançar exceção quando senha em branco | — | senha = "   " | IllegalArgumentException "Senha é obrigatória" | 🔥 Alta | Sim |
| UT-04 | ✅ Unitário | Lançar ConflictException para CPF duplicado | CPF já existe no repositório | CPF repetido | ConflictException, saveAndFlush nunca chamado | 🔥 Alta | Sim |
| UT-05 | ✅ Unitário | Lançar ConflictException para e-mail duplicado | E-mail já existe | E-mail repetido | ConflictException, saveAndFlush nunca chamado | 🔥 Alta | Sim |
| UT-06 | ✅ Unitário | Codificar senha antes de salvar | Dados válidos | senha "senha123" | passwordEncoder.encode chamado | 🔥 Alta | Sim |
| UT-07 | ✅ Unitário | Retornar usuário por aliasId válido | Usuário presente no repositório mock | UUID válido | Usuário retornado não-nulo | ⚠️ Média | Sim |
| UT-08 | ✅ Unitário | Retornar null quando usuário não encontrado | Repositório mock vazio | UUID inexistente | null retornado sem exceção | ⚠️ Média | Sim |
| UT-09 | ✅ Unitário | Buscar dados do perfil por username | Usuário com tipo de conta | username válido | UserProfileSummary com nome correto | ⚠️ Média | Sim |
| UT-10 | ✅ Unitário | Delegar adição de informação ao DetalhePerfilService | Usuário existente | chave "POSICAO", valor "Atacante" | detalhePerfilService.adicionarInformacao chamado | ⚠️ Média | Sim |
| UT-11 | ✅ Unitário | Delegar remoção de informação ao DetalhePerfilService | Usuário existente | chave "POSICAO" | detalhePerfilService.removerInformacao chamado | ⚠️ Média | Sim |
| BDD-01 | 🥒 BDD | Login com sucesso como atleta | Atleta cadastrado | email + senha válidos | Status 200, JWT retornado | 🔥 Alta | Sim |
| BDD-02 | 🥒 BDD | Login com sucesso como olheiro | Olheiro cadastrado | email + senha válidos | Status 200, JWT retornado | 🔥 Alta | Sim |
| BDD-03 | 🥒 BDD | Login com senha errada falha | Usuário cadastrado | email válido + senha errada | Exceção de autenticação | 🔥 Alta | Sim |
| BDD-04 | 🥒 BDD | Login com e-mail inexistente falha | — | email não cadastrado | Exceção de autenticação | 🔥 Alta | Sim |
| BDD-05 | 🥒 BDD | Login com campos em branco falha | — | email vazio + senha vazia | Exceção de autenticação | ⚠️ Média | Sim |
| BDD-06 | 🥒 BDD | Cadastro de atleta com sucesso | CPF e e-mail únicos | Dados completos do atleta | Usuário criado | 🔥 Alta | Sim |
| BDD-07 | 🥒 BDD | Falha ao cadastrar atleta com CPF duplicado | Atleta com CPF já existe | Mesmo CPF | ConflictException | 🔥 Alta | Sim |
| BDD-08 | 🥒 BDD | Falha ao cadastrar atleta com e-mail duplicado | Atleta com e-mail já existe | Mesmo e-mail | ConflictException | 🔥 Alta | Sim |
| BDD-09 | 🥒 BDD | Falha ao cadastrar atleta sem senha | — | senha = null | IllegalArgumentException | 🔥 Alta | Sim |
| BDD-10 | 🥒 BDD | Dar like em um post | Post existe, usuário autenticado | postId válido | Like registrado | ⚠️ Média | Sim |
| BDD-11 | 🥒 BDD | Dar dislike em um post | Post com like prévio | postId válido | Like removido | ⚠️ Média | Sim |
| BDD-12 | 🥒 BDD | Comentar em um post | Post existe, usuário autenticado | postId + texto | Comentário salvo | ⚠️ Média | Sim |
| BDD-13 | 🥒 BDD | Obter comentários de um post | Post com comentários | postId | Lista de comentários retornada | ⚠️ Média | Sim |
| BDD-14 | 🥒 BDD | Visualizar próprio perfil | Usuário autenticado | username próprio | souEu = true no perfil | ⚠️ Média | Sim |
| BDD-15 | 🥒 BDD | Adicionar informação ao perfil | Usuário autenticado | chave + valor | DetalhePerfil atualizado | ⚠️ Média | Sim |
| BDD-16 | 🥒 BDD | Remover informação do perfil | Usuário com DetalhePerfil | chave existente | Campo removido do JSONB | ⚠️ Média | Sim |
| BDD-17 | 🥒 BDD | Visualizar perfil de outro usuário | Outro usuário cadastrado | username alheio | souEu = false | 🟦 Baixa | Sim |
| RT-01 | 📝 Manual | Login com credenciais válidas | Usuário cadastrado no banco | email + senha corretos | Redirecionamento para feed | 🔥 Alta | Não |
| RT-02 | 📝 Manual | Cadastro de atleta com dados válidos | CPF e e-mail únicos | Formulário completo | Conta criada, login automático | 🔥 Alta | Não |
| RT-03 | 📝 Manual | Login com senha incorreta | Usuário cadastrado | senha errada | Mensagem de erro exibida | 🔥 Alta | Não |
| RT-04 | 📝 Manual | Visualização de perfil de outro usuário | Dois usuários cadastrados | Clicar no avatar do autor | Perfil do autor exibido | ⚠️ Média | Não |
| RT-05 | 📝 Manual | Criar e excluir post | Usuário autenticado | Arquivo de imagem + título | Post aparece e é removido do perfil | ⚠️ Média | Não |
| RT-06 | 📝 Manual | Dar like e unlike em post | Usuário autenticado, post no feed | Clicar no botão like | Contador incrementa e decrementa | ⚠️ Média | Não |

---

## 🗃️ Dados de teste

| ID | 🧺 Conjunto | 📝 Descrição | 🧪 Como criar | 📍 Onde armazenar | 💡 Observações |
|----|------------|-------------|--------------|-----------------|--------------|
| DT-01 | Atletas seed | 3 atletas com foto de perfil (Gabriel Mendonça, Ana Souza, Lucas Silva) | Endpoint `POST /api/signup` com tipoConta=ATLETA | PostgreSQL `scoutplaydb`, tabela `t_usuario` | Criados via script PowerShell com avatares de randomuser.me |
| DT-02 | Olheiros seed | 2 olheiros (Carlos Oliveira, Marina Costa) | Endpoint `POST /api/signup` com tipoConta=OLHEIRO | PostgreSQL `scoutplaydb` | Idem acima |
| DT-03 | Posts seed | 8 posts com imagens públicas | Endpoint `POST /api/post/` (multipart) | `back-end/uploads/media/` | Imagens de picsum.photos; avatar de randomuser.me |
| DT-04 | Usuário de teste BDD | Atleta `testbdd@scoutplay.com` / senha `senha@123` | Criado pelo step `@Dado` do Cucumber via `UsuarioService` em banco H2 | H2 em memória (perfil `test`) | Banco zerado a cada execução dos testes BDD |
| DT-05 | Usuário principal | Atleta `victor` (administrador dos testes manuais) | Cadastrado manualmente via UI | PostgreSQL `scoutplaydb` | Credenciais do desenvolvedor principal |
