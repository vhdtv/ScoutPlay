# Guia de Integração Frontend — ScoutPlay

> Para o Caio (ocai0) — referência rápida de todos os endpoints disponíveis,
> formato de request/response e como conectar cada rota React ao backend.

---

## Setup

Backend roda em `http://localhost:8080`.  
Configure o base URL no frontend (ex: `src/config/api.ts`):

```ts
export const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'
```

Todo endpoint autenticado exige o header:
```
Authorization: Bearer <token>
```

O token é retornado no login/registro e deve ser salvo (ex: `localStorage`).

---

## Autenticação

### POST /api/login
**Rota React:** `/login`  
**Pública**

```ts
// request
{ email: string, senha: string }

// response
{
  success: true,
  data: {
    token: string,
    userId: string,   // UUID — guardar para chamadas futuras
    userType: "ATLETA" | "OLHEIRO" | "RESPONSAVEL",
    nome: string,
    email: string,
    expiresIn: number // ms
  }
}
```

**Após o login:** salve `token`, `userId` e `userType`. Use `userType` para redirecionar:
- `ATLETA` → `/user/:userId`
- `OLHEIRO` → `/user/:userId`

---

### GET /api/me
**Rota React:** qualquer rota que precise revalidar a sessão  
**Requer autenticação**

```ts
// response
{
  success: true,
  data: {
    userId: string,
    userType: "ATLETA" | "OLHEIRO" | "RESPONSAVEL",
    nome: string,
    email: string
  }
}
```

Use esse endpoint no loader da rota raiz para checar se o token ainda é válido.

---

### POST /api/auth/forgot-password
**Rota React:** `/forgot-password`  
**Pública**

```ts
// request
{ email: string }

// response (200 sempre — não vaza quais e-mails existem)
{ success: true, message: string, data: { resetToken: string } }
```

> **Dev only:** o token é retornado no body. Em produção seria enviado por e-mail.

---

### POST /api/auth/reset-password
**Rota React:** `/forgot-password` (segundo passo)  
**Pública**

```ts
// request
{ token: string, novaSenha: string }  // novaSenha: mínimo 6 caracteres

// response
{ success: true, message: "Senha redefinida com sucesso" }
```

---

## Cadastro

### POST /api/atletas/registro
**Rota React:** `/signup?type=atleta`  
**Pública**

```ts
// request (campos obrigatórios marcados com *)
{
  nome: string,           // *
  email: string,          // *
  senha: string,          // *
  cpf: string,            // *
  dataNascimento: string, // * formato: "YYYY-MM-DD"
  telefone?: string,
  altura?: number,        // cm (ex: 1.75)
  peso?: number,          // kg
  posicao?: string,       // ex: "Atacante"
  peDominante?: string,   // "Direito" | "Esquerdo" | "Ambidestro"
  cep?: string,
  clubesAnteriores?: string
}

// response — igual ao /login (retorna token direto)
{ success: true, data: { token, userId, userType: "ATLETA", nome, email, expiresIn } }
```

---

### POST /api/olheiros/registro
**Rota React:** `/signup?type=olheiro`  
**Pública**

```ts
// request
{
  nome: string,           // *
  email: string,          // *
  senha: string,          // *
  cpf: string,            // *
  dataNascimento: string, // *
  telefone?: string,
  clube?: string,
  local?: string,
  cep?: string
}

// response — igual ao /login
```

---

## Perfil de Atleta

### GET /api/atletas/:id
**Rota React:** `/user/:id` (quando `userType === "ATLETA"`)  
**Requer autenticação**

```ts
// response
{
  data: {
    id: string,            // UUID
    nome: string,
    email: string,
    telefone?: string,
    cpf: string,
    dataNascimento: string,
    idade: number,
    fotoPerfil?: string,   // nome do arquivo — servir via GET /api/atletas/fotos/:filename
    posicao?: string,
    peso?: number,
    altura?: number,
    peDominante?: string,
    cep?: string,
    clubesAnteriores?: string,
    videos: [{ id, urlVideo, titulo, dataCriacao }]
  }
}
```

---

### PUT /api/atletas/:id
**Rota React:** `/settings` (atleta logado)  
**Requer autenticação — apenas o próprio atleta**

```ts
// request — todos opcionais, só envia o que mudou
{
  nome?: string,
  telefone?: string,
  altura?: number,
  peso?: number,
  posicao?: string,
  peDominante?: string,
  cep?: string,
  clubesAnteriores?: string
}
```

---

### DELETE /api/atletas/:id
**Rota React:** `/settings` (botão "Excluir conta")  
**Requer autenticação — apenas o próprio atleta**

```ts
// response
{ success: true, message: "Conta desativada com sucesso" }
```

---

### POST /api/atletas/:id/foto
**Rota React:** `/settings` (upload de avatar)  
**Requer autenticação — apenas o próprio atleta**  
Content-Type: `multipart/form-data`

```ts
// form field: "file" (JPG ou PNG)

// response
{ data: { fotoPerfil: "uuid_timestamp.jpg" } }
```

Para exibir a foto: `GET /api/atletas/fotos/:fotoPerfil` (endpoint público).

