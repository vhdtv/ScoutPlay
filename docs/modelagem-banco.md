# Modelagem do Banco de Dados — ScoutPlay

**Banco de dados:** PostgreSQL (`scoutplaydb`)  
**ORM:** Spring Data JPA / Hibernate  
**DDL:** `ddl-auto=update` (Hibernate gerencia o schema automaticamente)  
**Atualizado em:** 2026-06-16

---

## Estratégia de Modelagem

O projeto usa um **modelo flat de tabela única** para usuários.

Todos os usuários (atletas, olheiros, representantes de clube) são salvos na tabela `t_usuario`.
O tipo de conta é definido pela tabela de relacionamento `xref_usuario_tipoconta`.
Campos específicos de cada tipo (posição, peso, clube, etc.) são armazenados como JSON em `t_detalhes_perfil`.
Publicações, curtidas, comentários, seguidores e avaliações ficam em tabelas próprias.

```
t_usuario  ←→  xref_usuario_tipoconta  ←→  e_tipo_conta
    │
    ├──→ t_detalhes_perfil  (JSONB com campos do tipo de conta)
    ├──→ t_post             (publicacoes com imagem ou video)
    ├──→ t_seguidor         (relacao seguidor/seguido)
    ├──→ t_avaliacao        (olheiro avalia atleta)
    └──→ t_like (join)      (posts curtidos pelo usuario)
           └──→ t_post

t_post
    └──→ t_comentario       (comentarios e respostas)
```

---

## Tabelas

### `t_usuario`
| Coluna          | Tipo        | Observações                                    |
|-----------------|-------------|------------------------------------------------|
| id              | INT (PK)    | Auto-incremento interno. Nunca exposto na API. |
| alias_id        | UUID        | ID publico. Usado em todos os endpoints.       |
| nome            | VARCHAR     |                                                |
| sobrenome       | VARCHAR     |                                                |
| email           | VARCHAR     | Unico                                          |
| cpf             | VARCHAR     | Unico. Imutavel apos criacao.                  |
| senha           | VARCHAR     | Hash BCrypt                                    |
| telefone        | VARCHAR     |                                                |
| foto_perfil     | VARCHAR     | Nome do arquivo em `uploads/avatars/`          |
| username        | VARCHAR     | @handle unico gerado automaticamente           |
| data_nascimento | DATE        |                                                |
| ativo           | BOOLEAN     | `false` = conta desativada (soft delete)       |
| criado_em       | TIMESTAMP   | Preenchido via `@PrePersist`                   |
| atualizado_em   | TIMESTAMP   |                                                |

---

### `e_tipo_conta`
| id | nome                   |
|----|------------------------|
| 1  | OLHEIRO                |
| 2  | ATLETA                 |
| 3  | RESPONSAVEL            |
| 4  | REPRESENTANTE_CLUBE    |

---

### `xref_usuario_tipoconta`
| Coluna        | Tipo     | Observações                         |
|---------------|----------|-------------------------------------|
| id            | INT (PK) |                                     |
| alias_id      | UUID     |                                     |
| fk_usuario    | INT (FK) | → `t_usuario.id`                    |
| fk_tipo_conta | INT (FK) | → `e_tipo_conta.id`                 |
| ativo         | BOOLEAN  |                                     |
| criado_em     | TIMESTAMP|                                     |
| atualizado_em | TIMESTAMP|                                     |

---

### `t_detalhes_perfil`
| Coluna     | Tipo      | Observações                                                     |
|------------|-----------|-----------------------------------------------------------------|
| id         | INT (PK)  |                                                                 |
| alias_id   | UUID      |                                                                 |
| fk_usuario | INT (FK)  | → `t_usuario.id`                                               |
| data       | JSONB     | Campos especificos do tipo de conta (ver convencao abaixo)      |
| ativo      | BOOLEAN   |                                                                 |
| criado_em  | TIMESTAMP |                                                                 |
| atualizado_em | TIMESTAMP |                                                              |

**Convenção de chaves para atletas:**
```json
{
  "posicao":          "Centroavante",
  "peso":             78.5,
  "altura":           1.82,
  "peDominante":      "Direito",
  "cep":              "01310-100",
  "clubesAnteriores": "Corinthians, Santos"
}
```

**Convenção de chaves para olheiros:**
```json
{
  "clube": "Flamengo",
  "local": "Rio de Janeiro, RJ"
}
```

---

