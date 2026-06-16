# Módulo IA — Copiloto ScoutPlay

**Atualizado em:** 2026-06-16  
**Status:** Implementado e em operacao

---

## Arquitetura atual

```
Frontend → POST /api/ia/prompt
               ↓
         AIController (Spring Boot)
               ↓
         AIContextService
         [consulta t_usuario, t_detalhes_perfil, t_avaliacao do banco]
               ↓
         Monta contexto em texto com dados dos atletas
               ↓
         AIService → POST http://localhost:8081/perguntar
                     { pergunta, contexto }
               ↓
         Python (FastAPI — back-end/src/main/resources/ia/api.py)
         [RAG do PDF de fundamentos taticos + contexto do banco]
               ↓
         Resposta do LLM → frontend (chat no feed)
```

**Responsabilidades:**
- **Spring Boot** — busca os dados do banco e formata o contexto; a IA nunca acessa o banco diretamente
- **Python/FastAPI** — recebe `{pergunta, contexto}` e responde usando o LLM; mantem o RAG do PDF para conhecimento tatico

---

## Componentes implementados

### Backend Spring Boot

| Classe | Responsabilidade |
|--------|-----------------|
| `AIController` | Endpoint POST `/api/ia/prompt`, requer autenticacao |
| `AIContextService` | Consulta banco (atletas, olheiros, avaliacoes) e monta texto de contexto |
| `AIService` | Chama o servico Python via RestTemplate; fallback se IA indisponivel |

**Endpoint:**
```
POST /api/ia/prompt
Cookie: access_token=<jwt>
Content-Type: application/json

{ "pergunta": "Quais atacantes estao cadastrados na plataforma?" }
```

Resposta:
```json
{
  "success": true,
  "data": { "resposta": "..." },
  "message": null
}
```

### Servico Python (FastAPI)

**Localizacao:** `back-end/src/main/resources/ia/api.py`

**Como iniciar:**
```bash
cd back-end/src/main/resources/ia
python -m uvicorn api:app --host 0.0.0.0 --port 8081
```

**Endpoint Python:**
```
POST http://localhost:8081/perguntar
{ "pergunta": "...", "contexto": "..." }
```

**Modelos Ollama necessarios:**
```bash
ollama pull phi3:mini           # LLM para resposta
ollama pull embeddinggemma:latest # embeddings para RAG
```

**Base de conhecimento:** `back-end/src/main/resources/ia/notebook/base_rag.pkl`  
(395 chunks do PDF de fundamentos taticos e tecnicos do futebol — nao precisa re-gerar)

---

## Pre-requisitos para rodar localmente

1. **Ollama** instalado e rodando (`ollama serve`)
2. **Modelos** baixados: `phi3:mini` e `embeddinggemma:latest`
3. **Python 3.10+** com dependencias:
   ```bash
   pip install fastapi uvicorn ollama numpy
   ```
4. Iniciar a FastAPI antes do backend Spring Boot (ou ao menos antes de usar o chat)

---

## Configuracao da URL do servico IA

O backend Spring Boot aponta para o servico Python via:
```properties
# application.properties
app.ia.url=${IA_URL:http://localhost:8081}
```

Em producao, definir a variavel de ambiente `IA_URL` com o endereco do servico Python.

---

## Comportamento em caso de falha

Se o servico Python estiver indisponivel, `AIService` retorna uma mensagem de erro amigavel ao usuario — o restante da aplicacao continua funcionando normalmente (feed, posts, perfis, etc. nao sao afetados).
