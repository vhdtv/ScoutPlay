# Plano de Integração — Módulo IA (Copiloto ScoutPlay)

**Data:** 2026-05-20

---

## O que existe hoje

Em `src/main/resources/ia/` há um protótipo funcional de RAG (Retrieval-Augmented Generation) com:

- `app.py` — chatbot Streamlit chamado "Copiloto ScoutPlay"
- `notebook/base_rag.pkl` — embeddings pré-gerados de documentos de futebol
- `arquivos/Fundamentos-tecnicos-e-taticos-do-Futebol-FINAL.pdf` — base de conhecimento
- CSVs de datasets: Brasileirão, Transfermarkt, SofaScore (market value, partidas, estatísticas)

O RAG usa **Ollama** localmente:
- Modelo de embedding: `embeddinggemma:latest`
- LLM para resposta: `phi3:mini`

---

## Problema atual

O `app.py` é uma aplicação Streamlit standalone — não expõe nenhuma API REST. O Spring Boot não consegue chamá-lo diretamente. Os datasets CSV e o PDF não têm nenhum pipeline de ingestão integrado ao banco de dados.

---

## Plano de integração

### Fase 1 — Separar o módulo IA do projeto Java

Mover a pasta `ia/` para fora de `src/main/resources/` para evitar que o Maven tente empacotar ~36MB de dados:

```
ScoutPlay/
├── backend/            (Spring Boot — código Java atual)
├── ia/                 (módulo Python)
│   ├── app.py
│   ├── requirements.txt
│   ├── notebook/
│   ├── brasileirao/
│   └── safa e transfermarkt/
└── ...
```

> Não fazer isso agora sem aprovar com o time — envolve mexer na estrutura de diretórios que o Git rastreia.

---

### Fase 2 — Converter Streamlit para FastAPI

Refatorar `app.py` de Streamlit para FastAPI, expondo um endpoint REST:

```python
# ia/api.py
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class Pergunta(BaseModel):
    texto: str

@app.post("/perguntar")
def perguntar(pergunta: Pergunta):
    resposta = responder_pergunta(
        pergunta=pergunta.texto,
        textos=textos,
        matriz_norm=matriz_norm,
        top_k=3,
        modelo_llm="phi3:mini"
    )
    return {"resposta": resposta}
```

Rodar na porta `8081`:
```bash
uvicorn ia.api:app --host 0.0.0.0 --port 8081
```

**Dependências necessárias (`ia/requirements.txt`):**
```
fastapi
uvicorn
ollama
numpy
```

---

### Fase 3 — Integrar ao Spring Boot

Criar um service Java que chama o microserviço Python:

```java
// IaService.java
@Service
public class IaService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String perguntar(String texto) {
        String url = "http://localhost:8081/perguntar";
        Map<String, String> body = Map.of("texto", texto);
        Map<?, ?> resposta = restTemplate.postForObject(url, body, Map.class);
        return (String) resposta.get("resposta");
    }
}
```

Criar o controller:

```java
// IaController.java
@RestController
@RequestMapping("/api/ia")
public class IaController {

    @PostMapping("/copiloto")
    public ResponseEntity<ApiResponse<Map<String, String>>> perguntar(
            @RequestBody Map<String, String> body) {
        String resposta = iaService.perguntar(body.get("pergunta"));
        return ResponseEntity.ok(
            ApiResponse.success(Map.of("resposta", resposta))
        );
    }
}
```

**Endpoint resultante:**
```
POST /api/ia/copiloto
Authorization: Bearer <token>
{ "pergunta": "O que é pressing alto no futebol?" }
```

---

### Fase 4 — Ingestão dos datasets CSV

Os CSVs de Brasileirão e Transfermarkt podem ser usados de duas formas:

**Opção A — Estatísticas no banco de dados**
Criar uma tabela `t_estatistica_atleta` e importar os dados CSV. Permite que olheiros vejam métricas reais de desempenho.

**Opção B — Alimentar o RAG**
Converter os CSVs em texto estruturado e re-gerar os embeddings (`base_rag.pkl`) para incluir estatísticas dos jogadores. O copiloto poderá responder "qual a performance média de um atacante no Brasileirão 2024?"

Recomendação: Opção B primeiro (menor esforço), Opção A se houver tempo.

---

### Fase 5 — Docker Compose (produção)

```yaml
# docker-compose.yml
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - db
      - ia

  ia:
    build: ./ia
    ports:
      - "8081:8081"
    volumes:
      - ./ia:/app

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

## Pré-requisitos para rodar

1. **Ollama instalado** com os modelos baixados:
   ```bash
   ollama pull embeddinggemma:latest
   ollama pull phi3:mini
   ```

2. **Python 3.10+** com as dependências do `requirements.txt`

3. **PKL de embeddings** atualizado — se os CSVs forem alterados, re-rodar o notebook `notebook/nb.ipynb` para regenerar `base_rag.pkl`

---

## Resumo de tarefas

| Tarefa | Responsável | Esforço |
|--------|-------------|---------|
| Mover `ia/` para raiz do projeto | Backend | 30 min |
| Criar `ia/requirements.txt` | IA | 10 min |
| Converter `app.py` para FastAPI | IA | 2h |
| Criar `IaService.java` e `IaController.java` | Backend | 1h |
| Testar integração local | Backend + IA | 1h |
| Re-gerar embeddings com CSVs | IA | 2h |
| Docker Compose | Backend | 1h |
