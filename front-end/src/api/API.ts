import type { UUID } from "crypto";
import type { CommentDTO, ConfigItemDTO, MensagemDTO, PostDataInputDTO, PostDataOutputDTO, PostDetailsDTO, PostHighlightDTO, SearchParamsDTO, SearchResultsOutputDTO, UserProfileDetailDTO, UserProfileDTO, UserSummaryDTO } from "./tipos";

export default class {
    get USER_CACHE_KEY() { return "__usuario"; }
    get BACKEND_ENDPOINT() { return `http://localhost:8080/api`; }
    
    fazerLogin = async (email: string, senha: string): Promise<UserSummaryDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/login`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({email, senha}),
        });
        if(request.status !== 200) throw new Error(`Login failed`)
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

    obterDadosDoPerfil = async (perfil?: string): Promise<UserProfileDTO> => {
        const urlEndpoint = new URL(`${this.BACKEND_ENDPOINT}/user`);
        if(perfil) urlEndpoint.searchParams.append("user", perfil);
        else {
            const loggedUsername = this.obterDadosUsuarioNoNavegador();
            if(!loggedUsername) throw new Error(`User is not authenticated`);
            urlEndpoint.searchParams.append("user", loggedUsername.toString());
        }
        const backendRequest = await fetch(urlEndpoint, {
            method: 'GET',
            credentials: "include"
        });
        const { data } = await backendRequest.json()
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
            souEu: data.username === this.obterDadosUsuarioNoNavegador()
        }
    }
    atualizarPerfil = async (data: any) => { throw new Error("Not implemented") }
    definirDetalhePerfil = async (chave: string, valor: string): Promise<UserProfileDetailDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/profile-detail`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({chave, valor}),
        })
        const {data} = await request.json();
        return data as UserProfileDetailDTO;
    }
    removerDetalhePerfil = async (chave: string): Promise<UserProfileDetailDTO> => { 
        const request = await fetch(`${this.BACKEND_ENDPOINT}/profile-detail`, {
            method: "DELETE",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({chave}),
        })
        const {data} = await request.json();
        return data as UserProfileDetailDTO;
    }

    atualizarConfiguracao = async (config: ConfigItemDTO): Promise<boolean> => {
        try {
            const request = await fetch(`${this.BACKEND_ENDPOINT}/user`, {
                method: "PATCH",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({config}),
            })
            const {data} = await request.json();
            return true;
        }
        catch(e) {
            return false;
        }
    }
    vincularComoResponsavelAConta = async (nomeUsuario: string): Promise<boolean> => { throw new Error("Not implemented") }
    desvincularComoResponsavelAConta = async (nomeUsuario: string): Promise<boolean> => { throw new Error("Not implemented") }
    
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
    obterPost = async (postId: string): Promise<PostDetailsDTO> => { throw new Error("Not implemented") }
    deletarPost = async (id: PostDataOutputDTO["url"]): Promise<boolean> => { throw new Error("Not implemented") }
    atualizarPost = async (id: PostDataOutputDTO["url"], data: PostDataOutputDTO): Promise<PostDataOutputDTO> => { throw new Error("Not implemented") }
    darLikeEmPost = async (postId: string): Promise<boolean> => { throw new Error("Not implemented") }
    darDislikeEmPost = async (postId: string): Promise<boolean> => { throw new Error("Not implemented") }
    darDestaque = async (postId: UUID, data: string | number): Promise<PostHighlightDTO> => { throw new Error("Not implemented") }
    obterComentarios = async (url: string): Promise<CommentDTO[]> => { throw new Error("Not implemented") }
    enviarComentario = async (comentario: string): Promise<CommentDTO> => { throw new Error("Not implemented") }
    
    seguir = async (nomeUsuario: string): Promise<boolean> => { throw new Error("Not implemented") }
    pararDeSeguir = async (nomeUsuario: string): Promise<boolean> => { throw new Error("Not implemented") }
    mandarConvite = async (nomeUsuario: string, texto: string): Promise<boolean> => { throw new Error("Not implemented") }
    
    buscar = async (texto: string, filtros: SearchParamsDTO): Promise<SearchResultsOutputDTO> => { throw new Error("Not implemented") }
    chatIA = async (prompt: string): Promise<MensagemDTO> => { throw new Error("Not implemented") }
    
    obterPostsFeed = async ({page}: {page: number}): Promise<{page: number, pageSize: number, data: PostDetailsDTO[]}> => { throw new Error("Not implemented") }
    
    private salvarDadosUsuarioNoNavegador = (userData: UserSummaryDTO) => {
        localStorage.setItem(this.USER_CACHE_KEY, userData.username);
    }
    
    private deletarDadosUsuarioNoNavegador = () => {
        localStorage.removeItem(this.USER_CACHE_KEY)
    }
    
    obterDadosUsuarioNoNavegador = (): string | null => {
        if (typeof window === 'undefined' || !localStorage) return null;
        return localStorage.getItem(this.USER_CACHE_KEY)?.toString() ?? null;
    }
}