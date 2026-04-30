# 📋 Semana 3 - Backend Endpoints & CRUD

## 🎯 Objetivo
Implementar todos os endpoints CRUD que o frontend React necessita para funcionamento completo.

**Frontend Stack:** React 19 + TypeScript + React Router + React Query + Tailwind CSS

---

## 📊 Análise do Frontend

### Stack Identificado:
- ✅ React 19.2.0 (versão mais recente)
- ✅ React Router (TanStack Router)
- ✅ React Query (TanStack Query)
- ✅ TypeScript + Vite
- ✅ Radix UI + Tailwind CSS
- ✅ Zod para validação

### Componentes Existentes:
```
src/front-end/src/
├── routes/
│   ├── __root.tsx       (Layout raiz com Header/Footer)
│   └── index.tsx        (Home com LoginForm)
├── components/
│   ├── Header.tsx       (Navegação)
│   ├── LoginForm.tsx    (Form básico)
│   └── ui/              (Componentes Radix)
└── styles.css           (Tailwind global)
```

### Estado Atual do Frontend:
- 🟢 Estrutura de rotas: Configurada
- 🟡 LoginForm: Implementada mas básica (sem API calls)
- ⚫ Dashboard: Não implementado
- ⚫ Perfis: Não implementado
- ⚫ Videos: Não implementado
- ⚫ Autenticação: Não implementada

---

## 🔧 Implementações Necessárias - Backend

### SEMANA 3: PART 1 - Infraestrutura de Segurança (Dias 1-2)

#### 1️⃣ **Spring Security Configuration**
Arquivo: `SecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Ativar JwtAuthenticationFilter
    // Inicializar endpoint whitelist (/api/login, /api/registro)
    // CORS já está configurado em CorsConfig.java
}
```

**Tasks:**
- [ ] Criar SecurityConfig.java
- [ ] Ativar JwtAuthenticationFilter
- [ ] Testar autenticação em endpoints protegidos

---

### SEMANA 3: PART 2 - Auth Endpoints (Dias 2-3)

#### 2️⃣ **Registro de Atleta**
Endpoint: `POST /api/atletas/registro`

**Request:**
```json
{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123",
  "dataNascimento": "2005-03-15",
  "cpf": "12345678900",
  "telefone": "(11) 98765-4321",
  "posicao": "Atacante",
  "peDominante": "Direito",
  "cep": "01310-100",
  "altura": 1.85,
  "peso": 75.0
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "f47ac10b-...",
    "userType": "ATLETA",
    "message": "Atleta registrado com sucesso"
  }
}
```

#### 3️⃣ **Registro de Olheiro**
Endpoint: `POST /api/olheiros/registro`

**Request:**
```json
{
  "nome": "Carlos Santos",
  "email": "carlos@club.com",
  "senha": "senha123",
  "cpf": "98765432100",
  "telefone": "(11) 99999-8888",
  "clube": "São Paulo FC",
  "cep": "01310-100",
  "dataNascimento": "1980-05-20"
}
```

---

### SEMANA 3: PART 3 - CRUD de Atletas (Dias 4-5)

#### 4️⃣ **GET /api/atletas - Listar Atletas**
Endpoint para o feed/discovery do frontend

**Query Parameters:**
```
GET /api/atletas?page=0&size=10&posicao=Atacante&peDominante=Direito
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "f47ac10b-...",
        "nome": "João Silva",
        "posicao": "Atacante",
        "peDominante": "Direito",
        "idade": 19,
        "altura": 1.85,
        "peso": 75.0,
        "fotoPerfil": "https://scoutplay.com/uploads/fotos_perfil/f47ac10b.jpg",
        "videos": [
          {
            "id": "vid-123",
            "titulo": "Gol em amistoso",
            "urlVideo": "https://youtube.com/watch?v=...",
            "dataCriacao": "2026-03-15T10:30:00"
          }
        ]
      }
    ],
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 150,
    "totalPages": 15,
    "hasNext": true
  }
}
```

#### 5️⃣ **GET /api/atletas/{id} - Perfil Completo do Atleta**
Endpoint para visualizar perfil detalhado

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "f47ac10b-...",
    "nome": "João Silva",
    "email": "joao@example.com",
    "dataNascimento": "2005-03-15",
    "cpf": "12345678900",
    "telefone": "(11) 98765-4321",
    "posicao": "Atacante",
    "peDominante": "Direito",
    "cep": "01310-100",
    "altura": 1.85,
    "peso": 75.0,
    "clubesAnteriores": "Corinthians, Santos",
    "fotoPerfil": "url-foto",
    "idade": 19,
    "videos": []
  }
}
```

#### 6️⃣ **PUT /api/atletas/{id} - Atualizar Perfil**
Endpoint para o usuário editar seu perfil

**Request:**
```json
{
  "nome": "João Silva",
  "altura": 1.87,
  "peso": 76.5,
  "clubesAnteriores": "Corinthians, Santos",
  "telefone": "(11) 98765-4321"
}
```

**Requer:** Token JWT do próprio atleta (verificar userId)

#### 7️⃣ **DELETE /api/atletas/{id} - Deletar Perfil**
Endpoint para deletar conta de atleta

**Requer:** Token JWT do próprio atleta

---

### SEMANA 3: PART 4 - Upload de Fotos (Dias 5-6)

#### 8️⃣ **POST /api/atletas/{id}/foto - Upload de Foto de Perfil**
Endpoint para upload de foto de perfil

**Request:**
```
Content-Type: multipart/form-data
file: <binary-data>

