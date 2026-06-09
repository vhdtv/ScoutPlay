"""
Copiloto ScoutPlay — servidor FastAPI

Como rodar:
    pip install -r requirements.txt
    uvicorn api:app --host 0.0.0.0 --port 8081

O Spring Boot chama POST /perguntar com { "pergunta": "...", "contexto": "..." }.
O contexto é montado pelo IaContextService a partir do banco de dados — não use CSVs.
"""

import os
import pickle
from contextlib import asynccontextmanager

import numpy as np
import ollama
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

BASE_RAG = os.path.join(os.path.dirname(__file__), "notebook", "base_rag.pkl")

_textos = []
_matriz_norm = None


def _carregar_base():
    global _textos, _matriz_norm
    if not os.path.exists(BASE_RAG):
        print(f"[AVISO] base_rag.pkl não encontrado em {BASE_RAG}. Rodando sem RAG do PDF.")
        return
    with open(BASE_RAG, "rb") as f:
        data = pickle.load(f)
    emb = np.array(data["embeddings"], dtype=float)
    normas = np.linalg.norm(emb, axis=1, keepdims=True)
    normas[normas == 0] = 1e-12
    _textos = data["chunks"]
    _matriz_norm = emb / normas
    print(f"[OK] Base RAG carregada: {len(_textos)} chunks")


@asynccontextmanager
async def lifespan(app: FastAPI):
    _carregar_base()
    yield


app = FastAPI(title="Copiloto ScoutPlay", lifespan=lifespan)


def _gerar_emb(texto: str) -> np.ndarray:
    resp = ollama.embed(model="embeddinggemma:latest", input=[texto])
    raw = resp["embeddings"][0] if isinstance(resp, dict) else resp.embeddings[0]
    return np.array(raw, dtype=float)


def _buscar_chunks(pergunta: str, top_k: int = 3) -> list[str]:
    if _matriz_norm is None or not _textos:
        return []
    emb = _gerar_emb(pergunta)
    norma = np.linalg.norm(emb)
    if norma == 0:
        return []
    emb /= norma
    scores = np.dot(_matriz_norm, emb)
    indices = np.argsort(scores)[-top_k:][::-1]
    return [_textos[i] for i in indices]


class Pergunta(BaseModel):
    pergunta: str
    contexto: str = ""


@app.post("/perguntar")
def perguntar(body: Pergunta):
    try:
        chunks = _buscar_chunks(body.pergunta)
    except Exception:
        chunks = []

    contexto_pdf = "\n\n".join(chunks) if chunks else "Conhecimento tático não disponível."
    secao_atletas = body.contexto.strip() or "Nenhum atleta cadastrado na plataforma."

    prompt = f"""Você é o Copiloto ScoutPlay, assistente especialista em futebol e scouting.

Atletas cadastrados na plataforma:
{secao_atletas}

Conhecimento tático e técnico:
{contexto_pdf}

Regras:
- Use os dados dos atletas para responder perguntas sobre jogadores cadastrados.
- Use o conhecimento tático para responder perguntas teóricas sobre futebol.
- Não invente informações. Se não encontrar a resposta, diga claramente.
- Responda em português, de forma objetiva e natural.

Pergunta: {body.pergunta}
Resposta:"""

    try:
        resp = ollama.chat(model="phi3:mini", messages=[{"role": "user", "content": prompt}])
        texto = resp["message"]["content"] if isinstance(resp, dict) else resp.message.content
        return {"resposta": texto.strip()}
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"Modelo LLM indisponível: {e}")


@app.get("/health")
def health():
    return {"status": "ok", "rag_carregado": _matriz_norm is not None}
