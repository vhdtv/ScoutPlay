import { createFileRoute, Link, useRouter } from '@tanstack/react-router'
import { LoggedHeader } from '#/components/Header';
import Footer from '#/components/Footer';
import API from '#/api/API';
import { useRef, useState } from 'react';
import type { CommentDTO, PostDetailsDTO } from '#/api/tipos';
import type { UUID } from 'crypto';
import ProfilePicture from '#/components/ui/ProfilePicture';

const api = new API

function tempoRelativo(data: Date | string): string {
  const agora = Date.now()
  const then = new Date(data).getTime()
  const diff = Math.max(0, agora - then)
  const seg = Math.floor(diff / 1000)
  if (seg < 60) return 'agora'
  const min = Math.floor(seg / 60)
  if (min < 60) return `${min}min`
  const h = Math.floor(min / 60)
  if (h < 24) return `${h}h`
  const d = Math.floor(h / 24)
  if (d < 7) return `${d}d`
  const sem = Math.floor(d / 7)
  if (sem < 5) return `${sem}sem`
  const mes = Math.floor(d / 30)
  if (mes < 12) return `${mes}m`
  return `${Math.floor(d / 365)}a`
}

export const Route = createFileRoute('/post/$post_id')({
  component: RouteComponent,
  loader: async ({params}) => {
    const post = await api.obterPost(params.post_id);
    let comentarios: CommentDTO[] = [];
    try {
      const result = await api.obterComentarios(params.post_id as UUID);
      comentarios = Array.isArray(result) ? result : [];
    } catch {}
    return { post, comentarios }
  }
})

function LikeCommentButton({ commentId, postId, count, liked }: { commentId: string, postId: UUID, count: number, liked: boolean }) {
  const [curtido, setCurtido] = useState(liked);
  const [total, setTotal] = useState(count);

  const toggle = async () => {
    if (curtido) {
      await api.descurtirComentario(postId, commentId);
      setCurtido(false);
      setTotal(t => t - 1);
    } else {
      await api.curtirComentario(postId, commentId);
      setCurtido(true);
      setTotal(t => t + 1);
    }
  };

  return (
    <button onClick={toggle} className={`flex items-center gap-1 text-xs transition cursor-pointer ${curtido ? 'text-sky-600 font-semibold' : 'text-slate-500 hover:text-sky-600'}`}>
      <span className="material-symbols-outlined" style={{ fontSize: '0.9rem' }}>thumb_up</span>
      {total > 0 && <span>{total}</span>}
    </button>
  );
}

function ReplyInput({ postId, commentId, onReply }: { postId: UUID, commentId: string, onReply: (reply: CommentDTO) => void }) {
  const [texto, setTexto] = useState('');
  const [enviando, setEnviando] = useState(false);

  const enviar = async () => {
    const t = texto.trim();
    if (!t) return;
    setEnviando(true);
    try {
      const reply = await api.responderComentario(postId, commentId, t);
      setTexto('');
      onReply(reply);
    } finally {
      setEnviando(false);
    }
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      enviar();
    }
  };

  return (
    <div className='flex gap-1 mt-1 ms-8'>
      <textarea
        value={texto}
        onChange={e => setTexto(e.target.value)}
        onKeyDown={onKeyDown}
        placeholder='Responder... (Enter para enviar)'
        disabled={enviando}
        rows={1}
        className='outline-none grow text-xs p-1.5 border border-slate-300 rounded-md hover:border-slate-400 focus:border-slate-400 resize-none'
      />
      <button onClick={enviar} disabled={enviando || !texto.trim()} className='flex items-center p-1.5 bg-white hover:bg-sky-700 hover:text-white transition cursor-pointer rounded-full outline-none disabled:opacity-40'>
        <span className="material-symbols-outlined" style={{ fontSize: '1rem' }}>send</span>
      </button>
    </div>
  );
}

