# 🚀 ScoutPlay - Status Desenvolvimento

## ✅ Semana 1-2 Completo ✅

### Semana 1 - Infraestrutura Backend
- ✅ CORS Configuration
- ✅ ApiResponse Pattern Global
- ✅ DTOs com Validações JSR-380 (Email, CPF, CEP, Telefone)
- ✅ GlobalExceptionHandler (centralizado)
- ✅ LoginController refatorado
- ✅ PageResponse Helper (paginação)
- ✅ Environment Profiles (dev/prod)

**Documentação:** `docs/SEMANA1_MELHORIAS.md`

### Semana 2 - JWT Authentication
- ✅ JwtProperties (carrega configurações)
- ✅ JwtTokenProvider (gera/valida tokens HMAC-SHA256)
- ✅ JwtAuthenticationFilter (intercepta requisições)
- ✅ LoginController com JWT (retorna token)
- ✅ Token claims: userId, userType, iat, exp

**Documentação:** `docs/SEMANA2_JWT.md`

---

## 📋 Semana 3 - 18 Endpoints CRUD para Implementar

**Roadmap Completo:** `docs/SEMANA3_ROADMAP.md`

### Categorias:
1. **👤 Auth Endpoints** (2)
   - `POST /api/atletas/registro` - Novo atleta
   - `POST /api/olheiros/registro` - Novo scout

2. **⚽ CRUD Atletas** (4)
   - `GET /api/atletas` - Listar (paginado)
   - `GET /api/atletas/{id}` - Perfil
   - `PUT /api/atletas/{id}` - Editar
   - `DELETE /api/atletas/{id}` - Deletar

3. **📸 Upload Fotos** (1)
   - `POST /api/atletas/{id}/foto` - Upload com redimensionamento

4. **🎥 CRUD Videos** (4)
   - `POST /api/atletas/{id}/videos` - Adicionar
   - `GET /api/atletas/{id}/videos` - Listar
   - `PUT /api/videos/{id}` - Editar
   - `DELETE /api/videos/{id}` - Deletar

5. **👁️ CRUD Olheiros** (4)
   - `GET /api/olheiros` - Listar
   - `GET /api/olheiros/{id}` - Perfil
   - `PUT /api/olheiros/{id}` - Editar
   - `DELETE /api/olheiros/{id}` - Deletar

6. **⭐ Avaliações** (2)
   - `POST /api/avaliacoes` - Criar
   - `GET /api/atletas/{id}/avaliacoes` - Listar

7. **🔒 Spring Security Config** (1)
   - SecurityConfig.java - Ativar JwtAuthenticationFilter

---

## 📱 Frontend Status

**Repository:** https://github.com/ocai0/ScoutPlay/tree/feat-react-architecture

### Stack:
- React 19.2.0 + TypeScript
- React Router (TanStack Router)
- React Query (data fetching)
- Tailwind CSS + Radix UI
- Vite

### Componentes Implementados:
```
✅ Header.tsx
✅ LoginForm.tsx (básico, sem API calls ainda)
✅ Layout raiz com tema escuro/claro
```

### Em andamento:
- 🟡 Integração API (LoginForm precisa chamar /api/login)
- ⚫ Dashboard (após login)
- ⚫ Perfis (atleta/olheiro)
- ⚫ Feed de atletas
- ⚫ Upload de fotos
- ⚫ Adicionar vídeos

---

## 🧪 Teste Rápido - JWT Funcionando

### 1️⃣ Login e obter token:
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "atleta@example.com",
    "senha": "senha123"
  }'
```

**Resposta esperada:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyVHlwZSI6IkFUTEVUQSIsInVzZXJJZCI6IjEyMyIsImlhdCI6MTcxMjMzOTIwMCwiZXhwIjoxNzEyNDI1NjAwfQ...",
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "userType": "ATLETA",
    "nome": "Test User",
    "email": "user@test.com",
    "expiresIn": 86400000
  }
}
```

### 2️⃣ Decodificar token em https://jwt.io:
```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "userType": "ATLETA", "userId": "f47ac10b-...", "iat": 1712339200, "exp": 1712425600 }
```

---

## 🎯 Para Começar Semana 3

### Backend (Java Developer):
1. Copiar `SEMANA3_ROADMAP.md`
2. Começar por SecurityConfig.java
3. Implementar endpoints em ordem: Auth → Atletas → Videos → Olheiros → Avaliações
4. Usar padrão: Controller → Service → Repository
5. Todo endpoint retorna `ApiResponse<T>` (já configurado)

### Frontend (React Developer):
1. Update LoginForm.tsx para chamar `/api/login` realmente
2. Armazenar token em localStorage
3. Implementar client HTTP com token no header Authorization
4. Criar componentes para Feed de Atletas
5. Integrar cada endpoint conforme backend implementa

### Coordenação:
- Backend publica swagger ou postman collection
- Frontend testa endpoints em paralelo
- Daily sync para resolver bloqueadores

---

## 📚 Documentos Importantes

```
docs/
├── SEMANA1_MELHORIAS.md     (Validations + Exception Handling)
├── SEMANA2_JWT.md           (JWT Implementation)
└── SEMANA3_ROADMAP.md       (18 Endpoints + Cronograma)
```

---

## 🔐 Security Checklist Implementado

- ✅ Senhas não em plain text (preparado para BCrypt)
- ✅ JWT com HMAC-SHA256
- ✅ Secrets via variáveis de ambiente
- ✅ CORS restritivo (localhost:3000, localhost:5173)
- ✅ Validação entrada (JSR-380)
- ✅ Exception handling centralizado
- ⏳ BCrypt (Semana 4)
- ⏳ Rate limiting (Semana 4)
- ⏳ HTTPS em produção (Deploy)

---

## 📊 Resumo das Melhorias Implementadas

| Aspecto | Semana 1 | Semana 2 | Semana 3 |
|---------|----------|----------|----------|
| Validação | ✅ JSR-380 | ✅ | ✅ |
| Autenticação | - | ✅ JWT | ✅ |
| Exception Handling | ✅ Global | ✅ | ✅ |
| CRUD Endpoints | - | - | 🟡 18 endpoints |
| Upload Fotos | - | - | 🟡 1 endpoint |
| Testes | - | - | 🟡 |

---

## 💡 Próximas Semanas (Semana 4+)

- BCrypt para senhas
- Refresh tokens
- Rate limiting
- Tests (JUnit + Mockito)
- Swagger/OpenAPI
- Docker + CI/CD
- Deploy AWS

---

**Status Atual:** ✅ Backend ready para Semana 3
**Frontend:** 🟡 Esperando endpoints da Semana 3 para integração
**Data-alvo deploy:** 2026-04-24

---

Qualquer dúvida sobre implementação, consultar os arquivo de documentação correspondentes!
