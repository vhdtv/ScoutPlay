# Teste de Usabilidade — ScoutPlay

**Projeto:** ScoutPlay  
**Sprint:** 2  
**Data:** 2026-06-10  
**Responsável:** Equipe de Desenvolvimento  

---

## Objetivo

Avaliar a facilidade de uso da plataforma ScoutPlay com usuários reais, identificando problemas de navegação, clareza de mensagens e fluxos de cadastro/login.

---

## Metodologia

- **Tipo:** Teste presencial com observação direta
- **Perfil dos participantes:** Jovens entre 17–25 anos, familiarizados com aplicativos web
- **Tarefas avaliadas:**
  1. Criar uma conta como atleta
  2. Fazer login na plataforma
  3. Visualizar o próprio perfil
  4. Publicar um post (vídeo/foto)
  5. Dar like em um post de outro atleta

---

## Escala de Avaliação

| Nota | Significado |
|------|-------------|
| 1 | Não conseguiu completar |
| 2 | Completou com muita dificuldade |
| 3 | Completou com alguma dificuldade |
| 4 | Completou com facilidade |
| 5 | Completou sem nenhuma dificuldade |

---

## Participante 1

**Perfil:** Estudante, 19 anos, usa redes sociais diariamente  
**Data:** 2026-06-10

| Tarefa | Nota (1–5) | Observações |
|--------|-----------|-------------|
| Criar conta como atleta | 4 | Achou o formulário claro, mas ficou em dúvida sobre o campo CPF |
| Fazer login | 5 | Concluiu sem dificuldades |
| Visualizar próprio perfil | 4 | Navegou pelo menu e encontrou facilmente |
| Publicar um post | 3 | Ficou em dúvida sobre qual tipo de mídia selecionar |
| Dar like em post | 5 | Ícone intuitivo |

**Comentários gerais:** "A tela de cadastro é bem simples, mas poderia ter uma dica sobre o formato do CPF. No mais, achei fácil de usar."

**Nota média:** 4,2

---

## Participante 2

**Perfil:** Atleta amador, 22 anos, uso moderado de aplicativos  
**Data:** 2026-06-10

| Tarefa | Nota (1–5) | Observações |
|--------|-----------|-------------|
| Criar conta como atleta | 3 | Não entendeu inicialmente a diferença entre "Atleta" e "Olheiro" |
| Fazer login | 5 | Sem dificuldades |
| Visualizar próprio perfil | 4 | Precisou de um clique a mais para encontrar o menu de perfil |
| Publicar um post | 3 | Demorou para entender que precisava selecionar o tipo de mídia primeiro |
| Dar like em post | 4 | Completou, mas não percebeu o feedback visual de like ativo |

**Comentários gerais:** "Gostaria de uma tela inicial explicando o que cada tipo de conta faz. A parte de posts poderia ter um tutorial rápido."

**Nota média:** 3,8

---

## Participante 3

**Perfil:** Técnico de futebol juvenil, 34 anos, pouco familiarizado com apps  
**Data:** 2026-06-10

| Tarefa | Nota (1–5) | Observações |
|--------|-----------|-------------|
| Criar conta como atleta | 3 | Dificuldade em entender que deveria criar conta como atleta (pensou em olheiro) |
| Fazer login | 4 | Concluiu, mas ficou em dúvida sobre onde colocar o token |
| Visualizar próprio perfil | 3 | Demorou para encontrar o botão de perfil |
| Publicar um post | 2 | Não conseguiu concluir sem ajuda na primeira tentativa |
| Dar like em post | 4 | Completou após entender a interface |

**Comentários gerais:** "Para quem não é muito acostumado com apps assim, poderia ter mais texto explicativo. A parte de publicar vídeo foi a mais difícil."

**Nota média:** 3,2

---

## Resumo dos Resultados

| Tarefa | Participante 1 | Participante 2 | Participante 3 | Média |
|--------|---------------|---------------|---------------|-------|
| Criar conta | 4 | 3 | 3 | 3,3 |
| Login | 5 | 5 | 4 | 4,7 |
| Ver perfil | 4 | 4 | 3 | 3,7 |
| Publicar post | 3 | 3 | 2 | 2,7 |
| Dar like | 5 | 4 | 4 | 4,3 |
| **Média geral** | **4,2** | **3,8** | **3,2** | **3,7** |

---

## Problemas Identificados

| Prioridade | Problema | Sugestão de Melhoria |
|-----------|---------|---------------------|
| Alta | Publicar post é confuso (seleção de tipo de mídia) | Adicionar tooltip ou texto guia no formulário de post |
| Média | Diferença entre tipos de conta não é clara | Adicionar tela de onboarding explicando atleta vs olheiro |
| Média | Feedback visual de like ativo pouco perceptível | Aumentar contraste do ícone de like ativo |
| Baixa | Formato esperado do CPF não indicado | Adicionar máscara ou placeholder no campo CPF |
| Baixa | Menu de perfil difícil de localizar | Tornar avatar/foto do usuário clicável com menu dropdown |

---

## Conclusões

A plataforma apresentou **usabilidade satisfatória (média 3,7/5)** para os fluxos principais de login e like. O ponto de maior atenção é o **fluxo de publicação de posts**, que obteve a menor nota média (2,7). 

Recomendamos para a Sprint 3:
- Implementar guia visual no formulário de criação de posts
- Adicionar tela de boas-vindas com explicação dos tipos de conta
- Melhorar feedback visual de interações (like, comentário)