### `t_post`
| Coluna          | Tipo      | Observações                                    |
|-----------------|-----------|------------------------------------------------|
| id              | INT (PK)  |                                                |
| alias_id        | UUID      | ID publico do post na API                      |
| titulo          | VARCHAR   |                                                |
| descricao       | VARCHAR   | Opcional                                       |
| caminho_arquivo | VARCHAR   | Nome do arquivo em `uploads/media/`            |
| fk_tipo_midia   | INT (FK)  | → `e_tipo_midia.id` (IMAGEM ou VIDEO)          |
| fk_autor        | INT (FK)  | → `t_usuario.id`                               |
| ativo           | BOOLEAN   | `false` = deletado (soft delete)               |
| criado_em       | TIMESTAMP |                                                |
| atualizado_em   | TIMESTAMP |                                                |

---

### `e_tipo_midia`
| id | nome   |
|----|--------|
| 1  | IMAGEM |
| 2  | VIDEO  |

---

### `t_like` (tabela de junção)
| Coluna    | Tipo     | Observações             |
|-----------|----------|-------------------------|
| fk_usuario| INT (FK) | → `t_usuario.id`        |
| fk_post   | INT (FK) | → `t_post.id`           |

---

### `t_comentario`
| Coluna         | Tipo      | Observações                            |
|----------------|-----------|----------------------------------------|
| id             | INT (PK)  |                                        |
| alias_id       | UUID      |                                        |
| texto          | VARCHAR   |                                        |
| fk_post        | INT (FK)  | → `t_post.id`                          |
| fk_autor       | INT (FK)  | → `t_usuario.id`                       |
| fk_pai         | INT (FK)  | → `t_comentario.id` (para respostas)   |
| ativo          | BOOLEAN   |                                        |
| criado_em      | TIMESTAMP |                                        |
| atualizado_em  | TIMESTAMP |                                        |

---

### `t_seguidor`
| Coluna      | Tipo      | Observações                      |
|-------------|-----------|----------------------------------|
| id          | INT (PK)  |                                  |
| alias_id    | UUID      |                                  |
| fk_seguidor | INT (FK)  | → `t_usuario.id` (quem segue)    |
| fk_seguido  | INT (FK)  | → `t_usuario.id` (quem e seguido)|
| ativo       | BOOLEAN   |                                  |
| criado_em   | TIMESTAMP |                                  |
| atualizado_em | TIMESTAMP |                                 |

---

### `t_avaliacao`
| Coluna      | Tipo      | Observações                                    |
|-------------|-----------|------------------------------------------------|
| id          | INT (PK)  |                                                |
| alias_id    | UUID      |                                                |
| fk_atleta   | INT (FK)  | → `t_usuario.id` (deve ser atleta)             |
| fk_olheiro  | INT (FK)  | → `t_usuario.id` (deve ser olheiro)            |
| nota        | DOUBLE    | 0.0 a 10.0                                     |
| comentario  | VARCHAR   | Opcional                                       |
| ativo       | BOOLEAN   |                                                |
| criado_em   | TIMESTAMP |                                                |
| atualizado_em | TIMESTAMP |                                              |

---

## Relacionamentos

```
t_usuario  1 ──── N  t_post
t_usuario  1 ──── 1  t_detalhes_perfil
t_usuario  N ──── N  e_tipo_conta         (via xref_usuario_tipoconta)
t_usuario  N ──── N  t_post               (curtidas, via t_like)
t_usuario  1 ──── N  t_seguidor           (como seguidor ou seguido)
t_usuario  1 ──── N  t_avaliacao          (como atleta ou olheiro)
t_post     1 ──── N  t_comentario
t_comentario 1 ── N  t_comentario         (respostas, fk_pai)
```

---

## Dados de seed (DataSeeder)

Na primeira inicializacao (quando o usuario `lucas_striker` nao existe), o sistema cria automaticamente:

**5 Atletas:**
| username         | Nome          | Posicao           | Senha    |
|------------------|---------------|-------------------|----------|
| `lucas_striker`  | Lucas Santos  | Centroavante      | Senha@123|
| `rafael_lateral` | Rafael Costa  | Lateral Direito   | Senha@123|
| `diego_keeper`   | Diego Ferreira| Goleiro           | Senha@123|
| `mateus_meia`    | Mateus Oliveira| Meio-campista    | Senha@123|
| `vinicius_ponta` | Vinicius Lima | Ponta Esquerda    | Senha@123|

**5 Olheiros:**
| username          | Nome              | Clube         | Senha    |
|-------------------|-------------------|---------------|----------|
| `paulo_olheiro`   | Paulo Mendes      | Flamengo      | Senha@123|
| `ana_olheira`     | Ana Rodrigues     | Santos FC     | Senha@123|
| `carlos_gremio`   | Carlos Silva      | Gremio        | Senha@123|
| `fernanda_spfc`   | Fernanda Alves    | Sao Paulo FC  | Senha@123|
| `roberto_palestra`| Roberto Nunes     | Palmeiras     | Senha@123|

