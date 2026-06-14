import type { UUID } from "crypto";

export type UserSummaryDTO = {
    username: string,
    nome: string,
    iniciais: string,
    fotoPerfil: string | null,
    sobrenome: string | null,
}

export type UserProfileDTO = UserSummaryDTO & {
    idade: number,
    tipoConta: TipoContaDTO[]
    detalhesPerfil: UserProfileDetailDTO
    posts: PostDTO[],
    souEu: boolean,
    configuracoes?: ConfigItemDTO
}

export type TipoContaDTO = "ATLETA" | "RESPONSAVEL"

export type UserProfileDetailDTO = {
    RESPONSAVEL?: UserSummaryDTO[],
    PE_DOMINANTE?: string,
    CIDADE?: string,
    POSICOES?: string[],
    CLUBES?: PassagemUsuarioEmClubeDTO[],
    [v: string]: any
}

export type PassagemUsuarioEmClubeDTO = {
    url: string,
    nome: string,
    data: {
        inicio: Date,
        fim?: Date
    }
}

export type PostDataInputDTO = {
    titulo: string,
    descricao?: string,
    arquivo: File,
}

export type PostDataOutputDTO = {
    url: string,
    titulo: string,
    descricao?: string,
    src: string,
    poster: string,
    criadoEm: Date,
    tipoMidia: "VIDEO" | "IMAGE",
    autor: UserSummaryDTO,
}

export type PostHighlightDTO = {
    aliasId?: UUID,
    nome: string,
    count: number,
    marcadoPeloUsuario: boolean,
}

export type PostDetailsDTO = PostDTO & {autor: UserSummaryDTO, metadados: {segueConta: boolean}}

export type SearchParamsDTO = {
    peDominante?: "DIREITO" | "ESQUERDO",
    cidade?: string,
    posicoes?: UUID[],
    potencialDeCraques: boolean,
}

export type SearchResultsOutputDTO = {
    dados: SearchedItem[]
}

export type SearchedItem = 
      { tipo: "PERFIL", dado: UserSummaryDTO } 
    | { tipo: "POST", dado: PostDataOutputDTO }

export type PostDTO = {
    url: UUID,
    titulo: string,
    descricao: string | null,
    interacoes: {
        /* Fala se o perfil que está logado já deu like no post */
        deuLike: boolean,
        quantidadeLike: number,
        destaques?: PostHighlightDTO[] 
    },
    criadoEm: Date,
    media: {
        poster?: string,
        mimeType: HTMLSourceElement['type'],
        src: string,
    }
}

export type PostDataUpdateInputDTO = {
    titulo?: string,
    descricao?: string,
}

export type ConfigItemDTO = {
    OLHEIROS_PODEM_CONECTAR_PELO_WHATSAPP?: boolean,
    OCULTAR_INFORMACOES_PESSOAIS_PUBLICAMENTE?: boolean
}

export type CommentDTO = {
    por: UserSummaryDTO,
    texto: string,
    quantidadeLike: number
}

export type MensagemDTO = {
    autor: "IA" | "USUARIO",
    texto: string,
}

export type ConviteDTO = {
    id: UUID,
    aceito: boolean,
    destinatario: UserSummaryDTO,
    remetente: UserSummaryDTO,
    mensagem: string,
}

export type ProfileUpdateInputDTO = {
    nome?: string,
    sobrenome?: string,
    username?: string,
    fotoPerfil?: File,
    config?: ConfigItemDTO
} & ConfigItemDTO;


export type ClientSignupInputDTO = {
    email: string,
    senha: string,
    username?: string,
    dataNascimento: Date,
    cpf: string,
    nome: string,
    sobrenome: string,
    tipoConta: string,
}