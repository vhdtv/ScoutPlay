# Teste de Usabilidade — ScoutPlay

**Projeto:** ScoutPlay | **Sprint:** 3 | **Data:** 2026-06-10 | **Responsável:** Equipe de Desenvolvimento

---

## 🎯 Objetivo do teste

| Item | Preencher |
|------|-----------|
| 🎯 Objetivo | Avaliar a facilidade de uso da plataforma ScoutPlay com usuários reais, identificando problemas de navegação, clareza de mensagens e fluxos de cadastro, publicação e interação |
| ✅ Hipóteses | Os fluxos de login e like são intuitivos; o cadastro pode gerar dúvidas no campo CPF; a publicação de post pode ser confusa na seleção de mídia |
| 📌 Escopo | Telas: login, cadastro, feed, publicação de post, perfil do usuário |

---

## 👥 Participantes

| ID | 👤 Perfil | 🎓 Nível de experiência | 📍 Contexto | Observações |
|----|----------|------------------------|------------|-------------|
| P1 | Estudante universitário, 19 anos, usa redes sociais diariamente | Médio | Presencial | Familiarizado com Instagram e TikTok |
| P2 | Atleta amador de futebol, 22 anos, uso moderado de apps | Médio | Presencial | Conhece o contexto de scouting |
| P3 | Técnico de futebol juvenil, 34 anos, pouco familiarizado com apps | Leigo | Presencial | Público representativo de olheiros |

---

## ⚙️ Ambiente e preparação

| Item | Descrição |
|------|-----------|
| 💻 Dispositivo | Notebook Windows 11 |
| 🌐 Ambiente | Presencial — sala de reunião |
| 🧪 Versão testada | Branch `feat/db-remodel` — Sprint 3 |
| 🎥 Gravação | Não (observação direta + notas escritas) |
| 📎 Evidências | Notas de observação em `docs/testes/evidencias/` |

---

## 🧩 Tarefas

| 🏷️ Tarefa | 📝 Descrição | 🎯 Sucesso (critério) | ⏱️ Tempo alvo | ⭐ Prioridade |
|----------|-------------|----------------------|--------------|-------------|
| T1 | Criar uma conta como atleta | Conta criada com sucesso, usuário logado no feed | 3 min | 🔥 Alta |
| T2 | Fazer login na plataforma | Usuário autenticado e redirecionado ao feed | 1 min | 🔥 Alta |
| T3 | Visualizar o próprio perfil | Página de perfil aberta com dados corretos | 1 min | ⚠️ Média |
| T4 | Publicar um post (foto/vídeo) | Post criado e visível no feed | 3 min | ⚠️ Média |
| T5 | Dar like em um post de outro atleta | Like registrado, contador incrementado | 30 s | 🟦 Baixa |

---

## 📊 Resultados por participante e tarefa

Legenda: ✅ Concluiu · ⚠️ Concluiu com ajuda · ❌ Não concluiu

| 👤 Participante | 🏷️ Tarefa | 📌 Status | ⏱️ Tempo | ❗ Erros | 🧩 Dificuldades observadas | 💬 Comentários do participante | 📎 Evidência |
|----------------|----------|---------|---------|--------|--------------------------|-------------------------------|------------|
| P1 | T1 | ✅ | 2m 40s | 0 | Dúvida sobre formato do CPF | "Formulário claro, mas poderia indicar o formato do CPF" | Notas P1-T1 |
| P1 | T2 | ✅ | 45s | 0 | Nenhuma | "Muito fácil" | — |
| P1 | T3 | ✅ | 1m 10s | 0 | Procurou no menu por alguns segundos | "Encontrei clicando no avatar" | — |
| P1 | T4 | ⚠️ | 3m 20s | 1 | Ficou em dúvida sobre o tipo de mídia | "Poderia ter uma dica do que fazer primeiro" | Notas P1-T4 |
| P1 | T5 | ✅ | 20s | 0 | Nenhuma | "Ícone intuitivo, como no Instagram" | — |
| P2 | T1 | ⚠️ | 4m 05s | 1 | Não entendeu a diferença entre Atleta e Olheiro | "Precisava de uma explicação dos tipos de conta" | Notas P2-T1 |
| P2 | T2 | ✅ | 50s | 0 | Nenhuma | "Sem dificuldades" | — |
| P2 | T3 | ✅ | 1m 30s | 0 | Precisou de um clique a mais para acessar o perfil | "Achei que clicaria direto no nome" | — |
| P2 | T4 | ⚠️ | 3m 50s | 2 | Demorou para entender que precisava selecionar arquivo antes do título | "Um tutorial rápido ajudaria" | Notas P2-T4 |
| P2 | T5 | ✅ | 35s | 0 | Não percebeu o feedback visual de like ativo de imediato | "O azul poderia ser mais evidente" | — |
| P3 | T1 | ⚠️ | 5m 20s | 2 | Confundiu tipo de conta; tentou criar conta como olheiro por ser técnico | "Falta texto explicativo no formulário" | Notas P3-T1 |
| P3 | T2 | ✅ | 1m 15s | 0 | Ficou em dúvida se deveria usar token — resolveu sozinho | "Achei que teria um campo de token" | — |
| P3 | T3 | ⚠️ | 2m 00s | 1 | Demorou para encontrar o botão de perfil | "Não estava óbvio onde ver meu perfil" | Notas P3-T3 |
| P3 | T4 | ❌ | 6m 00s | 3 | Não concluiu sem ajuda na primeira tentativa | "A parte de publicar vídeo foi a mais difícil" | Notas P3-T4 |
| P3 | T5 | ✅ | 45s | 0 | Completou após entender a interface | "Depois que entendi, foi fácil" | — |