function CommentItem({ comment, postId, depth = 0 }: { comment: CommentDTO, postId: UUID, depth?: number }) {
  const [respondendo, setRespondendo] = useState(false);
  const [respostas, setRespostas] = useState<CommentDTO[]>(comment.respostas ?? []);

  const adicionarResposta = (r: CommentDTO) => {
    setRespostas(prev => [...prev, r]);
    setRespondendo(false);
  };

  return (
    <div className={depth > 0 ? 'ms-6 border-l-2 border-slate-200 ps-2' : ''}>
      <div className='flex gap-2 px-2 py-1 items-start'>
        <Link to='/user/$user' params={{ user: comment.por.username }} className='shrink-0'>
          <ProfilePicture user={comment.por} className='w-7' />
        </Link>
        <div className='flex flex-col grow min-w-0'>
          <span className='text-xs font-semibold leading-4'>{comment.por.nome}</span>
          <span className='text-sm leading-snug break-words'>{comment.texto}</span>
          <div className='flex gap-3 mt-1'>
            <LikeCommentButton commentId={comment.id} postId={postId} count={comment.quantidadeLike} liked={comment.euCurti} />
            {depth === 0 && (
              <button onClick={() => setRespondendo(v => !v)} className='text-xs text-slate-500 hover:text-sky-600 transition cursor-pointer'>
                Responder
              </button>
            )}
          </div>
        </div>
      </div>
      {respondendo && <ReplyInput postId={postId} commentId={comment.id} onReply={adicionarResposta} />}
      {respostas.map((r, i) => <CommentItem key={r.id ?? i} comment={r} postId={postId} depth={depth + 1} />)}
    </div>
  );
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

export function PostMediaComponent({post, className, showLike = true}: {post: PostDetailsDTO, className?: string, showLike?: boolean}) {
  return (
    <div className={`relative w-full h-full ${className ?? ''}`}>
      <div className="absolute w-full h-full flex justify-start items-end overflow-hidden gap-2">
        <div className="absolute z-0 top-0 start-0 end-0 bottom-0 bg-linear-to-b from-slate-0 to-slate-950/40"></div>
        {showLike && (
          <div className="shrink-0 bottom-16 start-4 absolute z-1">
            <LikeButton contagemAtualDeLike={post.interacoes?.quantidadeLike ?? 0} postUrl={post.url} usuarioJaDeuLike={post.interacoes?.deuLike ?? false} />
          </div>
        )}
        <div className='grow-1 overflow-hidden backdrop-blur-xs'>
          <div className='w-full p-3 ps-4 flex gap-4 items-center justify-start snap-x scroll-pl-6 overflow-x-auto no-scrollbar'>
            {post.interacoes?.destaques?.map((destaque, index) => <span key={index} className='shrink-0 p-1 rounded-full px-4 flex items-center border border-white text-white relative'>#{destaque.texto} <span className='absolute w-5 h-5 aspect-square rounded-full font-bold bottom-0 translate-x-2 translate-y-1 end-0 bg-white text-black flex items-center justify-center text-xs'>{destaque.contador}</span></span>)}
          </div>
        </div>
      </div>
      {
        post.media.mimeType.startsWith("video")
        ? <video className='w-full h-full object-contain' loop autoPlay={true} muted src={api.obterMidia(post.media.src)} />
        : <img src={api.obterMidia(post.media.src)} className='w-full h-full object-contain' />
      }
    </div>
  )
}

function RouteComponent() {
  const { post, comentarios: comentariosIniciais } = Route.useLoaderData();
  const router = useRouter();
  const [comentarios, setComentarios] = useState<CommentDTO[]>(comentariosIniciais ?? []);
  const [textoComentario, setTextoComentario] = useState('');
  const [enviando, setEnviando] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  const enviarComentario = async () => {
    const t = textoComentario.trim();
    if (!t) return;
    setEnviando(true);
    try {
      const novo = await api.enviarComentario(post.url, t);
      setComentarios(prev => [...prev, novo]);
      setTextoComentario('');
      setTimeout(() => bottomRef.current?.scrollIntoView({ behavior: 'smooth' }), 50);
    } finally {
      setEnviando(false);
    }
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      enviarComentario();
    }
  };

  return (
    <div className='min-h-screen flex flex-col bg-slate-900'>
      <LoggedHeader/>
      <div className="flex-1 flex items-stretch justify-center px-2 py-4">
        <div className='relative w-full max-w-5xl shadow-2xl flex flex-col md:flex-row rounded-xl overflow-hidden' style={{ minHeight: '70vh' }}>
          <button
            onClick={() => router.history.back()}
            className='absolute top-3 right-3 z-20 w-8 h-8 flex items-center justify-center rounded-full bg-black/60 hover:bg-red-600 text-white transition cursor-pointer'
            title='Fechar'
          >
            <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>close</span>
          </button>

          {/* Media */}
          <div className="md:flex-1 bg-slate-950 flex items-center justify-center min-h-64 md:min-h-0">
            <PostMediaComponent post={post} className='w-full h-full max-h-[80vh] object-contain' />
          </div>

          {/* Comments panel */}
          <div className="w-full md:w-80 lg:w-96 bg-white flex flex-col" style={{ minHeight: '60vh' }}>
            {/* Author header */}
            <div className='flex gap-2 p-3 border-b border-slate-200 bg-slate-50 shrink-0'>
              <ProfilePicture user={post.autor} className="w-9" />
              <div className='flex flex-col justify-center flex-1 min-w-0'>
                <div className='flex items-center gap-1.5'>
                  <span className='text-sm font-bold leading-tight'>{post.autor.nome}</span>
                  <span className='text-xs text-slate-400'>·</span>
                  <span className='text-xs text-slate-400'>{tempoRelativo(post.criadoEm)}</span>
                </div>
                {post.autor.posicao && <span className='text-xs text-sky-600'>{post.autor.posicao}</span>}
              </div>
            </div>

            {/* Post title/description */}
            {(post.titulo || post.descricao) && (
              <div className='px-3 py-2 border-b border-slate-100 shrink-0'>
                {post.titulo && <p className='text-sm font-semibold text-slate-800'>{post.titulo}</p>}
                {post.descricao && <p className='text-xs text-slate-500 mt-0.5'>{post.descricao}</p>}
              </div>
            )}

            {/* Comments list — scrollable */}
            <div className='flex-1 overflow-y-auto py-2 min-h-0'>
              {comentarios.length === 0 && (
                <div className='flex flex-col items-center justify-center h-full text-slate-400 gap-1 py-8'>
                  <span className='material-symbols-outlined' style={{ fontSize: '1.8rem' }}>chat_bubble_outline</span>
                  <span className='text-sm'>Sem comentários ainda</span>
                </div>
              )}
              {comentarios.map((c, i) => <CommentItem key={c.id ?? i} comment={c} postId={post.url} />)}
              <div ref={bottomRef} />
            </div>

            {/* Comment input — always at bottom */}
            <div className='p-3 border-t border-slate-200 flex gap-2 bg-white shrink-0'>
              <textarea
                value={textoComentario}
                onChange={e => setTextoComentario(e.target.value)}
                onKeyDown={onKeyDown}
                placeholder='Adicione um comentário... (Enter para enviar)'
                disabled={enviando}
                rows={2}
                className='outline-none flex-1 text-sm p-2 border border-slate-200 rounded-xl hover:border-slate-300 focus:border-sky-400 resize-none bg-slate-50'
              />
              <button
                onClick={enviarComentario}
                disabled={enviando || !textoComentario.trim()}
                className='w-9 h-9 flex items-center justify-center bg-sky-600 hover:bg-sky-700 text-white self-end transition cursor-pointer rounded-full disabled:opacity-40 shrink-0'
              >
                <span className="material-symbols-outlined" style={{ fontSize: '1.1rem' }}>send</span>
              </button>
            </div>
          </div>
        </div>
      </div>
      <Footer/>
    </div>
  )
}
