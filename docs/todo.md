cria os services 

usuarioService
    .buscarPor([aliasId, endereco_unico, nome, cpf, email], search)
    .atualizarPerfil(Usuario)
    .banir(Usuario)
    .removerBanimento(Usuario)
    .deletar(Usuario)
    .deletarPermanentemente(Usuario)
    .mudarFotoPerfil(Image, Usuario)
    .removerFotoPerfil(Usuario)
    .buscarSeguidores(Usuario)
    .buscarQuemSegue(Usuario)

postService
    .criar({})
    .buscarPor([aliasId, Atleta, Avaliacao, filtros])
    .atualizar(Post{titulo, descricao})
    .deletar(Post)
    .deletarPermanentemente(Post)

interacaoService
    .darLike(Post, Usuario)
    .darDeslike(Post, Usuario)
    .avaliar(Avaliacao, Usuario)
    .removerAvaliacao(Avaliacao, Usuario)
    .comentar(Comentario, Post, Usuario)
    .removerComentario(Comentario, Usuario)
    .adicionarVisualizacao(Post, Usuario)
    .seguir(Usuario, Usuario)
    .pararDeSeguir(Usuario, Usuario)