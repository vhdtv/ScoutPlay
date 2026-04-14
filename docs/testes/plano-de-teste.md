# Plano de Teste — ScoutPlay

## 1. Identificação

| Campo | Valor |
|---|---|
| Projeto | ScoutPlay |
| Versão | 0.0.1-SNAPSHOT |
| Data de criação | 2026-04-14 |
| Responsável | Equipe de desenvolvimento |

---

## 2. Objetivo

Garantir que as funcionalidades críticas do ScoutPlay atendam aos requisitos funcionais e não funcionais definidos, com foco em:
- Regras de negócio dos serviços (testes unitários)
- Fluxos de usuário completos (BDD com Cucumber)
- Validações de entrada e tratamento de erros
- Usabilidade da interface

---

## 3. Escopo

### 3.1 Incluído
- Autenticação de usuários (login com JWT)
- Cadastro de atletas, olheiros e responsáveis
- Validações de unicidade (CPF, e-mail)
- Validações de campos obrigatórios (senha)
- Upload de foto de perfil
- Consulta e filtro de atletas
- Gestão de vídeos e avaliações
- Autorização (ownership check)

### 3.2 Excluído
- Testes de carga / performance
- Testes de segurança de infraestrutura (pentest)
- Testes de integração com serviços externos (e-mail, storage cloud)

---

## 4. Tipos de Teste

| Tipo | Ferramenta | Meta de cobertura |
|---|---|---|
| Unitário (serviços) | JUnit 5 + Mockito | ≥ 80% nas classes de serviço |
| BDD (fluxos) | Cucumber 7 + Spring | ≥ 5 cenários automatizados |
| Integração de API | Spring MockMvc | Endpoints críticos |
| Manual (roteiros) | Roteiros em `/docs/testes/roteiros-de-teste.md` | ≥ 5 roteiros executados |
| Usabilidade | Sessões com participantes | ≥ 3 participantes |

---

## 5. Estratégia de Execução

### 5.1 Testes automatizados
```bash
# Rodar todos os testes e gerar relatório de cobertura
./mvnw test

# Ver relatório JaCoCo
# Abrir: target/site/jacoco/index.html

# Ver relatório Cucumber
# Abrir: target/cucumber-reports/report.html
```

### 5.2 Testes manuais
- Executar os roteiros em `/docs/testes/roteiros-de-teste.md`
- Registrar evidências (prints) em `/docs/testes/evidencias/`

---

## 6. Critérios de Aceite

| Critério | Condição de sucesso |
|---|---|
| Testes unitários passam | 100% dos testes unitários executam sem falha |
| Cobertura mínima | ≥ 80% de cobertura nas classes `AtletaService`, `OlheiroService`, `LoginService` |
| Cenários BDD passam | Todos os 9 cenários Gherkin são executados sem falha |
| Roteiros manuais | ≥ 5 roteiros executados com evidências registradas |
| Sem erros críticos | Nenhum erro 500 em fluxos normais |

---

## 7. Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Banco de dados indisponível nos testes BDD | Média | Alto | Usar H2 em memória no perfil `test` |
| Mudança na estrutura do JWT | Baixa | Médio | Testes mockam JwtTokenProvider |
| Falso positivo por dados residuais | Média | Baixo | `@Before` limpa estado entre cenários |

---

## 8. Onde ficam as evidências

```
/docs/testes/evidencias/
  sprint1-testes-unitarios.png
  sprint1-cucumber-report.png
  sprint1-jacoco-coverage.png
  roteiro-01-login-sucesso.png
  roteiro-02-login-senha-errada.png
  roteiro-03-cadastro-cpf-duplicado.png
  roteiro-04-upload-foto-valida.png
  roteiro-05-filtro-atletas.png
```
