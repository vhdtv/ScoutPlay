# Requisitos Não Funcionais — ScoutPlay

## RNF01 — Segurança
- Senhas armazenadas com BCrypt (fator de custo padrão do Spring Security)
- Autenticação via JWT (Bearer Token) — token expira em 24 horas
- Endpoints protegidos exigem token válido no cabeçalho `Authorization`
- Rotas públicas: `/api/login`, `/api/atletas/registro`, `/api/olheiros/registro`, `/api/responsaveis/registro`, recursos estáticos

## RNF02 — Desempenho
- Listagens retornadas de forma paginada para evitar carregamento excessivo de dados
- Paginação executada no banco de dados (não em memória)

## RNF03 — Escalabilidade
- Arquitetura em camadas (Controller → Service → Repository) permitindo substituição de componentes
- Banco de dados PostgreSQL em produção

## RNF04 — Portabilidade
- Aplicação executável via Maven Wrapper (`./mvnw`) sem dependência de instalação local do Maven
- Configuração por perfis: `dev`, `prod`, `test`

## RNF05 — Manutenibilidade
- Código organizado em 4 camadas: `models` (domínio), `services` (regras de negócio), `repositorys` (persistência), `controllers` (API REST)
- Respostas padronizadas via `ApiResponse<T>` com campos `success`, `data`, `message`, `timestamp`, `errorCode`

## RNF06 — Usabilidade da API
- API RESTful com verbos HTTP semânticos (GET, POST, PUT, DELETE)
- Respostas em JSON com estrutura consistente
- Mensagens de erro descritivas e códigos HTTP adequados (400, 401, 404, 409, 500)

## RNF07 — Qualidade de Código
- Cobertura mínima de 80% nas classes de serviço (verificada via JaCoCo)
- Testes unitários com JUnit 5 + Mockito
- Testes BDD com Cucumber

## RNF08 — Tamanho de Arquivos
- Upload de imagens limitado a 10 MB por arquivo
- Formatos de imagem aceitos: JPG, JPEG, PNG, WEBP