---

## ⭐ Questionário rápido (pós-teste)

| 👤 Participante | 😃 Facilidade (0–10) | 🧭 Clareza (0–10) | ⚡ Velocidade (0–10) | 💬 O que mais gostou? | 🛠️ O que melhoraria? |
|----------------|--------------------|--------------------|--------------------|-----------------------|---------------------|
| P1 | 8 | 7 | 8 | Design limpo, visual moderno | Indicar formato do CPF; dica no formulário de post |
| P2 | 7 | 6 | 7 | Familiar com outras redes, layout intuitivo | Explicar diferença entre Atleta e Olheiro; feedback de like mais visível |
| P3 | 5 | 5 | 5 | Copiloto de IA foi novidade positiva | Mais texto explicativo em tudo; tutorial de publicação |

---

## 🧠 Achados e melhorias

| 🧩 Achado | 📍 Onde ocorreu | 🎯 Impacto | 📈 Frequência | 🔥 Prioridade | ✅ Ação recomendada | 🔗 Issue |
|-----------|----------------|-----------|--------------|-------------|------------------|---------|
| Fluxo de publicação de post confuso (selecionar arquivo antes do título) | T4 | Alto | P1, P2, P3 | 🔥 Alta | Adicionar tooltip ou texto guia no formulário de post | — |
| Diferença entre tipos de conta (Atleta vs Olheiro) não é clara | T1 | Alto | P2, P3 | 🔥 Alta | Adicionar tela de onboarding ou tooltip explicativo | — |
| Feedback visual de like ativo pouco perceptível | T5 | Médio | P2 | ⚠️ Média | Aumentar contraste/tamanho do ícone de like ativo | — |
| Campo CPF sem indicação de formato esperado | T1 | Baixo | P1 | ⚠️ Média | Adicionar máscara ou placeholder `000.000.000-00` | — |
| Botão/acesso ao próprio perfil difícil de localizar | T3 | Médio | P2, P3 | ⚠️ Média | Tornar o avatar do header clicável com menu dropdown explícito | — |

---

## ✅ Conclusão e decisão

| Item | Resultado |
|------|-----------|
| ✅ Principais pontos positivos | Design visual moderno e clean; fluxos de login e like intuitivos (≥ 4/5); copiloto de IA agradou participantes experientes |
| ⚠️ Principais dificuldades | Publicação de post (média 2,7/5); distinção entre tipos de conta; ausência de onboarding |
| 🛠️ Top 3 melhorias | 1) Guia visual no formulário de criação de post · 2) Tela de boas-vindas explicando tipos de conta · 3) Feedback visual mais evidente no like |
| 🟢 Go/No-Go para entrega | **Go** — funcionalidades críticas (login, cadastro, feed, like) funcionam corretamente; pendências são melhorias de UX para Sprint futura |
| 📎 Links de evidência | `docs/testes/evidencias/` · Notas de observação dos participantes |

### Resumo de notas por tarefa

| Tarefa | P1 (1–5) | P2 (1–5) | P3 (1–5) | Média |
|--------|---------|---------|---------|-------|
| T1 — Criar conta | 4 | 3 | 3 | **3,3** |
| T2 — Login | 5 | 5 | 4 | **4,7** |
| T3 — Ver perfil | 4 | 4 | 3 | **3,7** |
| T4 — Publicar post | 3 | 3 | 1 | **2,3** |
| T5 — Dar like | 5 | 4 | 4 | **4,3** |
| **Média geral** | **4,2** | **3,8** | **3,0** | **3,7** |
