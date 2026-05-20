# Plano de Integração — Módulo IA (Copiloto ScoutPlay)

**Data:** 2026-05-20

---

## O que existe hoje

Em `src/main/resources/ia/` há um protótipo funcional de RAG com:

- `app.py` — chatbot Streamlit chamado "Copiloto ScoutPlay"
- `notebook/base_rag.pkl` — embeddings do PDF de fundamentos táticos
- `arquivos/Fundamentos-tecnicos-e-taticos-do-Futebol-FINAL.pdf` — base de conhecimento teórico
- CSVs (Brasileirão, Transfermarkt, SofaScore) — usados apenas como dados de teste do protótipo, **não serão usados na integração**

O RAG usa **Ollama** localmente:
- Modelo de embedding: `embeddinggemma:latest`
- LLM para resposta: `phi3:mini`

---

## Arquitetura de integração

A fonte de verdade sobre jogadores é o **banco de dados** (`t_usuario`, `t_detalhes_perfil`, `t_avaliacao`), não os CSVs.

O fluxo é:

```
Frontend → POST /api/ia/copiloto
               ↓
         IaController (Spring Boot)
               ↓
         IaContextService
         [consulta o banco: atletas, avaliações, detalhes]
               ↓
         Monta contexto em texto
               ↓
         IaService → POST http://localhost:8081/perguntar
                     { pergunta, contexto }
               ↓
         Python (FastAPI)
         [RAG do PDF + contexto do banco]
               ↓
         Resposta da IA → frontend
```

**Responsabilidades:**
- **Spring Boot** — busca os dados do banco e formata o contexto; a IA nunca acessa o banco diretamente
- **Python** — recebe `{pergunta, contexto}` e responde usando o LLM; mantém o RAG do PDF para conhecimento tático

---

## Fase 1 — Separar o módulo IA do projeto Java

Mover a pasta `ia/` para fora de `src/main/resources/` para que o Maven não empacote ~36MB de dados no JAR:

```
ScoutPlay/
├── src/                (Spring Boot)
├── ia/                 (módulo Python — novo local)
│   ├── api.py
│   ├── rag.py
│   ├── requirements.txt
│   ├── notebook/
│   └── arquivos/
└── ...
```

Os CSVs podem ser removidos ou arquivados, já que não serão usados na produção.

---

## Fase 2 — Converter Streamlit para FastAPI

Refatorar `app.py` em dois arquivos: `rag.py` (lógica de busca) e `api.py` (servidor HTTP).

O endpoint recebe a pergunta **e** o contexto já montado pelo Spring Boot:

```python
# ia/api.py
from fastapi import FastAPI
from pydantic import BaseModel
from rag import responder_com_contexto

app = FastAPI()

class Pergunta(BaseModel):
    pergunta: str
    contexto: str = ""     # dados dos jogadores vindos do banco

@app.post("/perguntar")
def perguntar(body: Pergunta):
    resposta = responder_com_contexto(
        pergunta=body.pergunta,
        contexto_banco=body.contexto,
        top_k=3,
        modelo_llm="phi3:mini"
    )
    return {"resposta": resposta}
```

```python
# ia/rag.py  (lógica principal)
def responder_com_contexto(pergunta, contexto_banco, top_k, modelo_llm):
    # 1. busca chunks relevantes no RAG do PDF
    chunks_pdf = buscar_chunks_relevantes(pergunta, textos, matriz_norm, top_k)
    contexto_pdf = "\n\n".join(chunks_pdf)

    # 2. combina conhecimento tático (PDF) + dados reais (banco)
    prompt = f"""
Você é o Copiloto ScoutPlay, um assistente especialista em futebol e scouting.

Dados reais dos jogadores cadastrados na plataforma:
{contexto_banco}

Conhecimento tático e técnico:
{contexto_pdf}

Regras:
- Use os dados dos jogadores para responder perguntas específicas sobre atletas.
- Use o conhecimento tático para responder perguntas teóricas sobre futebol.
- Não invente informações. Se não souber, diga claramente.

Pergunta: {pergunta}

Resposta:
"""
    resposta = ollama.chat(
        model=modelo_llm,
        messages=[{"role": "user", "content": prompt}]
    )
    return resposta.message.content.strip()
```

**Dependências (`ia/requirements.txt`):**
```
fastapi
uvicorn
ollama
numpy
```

Rodar: `uvicorn api:app --host 0.0.0.0 --port 8081`

