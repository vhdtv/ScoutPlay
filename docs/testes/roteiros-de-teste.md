# Roteiros de Teste — ScoutPlay

**Versão:** 2.0 | **Data:** 2026-06-12 | **Sprint:** 3

---

## 📋 Convenções

| Item | Padrão |
|------|--------|
| 🏷️ ID | RT-XX |
| 📌 Status | 🟡 Planejado · 🔵 Em execução · 🟢 Passou · 🔴 Falhou · ⚫ Bloqueado |
| ⭐ Prioridade | 🔥 Alta · ⚠️ Média · 🟦 Baixa |

---

## RT-01 — Login com credenciais válidas

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🔥 Alta |
| 🧩 Funcionalidade | Autenticação (RF-02) |
| 🎯 Objetivo | Verificar que um usuário cadastrado consegue se autenticar e acessar o feed |
| 🔐 Pré-condição | Backend rodando em `localhost:8080`; usuário `victor` cadastrado no banco |
| 🧪 Dados de teste | E-mail: e-mail do usuário victor · Senha: senha cadastrada |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Abrir `http://localhost:5173/login` | Tela de login exibida com campos e-mail e senha |
| 2 | Preencher e-mail e senha corretos | Campos preenchidos |
| 3 | Clicar em "Entrar" | Requisição `POST /api/login` enviada com cookie `access_token` |
| 4 | Verificar redirecionamento | Usuário redirecionado para `/feed` |
| 5 | Verificar o header | Avatar e nome do usuário exibidos no canto superior direito |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Login realizado, redirecionado para o feed com avatar e nome corretos | 🟢 Passou | `docs/testes/evidencias/roteiro-01-login-sucesso.png` |

---

## RT-02 — Cadastro de atleta com dados válidos

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🔥 Alta |
| 🧩 Funcionalidade | Cadastro de atleta (RF-01) |
| 🎯 Objetivo | Verificar que um novo atleta consegue se cadastrar com sucesso |
| 🔐 Pré-condição | CPF e e-mail não cadastrados no sistema |
| 🧪 Dados de teste | Nome: João Teste · CPF: 111.222.333-44 · E-mail: joao.teste@email.com · Senha: Teste@123 · Data nasc.: 15/03/2003 |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Acessar `http://localhost:5173/signup` | Formulário de cadastro exibido |
| 2 | Selecionar tipo de conta "Atleta" | Campos específicos de atleta aparecem |
| 3 | Preencher todos os campos obrigatórios | Campos preenchidos sem erro |
| 4 | Clicar em "Cadastrar" | Requisição `POST /api/signup` enviada |
| 5 | Verificar resposta | Status 200, cookie `access_token` definido |
| 6 | Verificar redirecionamento | Usuário logado, redirecionado para `/feed` |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Atleta cadastrado, cookie JWT gerado, feed exibido | 🟢 Passou | `docs/testes/evidencias/roteiro-02-cadastro-atleta.png` |

---

## RT-03 — Login com senha incorreta

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🔥 Alta |
| 🧩 Funcionalidade | Autenticação — validação de erro (RF-02) |
| 🎯 Objetivo | Verificar que o sistema rejeita credenciais inválidas com mensagem adequada |
| 🔐 Pré-condição | Usuário `atleta@scoutplay.com` cadastrado |
| 🧪 Dados de teste | E-mail: atleta@scoutplay.com · Senha: senhaErrada999 |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Acessar tela de login | Tela exibida normalmente |
| 2 | Informar e-mail válido e senha incorreta | Campos preenchidos |
| 3 | Clicar em "Entrar" | Requisição `POST /api/login` enviada |
| 4 | Verificar resposta da API | Status 401, body `{"success": false}` |
| 5 | Verificar UI | Mensagem de erro exibida na tela; usuário permanece na tela de login |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Erro 401 retornado, mensagem exibida, usuário não redirecionado | 🟢 Passou | `docs/testes/evidencias/roteiro-03-login-senha-errada.png` |

---

