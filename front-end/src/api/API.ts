import type { UserProfileDTO } from "./tipos";

export default class {
    get BACKEND_ENDPOINT() { return `http://localhost:8080/api` }
    
    obterDadosDoPerfil = async (perfil: string): Promise<UserProfileDTO> => {
        const urlEndpoint = new URL(`${this.BACKEND_ENDPOINT}/user`)
        urlEndpoint.searchParams.append("user", perfil)
        const backendRequest = await fetch(urlEndpoint, {
            method: 'GET',
            credentials: "include"
        });
        const { data } = await backendRequest.json()
        return {
            url: data.url,
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
}