import { createFileRoute, Link } from '@tanstack/react-router'
import { LoggedHeader } from '#/components/Header';
import Footer from '#/components/Footer';
import API from '#/api/API';
import { useEffect, useState } from 'react';
import type { CommentDTO, PostDetailsDTO, UserSummaryDTO } from '#/api/tipos';
import ProfilePicture from '#/components/ui/ProfilePicture';

const api = new API

export const Route = createFileRoute('/post/$post_id')({
  component: RouteComponent,
  loader: async ({params}) => {
    return { post_id: params.post_id }
  }
})

function Comment({user, valor}: {user: UserSummaryDTO, valor: string}) {
  return <div className='flex gap-2 px-2 py-1 items-center'>
    <Link to='/user/$user' params={{user: user.username}}>
      <ProfilePicture user={user} className='w-8' />
    </Link>
    <span className='text-sm'>{valor}</span>
  </div>
}

function LikeButton({postUrl, contagemAtualDeLike, usuarioJaDeuLike}: {postUrl: string, contagemAtualDeLike: number, usuarioJaDeuLike: boolean}) {
  const [contadorLike, definirContadorLike] = useState(contagemAtualDeLike);
  const [likeDoUsuarioLogado, definirLikeDoUsuarioLogado] = useState(usuarioJaDeuLike);

  const enviarGostei = async () => {
    const result = await api.darLikeEmPost(postUrl)
    if(result === true) {
      definirContadorLike(contadorLike + 1)
      definirLikeDoUsuarioLogado(!likeDoUsuarioLogado)
    }
  }
  const enviarNaoGostei = async () => {
    const result = await api.darDislikeEmPost(postUrl)
    if(result === true) {
      definirContadorLike(contadorLike - 1)
      definirLikeDoUsuarioLogado(!likeDoUsuarioLogado)
    }
  }

  return (
    <>
      { !likeDoUsuarioLogado 
        ? (
          <button onClick={enviarGostei} className='relative transition focus:backdrop-blur-sm focus:outline-2 outline-offset-3 outline-neutral-100 border-1 border-slate-100 p-5 flex flex-col items-center justify-center rounded-full cursor-pointer text-slate-100 hover:bg-white/50 hover:border-white/0 hover:text-slate-950 hover:backdrop-blur-sm focus:bg-white/50 focus:border-white/0 focus:text-slate-950'>
            <span className="-translate-y-1 material-symbols-outlined" style={{fontSize: "1rem", lineHeight: '1em'}}> thumb_up </span>
            <span className='-translate-y-0.5 absolute bottom-2' style={{fontSize: ".8rem"}}> {contadorLike}</span>
          </button>  
        )
        : (
          <button onClick={enviarNaoGostei} className='relative transition focus:backdrop-blur-sm focus:outline-2 outline-offset-3 outline-neutral-100 border-1 border-slate-100 p-5 flex flex-col items-center justify-center rounded-full cursor-pointer bg-white/50 border-white/0 text-slate-950 backdrop-blur-sm hover:bg-white/20 hover:text-white focus:bg-white/20 focus:text-white'>
            <span className="-translate-y-1 material-symbols-outlined" style={{fontSize: "1rem", lineHeight: '1em'}}> thumb_up </span>
            <span className='-translate-y-0.5 absolute bottom-2' style={{fontSize: ".8rem"}}> {contadorLike}</span>
          </button>  
        )
      }
    </>
  )
}