## RT-04 — Cadastro com CPF já existente

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🔥 Alta |
| 🧩 Funcionalidade | Cadastro de atleta — validação de unicidade (RF-01) |
| 🎯 Objetivo | Verificar que o sistema impede cadastro com CPF duplicado |
| 🔐 Pré-condição | Usuário cadastrado com CPF `123.456.789-09` já existe no banco |
| 🧪 Dados de teste | Nome: Outro Atleta · CPF: 123.456.789-09 (duplicado) · E-mail diferente: outro@email.com |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Acessar formulário de cadastro | Formulário exibido |
| 2 | Preencher campos com CPF já utilizado | Campos preenchidos |
| 3 | Usar e-mail diferente do cadastro original | Campo e-mail aceito |
| 4 | Submeter o formulário | Requisição `POST /api/signup` enviada |
| 5 | Verificar resposta | Status 409 (Conflict), mensagem contendo "CPF" |
| 6 | Verificar UI | Usuário não é criado; formulário exibe mensagem de conflito |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Erro 409 retornado, CPF duplicado bloqueado | 🟢 Passou | `docs/testes/evidencias/roteiro-04-cpf-duplicado.png` |

---

## RT-05 — Criar e excluir post

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | ⚠️ Média |
| 🧩 Funcionalidade | Criação e exclusão de post (RF-04) |
| 🎯 Objetivo | Verificar que o usuário consegue publicar uma imagem/vídeo e removê-la do perfil |
| 🔐 Pré-condição | Usuário autenticado (cookie `access_token` válido) |
| 🧪 Dados de teste | Arquivo JPG ≤ 10 MB · Título: "Treino de terça" · Descrição: opcional |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Acessar `/feed` autenticado | Feed exibido; botão "Criar Post" visível |
| 2 | Clicar em "Criar Post" | Dialog de criação aberto |
| 3 | Selecionar arquivo de imagem e preencher título | Preview da imagem exibido |
| 4 | Clicar em "Postar" | Requisição multipart `POST /api/post/` enviada; status 200 |
| 5 | Verificar feed atualizado | Novo post aparece no topo do feed |
| 6 | Acessar perfil próprio `/user/{username}` | Post visível na grade de publicações |
| 7 | Passar mouse sobre o post e clicar no ícone de exclusão | Confirmação implícita (botão vermelho) |
| 8 | Aguardar remoção | Post desaparece da grade sem recarregar a página |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Post criado e removido com sucesso; UI atualiza em tempo real | 🟢 Passou | `docs/testes/evidencias/roteiro-05-criar-excluir-post.png` |

---

## RT-06 — Dar like e unlike em post

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | ⚠️ Média |
| 🧩 Funcionalidade | Interações (like/dislike) — RF-06 |
| 🎯 Objetivo | Verificar que o contador de likes incrementa e decrementa corretamente |
| 🔐 Pré-condição | Usuário autenticado; pelo menos um post de outro usuário visível no feed |
| 🧪 Dados de teste | Post existente de qualquer usuário no feed |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Abrir `/feed` | Posts visíveis com botão de like e contador |
| 2 | Anotar o contador atual de likes de um post | Valor N |
| 3 | Clicar no botão de like | Requisição `POST /api/post/{id}/like`; contador passa para N+1; botão muda para estado ativo (azul) |
| 4 | Clicar novamente no botão (unlike) | Requisição `POST /api/post/{id}/dislike`; contador volta para N; botão retorna ao estado inativo |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Like e unlike funcionam; contador e cor do botão atualizam sem recarregar | 🟢 Passou | `docs/testes/evidencias/roteiro-06-like-unlike.png` |

---

## RT-07 — Seguir e deixar de seguir usuário

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | ⚠️ Média |
| 🧩 Funcionalidade | Seguir / deixar de seguir (RF-07) |
| 🎯 Objetivo | Verificar que o estado "Seguindo" é sincronizado em todos os cards do mesmo autor e no perfil |
| 🔐 Pré-condição | Usuário autenticado; outro usuário com pelo menos dois posts no feed |
| 🧪 Dados de teste | Qualquer usuário com 2+ posts no feed |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Abrir `/feed`, localizar dois posts do mesmo autor | Ambos exibem botão "Seguir" |
| 2 | Clicar em "Seguir" no primeiro card | Botão muda para "Seguindo" em **ambos** os cards do mesmo autor |
| 3 | Acessar o perfil do autor | Contador de seguidores exibido corretamente (não zero) |
| 4 | Clicar em "Seguindo" no perfil para deixar de seguir | Botão muda para "Seguir"; contador decrementado |
| 5 | Voltar ao feed | Ambos os cards do autor mostram "Seguir" novamente |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Estado de seguindo sincronizado entre cards e perfil; contadores corretos | 🟢 Passou | `docs/testes/evidencias/roteiro-07-seguir-usuario.png` |

