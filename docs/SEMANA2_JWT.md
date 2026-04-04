# 🔐 Semana 2 - Autenticação JWT

## ✅ O que foi implementado

### 1️⃣ **JwtProperties Configuration**
Carrega configurações de JWT do `application.properties`:
```properties
app.jwt.secret=sua-chave-secreta-minimo-32-caracteres
app.jwt.expirationMs=86400000
```

**Arquivo:** `src/main/java/.../config/JwtProperties.java`

---

### 2️⃣ **JwtTokenProvider - Geração e Validação**
Gerencia todas as operações com JWT:

```java
// Gerar token
String token = jwtTokenProvider.generateToken(userId, userType);

// Validar token
boolean isValid = jwtTokenProvider.validateToken(token);

// Extrair dados
String userId = jwtTokenProvider.extractUserId(token);
String userType = jwtTokenProvider.extractUserType(token);
```

**Recursos:**
- ✅ Geração de tokens com expiração configurável
- ✅ Validação de assinatura HMAC-SHA256
- ✅ Extração segura de claims
- ✅ Tratamento de exceções JWT
- ✅ Chave de assinatura gerada de forma segura

**Arquivo:** `src/main/java/.../security/JwtTokenProvider.java`

---

### 3️⃣ **LoginController Atualizado com JWT**

**Before (Semana 1):**
```json
{
  "userId": "123",
  "userType": "ATLETA",
  "nome": "João",
  "email": "joao@test.com"
}
```

**After (Semana 2):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "123",
    "userType": "ATLETA",
    "nome": "João",
    "email": "joao@test.com",
    "expiresIn": 86400000
  },
  "message": "Login realizado com sucesso",
  "timestamp": "2026-04-04T10:30:00"
}
```

**Como usar no frontend:**
```javascript
// 1. Login
const response = await fetch('/api/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, senha })
});
const data = await response.json();

// 2. Armazenar token
localStorage.setItem('authToken', data.data.token);

// 3. Usar em requisições futuras
fetch('/api/atletas', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
  }
});
```

**Arquivo:** `src/main/java/.../controllers/LoginController.java`

---

### 4️⃣ **JwtAuthenticationFilter - Validação em Requisições**
Intercepta todas as requisições e valida o JWT:

```
┌─────────────┐
│   Request   │
└──────┬──────┘
       │
       ▼
┌──────────────────────────┐
│ JwtAuthenticationFilter  │
│ - Extrai token do header │
│ - Valida assinatura      │
└──────┬───────────────────┘
       │
       ├─ Endpoint público? → Passa
       │
       ├─ Token válido? → Passa + context
       │
       └─ Token inválido? → 401 Unauthorized
```

**Endpoints Públicos (sem JWT):**
- `/api/login`
- `/api/registro` (Semana 3)
- `/static/**`
- `/`

**Endpoints Protegidos (com JWT):**
- `/api/atletas/**`
- `/api/olheiros/**`
- `/api/videos/**`

**Como configurar em Spring Security (Semana 3):**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**Arquivo:** `src/main/java/.../security/JwtAuthenticationFilter.java`

---

## 🧪 Como Testar JWT

### 1️⃣ Teste de Login - Obter Token
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "atleta@example.com",
    "senha": "senha123"
  }'

# Response (200):
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "userType": "ATLETA",
    "nome": "João Silva",
    "email": "atleta@example.com",
    "expiresIn": 86400000
  }
}
```

### 2️⃣ Decodificar Token (verificar claims)
```bash
# Ir em https://jwt.io e colar o token
# Você verá:
{
  "userType": "ATLETA",
  "userId": "f47ac10b-...",
  "iat": 1712239200,    # Issued at
  "exp": 1712325600     # Expiration
}
```

### 3️⃣ Usar Token em Requisição Protegida
```bash
# Com token válido (deve funcionar - Semana 3)
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  http://localhost:8080/api/atletas

# Sem token (deve retornar 401)
curl http://localhost:8080/api/atletas
# Response: { "success": false, "errorCode": "UNAUTHORIZED", "message": "Token inválido" }
```

---

## 🔐 Segurança

| Aspecto | Implementação | Status |
|--------|-------|--------|
| Algoritmo | HMAC-SHA256 | ✅ |
| Validade | Configurável (padrão 24h) | ✅ |
| Chave segura | Variáveis de ambiente | ✅ |
| Extração de token | Header Authorization | ✅ |
| Validação assinatura | Antes de processar | ✅ |
| Endpoints públicos | Whitelist | ✅ |
| Armazenamento seguro | LocalStorage (frontend) | ⏳ |
| Refresh tokens | Planejado (Semana 3) | ⏳ |

---

## 📊 Estrutura JWT

**Token Structure:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJ1c2VyVHlwZSI6IkFUTEVUQSIsInVzZXJJZCI6IjEyMyIsImlhdCI6MTcxMjIzOTIwMCwiZXhwIjoxNzEyMzI1NjAwfQ.
7-X-E5-jA3zQj_Z1k2L_m9N...
│                                                                                            │
└─ Header ────────────────────────────────────────┬───────────────────────────────────────┘
   { "alg": "HS256", "typ": "JWT" }
                                                   │
                        ┌──────────────────────────┴──────────────────────────────────┐
                        │                                                              │
                 Payload (Claims)                                              Signature
         { "userType": "ATLETA",                                      HMAC-SHA256(
           "userId": "123",                                            header + "." + payload,
           "iat": 1712239200,                                           secret_key
           "exp": 1712325600 }                                       )
```

---

## 📁 Arquivos Criados/Modificados

| Arquivo | Status | Mudança |
|---------|--------|---------|
| `JwtProperties.java` | ✅ NOVO | Carrega JWT properties |
| `JwtTokenProvider.java` | ✅ NOVO | Gera e valida tokens |
| `JwtAuthenticationFilter.java` | ✅ NOVO | Intercepta requisições |
| `LoginController.java` | ✅ MELHORADO | Agora retorna JWT token |

---

## ✨ Próximas Etapas (Semana 3)

- [ ] SecurityConfig para ativar JwtAuthenticationFilter
- [ ] Endpoints de Atleta (/api/atletas/**)
- [ ] Endpoints de Olheiro (/api/olheiros/**)
- [ ] Upload de fotos de perfil
- [ ] Refresh tokens
- [ ] Logout com blacklist

---

## 🎯 Status

**JWT Implementation**: ✅ 100% Completo
**Pronto para**: Semana 3 - Endpoints CRUD com Autenticação