export function PostMediaComponent({post, className}: {post: PostDetailsDTO, className?: string}) {
  return (
    <div className={`relative ${className}`}>
      <div className="absolute w-full h-full flex justify-start items-end overflow-hidden gap-2">
        <div className="absolute z-0 top-0 start-0 end-0 bottom-0 bg-linear-to-b from-slate-0 to-slate-950/40"></div>
        <div className="shrink-0 bottom-16 start-4 absolute z-1">
          <LikeButton contagemAtualDeLike={post.interacoes?.quantidadeLike ?? 0} postUrl={post.url} usuarioJaDeuLike={post.interacoes?.deuLike ?? false} />
        </div>
        <div className='grow-1 overflow-hidden backdrop-blur-xs'>
          <div className='w-full p-3 ps-4 flex gap-4 items-center justify-start snap-x scroll-pl-6 overflow-x-auto no-scrollbar'>
            {post.interacoes?.destaques?.map((destaque, index) => <span key={index} className='shrink-0 p-1 rounded-full px-4 flex items-center border border-white text-white relative'>#{destaque.texto} <span className='absolute w-5 h-5 aspect-square rounded-full font-bold bottom-0 translate-x-2 translate-y-1 end-0 bg-white text-black flex items-center justify-center text-xs'>{destaque.contador}</span></span>)}
          </div>
        </div>
      </div>
      {
        post.media.mimeType.startsWith("video")
        ? <video className='object-fit object-center' loop autoPlay={true} muted src={api.obterMidia(post.media.src)}></video>
        : <img src={post.media.src} />
      }
    </div>
  )
}

function RouteComponent() {
  const { post_id } = Route.useLoaderData();
  const [post, definirPost] = useState<PostDetailsDTO>();
  const [comentarios, definirCommentarios] = useState([] as CommentDTO[]);

  useEffect(() => {
    api.obterPost(post_id)
      .then((data) => {
        definirPost(data)
        api.obterComentarios(data.url)
          .then(dados => definirCommentarios(dados))
      })
  }, [])

  const enviarComentario = async (form: FormData) => {
    if(!post) return;
    const comentario = form.get("comentario")!.toString();
    const result = await api.enviarComentario(post.url, comentario);
    definirCommentarios([...comentarios, result])
  }

  const seguir = async () => {
    if(!post) return;
    const result = await api.seguir(post.autor.username);
    definirPost((_old): any => ({
        ..._old,
        metadados: {
          segueConta: true
        }
      }))
  }

  const deixarDeSeguir = async () => {
    if(!post) return;
    const result = await api.pararDeSeguir(post.autor.username);
    definirPost((_old): any => ({
        ..._old,
        metadados: {
          segueConta: false
        }
      }))
  }

  return (
    <div className='page-noscroll flex flex-col'>
      <LoggedHeader/>
      {
        post && (
        <div className="container grow-1 mx-auto flex items-center justify-center">
          <div className='shadow-lg grid grid-cols-12 rounded-lg overflow-hidden max-h-96'>
              <div className="col-span-8 bg-slate-900 flex items-center h-full relative">
                <PostMediaComponent post={post} />
              </div>
              <div className="col-span-4 bg-slate-100 overflow-hidden flex h-full flex-col">
                <div className='flex gap-2 p-2 border-b-1 border-slate-300 bg-slate-200'> 
                  <ProfilePicture user={post.autor} className="w-9" />
                  <div className='flex flex-col justify-center'>
                    <small className='leading-4 font-bold'>{post.autor.nome}</small>
                    { post.metadados?.segueConta
                       ? <button onClick={deixarDeSeguir} className='text-xs/3 cursor-pointer opacity-50 hover:opacity-75s'>Deixar de seguir</button>
                       : <button onClick={seguir} className='text-xs/3 cursor-pointer opacity-50 hover:opacity-75'>Seguir</button>
                    }
                  </div>
                </div>
                <div className='flex flex-col overflow-auto grow-1 py-2'>
                  {comentarios.length == 0 && <span className='opacity-50 text-bold w-full text-center block p-4'>Sem comentários ainda</span>}
                  {comentarios.map(((comentario, index) => <Comment key={index} user={comentario.por} valor={comentario.texto} /> ))}
                </div>
                <form action={enviarComentario} className='p-4 relative text-xs flex gap-2'>
                  <textarea name="comentario" placeholder='Adicione um comentário...' className='outline-none w-full p-2 border-1 border-slate-300 rounded-md hover:border-slate-400 hover:bg-slate-200 focus:border-slate-400 focus:bg-slate-200 resize-none'></textarea>
                  <button type="submit" className='flex items-center p-2 bg-white hover:bg-sky-700 m-auto hover:text-white focus:bg-sky-700 focus:text-white transition cursor-pointer rounded-full outline-none'>
                    <span className="material-symbols-outlined" style={{fontSize: "1.2rem"}}> send </span>
                  </button>
                </form>
              </div>
            </div>
        </div>
        )
      }
      <Footer/>
    </div>
  )
}

