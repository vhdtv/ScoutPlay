# language: pt

Funcionalidade: Autenticação de usuários
  Como um usuário do ScoutPlay
  Quero realizar login com meu e-mail e senha
  Para acessar a plataforma com segurança

  Cenário: Login de atleta com credenciais válidas
    Dado que existe um atleta com e-mail "atleta@scoutplay.com" e senha "senha123"
    Quando eu realizo login com e-mail "atleta@scoutplay.com" e senha "senha123"
    Então o login deve ser realizado com sucesso
    E o tipo de usuário retornado deve ser "ATLETA"

  Cenário: Login de olheiro com credenciais válidas
    Dado que existe um olheiro com e-mail "olheiro@scoutplay.com" e senha "senha456"
    Quando eu realizo login com e-mail "olheiro@scoutplay.com" e senha "senha456"
    Então o login deve ser realizado com sucesso
    E o tipo de usuário retornado deve ser "OLHEIRO"

  Cenário: Login com senha incorreta
    Dado que existe um atleta com e-mail "atleta@scoutplay.com" e senha "senha123"
    Quando eu realizo login com e-mail "atleta@scoutplay.com" e senha "senhaErrada"
    Então o login deve falhar com mensagem "Email ou senha inválidos"

  Cenário: Login com e-mail não cadastrado
    Quando eu realizo login com e-mail "inexistente@scoutplay.com" e senha "qualquer"
    Então o login deve falhar com mensagem "Email ou senha inválidos"

  Cenário: Login com campos em branco
    Quando eu realizo login com e-mail "" e senha ""
    Então o login deve falhar por dados inválidos
