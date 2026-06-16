import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import Header from '#/components/Header';
import Footer from '#/components/Footer';
import API from '#/api/API';
import { useEffect, useState } from 'react';
import type { CommentDTO, PostDetailsDTO, PostHighlightDTO } from '#/api/tipos';
import ProfilePicture from '#/components/ui/ProfilePicture';
import TextField from '#/components/ui/TextField';
import { mesclar } from '#/components/PostComponent';

const api = new API;
export const Route = createFileRoute('/post/$post_id')({
  component: RouteComponent,
  loader: async ({params}) => {
    return { post_id: params.post_id }
  }
})

function RouteComponent() {
  const { post_id } = Route.useLoaderData();
  const [post, definirPost] = useState<PostDetailsDTO>();
  const [comentarios, definirCommentarios] = useState([] as CommentDTO[]);
  const [listaDestaques, setListaDestaques] = useState(post?.interacoes.destaques ?? [])
  const navigate = useNavigate();

  useEffect(() => {
      api.obterPost(post_id)
        .then((data) => {
          definirPost(data)
          api.obterDestaques(data.url)
            .then(listaDestaques => {
                setListaDestaques((destaquesQueJaPossui) => mesclar(destaquesQueJaPossui, listaDestaques))
          })
          api.obterComentarios(data.url)
            .then(dados => definirCommentarios(dados))
      })
  }, [])

  function HighlightButtonComponent({post, destaque}: {post: PostDetailsDTO, destaque: PostHighlightDTO}) {
    const darDestaque = async () => {
      await api.darDestaque(post.url, {id: destaque.aliasId, texto: destaque.nome});
      navigate({reloadDocument: true})
    }
    
    const retirarDestaque = async () => {
      if(!destaque.aliasId) return;
      await api.retirarDestaque(post.url, destaque.aliasId)
      navigate({reloadDocument: true})
    }

    return (
    <>
      {
        destaque.marcadoPeloUsuario
        ? (
          <button onClick={retirarDestaque} className="cursor-pointer uppercase font-semibold py-1 px-3 shrink-0 rounded-full border border-mist-300 text-xs/4 relative bg-mist-100 hover:bg-mist-50">
              <span>{destaque.nome}</span>
              { destaque.count < 10 && <span className='w-5 h-5 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-3 translate-y-3'>{destaque.count}</span>}
              { destaque.count < 100 && <span className='w-6 h-6 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-3 translate-y-3'>{destaque.count}</span>}
              { destaque.count >= 100 && <span className='w-10 h-6 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-6 translate-y-4'>{destaque.count}</span>}
          </button>
        )
        : (
          <button onClick={darDestaque} className="cursor-pointer uppercase font-semibold py-1 px-3 shrink-0 rounded-full border border-mist-300 text-xs/4 relative text-mist-500 hover:bg-mist-50">
              <span>{destaque.nome}</span>
              { destaque.count < 10 && <span className='w-5 h-5 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-3 translate-y-3'>{destaque.count}</span>}
              { destaque.count < 100 && <span className='w-6 h-6 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-3 translate-y-3'>{destaque.count}</span>}
              { destaque.count >= 100 && <span className='w-10 h-6 flex items-center text-mist-700 justify-center bg-white absolute bottom-0 end-0 rounded-full border border-mist-300 translate-x-6 translate-y-4'>{destaque.count}</span>}
          </button>
        )
      }
    </>
      
    )
  }
  
  const enviarComentario = async (form: FormData) => {
    if(!post) return;
    const comentario = form.get("comment")!.toString();
    const result = await api.enviarComentario(post.url, comentario);
    definirCommentarios([...comentarios, result])
  }

  return (
    <>
      {
        post && (
          <div className='w-screen min-h-screen bg-pattern field-pattern flex flex-col relative'>
            <Header />
            <main className='grow-1 flex container mx-auto'>
              <div className="card m-auto overflow-hidden flex w-full max-w-[1200px] h-[60vh]">
                <div className='basis-7/12 bg-mist-900 flex items-center justify-center shrink-0 overflow-hidden'>
                  {
                    post.media.mimeType.startsWith("image")
                    ? <img className='w-full h-full grow object-contain object-center max-w-full' src={api.obterMidia(post.media.src)}/>
                    : <video className='w-full grow object-contain object-center max-w-full' src={api.obterMidia(post.media.src)} muted loop autoPlay={true}></video>
                  }
                </div>
                <div className='basis-5/12 shrink-0 h-full flex flex-col overflow-hidden'>
                  <div className='border-b-1 shrink-0 border-gray-300 p-2 flex gap-2 items-center overflow-hidden'>
                    <Link to='/user/$user' params={{user: post.autor.username}}>
                      <ProfilePicture user={post.autor} className="w-10" />
                    </Link>
                    <div className=''>
                      <Link to='/user/$user' params={{user: post.autor.username}} className='font-semibold h6 text-mist-700 text-sm/4'>{`${post.autor.nome} ${post.autor.sobrenome}`}</Link>
                      <FollowButtonComponent username={post.autor.username} value={post.metadados.segueConta} />
                    </div>
                  </div>
                  <div className='overflow-auto grow pt-5'>
                    { comentarios.length == 0 
                      ? <span className='block text-center text-mist-400 py-8'>Sem comentários ainda</span>
                      : comentarios.map((comment, index) => <CommentComponent key={index} comentario={comment} />)
                    }
                  </div>
                  <div className='relative w-full'>
                    <div className='flex gap-4 overflow-x-auto no-scrollbar w-full px-4 py-4'>
                      {listaDestaques.map((destaque, index) => <HighlightButtonComponent key={index} destaque={destaque} post={post} />)}
                    </div>
                  </div>
                  <form action={enviarComentario} className='border-t-1 shrink-0 border-gray-300 p-2 flex gap-2 items-center overflow-hidden flex items-center justify-center'>
                    <TextField fieldName='comment' placeholder='Faça um comentário' className='w-full'/>
                    <button className='bg-mist-200 outline-0 p-2 rounded-full relative flex items-center justify-center cursor-pointer transition hover:bg-indigo-200 hover:text-indigo-900 focus:bg-indigo-200 focus:text-indigo-900'>
                      <i className="ri-send-ins-line translate-y-0.5"></i>
                    </button>
                  </form>
                </div>
              </div>
            </main>
            <Footer />
          </div>
        )
      }
    </>
  )
}

function FollowButtonComponent({username, value}: {username: string, value: boolean}) {
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
          ? <button onClick={pararDeSeguirConta} className='text-xs/3 block hover:text-mist-500 cursor-pointer'>Seguindo</button>
          : <button onClick={seguirConta} className='text-xs/3 block hover:text-mist-500 cursor-pointer'>Seguir</button>
}

function CommentComponent({comentario}: {comentario: CommentDTO}) {
  return (
    <div className='block border-red-400 w-full px-3 mb-4 flex gap-3 items-center'>
      <ProfilePicture user={comentario.por} className="w-9 shrink-0" />
      <div className='flex flex-col gap-0.5'>
        <span className='text-xs/3 font-semibold h6 text-mist-600'>{`${comentario.por.nome} ${comentario.por.sobrenome}`}</span>
        <p className='pt-0.5'>{comentario.texto}</p>
      </div>
    </div>
  )
}