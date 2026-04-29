# Modelagem do Banco de Dados — ScoutPlay

**Banco de dados:** PostgreSQL (`scoutplaydb`)  
**ORM:** Spring Data JPA / Hibernate  
**DDL:** `ddl-auto=update` (Hibernate gerencia o schema automaticamente)

---

## Estratégia de Herança

O projeto usa a estratégia **TABLE_PER_CLASS** implícita via `@MappedSuperclass`.

`Usuario` é uma classe abstrata com `@MappedSuperclass` — ela **não gera tabela própria**. Os campos comuns são copiados para cada tabela filha (`atleta`, `olheiro`, `responsavel`).

```
Usuario (abstract — @MappedSuperclass)
 ├── Atleta     → tabela: atleta
 ├── Olheiro    → tabela: olheiro
 └── Responsavel → tabela: responsavel
```

---

## Tabelas

### `atleta`
| Coluna           | Tipo           | Observações                          |
|------------------|----------------|--------------------------------------|
| id               | VARCHAR (PK)   | Formato: `ATL-{UUID}`                |
| nome             | VARCHAR        | Herdado de Usuario                   |
| telefone         | VARCHAR        | Herdado de Usuario                   |
| cpf              | VARCHAR        | Herdado de Usuario                   |
| data_nascimento  | DATE           | Herdado de Usuario                   |
| cep              | VARCHAR        | Herdado de Usuario                   |
| email            | VARCHAR        | Herdado de Usuario                   |
| senha            | VARCHAR        | Herdado de Usuario (hash)            |
| posicao          | VARCHAR        |                                      |
| peso             | DOUBLE         |                                      |
| altura           | DOUBLE         |                                      |
| clubes_anteriores| VARCHAR        |                                      |
| foto_perfil      | VARCHAR        | Caminho do arquivo em `uploads/`     |
| pe_dominante     | VARCHAR        | Enum: `DESTRO`, `CANHOTO`, `AMBIDESTRO` |
| responsavel_id   | VARCHAR (FK)   | Referencia `responsavel.id` (nullable) |

---

### `olheiro`
| Coluna          | Tipo           | Observações                          |
|-----------------|----------------|--------------------------------------|
| id              | VARCHAR (PK)   | Formato: `OLH-{UUID}`                |
| nome            | VARCHAR        | Herdado de Usuario                   |
| telefone        | VARCHAR        | Herdado de Usuario                   |
| cpf             | VARCHAR        | Herdado de Usuario                   |
| data_nascimento | DATE           | Herdado de Usuario                   |
| cep             | VARCHAR        | Herdado de Usuario                   |
| email           | VARCHAR        | Herdado de Usuario                   |
| senha           | VARCHAR        | Herdado de Usuario (hash)            |
| clube           | VARCHAR        |                                      |
| local           | VARCHAR        |                                      |

---

### `responsavel`
| Coluna          | Tipo           | Observações                          |
|-----------------|----------------|--------------------------------------|
| id              | VARCHAR (PK)   | Formato: `RESP-{UUID}`               |
| nome            | VARCHAR        | Herdado de Usuario                   |
| telefone        | VARCHAR        | Herdado de Usuario                   |
| cpf             | VARCHAR        | Herdado de Usuario                   |
| data_nascimento | DATE           | Herdado de Usuario                   |
| cep             | VARCHAR        | Herdado de Usuario                   |
| email           | VARCHAR        | Herdado de Usuario                   |
| senha           | VARCHAR        | Herdado de Usuario (hash)            |

---

### `video_atleta`
| Coluna      | Tipo           | Observações                          |
|-------------|----------------|--------------------------------------|
| id_video    | UUID (PK)      | Gerado automaticamente               |
| url_video   | VARCHAR        |                                      |
| titulo      | VARCHAR        |                                      |
| data_criacao| TIMESTAMP      | Preenchido automaticamente (@PrePersist) |
| atleta_id   | VARCHAR (FK)   | Referencia `atleta.id`               |

---

### `avaliacao`
| Coluna      | Tipo           | Observações                          |
|-------------|----------------|--------------------------------------|
| id          | UUID (PK)      | Gerado automaticamente               |
| nota        | DOUBLE         |                                      |
| comentario  | VARCHAR        |                                      |
| data_criacao| TIMESTAMP      | Preenchido automaticamente (@PrePersist) |
| atleta_id   | VARCHAR (FK)   | Referencia `atleta.id`               |
| olheiro_id  | VARCHAR (FK)   | Referencia `olheiro.id`              |
| video_id    | UUID (FK)      | Referencia `video_atleta.id_video` (nullable) |

---

## Relacionamentos

```
responsavel 1 ──────── N atleta
                          │
                          │ 1
                          │
                    N video_atleta
                          │
                          │ 1 (opcional)
                          │
olheiro N ────── N avaliacao ──── 1 atleta
```

| Relacionamento              | Tipo    | Cascade          |
|-----------------------------|---------|------------------|
| Responsavel → Atleta        | 1:N     | ALL              |
| Atleta → VideoAtleta        | 1:N     | ALL + orphanRemoval |
| Avaliacao → Atleta          | N:1     | —                |
| Avaliacao → Olheiro         | N:1     | —                |
| Avaliacao → VideoAtleta     | N:1     | — (nullable)     |

---

## Observações Importantes

- **IDs não são auto-incremento numérico.** São strings com prefixo (`ATL-`, `OLH-`, `RESP-`) + UUID, gerados no construtor de `Usuario`. `VideoAtleta` e `Avaliacao` usam UUID puro via `@GeneratedValue`.
- **`idade` é calculada em runtime** (`@Transient`) a partir de `dataNascimento` — não persiste no banco.
- **`responsavel_id` é nullable** em `atleta`, pois atletas maiores de idade não possuem responsável.
- **Não existe tabela `usuario`** — `@MappedSuperclass` replica as colunas nas tabelas filhas.
- **Fotos de perfil** são armazenadas no filesystem (`uploads/fotos_perfil/`) e apenas o caminho é salvo no banco.
