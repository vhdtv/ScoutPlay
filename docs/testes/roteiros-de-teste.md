# Roteiros de Teste — ScoutPlay

Versão: 1.0 | Data: 2026-04-14

---

## RT-01 — Login com credenciais válidas (Fluxo principal)

**Objetivo:** Verificar que um usuário cadastrado consegue se autenticar e receber um token JWT.
**Pré-condição:** Atleta com e-mail `atleta@scoutplay.com` e senha `Teste@123` cadastrado no banco.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Abrir a tela de login | Tela de login exibida com campos e-mail e senha |
| 2 | Informar e-mail: `atleta@scoutplay.com` | Campo preenchido |
| 3 | Informar senha: `Teste@123` | Campo preenchido (senha oculta) |
| 4 | Clicar em "Entrar" | Requisição `POST /api/login` enviada |
| 5 | Verificar resposta | Status 200, `success: true`, `data.token` presente, `data.userType: "ATLETA"` |
| 6 | Verificar redirecionamento | Usuário redirecionado para o feed de atleta |

**Resultado esperado:** Login realizado, token JWT retornado.
**Evidência:** `/docs/testes/evidencias/roteiro-01-login-sucesso.png`

---

## RT-02 — Cadastro de atleta com dados válidos (Fluxo principal)

**Objetivo:** Verificar que um novo atleta consegue se cadastrar com sucesso.
**Pré-condição:** CPF e e-mail não cadastrados no sistema.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Acessar tela de cadastro de atleta | Formulário exibido |
| 2 | Preencher todos os campos obrigatórios | Campos preenchidos |
| 3 | Informar senha com ≥ 6 caracteres | Campo de senha aceito |
| 4 | Submeter o formulário | Requisição `POST /api/atletas/registro` enviada |
| 5 | Verificar resposta | Status 201, `success: true`, token JWT retornado |
| 6 | Verificar que usuário está logado | Redirecionamento para o feed |

**Resultado esperado:** Atleta cadastrado, login automático com JWT.
**Evidência:** `/docs/testes/evidencias/roteiro-02-cadastro-atleta-sucesso.png`

---

## RT-03 — Login com senha incorreta (Validação/Erro)

**Objetivo:** Verificar que o sistema rejeita credenciais inválidas.
**Pré-condição:** Atleta com e-mail `atleta@scoutplay.com` cadastrado.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Acessar tela de login | Tela de login exibida |
| 2 | Informar e-mail: `atleta@scoutplay.com` | Campo preenchido |
| 3 | Informar senha incorreta: `senhaErrada` | Campo preenchido |
| 4 | Clicar em "Entrar" | Requisição enviada |
| 5 | Verificar resposta | Status 401, `success: false`, `errorCode: "UNAUTHORIZED"` |
| 6 | Verificar mensagem na tela | "Email ou senha inválidos" exibido |

**Resultado esperado:** Login bloqueado com mensagem de erro adequada.
**Evidência:** `/docs/testes/evidencias/roteiro-03-login-senha-errada.png`

---

## RT-04 — Tentativa de cadastro com CPF já existente (Validação/Erro)

**Objetivo:** Verificar que o sistema impede duplicidade de CPF.
**Pré-condição:** Atleta com CPF `123.456.789-00` já cadastrado.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Acessar tela de cadastro | Formulário exibido |
| 2 | Preencher campos com CPF `123.456.789-00` | Campo preenchido |
| 3 | Usar e-mail diferente do existente | Campo preenchido |
| 4 | Submeter o formulário | Requisição `POST /api/atletas/registro` enviada |
| 5 | Verificar resposta | Status 409, `success: false`, mensagem contendo "CPF" |
| 6 | Verificar mensagem na tela | Erro de duplicidade exibido ao usuário |

**Resultado esperado:** Cadastro bloqueado com mensagem indicando CPF duplicado.
**Evidência:** `/docs/testes/evidencias/roteiro-04-cpf-duplicado.png`

---

## RT-05 — Upload de foto de perfil com formato inválido (Borda/Limite)

**Objetivo:** Verificar que o sistema rejeita formatos de imagem não suportados.
**Pré-condição:** Atleta autenticado com token JWT válido.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Acessar tela de edição de perfil | Tela exibida |
| 2 | Selecionar arquivo `.gif` ou `.pdf` para upload | Arquivo selecionado |
| 3 | Submeter o upload | Requisição `POST /api/atletas/{id}/foto` enviada |
| 4 | Verificar resposta | Status 400, `success: false`, mensagem sobre formato não suportado |
| 5 | Repetir com arquivo `.jpg` válido | Requisição aceita, status 200 |

**Resultado esperado:** Arquivo inválido rejeitado; arquivo válido aceito com URL retornada.
**Evidência:** `/docs/testes/evidencias/roteiro-05-upload-formato-invalido.png`

---

## RT-06 — Busca de atletas com filtros (Fluxo principal)

**Objetivo:** Verificar que olheiro consegue filtrar atletas por posição.
**Pré-condição:** Olheiro autenticado; atletas cadastrados com diferentes posições.

| Passo | Ação | Resultado Esperado |
|---|---|---|
| 1 | Acessar feed de atletas como olheiro | Lista paginada exibida |
| 2 | Filtrar por posição `"Atacante"` | Requisição `GET /api/atletas?posicao=Atacante` |
| 3 | Verificar resposta | Somente atletas com posição "Atacante" retornados |
| 4 | Verificar paginação | Campos `page`, `totalElements`, `totalPages` presentes |

**Resultado esperado:** Filtro aplicado corretamente, resultado paginado.
**Evidência:** `/docs/testes/evidencias/roteiro-06-filtro-atletas.png`
