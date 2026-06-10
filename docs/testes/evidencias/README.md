# Evidências de Testes — ScoutPlay

Esta pasta contém prints e logs das execuções de testes manuais e automatizados.

## Testes Automatizados (BDD + JUnit)

| Arquivo | Descrição |
|---------|-----------|
| `cucumber-report-sprint2.png` | Relatório do Cucumber com 17 cenários passando |
| `jacoco-coverage-sprint2.png` | Relatório JaCoCo de cobertura de código |
| `junit-results-sprint2.png` | Resultado dos testes unitários (UsuarioServiceTest) |

## Roteiros de Teste Manual

| Arquivo | Roteiro |
|---------|---------|
| `roteiro-01-login-sucesso.png` | RT-01: Login com credenciais válidas |
| `roteiro-02-cadastro-atleta.png` | RT-02: Cadastro de atleta com dados válidos |
| `roteiro-03-login-senha-errada.png` | RT-03: Login com senha incorreta |
| `roteiro-04-cpf-duplicado.png` | RT-04: Cadastro com CPF já existente |
| `roteiro-05-upload-formato-invalido.png` | RT-05: Upload de arquivo com formato inválido |
| `roteiro-06-listagem-atletas.png` | RT-06: Listagem paginada de atletas |

## Como gerar os relatórios automáticos

```bash
# Na pasta back-end
./mvnw test

# Relatório JaCoCo → abrir no navegador:
# back-end/target/site/jacoco/index.html

# Relatório Cucumber → abrir no navegador:
# back-end/target/cucumber-reports/report.html
```
