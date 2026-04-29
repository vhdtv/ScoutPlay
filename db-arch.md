## Considerações:
- Proponho o uso de prefixos no nome das tabelas, sendo elas:
    - t_: Table
    - enum_: Enumerador
    - xref_: Para tabelas NxM
- Tentar ao máximo usar campos em portugues, tendo como exxceção palavras que já estão muito consolidadas no meio tech

## Tabelas: 
### t_usuario -> Serve para cadastrar pessoas que vao usar o sistema, com login/senha
- id (int, not null, auto increment)
- identificador (uuid, not null)
- ativo (bool) -> para evitar um delete verdadeiro, os registros são apenas desativados
- criado_em (timestamp, not null)
- atualizado_em (timestamp, not null)
- cpf (VARCHAR(20), not null)
- data_nascimento (datetime, not null)
- email (VARCHAR(64), not null)
- url (VARCHAR(32), not null)
- nome (VARCHAR(32), not null)
- sobrenome (VARCHAR(32), not null)
- endereco_unico (VARCHAR(32), not null) # é o arroba das redes sociais
- senha (VARCHAR(256)) -> sempre criptografado
- telefone (VARCHAR(20))
- foto_perfil (VARCHAR(255)) -> nome do arquivo salvo dentro da pasta media/i/1200x/[ANO]/[MES]/

### t_detalhes_perfil -> feito para adicionar informações diversas no perfil. Como a natureza dessas informações não é padronizada, é usado o jsonb como "coringa", assim o front tem maior flexibilidade de usar e exibir essas informações, enquanto a modelagem do banco continua simples.
- id (int, not null, auto increment)
- identificador (uuid, not null)
- t_usuario::f_usuario_identificador (uuid, not null, FK)
- ativo (bool)
- enum_tipo_detalhe_perfil::identificador
- data (jsonb)

### enum_tipo_detalhe_perfil -> categoriza de forma a manter a facilidade de entender os registros
- identificador (int, not null, auto_increment)
- nome_exibicao (VARCHAR(64), not null)
- nome_slug (VARCHAR(64), not null)
- ativo (bool)
[Clubes, Video Apresentação, altura(cm), configuração, posição]

### enum_tipo_conta -> lista os tipos de conta que existem, e consequentemente o que o usuario pode fazer 
- identificador (int, not null, auto_increment)
- nome (VARCHAR(64), not null)
- ativo (bool)
Exemplos de valores: [atleta, responsavel, olheiro, clube, representante_clube]

### xref_usuario_tipo_conta -> tabela MxN entre usuario e tipo_conta, já que uma conta pode ter múltiplos "poderes" (tipo atleta e responsável, assim a própria conta pode aceitar convites de olheiros)
- id
- t_usuario::f_usuario_identificador (uuid, not null, FK)
- enum_tipo_conta::f_tipo_conta_identificador (int, not null, FK)

## Grafos
- [VERTEX] POST
    - uuid: string
    - ativo: boolean
    - titulo: string
    - criado_em: datetime
    - descricao: string
    - tipo_midia: string(PHOTO, VIDEO) 
    - caminho_arquivo: string

- [VERTEX] USUARIO
    - uuid: string

- [VERTEX] AVALIACAO
    - uuid: string
    - nome: string (mas pode evoluir para um id que tem seu significado guardado em uma tabela propria)
    
- [VERTEX] COMENTARIO
    - uuid: string
    - content: string

________________________________________________________

- [EDGE] TEM_AVALIACAO -> De post para Avaliacao
    - uuid: string
    - usuario: string (FK)

- [EDGE] AVALIOU -> De usuario para Avaliacao
    - uuid: string
    - post_id: string (FK)

- [EDGE] SEGUE -> De Usuario para Usuario
    - uuid: string

- [EDGE] FEITO_POR -> De Comentario para Usuario
    - uuid: string

- [EDGE] TEM_COMENTARIO -> De Post para Comentario
    - uuid: string

- [EDGE] CRIADO_POR -> De Post para Usuario
    - uuid: string
    - data_criacao: datetime

- [EDGE] RECEBEU_LIKE -> De [Comentario, Post] para Usuario
    - uuid: string


