import type { UUID } from "crypto";
import type { CommentDTO, ConfigItemDTO, MensagemDTO, PostDataInputDTO, PostDataOutputDTO, PostDataUpdateDTO, PostDetailsDTO, PostHighlightDTO, SearchParamsDTO, SearchResultsOutputDTO, UserProfileDetailDTO, UserProfileDTO, UserSummaryDTO } from "./tipos";

export default class {
    get USER_CACHE_KEY() { return "__usuario"; }
    get USER_MOCK(): UserSummaryDTO {
        return {
            url: "nome_sobrenome_11",
            nome: "Nome",
            sobrenome: "Sobrenome",
            iniciais: "NS",
            fotoPerfil: "https://unsplash.it/100"
        }
    }
    get POST_MOCK(): PostDataOutputDTO {
        return {
            enderecoUnico: "9126357153",
            poster: "/assets/posters/mock.jpg",
            src: "/assets/imgs/mock.jpg",
            tipoMidia: "IMAGE",
            titulo: "+ uma"
        };
    }


    fazerLogin = async (usuario: string, senha: string): Promise<UserSummaryDTO> => {
        this.salvarDadosUsuarioNoNavegador(this.USER_MOCK)
        return this.USER_MOCK;
    }
    
    fazerLogout = async (): Promise<boolean> => {
        this.deletarDadosUsuarioNoNavegador();
        return true;
    }
    
    mandarEmailParaRecuperarSenha = async (email: string): Promise<boolean> => {
        return true;
    }
    
    definirDetalhePerfil = async (chave: string, dado: unknown): Promise<UserProfileDetailDTO> => {
        return {[chave]: dado};
    }

    atualizarConfiguracao = async (config: ConfigItemDTO): Promise<boolean> => {
        return true;
    }
    
    removerDetalhePerfil = async (key: string): Promise<boolean> => {
        return true;
    }
    
    vincularComoResponsavelAConta = async (nomeUsuario: string): Promise<boolean> => {
        // RESPONSAVEL pede, ATLETA confirma
        return true;
    }
    
    desvincularComoResponsavelAConta = async (nomeUsuario: string): Promise<boolean> => {
        // RESPONSAVEL ou atleta, se o mesmo for maior de 18 anos
        return true;
    }
    
    criarPost = async (data: PostDataInputDTO): Promise<PostDataOutputDTO> => {
        return this.POST_MOCK;
    }
    
    deletarPost = async (id: PostDataOutputDTO["enderecoUnico"]): Promise<boolean> => {
        return true;
    }
    
    atualizarPost = async (id: PostDataOutputDTO["enderecoUnico"], data: PostDataUpdateDTO): Promise<PostDataOutputDTO> => {
        return {
            enderecoUnico: "9126357153",
            poster: "/assets/posters/mock.jpg",
            src: "/assets/imgs/mock.jpg",
            tipoMidia: "IMAGE",
            titulo: "+ uma",
            descricao: data.descricao,
        } as PostDataOutputDTO;
    }
    
    darLikeEmPost = async (postId: string): Promise<boolean> => {
        return true;
    }
    
    darDislikeEmPost = async (postId: string): Promise<boolean> => {
        return true;
    }
    
    darDestaque = async (postId: UUID, data: string | number): Promise<PostHighlightDTO> => {
        return {
            contador: 1,
            texto: "Incrível"
        }
    }
    
    seguir = async (nomeUsuario: string): Promise<boolean> => {
        await new Promise((resolve, reject) => {setTimeout(() => resolve(null), 5000)})
        return true;
    }
    
    pararDeSeguir = async (nomeUsuario: string): Promise<boolean> => {
        await new Promise((resolve, reject) => {setTimeout(() => resolve(null), 5000)})
        return true;
    }
    
    mandarConvite = async (nomeUsuario: string, texto: string): Promise<boolean> => {
        return true;
    }
    
    buscar = async (texto: string, filtros: SearchParamsDTO): Promise<SearchResultsOutputDTO> => {
        return {
            dados: [
                {
                    tipo: "PERFIL",
                    dado: this.USER_MOCK
                },
                {
                    tipo: "POST",
                    dado: this.POST_MOCK
                }
            ]
        }
    }
    