POST /api/atletas/f47ac10b.../foto
```

**Response:**
```json
{
  "success": true,
  "data": {
    "fotoPerfil": "https://scoutplay.com/uploads/fotos_perfil/f47ac10b-abc123.jpg",
    "message": "Foto enviada com sucesso"
  }
}
```

**Validações:**
- Extensão: PNG, JPG, JPEG
- Tamanho máximo: 10MB (configurado em properties)
- Resizing: Redimensionar para 500x500px

---

### SEMANA 3: PART 5 - CRUD de Videos (Dias 7-8)

#### 9️⃣ **POST /api/atletas/{id}/videos - Adicionar Video**
Endpoint para atleta adicionar video de performance

**Request:**
```json
{
  "urlVideo": "https://youtube.com/watch?v=abc123",
  "titulo": "Gol em amistoso contra Flamengo"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "vid-123",
    "urlVideo": "https://youtube.com/watch?v=abc123",
    "titulo": "Gol em amistoso contra Flamengo",
    "dataCriacao": "2026-04-04T10:30:00"
  }
}
```

#### 🔟 **GET /api/atletas/{id}/videos - Listar Videos**
```
GET /api/atletas/f47ac10b.../videos?page=0&size=5
```

#### 1️⃣1️⃣ **PUT /api/videos/{id} - Editar Video**
```json
{
  "titulo": "Gol atualizado",
  "urlVideo": "https://youtube.com/watch?v=xyz789"
}
```

#### 1️⃣2️⃣ **DELETE /api/videos/{id} - Deletar Video**

---

### SEMANA 3: PART 6 - CRUD de Olheiros (Dias 8-9)

#### 1️⃣3️⃣ **GET /api/olheiros - Listar Olheiros**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "scout-123",
        "nome": "Carlos Santos",
        "clube": "São Paulo FC",
        "email": "carlos@club.com",
        "telefone": "(11) 99999-8888"
      }
    ]
  }
}
```

#### 1️⃣4️⃣ **GET /api/olheiros/{id} - Perfil de Olheiro**

#### 1️⃣5️⃣ **PUT /api/olheiros/{id} - Atualizar Olheiro**

#### 1️⃣6️⃣ **DELETE /api/olheiros/{id} - Deletar Olheiro**

---

### SEMANA 3: PART 7 - Interações (Dias 9-10)

#### 1️⃣7️⃣ **POST /api/avaliacoes - Olheiro Avaliar Atleta**
Endpoint para scout adicionar avaliação/comentário

**Request:**
```json
{
  "atletaId": "f47ac10b-...",
  "olheiroId": "scout-123",
  "nota": 8.5,
  "comentario": "Atleta com bom potencial, precisa melhorar a defesa.",
  "videoId": "vid-123"
}
```

#### 1️⃣8️⃣ **GET /api/atletas/{id}/avaliacoes - Listar Avaliações do Atleta**

---

## 📅 Cronograma Detalhado

| Dia | Tarefa | Endpoints | Horas |
|-----|--------|-----------|-------|
| **Dia 1** | Spring Security Config | (setup) | 2h |
| **Dia 2** | Atleta Registration | `POST /api/atletas/registro` | 2h |
| **Dia 3** | Olheiro Registration | `POST /api/olheiros/registro` | 2h |
| **Dia 4** | Listar Atletas | `GET /api/atletas` (paginado) | 3h |
| **Dia 5** | Perfil + Upload Foto | `GET/PUT /api/atletas/{id}`, `POST /foto` | 4h |
| **Dia 6** | CRUD Videos | `POST/GET/PUT/DELETE /api/atletas/{id}/videos` | 3h |
| **Dia 7** | CRUD Olheiros | `GET/PUT/DELETE /api/olheiros/{id}` | 3h |
| **Dia 8** | Avaliações | `POST /api/avaliacoes`, `GET /avaliacoes` | 3h |
| **Dia 9** | Testes & Bugfixes | (validação) | 4h |
| **Dia 10** | Documentação & Deploy | (finalizacao) | 3h |

---

## 🛠️ Tecnologias & Padrões

