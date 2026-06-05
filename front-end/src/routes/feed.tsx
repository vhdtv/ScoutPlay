import MockAPI from '#/api/MockAPI'
import type { MensagemDTO, PostDetailsDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import { createFileRoute, Link } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { PostMediaComponent } from './post/$post_id'
import ProfilePicture from '#/components/ui/ProfilePicture'

const api = new MockAPI

export const Route = createFileRoute('/feed')({
  component: RouteComponent,
})

function AIChatComponent() {
  const [mensagens, definirMensagens] = useState([] as MensagemDTO[])

  const enviarPrompt = async (form: FormData) => {
    const pergunta = form.get("pergunta")?.toString();
    if(!pergunta || pergunta == "") return;
    
    definirMensagens((_valorAntigo) => [..._valorAntigo, {autor: 'USUARIO', texto: pergunta}]);
    const resposta = await api.chatIA(pergunta);
    definirMensagens((_valorAntigo) => [..._valorAntigo, resposta]);
  }

  return (
    <div className='fixed rounded-t-lg self-end bottom-16 end-2 bg-slate-100 shadow-xl max-w-[400px] md:min-w-[400px]'>
      <div className='px-4 py-3 border-b-1'>
        <span className='font-bold text-slate-900'>O Espec<span className='text-sky-500 uppercase'>ia</span>lista</span>
      </div>
      <div className='overflow-hidden relative h-[30vh]'>
        <div className="h-full flex flex-col gap-2 relative grow basis-0 w-full overflow-y-auto p-3">
          {mensagens.map((mensagem, index) => {
            return mensagem.autor == "USUARIO"
            ? <span key={index} className='p-2 px-4 rounded-lg bg-slate-200 self-end text-end'>{mensagem.texto}</span>
            : <span key={index} className='p-2 text-start'>{mensagem.texto}</span>
          })}
        </div>
      </div>
      <form action={enviarPrompt} className='shrink-0 p-2 relative text-xs flex gap-1'>
        <textarea name="pergunta" placeholder='Adicione um comentário...' className='outline-none w-full p-2 border-1 border-slate-300 rounded-md hover:border-slate-400 hover:bg-slate-200 focus:border-slate-400 focus:bg-slate-200 resize-none'> </textarea>
        <button type="submit" className='flex items-center p-2 bg-white hover:bg-sky-700 m-auto hover:text-white focus:bg-sky-700 focus:text-white transition cursor-pointer rounded-full outline-none'>
          <span className="material-symbols-outlined" style={{fontSize: "1.2rem"}}> send </span>
        </button>
      </form>
    </div>
  )
}

function PostFeedComponent({post}: {post: PostDetailsDTO}) {
  const [esperandoServidor, definirEsperandoServidor] = useState(false);
  const [seguindoConta, definirSeguindoConta] = useState(post.metadados.segueConta ?? false);

  const seguirConta = async (username: string) => {
    if(esperandoServidor) return;
    definirEsperandoServidor(true);
    const result = await api.seguir(username);
    definirEsperandoServidor(false);
    if(!result) {
      definirSeguindoConta(false);
      return;
    }
    else definirSeguindoConta(true);
  }
  const pararDeSeguirConta = async (username: string) => {
    if(esperandoServidor) return;
    definirEsperandoServidor(true);
    const result = await api.pararDeSeguir(username);
    definirEsperandoServidor(false);
    if(!result) {
      definirSeguindoConta(true);
      return;
    }
    else definirSeguindoConta(false);
  }

  function BotaoSeguirComponent() {
    return seguindoConta
      ? <button onClick={() => pararDeSeguirConta(post.autor.url)} className={`text-xs font-bold text-xs/3 underline-offset-4 ${esperandoServidor ? "opacity-40" : "hover:underline cursor-pointer"}`}>Seguindo</button>
      : <button onClick={() => seguirConta(post.autor.url)} className={`text-xs font-bold text-xs/3 underline-offset-4 text-sky-700 ${esperandoServidor ? "opacity-40" : "hover:underline cursor-pointer"}`}>Seguir</button>
  }

  return (
    <Link to="/post/$post_id" params={{post_id: post.url}} className='relative overflow-hidden rounded-2xl'>
      <div className='flex gap-2 items-center justify-start bg-slate-200 p-2'>
        <div className="w-8">
          <ProfilePicture user={post.autor} />
        </div>
        <div className='flex flex-col'>
          <span className='text-sm'>{post.autor.nome}</span>
          <BotaoSeguirComponent />
        </div>
      </div>
      <PostMediaComponent post={post} />
    </Link>
  );
}

export function RouteComponent() {
  const postsCache: PostDetailsDTO[] = []
  const [postsInView, setPostsInView] = useState([] as PostDetailsDTO[]);

  const obterPostsParaFeed = async (page = 0) => {
    const { data } = await api.obaterPostsFeed({page});
    setPostsInView(data);
    postsCache.push(...data);
  }
  useEffect(() => {
    obterPostsParaFeed()
  }, [])

  return (
    <div className='min-h-screen flex flex-col'>
          <LoggedHeader/>
          <div className="container grow-1 mx-auto flex justify-center grid grid-cols-12 gap-2">
            <div className='col-span-12 md:col-span-3 xl:col-span-4 relative'>
              <div className='top-12 sticky bg-white px-6 py-4 mt-12 rounded-xl shadow-xl flex flex-col gap-3'>
                <h3 className='text-slate-900 opacity-60 text-2xl'>Filtros</h3>
                <button className='transition outline-none rounded-md px-5 text-sm py-2 text-slate-700 cursor-pointer self-end bg-white border border-slate-700 hover:bg-slate-700 hover:text-white focus:bg-slate-700 focus:text-white'>Aplicar Filtros</button>
              </div>
            </div>
            <div className='col-span-12 md:col-span-6 xl:col-span-4 flex flex-col gap-8 items-center p-4'>
              {postsInView.map((post, index) => (<PostFeedComponent key={index} post={post} />))}
            </div>
            <div className='col-span-12 md:col-span-3 xl:col-span-4'></div>
          </div>
          <AIChatComponent />
          <Footer/>
    </div>
  )
}
