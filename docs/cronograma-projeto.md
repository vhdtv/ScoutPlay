# Cronograma do Projeto - ScoutPlay

## 1. Equipe e papéis

- **Backend 1 (BE1):** arquitetura, regras de negócio, services
- **Backend 2 (BE2):** persistência/infra, integração, cobertura de testes
- **Frontend 1 (FE1):** telas principais e fluxos centrais
- **Frontend 2 (FE2):** validações de UI, estados de erro, UX
- **IA 1 (IA1):** cenários BDD (Gherkin) + automação Cucumber
- **IA 2 (IA2):** plano de testes, roteiros manuais, usabilidade e evidências

---

## 2. Planejamento por Sprint (8 semanas)

| Sprint | Semanas | Objetivo | Entregas |
|---|---|---|---|
| **Sprint 0 (Setup)** | 1-2 | Preparar base técnica e gestão | Repo GitHub com professora `rafapcmor`, Kanban (Backlog/Em Progresso/Em Revisão/Concluído), arquitetura 4 camadas, setup Maven/JUnit/Cucumber, 1 feature simples, README inicial |
| **Sprint 1 (MVP)** | 3-4 | Entregar versão mínima utilizável | 2-3 funcionalidades, testes unitários iniciais, 2 cenários BDD automatizados, requisitos iniciais em `/docs/requisitos`, HUB (Pré-Projeto + Canvas) |
| **Sprint 2 (Incremento)** | 5-6 | Expandir funcionalidades e qualidade | +2-3 funcionalidades (total >=6), regra de negócio não-trivial, completar 5 cenários BDD, evolução dos testes unitários, plano de teste inicial, relatório + pitch |
| **Sprint 3 (Estabilização)** | 7-8 | Finalizar qualidade e documentação | cobertura >=80% em service/domain, 5 roteiros manuais executados com evidências, teste de usabilidade (3 participantes), README final, slides, demo e revisão final |

---

## 3. Distribuição de trabalho por Sprint

### Sprint 0
- **BE1/BE2:** estrutura `domain/service/infra/ui`, build e testes base
- **FE1/FE2:** base da interface e navegação inicial
- **IA1:** setup de BDD e padrão de escrita Gherkin
- **IA2:** estrutura de documentos de teste e pasta de evidências

### Sprint 1
- **BE1/BE2:** funcionalidades 1-3
- **FE1/FE2:** telas de cadastro/listagem/edição (ou equivalentes)
- **IA1:** automação de 2 cenários BDD
- **IA2:** início do plano de teste e critérios de aceite por issue

### Sprint 2
- **BE1/BE2:** funcionalidades 4-6 + regra não-trivial
- **FE1/FE2:** busca/filtro, validações de erro, ajustes de UX
- **IA1:** completar 5 cenários BDD automatizados
- **IA2:** roteiros de teste detalhados e organização de evidências

### Sprint 3
- **BE1/BE2:** refatoração, cobertura e estabilidade
- **FE1/FE2:** ajustes finais de usabilidade e consistência visual
- **IA1:** regressão automatizada e suporte à demo
- **IA2:** teste de usabilidade (3 participantes), consolidação de resultados

---

## 4. Cerimônias Scrum (por sprint)

- **Planning:** definição da meta da sprint + seleção de issues
- **Daily (assíncrona):** atualização curta no board (comentário/checklist)
- **Review:** validação do que foi entregue
- **Retrospectiva:** manter / parar / começar

---

## 5. Definition of Done (DoD)

Uma issue só vai para **Concluído** quando tiver:

1. Código implementado
2. Testes criados/atualizados (quando aplicável)
3. Documentação e README atualizados se houver mudança de comportamento
4. Evidência vinculada (issue/PR/print/log)

---

## 6. Checklist final de entrega

- [ ] Board GitHub com colunas mínimas e issues por sprint
- [ ] Mínimo de 10 classes autorais + 4 camadas/pacotes
- [ ] Mínimo de 6 funcionalidades
- [ ] Testes unitários implementados (meta >=80% em regras/serviços)
- [ ] Mínimo de 5 cenários BDD automatizados
- [ ] `/docs/testes/plano-de-teste.md` preenchido
- [ ] `/docs/testes/roteiros-de-teste.md` com 5 roteiros executados
- [ ] `/docs/testes/usabilidade.md` com 3 participantes
- [ ] Evidências em `/docs/testes/evidencias`
- [ ] `/slides` com apresentação final e demo
- [ ] README completo e atualizado