Cada usuario tem 1 post com imagem baixada do Pexels, com curtidas e comentarios cruzados.

---

## IDs na API

Todos os IDs expostos pela API sao o campo `alias_id` (UUID), nunca o `id` (INT) interno.

---

## Endpoints disponíveis

### Autenticacao (`AuthController`)
| Metodo | Endpoint                | Auth | Descricao                                        |
|--------|-------------------------|------|--------------------------------------------------|
| POST   | /api/login              | Nao  | Login, define cookie `access_token` (HttpOnly)   |
| POST   | /api/logout             | Nao  | Remove cookie `access_token`                     |
| POST   | /api/signup             | Nao  | Cadastro (atleta ou olheiro), retorna cookie     |
| POST   | /api/forgot-password    | Nao  | Envia nova senha temporaria por email            |

### Usuarios e Perfis (`UserController`)
| Metodo | Endpoint                       | Auth | Descricao                                 |
|--------|--------------------------------|------|-------------------------------------------|
| GET    | /api/user?user={username}      | Opt  | Perfil de usuario com posts e seguidores  |
| PATCH  | /api/user/info                 | Sim  | Atualiza nome, sobrenome, username        |
| PATCH  | /api/user                      | Sim  | Atualiza configuracoes da conta           |
| POST   | /api/user/avatar               | Sim  | Upload de foto de perfil (multipart)      |
| POST   | /api/profile-detail            | Sim  | Adiciona ou atualiza detalhe do perfil    |
| DELETE | /api/profile-detail            | Sim  | Remove detalhe do perfil                  |
| POST   | /api/user/{username}/seguir    | Sim  | Seguir usuario                            |
| DELETE | /api/user/{username}/seguir    | Sim  | Parar de seguir usuario                   |
| POST   | /api/user/{username}/avaliar   | Sim  | Olheiro avalia atleta (nota + comentario) |
| GET    | /api/user/{username}/avaliacoes| Opt  | Lista avaliacoes de um atleta             |
| POST   | /api/user/{username}/shortlist | Sim  | Adicionar atleta a minha lista            |
| DELETE | /api/user/{username}/shortlist | Sim  | Remover atleta da minha lista             |
| GET    | /api/shortlist                 | Sim  | Listar minha lista de atletas             |
| GET    | /api/atletas                   | Sim  | Buscar atletas com filtros e paginacao    |

### Posts (`PostController`)
| Metodo | Endpoint                                    | Auth | Descricao                        |
|--------|---------------------------------------------|------|----------------------------------|
| GET    | /api/post/?page={n}&size={n}                | Opt  | Feed de posts paginado           |
| POST   | /api/post/                                  | Sim  | Criar post (multipart: arquivo)  |
| GET    | /api/post/{postId}                          | Opt  | Detalhe de um post               |
| DELETE | /api/post/{postId}                          | Sim  | Deletar post (soft delete)       |
| POST   | /api/post/{postId}/like                     | Sim  | Curtir post                      |
| POST   | /api/post/{postId}/dislike                  | Sim  | Descurtir post                   |
| POST   | /api/post/{postId}/comment                  | Sim  | Comentar em post                 |
| GET    | /api/post/{postId}/comments                 | Opt  | Listar comentarios de um post    |
| POST   | /api/post/{postId}/comment/{id}/like        | Sim  | Curtir comentario                |
| POST   | /api/post/{postId}/comment/{id}/dislike     | Sim  | Descurtir comentario             |
| POST   | /api/post/{postId}/comment/{id}/reply       | Sim  | Responder comentario             |

### Midia (`MediaController`)
| Metodo | Endpoint               | Auth | Descricao                        |
|--------|------------------------|------|----------------------------------|
| GET    | /api/media/{filename}  | Nao  | Serve arquivo de post (imagem/video) |
| GET    | /api/avatar/{filename} | Nao  | Serve foto de perfil             |

### IA (`AIController`)
| Metodo | Endpoint        | Auth | Descricao                              |
|--------|-----------------|------|----------------------------------------|
| POST   | /api/ia/prompt  | Sim  | Pergunta ao Copiloto ScoutPlay (RAG)   |

---

## Padrão de resposta da API

Todas as respostas seguem o envelope `ApiResponse<T>`:

```json
{
  "success": true,
  "data": { ... },
  "message": "Operacao realizada com sucesso",
  "timestamp": "2026-06-16T14:30:00"
}
```

Erros retornam `success: false` com `errorCode` e `message`.
