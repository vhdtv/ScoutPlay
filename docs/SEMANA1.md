# Semana 1 - Infraestrutura Backend

> **Status:** Concluido. Este documento registra o que foi implementado na Semana 1.
> O banco de dados foi remodelado posteriormente — consulte `docs/modelagem-banco.md` para o estado atual.

---

## O que foi implementado

### CORS Configuration
- Configurado em `application.properties` para aceitar requisições de `localhost:3000` e `localhost:5173` (React/Vite)
- `CorsConfig.java` como classe de configuração adicional
- Permite métodos HTTP: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Habilita envio de credentials (cookies/headers de autenticação)

### ApiResponse Global
Padrão uniforme para todas as respostas JSON da API.

```json
{
  "success": true,
  "data": { ... },
  "message": "Operação realizada com sucesso",
  "timestamp": "2026-05-20T10:30:00"
}
```

Arquivo: `src/main/java/.../api/response/ApiResponse.java`

### DTOs (Data Transfer Objects)
- `AtletaDTO` — dados completos de um atleta (campos evoluíram com o remodel)
- `VideoDTO` — dados de vídeo do atleta
- `OlheiroDTO` — dados de um olheiro
- `LoginRequest` — email + senha
- `LoginResponse` — token JWT + userId + userType

Pasta: `src/main/java/.../api/dto/`

### Dependências (pom.xml)
- JWT (jjwt 0.11.5)
- Spring Boot Validation

---

## Evolução após Semana 1

| Semana | Entrega |
|--------|---------|
| Semana 1 | Infraestrutura, DTOs, CORS |
| Semana 2 | JWT auth completo (ver `SEMANA2_JWT.md`) |
| Semana 3 | 18 endpoints CRUD implementados (ver `modelagem-banco.md`) |
| DB Remodel | Modelo flat (`t_usuario` + `t_detalhes_perfil` JSONB), realizado por Caio |

Para o estado atual completo da API: `docs/modelagem-banco.md`
