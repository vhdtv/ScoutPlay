# language: pt

Funcionalidade: Perfil de usuário
  Como um usuário autenticado
  Quero visualizar e editar meu perfil
  Para manter minhas informações atualizadas na plataforma

  Cenário: Buscar dados do próprio perfil
    Dado que o atleta "fabio@atleta.com" com senha "12345" está autenticado
    Quando busco os dados do perfil do username "fabin_123"
    Então os dados do perfil devem ser retornados com sucesso

  Cenário: Adicionar detalhe ao perfil
    Dado que o atleta "fabio@atleta.com" com senha "12345" está autenticado
    Quando adiciono o detalhe de perfil com chave "POSICAO" e valor "Atacante"
    Então o detalhe de perfil deve ser salvo com sucesso

  Cenário: Remover detalhe do perfil
    Dado que o atleta "fabio@atleta.com" com senha "12345" está autenticado
    E que o detalhe de perfil com chave "POSICAO" e valor "Atacante" existe
    Quando removo o detalhe de perfil com chave "POSICAO"
    Então o detalhe de perfil deve ser removido com sucesso

  Cenário: Buscar perfil de outro usuário sem autenticação
    Quando busco os dados do perfil do username "fabin_123" sem autenticação
    Então os dados do perfil devem ser retornados com sucesso