---

### GET /api/atletas
**Rota React:** `/feed` (olheiro buscando atletas)  
**Requer autenticação**

```ts
// query params
?page=0&size=10
&posicao=Atacante       // opcional
&alturaMin=1.70         // opcional
&alturaMax=1.90         // opcional
&pesoMin=60             // opcional
&pesoMax=85             // opcional

// response
{
  data: {
    content: [AtletaDTO],
    currentPage: number,
    pageSize: number,
    totalElements: number,
    totalPages: number,
    hasNext: boolean,
    hasPrevious: boolean
  }
}
```

---

## Perfil de Olheiro

### GET /api/olheiros/:id
**Rota React:** `/user/:id` (quando `userType === "OLHEIRO"`)

```ts
// response
{
  data: { id, nome, email, telefone, cpf, dataNascimento, clube, local, cep }
}
```

### PUT /api/olheiros/:id — mesma lógica do atleta
### DELETE /api/olheiros/:id — mesma lógica do atleta

---

## Posts

### POST /api/posts
**Rota React:** botão "Novo Post" no feed  
**Requer autenticação**

```ts
// request
{
  titulo: string,          // * máx 120 chars
  descricao?: string,      // máx 1000 chars
  caminhoArquivo?: string  // URL ou path do arquivo de mídia
}

// response 201
{ data: { id, titulo, descricao, autorId, autorNome, criadoEm } }
```

---

### GET /api/posts
**Rota React:** `/feed`  
**Pública**

```ts
?page=0&size=10
// response — PageResponse<PostDTO>
```

---

### GET /api/posts/:id
**Rota React:** `/post/:post_id`  
**Pública**

```ts
// response
{ data: { id, titulo, descricao, caminhoArquivo, tipoMidia, autorId, autorNome, criadoEm } }
```

---

### GET /api/posts/usuario/:autorId
**Rota React:** `/user/:id` (aba "Posts" do perfil)  
**Pública**

```ts
?page=0&size=10
// response — PageResponse<PostDTO>
```

---

### PUT /api/posts/:id
**Apenas o autor**

```ts
// request — campos opcionais
{ titulo?, descricao?, caminhoArquivo? }
```

### DELETE /api/posts/:id — apenas o autor

---

## Vídeos (atleta)

### POST /api/atletas/:atletaId/videos
```ts
{ urlVideo: string, titulo: string }
```

### GET /api/atletas/:atletaId/videos
```ts
// response — PageResponse<VideoDTO>
```

### PUT /api/videos/:videoId
```ts
{ urlVideo?: string, titulo?: string }
```

### DELETE /api/videos/:videoId

---

## Avaliações (olheiro → atleta)

### POST /api/avaliacoes
**Apenas olheiros**

```ts
{ atletaId: string, nota: number, comentario: string, videoId?: string }
```

### GET /api/atletas/:atletaId/avaliacoes
```ts
// response — PageResponse<AvaliacaoDTO>
```

---

## Tratamento de erros

Todos os erros seguem o formato:
```ts
{
  success: false,
  errorCode: string,
  message: string,
  timestamp: string
}
```

| HTTP | Situação |
|------|----------|
| 401  | Token ausente ou inválido — redirecionar para `/login` |
| 403  | Operação não permitida (ex: editar perfil de outro usuário) |
| 404  | Recurso não encontrado |
| 409  | Conflito (ex: e-mail já cadastrado) |
| 422  | Validação falhou — `message` explica o campo |

---

## Sugestão de estrutura de serviço React

```ts
// src/services/api.ts
const token = () => localStorage.getItem('token')

export const api = {
  get: (path: string) =>
    fetch(`${API_BASE}${path}`, { headers: { Authorization: `Bearer ${token()}` } })
      .then(r => r.json()),

  post: (path: string, body: unknown) =>
    fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token()}` },
      body: JSON.stringify(body)
    }).then(r => r.json()),

  put: (path: string, body: unknown) =>
    fetch(`${API_BASE}${path}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token()}` },
      body: JSON.stringify(body)
    }).then(r => r.json()),

  delete: (path: string) =>
    fetch(`${API_BASE}${path}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token()}` }
    }).then(r => r.json()),
}
```

---

## Checklist de rotas React × endpoints

| Rota React | Endpoint(s) necessário(s) | Status backend |
|---|---|---|
| `/login` | POST /api/login | ✅ |
| `/signup?type=atleta` | POST /api/atletas/registro | ✅ |
| `/signup?type=olheiro` | POST /api/olheiros/registro | ✅ |
| `/forgot-password` | POST /api/auth/forgot-password + reset-password | ✅ |
| `/feed` (olheiro) | GET /api/atletas + GET /api/posts | ✅ |
| `/feed` (atleta) | GET /api/posts | ✅ |
| `/user/:id` (atleta) | GET /api/atletas/:id + GET /api/posts/usuario/:id | ✅ |
| `/user/:id` (olheiro) | GET /api/olheiros/:id | ✅ |
| `/post/:id` | GET /api/posts/:id | ✅ |
| `/settings` | PUT + DELETE /api/atletas/:id ou /olheiros/:id | ✅ |