---

## RT-08 — Consultar perfil de outro usuário

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | ⚠️ Média |
| 🧩 Funcionalidade | Visualização de perfil (RF-03) |
| 🎯 Objetivo | Verificar exibição dos dados, posts e estatísticas de seguidores de outro usuário |
| 🔐 Pré-condição | Usuário autenticado; outro usuário com posts e dados de perfil cadastrados |
| 🧪 Dados de teste | Qualquer atleta seed (ex.: `gabriel_mendonca`) |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | No feed, clicar no avatar ou nome do autor de um post | Navegação para `/user/{username}` |
| 2 | Verificar cabeçalho do perfil | Nome, foto, tipo de conta e @username exibidos |
| 3 | Verificar dados esportivos | Posição, altura, peso e pé dominante visíveis (se preenchidos) |
| 4 | Verificar publicações | Grade de posts exibida |
| 5 | Verificar que **não** aparece botão de excluir posts | Ícone de lixeira não deve ser visível (perfil alheio) |
| 6 | Verificar contadores | "Seguidores" e "Seguindo" exibidos com valores corretos |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Perfil exibido com dados corretos; botão de exclusão ausente; contadores presentes | 🟢 Passou | `docs/testes/evidencias/roteiro-08-perfil-usuario.png` |

---

## RT-09 — Copiloto IA responde pergunta sobre atletas

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🟦 Baixa |
| 🧩 Funcionalidade | Copiloto IA (RF-09) |
| 🎯 Objetivo | Verificar que o chatbot responde perguntas sobre atletas cadastrados |
| 🔐 Pré-condição | Serviço IA rodando em `localhost:8081` (uvicorn); usuário autenticado no feed |
| 🧪 Dados de teste | Pergunta: "Quais atletas estão cadastrados como centroavante?" |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Abrir `/feed` | Painel do copiloto "O Especialista" visível no canto inferior direito |
| 2 | Digitar a pergunta no campo de texto | Texto inserido corretamente |
| 3 | Clicar no botão enviar | Requisição `POST /api/ia/prompt` enviada |
| 4 | Aguardar resposta | Resposta textual exibida no painel, referenciando atletas reais do banco |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Resposta recebida com dados dos atletas cadastrados | 🟢 Passou | `docs/testes/evidencias/roteiro-09-copiloto-ia.png` |

---

## RT-10 — Upload de avatar pelo usuário

| Campo | Valor |
|-------|-------|
| ⭐ Prioridade | 🟦 Baixa |
| 🧩 Funcionalidade | Upload de foto de perfil (RF-08) |
| 🎯 Objetivo | Verificar que o usuário consegue atualizar sua foto de perfil |
| 🔐 Pré-condição | Usuário autenticado; acesso à tela de configurações |
| 🧪 Dados de teste | Arquivo PNG ou JPEG ≤ 10 MB |

**Passos:**

| # | Ação | Resultado Esperado |
|---|------|-------------------|
| 1 | Acessar `/settings` | Página de configurações exibida |
| 2 | Clicar no campo de upload de avatar | Seletor de arquivo aberto |
| 3 | Selecionar arquivo PNG/JPEG válido | Preview exibido |
| 4 | Salvar | Requisição `POST /api/user/avatar` enviada com sucesso |
| 5 | Verificar header | Novo avatar exibido imediatamente no header |

| 🧾 Resultado obtido | 📌 Status | 📎 Evidência |
|--------------------|---------|------------|
| Avatar atualizado; exibido no header e no perfil do usuário | 🟢 Passou | `docs/testes/evidencias/roteiro-10-upload-avatar.png` |
