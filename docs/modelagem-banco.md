# Modelagem do Banco de Dados — ScoutPlay

**Banco de dados:** PostgreSQL (`scoutplaydb`)
**ORM:** Spring Data JPA / Hibernate
**DDL:** `ddl-auto=update` (Hibernate gerencia o schema automaticamente)

---

## Estratégia de Herança

O projeto usa um **modelo flat de tabela única** para usuários.

Todos os usuários (atletas, olheiros, responsáveis) são salvos na mesma tabela `t_usuario`.
O tipo de conta é definido pela tabela de relacionamento `xref_usuario_tipoconta`.
Campos específicos de cada tipo (posição, peso, clube, etc.) são armazenados como JSON em `t_detalhes_perfil`.

```
t_usuario  ←→  xref_usuario_tipoconta  ←→  e_tipo_conta
    │
    ↓
t_detalhes_perfil  (JSON com campos do tipo de conta)
t_video_atleta     (apenas para atletas)
t_avaliacao        (olheiro → atleta)
```

---

## Tabelas

### `t_usuario`
| Coluna          | Tipo        | Observações                                    |
|-----------------|-------------|------------------------------------------------|
| id              | INT (PK)    | Auto-incremento interno. Não exposto na API.   |
| alias_id        | UUID        | ID público. Retornado como `userId` na API.    |
| nome            | VARCHAR     |                                                |
| sobrenome       | VARCHAR     |                                                |
| email           | VARCHAR     | Único                                          |
| cpf             | VARCHAR     | Único. Imutável após criação.                  |
| senha           | VARCHAR     | Hash BCrypt                                    |
| telefone        | VARCHAR     |                                                |
| foto_perfil     | VARCHAR     | Nome do arquivo em `uploads/fotos_perfil/`     |
| endereco_unico  | VARCHAR     | "@handle" gerado automaticamente               |
| data_nascimento | DATE        |                                                |
| ativo           | BOOLEAN     | `false` = conta desativada (soft delete)       |
| criado_em       | DATE        | Preenchido via `@PrePersist`                   |
| atualizado_em   | DATE        |                                                |

---

### `e_tipo_conta`
| id | nome                  |
|----|-----------------------|
| 1  | olheiro               |
| 2  | atleta                |
| 3  | responsavel           |
| 4  | representante de clube|

---

### `xref_usuario_tipoconta`
| Coluna        | Tipo     | Observações                         |
|---------------|----------|-------------------------------------|
| id            | INT (PK) |                                     |
| alias_id      | UUID     |                                     |
| fk_usuario    | INT (FK) | → `t_usuario.id`                    |
| fk_tipo_conta | INT (FK) | → `e_tipo_conta.id`                 |
| ativo         | BOOLEAN  |                                     |
| criado_em     | DATE     |                                     |

---

### `t_detalhes_perfil`
| Coluna     | Tipo     | Observações                                                     |
|------------|----------|-----------------------------------------------------------------|
| id         | INT (PK) |                                                                 |
| alias_id   | UUID     |                                                                 |
| fk_usuario | INT (FK) | → `t_usuario.id`                                               |
| data       | JSONB    | Campos específicos do tipo de conta (ver convenção abaixo)      |

**Convenção de chaves para atletas:**
```json
{
  "posicao":          "Atacante",
  "peso":             75.0,
  "altura":           1.85,
  "peDominante":      "Direito",
  "cep":              "01310-100",
  "clubesAnteriores": "Corinthians, Santos"
}
```

**Convenção de chaves para olheiros:**
```json
{
  "clube": "São Paulo FC",
  "local": "São Paulo, SP",
  "cep":   "01310-100"
}
```

**Chave especial (responsável de atleta menor):**
```json
{
  "RESPONSAVEL": "uuid-do-responsavel"
}
```

---

### `t_video_atleta`
| Coluna     | Tipo        | Observações                          |
|------------|-------------|--------------------------------------|
| id         | INT (PK)    |                                      |
| alias_id   | UUID        | ID público do vídeo na API           |
| fk_usuario | INT (FK)    | → `t_usuario.id` (deve ser atleta)   |
| url_video  | VARCHAR     | URL do vídeo (YouTube, etc.)         |
| titulo     | VARCHAR     |                                      |
| data_criacao | TIMESTAMP | Preenchido via `@PrePersist`         |
| ativo      | BOOLEAN     |                                      |
| criado_em  | DATE        |                                      |