---

## Fase 3 — IaContextService (Spring Boot)

Este service consulta o banco e formata os dados dos jogadores como texto para o LLM:

```java
@Service
@RequiredArgsConstructor
public class IaContextService {

    private final UsuarioRepository usuarioRepository;
    private final DetalhePerfilRepository detalhePerfilRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public String montarContexto(String pergunta) {
        // Busca todos os atletas ativos
        List<Usuario> atletas = usuarioRepository
            .findAllAtivosByTipoContaId(TipoConta.ATLETA, Pageable.unpaged())
            .getContent();

        StringBuilder sb = new StringBuilder();
        sb.append("Atletas cadastrados na plataforma:\n\n");

        for (Usuario atleta : atletas) {
            sb.append("- ").append(atleta.getNome()).append(" ").append(atleta.getSobrenome());

            DetalhePerfil detalhe = detalhePerfilRepository.getByUsuario(atleta);
            if (detalhe != null) {
                Map<String, Object> data = detalhe.getData();
                sb.append(", posição: ").append(data.getOrDefault("posicao", "N/A"));
                sb.append(", altura: ").append(data.getOrDefault("altura", "N/A"));
                sb.append(", peso: ").append(data.getOrDefault("peso", "N/A"));
                sb.append(", pé dominante: ").append(data.getOrDefault("peDominante", "N/A"));
                sb.append(", clubes anteriores: ").append(data.getOrDefault("clubesAnteriores", "N/A"));
            }

            // média de avaliações
            List<Avaliacao> avaliacoes = avaliacaoRepository.findByAtleta(atleta);
            if (!avaliacoes.isEmpty()) {
                double media = avaliacoes.stream()
                    .mapToDouble(Avaliacao::getNota).average().orElse(0);
                sb.append(String.format(", nota média: %.1f", media));
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}
```

---

## Fase 4 — IaController (Spring Boot)

```java
@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IaController {

    private final IaContextService iaContextService;
    private final IaService iaService;

    @PostMapping("/copiloto")
    public ResponseEntity<ApiResponse<Map<String, String>>> perguntar(
            @RequestBody Map<String, String> body) {

        String pergunta = body.get("pergunta");
        String contexto = iaContextService.montarContexto(pergunta);
        String resposta = iaService.perguntar(pergunta, contexto);

        return ResponseEntity.ok(
            ApiResponse.success(Map.of("resposta", resposta))
        );
    }
}
```

```java
@Service
public class IaService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String perguntar(String pergunta, String contexto) {
        Map<String, String> body = Map.of("pergunta", pergunta, "contexto", contexto);
        Map<?, ?> resposta = restTemplate.postForObject(
            "http://localhost:8081/perguntar", body, Map.class);
        return (String) resposta.get("resposta");
    }
}
```

**Endpoint resultante:**
```
POST /api/ia/copiloto
Authorization: Bearer <token>

{ "pergunta": "Quais atletas atacantes têm nota acima de 8?" }
```

---

## Fase 5 — Docker Compose

```yaml
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      IA_SERVICE_URL: http://ia:8081
    depends_on:
      - db
      - ia

  ia:
    build: ./ia
    ports:
      - "8081:8081"

  db:
    image: postgres:16
    environment:
      POSTGRES_DB: scoutplaydb
      POSTGRES_USER: scoutplay
      POSTGRES_PASSWORD: scoutplay
    ports:
      - "5432:5432"
```

---

## Pré-requisitos para rodar localmente

1. **Ollama instalado** com os modelos:
   ```bash
   ollama pull embeddinggemma:latest
   ollama pull phi3:mini
   ```

2. **Python 3.10+** com `pip install -r ia/requirements.txt`

3. O `base_rag.pkl` já existente cobre o PDF de fundamentos táticos — **não precisa re-gerar**

---

## Resumo de tarefas

| Tarefa | Responsável | Esforço |
|--------|-------------|---------|
| Mover `ia/` para raiz do projeto | Backend | 30 min |
| Criar `ia/requirements.txt` | IA | 10 min |
| Criar `ia/api.py` e `ia/rag.py` (FastAPI) | IA | 2h |
| Criar `IaContextService.java` | Backend | 2h |
| Criar `IaService.java` e `IaController.java` | Backend | 1h |
| Testar integração local | Backend + IA | 1h |
| Docker Compose | Backend | 1h |
| Remover/arquivar CSVs de teste | Qualquer | 10 min |
