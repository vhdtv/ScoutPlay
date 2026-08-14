# language: pt

Funcionalidade: Gerenciamento de posts
  Como um atleta autenticado
  Quero criar e interagir com posts
  Para compartilhar meu desempenho na plataforma

  Cenário: Dar like em um post
    Dado que o atleta "fabio@atleta.com" com senha "senha123" está autenticado
    E que existe um post cadastrado
    Quando o atleta dá like no post
    Então o like deve ser registrado com sucesso

  Cenário: Remover like de um post (dislike)
    Dado que o atleta "fabio@atleta.com" com senha "senha123" está autenticado
    E que existe um post cadastrado
    Quando o atleta dá like no post
    E o atleta dá dislike no post
    Então o dislike deve ser registrado com sucesso

  Cenário: Comentar em um post
    Dado que o atleta "fabio@atleta.com" com senha "senha123" está autenticado
    E que existe um post cadastrado
    Quando o atleta comenta "Excelente jogada!" no post
    Então o comentário deve ser salvo com sucesso

  Cenário: Buscar comentários de um post
    Dado que o atleta "fabio@atleta.com" com senha "senha123" está autenticado
    E que existe um post cadastrado
    Quando o atleta comenta "Bom post!" no post
    E busco os comentários do post
    Então a lista de comentários não deve estar vazia
