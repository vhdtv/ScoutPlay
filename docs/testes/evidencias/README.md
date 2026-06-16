# Evidências de Testes — ScoutPlay

**Atualizado em:** 2026-06-16

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
| `roteiro-07-recuperacao-senha.png` | RT-07: Recuperação de senha por email |
| `roteiro-08-publicar-post.png` | RT-08: Publicação de imagem no feed |
| `roteiro-09-curtir-comentar.png` | RT-09: Curtir e comentar em post |
| `roteiro-10-seguir-usuario.png` | RT-10: Seguir e parar de seguir usuário |

## Como gerar os relatórios automáticos

```bash
# Na pasta back-end
./mvnw test

# Relatório JaCoCo → abrir no navegador:
# back-end/target/site/jacoco/index.html

# Relatório Cucumber → abrir no navegador:
# back-end/target/cucumber-reports/report.html
```

## Usuários de teste disponíveis (seed automático)

| Tipo    | Username           | Senha     |
|---------|--------------------|-----------|
| Atleta  | lucas_striker      | Senha@123 |
| Atleta  | rafael_lateral     | Senha@123 |
| Atleta  | diego_keeper       | Senha@123 |
| Atleta  | mateus_meia        | Senha@123 |
| Atleta  | vinicius_ponta     | Senha@123 |
| Olheiro | paulo_olheiro      | Senha@123 |
| Olheiro | ana_olheira        | Senha@123 |
| Olheiro | carlos_gremio      | Senha@123 |
| Olheiro | fernanda_spfc      | Senha@123 |
| Olheiro | roberto_palestra   | Senha@123 |

> Para recriar os dados de seed: `TRUNCATE TABLE t_usuario CASCADE;` no banco e reiniciar o backend.
