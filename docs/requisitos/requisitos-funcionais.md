# Requisitos Funcionais — ScoutPlay

## RF01 — Cadastro de Atleta
**Descrição:** O sistema deve permitir que jovens atletas se cadastrem informando nome, e-mail, CPF, senha, data de nascimento, CEP e telefone.
**Regras:**
- CPF deve ser único no sistema
- E-mail deve ser único no sistema
- Senha é obrigatória e deve ter no mínimo 6 caracteres
- Atletas menores de idade devem estar vinculados a um responsável

## RF02 — Cadastro de Olheiro
**Descrição:** O sistema deve permitir que olheiros (profissionais de scouting) se cadastrem informando nome, e-mail, CPF, senha, clube e localização.
**Regras:**
- CPF deve ser único no sistema
- E-mail deve ser único no sistema
- Senha é obrigatória e deve ter no mínimo 6 caracteres

## RF03 — Cadastro de Responsável
**Descrição:** O sistema deve permitir o cadastro de responsáveis para atletas menores de idade.
**Regras:**
- CPF e e-mail únicos no sistema
- Senha obrigatória

## RF04 — Autenticação (Login)
**Descrição:** O sistema deve autenticar usuários (atletas, olheiros e responsáveis) via e-mail e senha, retornando um token JWT.
**Regras:**
- Credenciais inválidas retornam erro 401
- Token JWT tem validade de 24 horas

## RF05 — Consultar Perfil Autenticado
**Descrição:** Um usuário autenticado pode consultar seus próprios dados via `GET /api/me`.

## RF06 — Listagem e Busca de Atletas (Feed do Olheiro)
**Descrição:** Olheiros podem listar atletas com filtros por nome, ano de nascimento, peso, altura, posição e pé dominante.
**Regras:**
- Resultado retornado paginado (padrão: 10 por página)
- Todos os filtros são opcionais

## RF07 — Upload de Foto de Perfil
**Descrição:** Atletas podem enviar uma foto de perfil.
**Regras:**
- Formatos aceitos: JPG, JPEG, PNG, WEBP
- Imagem é redimensionada para 500x500 pixels
- Outros formatos retornam erro

## RF08 — Gerenciamento de Vídeos do Atleta
**Descrição:** Atletas podem associar vídeos ao seu perfil (URL + título).
**Operações:** criar, listar por atleta, atualizar, deletar

## RF09 — Avaliações de Atletas
**Descrição:** Olheiros podem registrar avaliações sobre atletas, contendo nota, comentário e a identificação do olheiro.
**Operações:** criar avaliação, listar avaliações de um atleta

## RF10 — Atualização e Exclusão de Perfil
**Descrição:** Usuários autenticados podem atualizar e excluir o próprio perfil.
**Regras:**
- Apenas o próprio usuário pode alterar/excluir sua conta (verificação de ownership via JWT)
- A senha é atualizada somente se um novo valor for informado
