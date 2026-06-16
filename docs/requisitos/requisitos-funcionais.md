# Requisitos Funcionais — ScoutPlay

**Versão:** 2.0 | **Atualizado em:** 2026-06-12

---

## RF-01 — Cadastro de Atleta

**Descrição:** O sistema deve permitir que jovens atletas se cadastrem informando nome, sobrenome, e-mail, CPF, senha, data de nascimento e tipo de conta `ATLETA`.

**Regras:**
- CPF deve ser único no sistema → retorna 409 Conflict se duplicado
- E-mail deve ser único no sistema → retorna 409 Conflict se duplicado
- Senha é obrigatória; campos nulos ou em branco lançam `IllegalArgumentException`
- Senha armazenada com codificação bcrypt antes de persistir

**Endpoint:** `POST /api/signup`

---

## RF-02 — Cadastro de Olheiro

**Descrição:** O sistema deve permitir que olheiros (profissionais de scouting) se cadastrem com tipo de conta `OLHEIRO`.

**Regras:**
- Mesmas validações de unicidade de RF-01
- Conta categorizada como `OLHEIRO` via `XUsuarioTipoConta`

**Endpoint:** `POST /api/signup`

---

## RF-03 — Cadastro de Responsável

**Descrição:** O sistema deve permitir o cadastro de responsáveis legais de atletas menores de idade com tipo de conta `RESPONSAVEL`.

**Regras:**
- CPF e e-mail únicos no sistema
- Senha obrigatória

**Endpoint:** `POST /api/signup`

---

## RF-04 — Autenticação (Login / Logout)

**Descrição:** O sistema deve autenticar usuários via e-mail e senha, retornando um token JWT armazenado em cookie HttpOnly.

**Regras:**
- Credenciais inválidas retornam 401 Unauthorized
- Token JWT com validade configurável via `JWT_EXPIRATION` (padrão: 24 h)
- Logout limpa o cookie `access_token`

**Endpoints:** `POST /api/login` · `POST /api/logout`

---

## RF-05 — Feed Paginado de Posts

**Descrição:** Qualquer usuário pode visualizar um feed paginado de publicações de todos os atletas.

**Regras:**
- Resultado paginado (padrão: 10 por página), ordenado por data decrescente
- Cada post exibe: autor, título, mídia (imagem/vídeo), contagem de likes, indicação de "deu like" e "segue conta"
- Filtros opcionais na UI: posição do atleta e tipo de conta

**Endpoint:** `GET /api/post/?page={n}`

---

## RF-06 — Criação e Exclusão de Post

**Descrição:** Usuários autenticados podem publicar imagens ou vídeos com título e descrição, e excluir suas próprias publicações.

**Regras:**
- Criação via upload multipart (arquivo + título + descrição)
- Tipos aceitos: JPG, WEBP, PNG (imagem) · outros (vídeo)
- Exclusão lógica (`ativo = false`) — somente o autor pode excluir (ownership via JWT)

**Endpoints:** `POST /api/post/` · `DELETE /api/post/{postId}`

---

## RF-07 — Interações: Like, Dislike e Comentário

**Descrição:** Usuários autenticados podem dar like/dislike em posts e adicionar comentários.

**Regras:**
- Like idempotente: dar like duas vezes no mesmo post remove o like
- Comentários são persistidos com autor e timestamp

**Endpoints:** `POST /api/post/{id}/like` · `POST /api/post/{id}/dislike` · `POST /api/post/{id}/comment` · `GET /api/post/{id}/comments`

---

## RF-08 — Seguir / Deixar de Seguir Usuário

**Descrição:** Usuários autenticados podem seguir e deixar de seguir outros usuários.

**Regras:**
- Relação armazenada em `t_seguidor` (seguidor → seguido)
- Contadores de seguidores e seguindo atualizados no perfil
- O usuário não pode seguir a si mesmo (botão oculto no próprio perfil)

**Endpoints:** `POST /api/user/{username}/seguir` · `DELETE /api/user/{username}/seguir`

---

## RF-09 — Visualização e Edição de Perfil

**Descrição:** Qualquer usuário pode visualizar o perfil público de outro usuário; o usuário autenticado pode editar o próprio perfil.

**Regras:**
- Perfil exibe: nome, foto, tipo de conta, dados esportivos (posição, altura, peso, pé dominante, clubes), posts e contadores
- Edição de dados esportivos armazenados como JSONB em `DetalhePerfil`
- Apenas o próprio usuário vê o botão "Editar perfil" e os botões de exclusão de posts

**Endpoints:** `GET /api/user?username={u}` · `PATCH /api/user` · `POST /api/profile-detail` · `DELETE /api/profile-detail`

---

## RF-10 — Upload de Foto de Perfil

**Descrição:** Usuários autenticados podem enviar ou atualizar sua foto de perfil.

**Regras:**
- Formatos aceitos: JPG, JPEG, PNG, WEBP
- Arquivo salvo em `uploads/avatars/`
- Retorna URL pública via `/api/avatar/{filename}`

**Endpoint:** `POST /api/user/avatar`

---

## RF-11 — Copiloto de IA (Chatbot RAG)

**Descrição:** Usuários podem fazer perguntas em linguagem natural sobre atletas cadastrados no sistema.

**Regras:**
- Respostas geradas pela API Claude (Haiku 4.5) ou serviço Python local como fallback
- Contexto montado por `AIContextService`: inclui nome, idade, posição, altura, peso, pé dominante, clubes e avaliações
- **Nunca expõe** CPF, e-mail ou telefone dos atletas
- Serviço Python (`FastAPI`) roda na porta 8081

**Endpoint:** `POST /api/ia/prompt`

---

## RF-12 — Recuperação de Senha

**Descrição:** Usuários podem solicitar a redefinição de senha por e-mail.

**Regras:**
- Token de redefinição gerado e associado ao e-mail
- Nova senha substituída com codificação bcrypt

**Endpoints:** `POST /api/forgot-password` · `POST /api/reset-password`

---

## Regras de negócio não-triviais

| Regra | Implementação |
|-------|--------------|
| Unicidade de CPF e e-mail | `UsuarioRepository.existsByCpf()` + `existsByEmail()` → `ConflictException` (409) |
| Senha obrigatória e codificada | Validação em `UsuarioService.cadastrarAtleta()` + `passwordEncoder.encode()` antes do save |
| Ownership de posts e perfil | `SecurityUtils.currentUserId()` comparado ao `aliasId` do recurso em todos os PUT/DELETE |
| Privacidade no contexto IA | `AIContextService.montarContexto()` acessa apenas campos públicos — CPF/e-mail/telefone nunca incluídos |
| Segurança de sessão | JWT em cookie HttpOnly — inacessível via JavaScript (proteção contra XSS) |
