import streamlit as st
import pickle
import ollama
import numpy as np

BASE_RAG = r"C:\Users\Lucas\OneDrive\Desktop\ia\notebook\base_rag.pkl"


def carregar_base(caminho_pkl):
    with open(caminho_pkl, "rb") as f:
        data = pickle.load(f)

    chunks = data["chunks"]
    emb_doc = data["embeddings"]
    return chunks, emb_doc


def preparar_base(chunks, emb_doc):
    matriz_embeddings = np.array(emb_doc, dtype=float)
    textos = chunks

    normas = np.linalg.norm(matriz_embeddings, axis=1, keepdims=True)
    normas[normas == 0] = 1e-12

    matriz_norm = matriz_embeddings / normas
    return textos, matriz_norm


def gerar_emb(textos):
    response = ollama.embed(
        model="embeddinggemma:latest",
        input=textos
    )

    if isinstance(response, dict):
        return response["embeddings"]
    return response.embeddings


def buscar_chunks_relevantes(pergunta, textos, matriz_norm, top_k=3):
    emb_perg = np.array(gerar_emb([pergunta])[0], dtype=float)

    norma = np.linalg.norm(emb_perg)
    if norma == 0:
        return []

    emb_perg = emb_perg / norma

    scores = np.dot(matriz_norm, emb_perg)
    top_indices = np.argsort(scores)[-top_k:][::-1]

    return [textos[idx] for idx in top_indices]


def montar_contexto(chunks_relevantes):
    return "\n\n".join(chunks_relevantes)


def responder_pergunta(pergunta, textos, matriz_norm, top_k=3, modelo_llm="phi3:mini"):
    chunks_relevantes = buscar_chunks_relevantes(
        pergunta=pergunta,
        textos=textos,
        matriz_norm=matriz_norm,
        top_k=top_k
    )

    contexto = montar_contexto(chunks_relevantes)

    prompt = f"""
Responda à pergunta usando apenas o contexto fornecido.

Regras:
- Não invente informações.
- Não use conhecimento externo.
- Não mencione a fonte, contexto ou texto.
- Se não encontrar a resposta, diga claramente que não encontrou.
- Responda de forma natural, objetiva e completa.

Contexto:
{contexto}

Pergunta:
{pergunta}

Resposta:
"""

    resposta = ollama.chat(
        model=modelo_llm,
        messages=[{"role": "user", "content": prompt}]
    )

    if isinstance(resposta, dict):
        return resposta["message"]["content"].strip()
    return resposta.message.content.strip()


@st.cache_resource
def inicializar_base(arq):
    chunks, emb_doc = carregar_base(arq)
    textos, matriz_norm = preparar_base(chunks, emb_doc)
    return textos, matriz_norm


textos, matriz_norm = inicializar_base(BASE_RAG)

st.set_page_config(page_title="RAG Teste", layout="wide")
st.title("🤖 Copiloto ScoutPlay")

pergunta = st.text_input("Digite sua pergunta:")

if st.button("Perguntar"):
    if pergunta.strip():
        with st.spinner("Pensando..."):
            resposta = responder_pergunta(
                pergunta=pergunta,
                textos=textos,
                matriz_norm=matriz_norm,
                top_k=3,
                modelo_llm="phi3:mini"
            )

        st.subheader("Resposta")
        st.write(resposta)