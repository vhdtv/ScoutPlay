import type { UUID } from "crypto";
import type { AtletaCardDTO, AvaliacaoDTO, CommentDTO, ConfigItemDTO, MensagemDTO, PostDataInputDTO, PostDataOutputDTO, PostDetailsDTO, PostHighlightDTO, SearchParamsDTO, SearchResultsOutputDTO, UserProfileDetailDTO, UserProfileDTO, UserSummaryDTO } from "./tipos";

export default class {
    get USER_CACHE_KEY() { return "__usuario"; }
    get TIPO_CONTA_KEY() { return "__tipoConta"; }
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
            posts: (data.posts ?? []).map((p: any) => ({
                url: p.url,
                titulo: p.titulo,
                descricao: null,
                interacoes: { deuLike: false, quantidadeLike: 0 },
                criadoEm: null,
                media: { src: p.src, mimeType: p.mimeType ?? 'video/mp4', poster: null },
            })),
            souEu: data.username === this.obterDadosUsuarioNoNavegador(),
            seguidores: data.seguidores ?? 0,
            seguindo: data.seguindo ?? 0,
            souSeguidor: data.souSeguidor ?? false,
        }
    }
    atualizarPerfil = async (data: any) => { throw new Error("Not implemented") }

    atualizarInfoPerfil = async (info: { nome?: string; sobrenome?: string; username?: string }): Promise<UserSummaryDTO> => {
        const body: Record<string, string> = {}
        if (info.nome !== undefined)      body.nome      = info.nome
        if (info.sobrenome !== undefined) body.sobrenome = info.sobrenome
        if (info.username !== undefined)  body.username  = info.username

        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/info`, {
            method: 'PATCH',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        })
        if (!request.ok) {
            const json = await request.json().catch(() => ({}))
            throw new Error(json.message ?? 'Erro ao salvar perfil')
        }
        const { data } = await request.json()
        return data as UserSummaryDTO
    }
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
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/`, {
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
        try {
            const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${id}`, {
                method: "DELETE",
                credentials: "include",
            })
            return request.ok;
        } catch {
            return false;
        }
    }
    atualizarPost = async (id: PostDataOutputDTO["url"], data: PostDataOutputDTO): Promise<PostDataOutputDTO> => { throw new Error("Not implemented") }
    darLikeEmPost = async (postId: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/like`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });
        if(request.status != 200) return false;
        return true;
    }
    darDislikeEmPost = async (postId: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/dislike`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });
        if(request.status != 200) return false;
        return true;
    }
    darDestaque = async (postId: UUID, data: string | number): Promise<PostHighlightDTO> => { throw new Error("Not implemented") }
    obterComentarios = async (postId: UUID): Promise<CommentDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comments`, {
            credentials: "include",
            headers: { "Content-Type": "application/json" }
        })
        const { data } = await request.json();
        const lista: CommentDTO[] = Array.isArray(data) ? data : [];
        return lista.map(c => ({ ...c, respostas: Array.isArray(c.respostas) ? c.respostas : [] }));
    }
    enviarComentario = async (postId: UUID, comentario: string): Promise<CommentDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comment`, {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ texto: comentario })
        });
        if (!request.ok) throw new Error('Erro ao comentar')
        const { data } = await request.json();
        return { id: data.id, por: data.por, texto: data.texto, quantidadeLike: 0, euCurti: false, respostas: [] }
    }

    responderComentario = async (postId: UUID, commentId: string, texto: string): Promise<CommentDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comment/${commentId}/reply`, {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ texto })
        });
        if (!request.ok) throw new Error('Erro ao responder')
        const { data } = await request.json();
        return { id: data.id, por: data.por, texto: data.texto, quantidadeLike: 0, euCurti: false, respostas: [] }
    }

    curtirComentario = async (postId: UUID, commentId: string): Promise<void> => {
        await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comment/${commentId}/like`, {
            method: "POST", credentials: "include"
        })
    }

    descurtirComentario = async (postId: UUID, commentId: string): Promise<void> => {
        await fetch(`${this.BACKEND_ENDPOINT}/post/${postId}/comment/${commentId}/dislike`, {
            method: "POST", credentials: "include"
        })
    }
    obterMidia = (mediaPath: string): string => {
        return `${this.BACKEND_ENDPOINT}/media/${mediaPath}`;
    }
    
    seguir = async (nomeUsuario: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${nomeUsuario}/seguir`, {
            method: 'POST',
            credentials: 'include',
        });
        return request.ok;
    }
    pararDeSeguir = async (nomeUsuario: string): Promise<boolean> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${nomeUsuario}/seguir`, {
            method: 'DELETE',
            credentials: 'include',
        });
        return request.ok;
    }
    mandarConvite = async (nomeUsuario: string, texto: string): Promise<boolean> => { throw new Error("Not implemented") }
    
    buscar = async (texto: string, filtros: SearchParamsDTO): Promise<SearchResultsOutputDTO> => { throw new Error("Not implemented") }
    chatIA = async (prompt: string): Promise<MensagemDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/ia/prompt`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pergunta: prompt }),
        });
        const json = await request.json();
        if (!json.success) throw new Error(json.message ?? 'Serviço de IA indisponível');
        return { autor: 'IA', texto: json.data.resposta };
    }
    
    atualizarFotoPerfil = async (arquivo: File): Promise<string> => {
        const form = new FormData();
        form.append("arquivo", arquivo);
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/avatar`, {
            method: "POST",
            credentials: "include",
            body: form,
        });
        if (!request.ok) throw new Error("Erro ao atualizar foto de perfil");
        const { data } = await request.json();
        return data.fotoPerfil as string;
    }

    cadastrar = async (dados: {
        nome: string, sobrenome: string, email: string, senha: string,
        dataNascimento: string, tipoConta: string,
        cpf?: string, telefone?: string, cep?: string,
        peso?: string, altura?: string, posicao?: string,
        peDominante?: string, clubesAnteriores?: string
    }): Promise<UserSummaryDTO> => {
        const body = {
            ...dados,
            peso: dados.peso ? parseFloat(dados.peso) : undefined,
            altura: dados.altura ? parseFloat(dados.altura) : undefined,
        };
        const request = await fetch(`${this.BACKEND_ENDPOINT}/signup`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });
        if (request.status !== 200) {
            const { message } = await request.json();
            throw new Error(message || 'Erro ao criar conta');
        }
        const { data } = await request.json();
        this.salvarDadosUsuarioNoNavegador(data);
        return data as UserSummaryDTO;
    }

    obterPostsFeed = async ({page}: {page: number}): Promise<{page: number, pageSize: number, data: PostDetailsDTO[]}> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/post/?page=${page}&size=10`, {
            method: 'GET',
            credentials: 'include',
        });
        const { data } = await request.json();
        return {
            page,
            pageSize: 10,
            data: (data as any[]).map(item => ({
                autor: item.autor,
                criadoEm: item.criadoEm,
                descricao: item.descricao,
                interacoes: item.interacoes,
                media: item.media,
                metadados: item.metadados ?? { segueConta: false },
                titulo: item.titulo,
                url: item.url,
            })) as PostDetailsDTO[],
        };
    }
    
    // ── Avaliações ────────────────────────────────────────────────────────────

    avaliarAtleta = async (username: string, nota: number, comentario?: string): Promise<AvaliacaoDTO> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/avaliar`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nota, comentario }),
        });
        if (!request.ok) throw new Error('Erro ao avaliar atleta');
        const { data } = await request.json();
        return data as AvaliacaoDTO;
    }

    listarAvaliacoes = async (username: string): Promise<AvaliacaoDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/avaliacoes`, {
            credentials: 'include',
        });
        const { data } = await request.json();
        return (data ?? []) as AvaliacaoDTO[];
    }

    // ── Shortlist ─────────────────────────────────────────────────────────────

    adicionarAShortlist = async (username: string): Promise<void> => {
        await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/shortlist`, {
            method: 'POST',
            credentials: 'include',
        });
    }

    removerDaShortlist = async (username: string): Promise<void> => {
        await fetch(`${this.BACKEND_ENDPOINT}/user/${username}/shortlist`, {
            method: 'DELETE',
            credentials: 'include',
        });
    }

    obterShortlist = async (): Promise<AtletaCardDTO[]> => {
        const request = await fetch(`${this.BACKEND_ENDPOINT}/shortlist`, {
            credentials: 'include',
        });
        const { data } = await request.json();
        return (data ?? []) as AtletaCardDTO[];
    }

    // ── Busca de atletas ──────────────────────────────────────────────────────

    buscarAtletas = async (params: {
        posicao?: string, peDominante?: string, nome?: string, page?: number
    }): Promise<AtletaCardDTO[]> => {
        const url = new URL(`${this.BACKEND_ENDPOINT}/atletas`);
        if (params.posicao) url.searchParams.set('posicao', params.posicao);
        if (params.peDominante) url.searchParams.set('peDominante', params.peDominante);
        if (params.nome) url.searchParams.set('nome', params.nome);
        if (params.page != null) url.searchParams.set('page', String(params.page));
        const request = await fetch(url.toString(), { credentials: 'include' });
        const { data } = await request.json();
        return (data ?? []) as AtletaCardDTO[];
    }

    // ── Local storage ─────────────────────────────────────────────────────────

    private salvarDadosUsuarioNoNavegador = (userData: any) => {
        localStorage.setItem(this.USER_CACHE_KEY, userData.username);
        if (userData.tipoConta) {
            const tipos: string[] = Array.isArray(userData.tipoConta)
                ? userData.tipoConta
                : [userData.tipoConta];
            localStorage.setItem(this.TIPO_CONTA_KEY, JSON.stringify(tipos));
        }
    }

    private deletarDadosUsuarioNoNavegador = () => {
        localStorage.removeItem(this.USER_CACHE_KEY);
        localStorage.removeItem(this.TIPO_CONTA_KEY);
    }

    obterDadosUsuarioNoNavegador = (): string | null => {
        if (typeof window === 'undefined' || !localStorage) return null;
        return localStorage.getItem(this.USER_CACHE_KEY)?.toString() ?? null;
    }

    obterTipoContaUsuario = (): string[] => {
        if (typeof window === 'undefined' || !localStorage) return [];
        try {
            return JSON.parse(localStorage.getItem(this.TIPO_CONTA_KEY) ?? '[]');
        } catch {
            return [];
        }
    }

    isOlheiro = (): boolean => this.obterTipoContaUsuario().includes('OLHEIRO');
}