### Controllers a Criar:
```
src/main/java/.../controllers/
├── AtletaController.java       (CRUD Atleta)
├── OlheiroController.java      (CRUD Olheiro)
├── VideoController.java        (CRUD Video)
├── AvaliacaoController.java    (Interações)
└── UploadController.java       (Fotos)
```

### Services a Criar:
```
src/main/java/.../services/
├── AtletaService.java
├── OlheiroService.java
├── VideoService.java
├── AvaliacaoService.java
└── UploadService.java
```

### DTO Validation:
```
jakarta.validation.constraints:
- @NotBlank
- @Email
- @Size(min, max)
- @Pattern(regexp)
- @Positive
- @Valid (para nested)
```

### Response Pattern (ALL endpoints):
```java
// Sucesso
return ResponseEntity.ok(
    ApiResponse.success(data, "Mensagem de sucesso")
);

// Erro
return ResponseEntity.status(HttpStatus.CONFLICT)
    .body(ApiResponse.error("ERROR_CODE", "Mensagem"));
```

---

## 🧪 Testes Essenciais

### Por Endpoint:
```bash
# 1. Registro Atleta
curl -X POST http://localhost:8080/api/atletas/registro \
  -H "Content-Type: application/json" \
  -d '{ ... }'

# 2. Login + Obter Token
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@example.com","senha":"senha123"}'

# 3. Usar Token em Requisição Protegida
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/atletas

# 4. Upload Foto
curl -X POST http://localhost:8080/api/atletas/$ID/foto \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/foto.jpg"

# 5. Paginação
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/atletas?page=0&size=10&posicao=Atacante"
```

---

## 🔐 Security Checklist

- [ ] Todos endpoints protegidos (exceto /login, /registro)
- [ ] Validação de JWT no header Authorization
- [ ] Usuário não pode acessar dados de outro usuário
- [ ] Admin checks para deletar recursos
- [ ] Rate limiting (próxima semana)
- [ ] Validação de arquivo (upload de fotos)
- [ ] HTTPS em produção (próxima semana)

---

## 📱 Frontend Integration Points

### Frontend esperará destes endpoints:

1. **LoginForm** → `POST /api/login`
2. **RegistroForm** → `POST /api/atletas/registro`
3. **FeedAtleta** → `GET /api/atletas?page=0&size=10`
4. **PerfilAtleta** → `GET /api/atletas/{id}`
5. **EditarPerfil** → `PUT /api/atletas/{id}`
6. **UploadFoto** → `POST /api/atletas/{id}/foto`
7. **AdicionarVideo** → `POST /api/atletas/{id}/videos`
8. **ListarVideos** → `GET /api/atletas/{id}/videos`
9. **PerfilOlheiro** → `GET /api/olheiros/{id}`
10. **CriarAvaliacao** → `POST /api/avaliacoes`

**Padrão de Requisição (Frontend):**
```javascript
const token = localStorage.getItem('authToken');
const response = await fetch('/api/atletas', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

---

## 📊 Base de Dados - Modelagem

### Tabelas Necessárias (Verificar/Criar):
```sql
-- Usuários (base class - existe)
CREATE TABLE usuario (
  id UUID PRIMARY KEY,
  nome VARCHAR(150),
  email VARCHAR(100),
  senha VARCHAR(255),
  tipo VARCHAR(20), -- ATLETA, OLHEIRO, RESPONSAVEL
  ...
);

-- Atletas (herda de usuario)
CREATE TABLE atleta (
  id UUID PRIMARY KEY REFERENCES usuario(id),
  posicao VARCHAR(50),
  pé_dominante VARCHAR(20),
  altura DECIMAL,
  peso DECIMAL,
  ...
);

-- Videos
CREATE TABLE video_atleta (
  id UUID PRIMARY KEY,
  atleta_id UUID REFERENCES atleta(id),
  url VARCHAR(500),
  titulo VARCHAR(255),
  data_criacao TIMESTAMP
);

-- Avaliações
CREATE TABLE avaliacao (
  id UUID PRIMARY KEY,
  atleta_id UUID REFERENCES atleta(id),
  olheiro_id UUID REFERENCES olheiro(id),
  nota DECIMAL,
  comentario TEXT,
  data_criacao TIMESTAMP
);
```

---

## ✨ Nice-to-Have (Semana 4+)

- [ ] Favoritos (Olheiro favorita Atleta)
- [ ] Mensagens diretas (Chat)
- [ ] Notificações em tempo real (WebSocket)
- [ ] Estatísticas de perfil
- [ ] Recomendações por IA
- [ ] Export de CV de Atleta
- [ ] Integração YouTube API
- [ ] Analytics dashboard

---

## 🎯 Status

**Backend Semana 1**: ✅ Infraestrutura + Validação
**Backend Semana 2**: ✅ JWT Authentication
**Backend Semana 3**: 🟡 Endpoints CRUD (Este documento)
**Frontend**: Status Basic (apenas login form)

**Data-alvo Semana 3:** 2026-04-14 (10 dias)

