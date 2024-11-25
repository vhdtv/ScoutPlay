
# Scout Play Application

Bem-vindo ao **Scout Play Application**, uma plataforma que conecta jovens jogadores de futebol a olheiros e clubes. Esta aplicação utiliza Spring Boot, PostgreSQL e oferece uma API RESTful para gerenciar perfis de atletas e olheiros.

## Pré-requisitos

Certifique-se de ter os seguintes softwares instalados:

1. **Java 21** (ou versão compatível).
2. **Maven** (para gerenciamento de dependências).
3. **PostgreSQL** (para o banco de dados).
4. **IntelliJ IDEA** ou qualquer outro IDE para desenvolvimento em Java.

## Configuração do Ambiente

1. **Clone o repositório:**
   git clone https://github.com/vhdtv/ScoutPlay.git

2. **Configuração do banco de dados:**
   - Crie um banco de dados no PostgreSQL chamado `scoutplaydb`.
   - Configure o usuário e a senha no arquivo `application.properties` localizado em `src/main/resources`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/scoutplaydb
     spring.datasource.username=postgres
     spring.datasource.password=postgres
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
     ```

3. **Build do projeto:**
   No terminal, execute:
   ```bash
   mvn clean install
   ```

4. **Inicie a aplicação:**
   - Via IDE: Execute a classe principal `ScoutPlayApplication`.
   - Via terminal:
     ```bash
     mvn spring-boot:run
     ```

5. **Crie as pastas para upload de imagens:**
   - No diretório do projeto, crie a pasta `uploads/fotos_perfil` para armazenar as fotos de perfil.

## Endpoints Disponíveis

### Atletas (`/api/atletas`)
- **POST** `/`: Cria um novo atleta com dados e foto de perfil.
- **GET** `/`: Lista todos os atletas com filtros opcionais.
- **GET** `/{id}`: Busca um atleta por ID.
- **PUT** `/{id}`: Atualiza informações de um atleta existente.
- **DELETE** `/{id}`: Exclui um atleta por ID.
- **GET** `/fotos/{filename}`: Retorna uma foto de perfil.

### Olheiros (`/api/olheiros`)
- **POST** `/`: Cria um novo olheiro.
- **GET** `/`: Lista todos os olheiros.
- **GET** `/{id}`: Busca um olheiro por ID.
- **PUT** `/{id}`: Atualiza informações de um olheiro existente.
- **DELETE** `/{id}`: Exclui um olheiro por ID.

## Testando a Aplicação

- **Requisições API:** Utilize ferramentas como **Postman**, **Thunder Client**, ou **curl** para testar os endpoints.
- **Banco de dados:** Conecte-se ao banco `scoutplaydb` para validar os dados inseridos.

## Contribuição

Sinta-se à vontade para contribuir para o projeto. Envie pull requests ou relate problemas no repositório oficial.

## Licença

Este projeto está licenciado sob os termos do [MIT License](LICENSE).
