# language: pt

Funcionalidade: Cadastro de atletas
  Como um jovem atleta
  Quero me cadastrar na plataforma ScoutPlay
  Para ser descoberto por olheiros

  Cenário: Cadastro de atleta com dados válidos
    Dado que não existe atleta com CPF "111.222.333-44"
    E que não existe atleta com e-mail "novo@atleta.com"
    Quando eu cadastro um atleta com nome "Pedro Lima", CPF "111.222.333-44", e-mail "novo@atleta.com" e senha "senha123"
    Então o atleta deve ser cadastrado com sucesso

  Cenário: Tentativa de cadastro com CPF já existente
    Dado que já existe um atleta com CPF "111.222.333-44"
    Quando eu cadastro um atleta com nome "Outro Atleta", CPF "111.222.333-44", e-mail "outro@email.com" e senha "senha123"
    Então o cadastro deve falhar com conflito de CPF

  Cenário: Tentativa de cadastro com e-mail já existente
    Dado que não existe atleta com CPF "999.888.777-66"
    E que já existe um atleta com e-mail "existente@email.com"
    Quando eu cadastro um atleta com nome "Novo Atleta", CPF "999.888.777-66", e-mail "existente@email.com" e senha "senha123"
    Então o cadastro deve falhar com conflito de e-mail

  Cenário: Tentativa de cadastro sem senha
    Dado que não existe atleta com CPF "555.444.333-22"
    Quando eu cadastro um atleta sem senha
    Então o cadastro deve falhar por senha obrigatória