---

### `t_avaliacao`
| Coluna      | Tipo     | Observações                                    |
|-------------|----------|------------------------------------------------|
| id          | INT (PK) |                                                |
| alias_id    | UUID     | ID público da avaliação na API                 |
| fk_atleta   | INT (FK) | → `t_usuario.id` (deve ser atleta)             |
| fk_olheiro  | INT (FK) | → `t_usuario.id` (deve ser olheiro)            |
| fk_video    | INT (FK) | → `t_video_atleta.id` (nullable)               |
| nota        | DOUBLE   | 0.0 a 10.0                                     |
| comentario  | VARCHAR  | Máx. 500 caracteres                            |
| ativo       | BOOLEAN  |                                                |
| criado_em   | DATE     |                                                |

---

## Relacionamentos

```
t_usuario (atleta) 1 ──── N t_video_atleta
t_usuario (atleta) 1 ──── N t_avaliacao (fk_atleta)
t_usuario (olheiro) 1 ─── N t_avaliacao (fk_olheiro)
t_avaliacao N ──── 1 t_video_atleta (opcional)
t_usuario 1 ──── 1 t_detalhes_perfil
t_usuario N ──── N e_tipo_conta  (via xref_usuario_tipoconta)
```

---

## IDs na API

Todos os IDs expostos pela API são o campo `alias_id` (UUID), não o `id` (INT) interno.

- `userId` no token JWT = `alias_id` do usuário
- `atletaId` nos endpoints = `alias_id` do atleta
- `videoId` nos endpoints = `alias_id` do vídeo

---

## Endpoints disponíveis

| Método | Endpoint                              | Auth   | Descrição                        |
|--------|---------------------------------------|--------|----------------------------------|
| POST   | /api/login                            | Não    | Login, retorna JWT               |
| POST   | /api/atletas/registro                 | Não    | Cria atleta, retorna JWT         |
| GET    | /api/atletas                          | Sim    | Lista atletas paginada           |
| GET    | /api/atletas/{id}                     | Sim    | Perfil do atleta com vídeos      |
| PUT    | /api/atletas/{id}                     | Sim    | Atualiza perfil (só dono)        |
| POST   | /api/atletas/{id}/foto                | Sim    | Upload foto (só dono)            |
| GET    | /api/atletas/fotos/{filename}         | Não    | Serve arquivo de foto            |
| POST   | /api/atletas/{id}/videos              | Sim    | Adiciona vídeo (só dono)         |
| GET    | /api/atletas/{id}/videos              | Sim    | Lista vídeos do atleta           |
| GET    | /api/atletas/{id}/avaliacoes          | Sim    | Lista avaliações do atleta       |
| POST   | /api/olheiros/registro                | Não    | Cria olheiro, retorna JWT        |
| GET    | /api/olheiros                         | Sim    | Lista olheiros paginada          |
| GET    | /api/olheiros/{id}                    | Sim    | Perfil do olheiro                |
| PUT    | /api/olheiros/{id}                    | Sim    | Atualiza perfil (só dono)        |
| POST   | /api/responsaveis/registro            | Não    | Cria responsável, retorna JWT    |
| POST   | /api/avaliacoes                       | Sim    | Olheiro avalia atleta            |
| PUT    | /api/videos/{videoId}                 | Sim    | Edita vídeo (só dono)            |
| DELETE | /api/videos/{videoId}                 | Sim    | Remove vídeo (só dono)           |

---

## Padrão de resposta da API

Todas as respostas seguem o envelope `ApiResponse<T>`:

```json
{
  "success": true,
  "data": { ... },
  "message": "Operação realizada com sucesso",
  "timestamp": "2026-05-20T14:30:00"
}
```

Listas paginadas retornam `data` com a estrutura `PageResponse<T>`:

```json
{
  "content": [ ... ],
  "currentPage": 0,
  "pageSize": 10,
  "totalElements": 150,
  "totalPages": 15,
  "hasNext": true,
  "hasPrevious": false
}
```

Erros retornam `success: false` com `errorCode` e `message`.
