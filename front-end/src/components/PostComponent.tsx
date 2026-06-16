import API from "#/api/API";
import type { PostDetailsDTO, PostHighlightDTO } from "#/api/tipos";
import { useEffect, useState, type ComponentProps } from "react";
import ProfilePicture from "./ui/ProfilePicture";
import { Link } from "@tanstack/react-router";

const api = new API;
export default function({post, ...props}: {post: PostDetailsDTO} & ComponentProps<"div">) {
    const [listaDestaques, setListaDestaques] = useState(post.interacoes.destaques ?? [])
    useEffect(() => {
        api.obterDestaques(post.url)
            .then(listaDestaques => {
                setListaDestaques((destaquesQueJaPossui) => mesclar(destaquesQueJaPossui, listaDestaques))
            })
    }, [])

    return (
        <div className={`card overflow-hidden flex flex-col ${props.className}`}>
            <div className="media bg-mist-800 min-h-[15em]">
                {
                    post.media.mimeType.startsWith("image")
                    ? <img src={api.obterMidia(post.media.src)}/>
                    : <video src={api.obterMidia(post.media.src)} muted loop autoPlay={true}></video>
                }
            </div>
            <div className="content">
                <div className='px-8 pt-6 mb-4 flex flex-col gap-4'>
                <div role="author-details" className="header flex gap-3 items-center">
                    <ProfilePicture user={post.autor} className="w-12" />
                    <div className="user-data flex items-start flex-col">
                        <span className='block font-semibold h6 text-md/6 mb-1'>{`${post.autor.nome} ${post.autor.sobrenome}`}</span>
                        <div className='block text-sm/4 cursor-pointer rounded-full'>
                            <FollowButtonComponent username={post.autor.username} value={post.metadados.segueConta} />
                        </div>
                    </div>
                </div>
                <p role="post-description" className='text-lg/6'>{post.descricao}</p>
                <div role='post-actions' className='flex items-center gap-3'>
                    <LikeButtomComponent post={post} />
                    <Link to="/post/$post_id" params={{post_id: post.url}} className='py-2 ps-3 pe-3 rounded-full bg-mist-100 flex gap-1.5 items-center text-sm/6 hover:bg-mist-200'>
                        <i className="ri-chat-3-line"></i>
                        <span className='h6'>{post.interacoes.quantidadeComentarios}</span>
                    </Link>
                </div>
                </div>
                <div role="highlights" className='flex flex-col'>
                <div className="flex items-center pb-10 px-8 no-scrollbar overflow-x-auto overflow-y-hidden gap-4">
                    {listaDestaques?.filter(destaque => destaque.marcadoPeloUsuario).map((destaque, index) => <HightlightComponent key={index} data={destaque} /> )}
                    {/* <button className="p-2 border border-mist-400 rounded-full text-mist-400 border-dashed hover:bg-mist-100 cursor-pointer">
                        <i className="ri-add-line"></i>
                    </button> */}
                </div>
                </div>
            </div>
        </div>
    )
}

export function FollowButtonComponent({username, value}: {username: string, value: boolean}) {
    const [serverRequested, setServerRequested] = useState(false);
    const [segueConta, setSegueConta] = useState(value);
    const seguirConta = async () => {
        if(serverRequested) return;
        setServerRequested(true);
        const result = await api.seguir(username);
        setServerRequested(false);
        if(!result) {
        setSegueConta(false);
        return;
        }
        else setSegueConta(true);
    }
    const pararDeSeguirConta = async () => {
        if(serverRequested) return;
        setServerRequested(true);
        const result = await api.pararDeSeguir(username);
        setServerRequested(false);
        if(!result) {
        setSegueConta(true);
        return;
        }
        else setSegueConta(false);
    }

    return segueConta
        ? <button onClick={pararDeSeguirConta} className='text-xs/4 flex items-center py-1 px-2 rounded-full transition cursor-pointer outline-none bg-indigo-200 text-indigo-900 font-semibold hover:bg-mist-100 hover:text-mist-600 focus:bg-mist-100 focus:text-mist-600'>Seguindo</button>
        : <button onClick={seguirConta} className='text-xs/4 flex items-center py-1 px-2 rounded-full transition cursor-pointer outline-none font-semibold hover:bg-mist-100 focus:bg-mist-100'>Seguir</button>
}

function LikeButtomComponent({post}: {post: PostDetailsDTO}) {
    const [currentValue, setCurrentValue] = useState(post.interacoes.deuLike)
    const [likeCount, setLikeCount] = useState(post.interacoes.quantidadeLike)

    const darLike = async () => {
        const result = await api.darLikeEmPost(post.url)
        if(result !== true) return;
        setLikeCount(likeCount + 1);
        setCurrentValue(true);
    }

    const retirarLike = async () => {
        const result = await api.darDislikeEmPost(post.url)
        if(result !== true) return;
        setLikeCount(likeCount - 1);
        setCurrentValue(false);
    }

    if(!currentValue) {
        return (
            <button onClick={darLike} className='py-2 ps-3 pe-3 rounded-full flex gap-1.5 items-center text-sm/6 cursor-pointer bg-mist-100 hover:bg-indigo-200'>
                <i className="ri-thumb-up-line"></i>
                <span className='h6'>{likeCount}</span>
            </button>
        )
    }
    return (
        <button onClick={retirarLike} className='py-2 ps-3 pe-3 rounded-full flex gap-1.5 items-center text-sm/6 cursor-pointer hover:bg-mist-100 text-indigo-800 bg-indigo-200'>
            <i className="ri-thumb-up-line"></i>
            <span className='h6'>{likeCount}</span>
        </button>
    ) 
}

export function HightlightComponent({data}: {data: PostHighlightDTO}) {
    return (
        <button className={`py-1 px-3 shrink-0 uppercase font-semibold text-xs rounded-full border border-mist-300 text-sm/4 relative ${data.marcadoPeloUsuario ? "bg-mist-200" : "text-mist-500"}`}>
            <span>{data.nome}</span>
        </button>
    )
}

export const mesclar = (destaque: PostHighlightDTO[], novosDestaques: PostHighlightDTO[]) => {
    const result = [...destaque];
    novosDestaques.forEach(destaque => {
        const index = result.findIndex(item => item.aliasId === destaque.aliasId);
        if(index === -1) result.push(destaque);
        else result[index] = destaque;
    })
    return result;
}