    chatIA = async (prompt: string): Promise<MensagemDTO> => {
        return {
            autor: "IA",
            texto: "Sei lá"
        }
    }

    private salvarDadosUsuarioNoNavegador = (userData: UserSummaryDTO) => {
        localStorage.setItem(this.USER_CACHE_KEY, userData.url);
    }
    
    private deletarDadosUsuarioNoNavegador = () => {
        localStorage.removeItem(this.USER_CACHE_KEY)
    }
    
    obterDadosDoPerfil = async (perfil: string): Promise<UserProfileDTO> => {
        let output: UserProfileDTO = {
            url: "user01923",
            nome: "Nome",
            sobrenome: "Sobrenome",
            iniciais: "NS",
            idade: 19,
            tipoConta: ["ATLETA", "RESPONSAVEL"],
            fotoPerfil: "https://unsplash.it/100",
            detalhesPerfil: {
                CIDADE: "Belo Horizonte",
                POSICOES: ["Meia-Ofensivo(CAM)", "Volante(CDM)", "Ponta(LW / RW)"],
                PE_DOMINANTE: "DIREITO",
                RESPONSAVEL: [
                    {
                        nome: "Responsavel",
                        sobrenome: null,
                        fotoPerfil: null,
                        iniciais: "R",
                        url: "_respo1"
                    },
                    {
                        nome: "Responsavel",
                        sobrenome: null,
                        fotoPerfil: null,
                        iniciais: "R",
                        url: "_respo1"
                    }
                ],
                CLUBES: [
                    {
                        url: "comite11",
                        nome: "Clube Comitê 11",
                        data: {
                            inicio: new Date("12/05/2020"),
                            fim: new Date("15/09/2022")
                        }
                    },
                    {
                        url: "_barracacquiofficial",
                        nome: "Barra Caqui Oficial Clube",
                        data: {
                            inicio: new Date("12/05/2022")
                        }
                    },
                ]
            },
            posts: [
                {
                    url: "172345617823",
                    titulo: "Mais uma pra conta",
                    subtitulo: null,
                    interacoes: {
                        deuLike: false,
                        quantidadeLike: 192,
                        destaques: [
                            { contador: 15, texto: "Boa Jogada" },
                            { contador: 1, texto: "Mostrou Garra" },
                            { contador: 7, texto: "Velocista" },
                            { contador: 8, texto: "Estrategista" },
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/3560017-uhd_3840_2160_25fps.mp4"
                    },
                },
                {
                    url: "172345617822",
                    titulo: "O cara não acompanhou kakaka",
                    subtitulo: "Aqui tem habilidade",
                    interacoes: {
                        deuLike: false,
                        quantidadeLike: 75,
                        destaques: [
                            { contador: 30, texto: "Boa Jogada" },
                            { contador: 27, texto: "Mostrou Garra" }
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/12462341-hd_1080_1920_60fps.mp4"
                    },
                },
                {
                    url: "172345617823",
                    titulo: "Mais uma pra conta",
                    subtitulo: null,
                    interacoes: {
                        deuLike: true,
                        quantidadeLike: 192,
                        destaques: [
                            { contador: 15, texto: "Boa Jogada" },
                            { contador: 1, texto: "Mostrou Garra" },
                            { contador: 7, texto: "Velocista" },
                            { contador: 8, texto: "Estrategista" },
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/14621116_1920_1080_25fps.mp4"
                    },
                },
                {
                    url: "172345617822",
                    titulo: "O cara não acompanhou kakaka",
                    subtitulo: "Aqui tem habilidade",
                    interacoes: {
                        deuLike: false,
                        quantidadeLike: 75,
                        destaques: [
                            { contador: 30, texto: "Boa Jogada" },
                            { contador: 27, texto: "Mostrou Garra" }
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/15390776_2160_3840_60fps.mp4"
                    },
                },
                {
                    url: "172345617823",
                    titulo: "Mais uma pra conta",
                    subtitulo: null,
                    interacoes: {
                        deuLike: false,
                        quantidadeLike: 192,
                        destaques: [
                            { contador: 15, texto: "Boa Jogada" },
                            { contador: 1, texto: "Mostrou Garra" },
                            { contador: 7, texto: "Velocista" },
                            { contador: 8, texto: "Estrategista" },
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/15391407_2160_3840_60fps.mp4"
                    },
                },
                {
                    url: "172345617822",
                    titulo: "O cara não acompanhou kakaka",
                    subtitulo: "Aqui tem habilidade",
                    interacoes: {
                        deuLike: true,
                        quantidadeLike: 75,
                        destaques: [
                            { contador: 30, texto: "Boa Jogada" },
                            { contador: 27, texto: "Mostrou Garra" }
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/15448985-hd_1920_1080_60fps.mp4"
                    },
                },
            ]
        }
        if(perfil === "me") {
            output.souEu = true;
            output.configuracoes = {OLHEIROS_PODEM_CONECTAR_PELO_WHATSAPP: true, OCULTAR_INFORMACOES_PESSOAIS_PUBLICAMENTE: false};
        }
        return output;
    }

    obterDadosDoPost = async (postId: string): Promise<PostDetailsDTO> => {
        return {
            autor: {
                url: "user01923",
                nome: "Nome",
                sobrenome: "Sobrenome",
                iniciais: "NS",
                fotoPerfil: "https://unsplash.it/100"
            },
            url: "172345617823",
            titulo: "Mais uma pra conta",
            subtitulo: null,
            interacoes: {
                quantidadeLike: 192,
                deuLike: false,
                destaques: [
                    { contador: 15, texto: "Boa Jogada" },
                    { contador: 1, texto: "Mostrou Garra" },
                    { contador: 7, texto: "Velocista" },
                    { contador: 8, texto: "Estrategista" },
                ]
            },
            media: {
                tipo: "video/mp4",
                src: "/assets/videos/3560017-uhd_3840_2160_25fps.mp4"
            },
            metadados: {
                segueConta: false
            }
        }
    }

    obterComentarios = async (url: string): Promise<CommentDTO[]> => {
        return [
            {
                por: this.USER_MOCK,
                quantidadeLike: 12,
                texto: "Muito fera!!!"
            }
        ]
    }

    enviarComentario = async (comentario: string): Promise<CommentDTO> => {
        return {
            por: this.USER_MOCK,
            quantidadeLike: 0,
            texto: comentario
        }
    }

    obaterPostsFeed = async ({page}: {page: number}): Promise<{page: number, pageSize: number, data: PostDetailsDTO[]}> => {
        return {
            page: page++,
            pageSize: 2,
            data: [
                {
                    metadados: {
                        segueConta: false
                    },
                    autor: {
                        url: "user01923",
                        nome: "Nome",
                        sobrenome: "Sobrenome",
                        iniciais: "NS",
                        fotoPerfil: "https://unsplash.it/100"
                    },
                    url: "172345617823",
                    titulo: "Mais uma pra conta",
                    subtitulo: null,
                    interacoes: {
                        quantidadeLike: 48,
                        deuLike: false,
                        destaques: [
                            { contador: 8, texto: "Técnico" },
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/15449387-hd_1920_1080_60fps.mp4"
                    },
                },
                {
                    autor: {
                        url: "user01923",
                        nome: "Nome",
                        sobrenome: "Sobrenome",
                        iniciais: "NS",
                        fotoPerfil: "https://unsplash.it/100"
                    },
                    url: "172345617823",
                    titulo: "Mais uma pra conta",
                    subtitulo: null,
                    interacoes: {
                        quantidadeLike: 192,
                        deuLike: false,
                        destaques: [
                            { contador: 15, texto: "Boa Jogada" },
                            { contador: 1, texto: "Mostrou Garra" },
                            { contador: 7, texto: "Velocista" },
                            { contador: 8, texto: "Estrategista" },
                        ]
                    },
                    media: {
                        tipo: "video/mp4",
                        src: "/assets/videos/3560017-uhd_3840_2160_25fps.mp4"
                    },
                    metadados: {
                        segueConta: true
                    }
                }
            ]
        }
    }
}    

