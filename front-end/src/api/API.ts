import type { UUID } from "crypto";
import type { CommentDTO, ConfigItemDTO, ConviteDTO, MensagemDTO, PostDataInputDTO, PostDataOutputDTO, PostDataUpdateInputDTO, PostDetailsDTO, PostHighlightDTO, ProfileUpdateInputDTO, SearchParamsDTO, SearchResultsOutputDTO, UserProfileDetailDTO, UserProfileDTO, UserSummaryDTO, ClientSignupInputDTO } from "./tipos";

export default class {
    get USER_CACHE_KEY() { return "__usuario"; }
    get BACKEND_ENDPOINT() { return `http://localhost:8080/api`; }
    get GET_HEADERS() { return { "Content-Type": "application/json" } }

    // auth-related
    fazerCadastro = async (input: ClientSignupInputDTO): Promise<UserSummaryDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/signup`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({
                email: input.email,
                senha: input.senha,
                username: input.username,
                dataNascimento: input.dataNascimento,
                cpf: input.cpf,
                nome: input.nome,
                sobrenome: input.sobrenome,
                tipoConta: input.tipoConta,
            })
        })
        if(request.status != 200) throw new Error("Falha ao cadastrar");
        const { data } = await request.json();
        return {
            fotoPerfil: data.fotoPerfil,
            iniciais: data.iniciais,
            nome: data.nome,
            sobrenome: data.sobrenome,
            username: data.username,
        }
    }
    fazerLogin = async (email: string, senha: string): Promise<UserSummaryDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/login`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({email, senha}),
        });
        if(request.status !== 200) throw new Error(`Falha ao logar`)
        const { data } = (await request.json());
        this.salvarDadosUsuarioNoNavegador(data);
        return {
            nome: data.nome,
            sobrenome: data.sobrenome,
            username: data.username,
            iniciais: data.iniciais,
            fotoPerfil: data.fotoPerfil,
        };
    }
    fazerLogout = async (): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/logout`, {
            method: "POST",
            credentials: "include"
        })
        await request.json();
        this.deletarDadosUsuarioNoNavegador();
        return true;
    }
    mandarEmailParaRecuperarSenha = async (email: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/forgot-password`, {
            method: "POST",
            body: JSON.stringify({email})
        });
        await request.json();
        return true;
    }

    // profile-related
    obterDadosDoPerfil = async (username?: UserSummaryDTO["username"]): Promise<UserProfileDTO> => {
        let urlEndpoint = `${this.BACKEND_ENDPOINT}/user/`;
        urlEndpoint += username ?? ""
        if(!username) {
            const loggedUsername = this.obterDadosUsuarioNoNavegador();
            urlEndpoint += loggedUsername;
        }
        const backendRequest = await fetch(urlEndpoint, {
            method: 'GET',
            credentials: "include"
        });
        const { data } = await backendRequest.json()
        if(!data) throw new Error("obterDadosDoPerfil veio com data = null")
        return {
            username: data.username,
            nome: data.nome,
            sobrenome: data.sobrenome,
            iniciais: data.iniciais,
            fotoPerfil: data.fotoPerfil,
            idade: data.idade,
            tipoConta: data.tipoConta,
            detalhesPerfil: data.detalhesPerfil,
            posts: data.posts,
            souEu: data.souEu
        }
    }
    atualizarPerfil = async (input: ProfileUpdateInputDTO): Promise<UserProfileDTO> => {
        const formData = new FormData();
        input.nome && formData.append("nome", input.nome);
        input.sobrenome && formData.append("sobrenome", input.sobrenome);
        input.username && formData.append("username", input.username);
        input.fotoPerfil && formData.append("fotoPerfil", input.fotoPerfil);
        input.config && formData.append("config", JSON.stringify(input.config));

        const request = await fetch(`${this.BACKEND_ENDPOINT}/user`, {
            method: "PATCH",
            credentials: "include",
            body: formData
        })
        const { data } = await request.json();
        if(!data) throw new Error("atualizarPerfil veio com data = null")
        return {
            username: data.username,
            nome: data.nome,
            sobrenome: data.sobrenome,
            iniciais: data.iniciais,
            fotoPerfil: data.fotoPerfil,
            idade: data.idade,
            tipoConta: data.tipoConta,
            detalhesPerfil: data.detalhesPerfil,
            posts: data.posts,
            souEu: data.souEu
        };
    }
    definirDetalhePerfil = async (chave: string, valor: string): Promise<UserProfileDetailDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/profile-detail`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({chave, valor}),
        })
        const {data} = await request.json();
        return data as UserProfileDetailDTO;
    }
    removerDetalhePerfil = async (chave: string): Promise<UserProfileDetailDTO> => { 
        const request = await fetch(`${this.BACKEND_ENDPOINT}/profile-detail`, {
            method: "DELETE",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({chave}),
        })
        const {data} = await request.json();
        return data as UserProfileDetailDTO;
    }

    // settings
    atualizarConfiguracao = async (config: ConfigItemDTO): Promise<boolean> => {
        try {
            const data = await this.atualizarPerfil({config});
            return true;
        }
        catch(e) {
            return false;
        }
    }
    vincularComoResponsavelAConta = async (username: UserSummaryDTO["username"]): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/responsible`, {
            method: "POST",
            credentials: "include",
        })
        if(request.status != 200) return false;
        return true;
    }
    desvincularComoResponsavelAConta = async (username: UserSummaryDTO["username"]): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/responsible`, {
            method: "DELETE",
            credentials: "include",
        })
        if(request.status != 200) return false;
        return true;
    }
    
    // post-related
    criarPost = async ({titulo, arquivo, descricao}: PostDataInputDTO): Promise<PostDataOutputDTO> => {
        const formData = new FormData();
        formData.append("arquivo", arquivo);
        formData.append("titulo", titulo);
        formData.append("descricao", descricao ?? "");
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post`, {
            method: "POST",
            credentials: "include",
            body: formData,
        })
        const { data } = await request.json();
        return {
            url: data.url,
            titulo: data.titulo,
            descricao: data.descricao,
            tipoMidia: data.tipoMidia,
            src: data.src,
            poster: data.poster,
            autor: data.autor,
            criadoEm: data.criadoEm,
        };
    }
    obterPost = async (postId: string): Promise<PostDetailsDTO> => {
        const backendRequest = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}`, {
            method: 'GET',
            credentials: "include"
        });
        const { data } = await backendRequest.json();
        return {
            autor: data.autor,
            criadoEm: data.criadoEm,
            descricao: data.descricao,
            interacoes: data.interacoes,
            media: data.media,
            metadados: data.metadados,
            titulo: data.titulo,
            url: data.url,
        }

    }
    deletarPost = async (id: PostDataOutputDTO["url"]): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${id}`, {
            method: "DELETE",
            credentials: "include",
        })
        await request.json();
        return true;
    }
    atualizarPost = async (id: PostDataOutputDTO["url"], input: PostDataUpdateInputDTO): Promise<PostDataOutputDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${id}`, {
            method: "PATCH",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify(input)
        })
        const { data } = await request.json();
        return data;
    }
    darLikeEmPost = async (postId: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/like`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
        });
        if(request.status != 200) return false;
        return true;
    }
    darDislikeEmPost = async (postId: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/dislike`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
        });
        if(request.status != 200) return false;
        return true;
    }
    obterDestaques = async (postId: UUID): Promise<PostHighlightDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/highlight`, {
            method: "GET",
            credentials: "include",
            headers: this.GET_HEADERS,
        });
        const { data } = await request.json();
        return data;
    }
    darDestaque = async (postId: UUID, input: {id?: UUID, texto: string}): Promise<PostHighlightDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/highlight`, {
            method: "POST",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({aliasId: input.id, nome: input.texto})
        });
        const { data } = await request.json();
        return data;
    }
    retirarDestaque = async (postId: UUID, destaqueId: UUID): Promise<PostHighlightDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/highlight`, {
            method: "DELETE",
            credentials: "include",
            headers: this.GET_HEADERS,
            body: JSON.stringify({aliasId: destaqueId})
        });
        const { data } = await request.json();
        return data;
    }
    obterComentarios = async (postId: UUID): Promise<CommentDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comments`, {
            headers: this.GET_HEADERS
        })
        const { data } = await request.json();
        return data as CommentDTO[];
    }
    enviarComentario = async (postId: UUID, comentario: string): Promise<CommentDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comment`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ texto: comentario })
        });
        const { data } = await request.json();
        return {
            por: data.por,
            texto: data.texto,
            quantidadeLike: data.quantidadeLike ?? 0,
        }
    }
    obterMidia = (mediaPath: string): string => {
        return `${this.BACKEND_ENDPOINT}/media/${mediaPath}`;
    }
    obterPostsFeed = async ({page}: {page: number}): Promise<{page: number, last: boolean, data: PostDetailsDTO[]}> => {
        const urlEndpoint = new URL(`${this.BACKEND_ENDPOINT}/feed`);
        if(page) urlEndpoint.searchParams.append("page", page.toString());
        const request = await fetch(urlEndpoint, {
            credentials: "include"
        });
        const { data } = await request.json();
        if(!data) return { data: [], last: true, page: 1};
        const {content, last, number} = data;
        return { data: content, last, page: number};
    }
    
    // interactions
    seguir = async (username: UserSummaryDTO["username"]): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/follow`, {
            method: "POST",
            credentials: "include"
        });
        if(request.status != 200) return false;
        return true;
    }
    pararDeSeguir = async (username: UserSummaryDTO["username"]): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/unfollow`, {
            method: "POST",
            credentials: "include"
        });
        if(request.status != 200) return false;
        return true;
    }
    obterConvites = async (): Promise<ConviteDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/message`, {
            credentials: "include"
        })
        const {data} = await request.json();
        return data;
    }
    mandarConvite = async (username: UserSummaryDTO["username"], mensagem: string): Promise<ConviteDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/message`, {
            method: "POST",
            headers: this.GET_HEADERS,
            credentials: "include",
            body: JSON.stringify({usernameDestinatario: username, mensagem}),
        })
        const {data} = await request.json();
        return data;
    }
    aceitarConvite = async (conviteId: UUID): Promise<boolean> => {
        try {
            const request = await fetch(`${this.BACKEND_ENDPOINT}/message/${conviteId}/accept`, {
                method: "POST",
                headers: this.GET_HEADERS,
                credentials: "include",
            })
            const {data} = await request.json();
            return data;
        }
        catch(e) {
            return false;
        }
    }
    recusarConvite = async (conviteId: UUID): Promise<boolean> => {
         try {
            const request = await fetch(`${this.BACKEND_ENDPOINT}/message/${conviteId}/decline`, {
                method: "POST",
                headers: this.GET_HEADERS,
                credentials: "include",
            })
            const {data} = await request.json();
            return data;
        }
        catch(e) {
            return false;
        }
    }
    
    // search
    buscar = async (texto: string, filtros: SearchParamsDTO): Promise<SearchResultsOutputDTO> => { throw new Error("Not implemented: 8") }
    chatIA = async (prompt: string): Promise<MensagemDTO> => { throw new Error("Not implemented: 9") }
    
    // helpers
    private salvarDadosUsuarioNoNavegador = (userData: UserSummaryDTO) => {
        localStorage.setItem(this.USER_CACHE_KEY, userData.username);
    }
    deletarDadosUsuarioNoNavegador = () => {
        localStorage.removeItem(this.USER_CACHE_KEY)
    }
    obterDadosUsuarioNoNavegador = (): string | null => {
        if (typeof window === 'undefined' || !localStorage) return null;
        return localStorage.getItem(this.USER_CACHE_KEY)?.toString() ?? null;
    }
}