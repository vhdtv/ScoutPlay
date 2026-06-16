import API from '#/api/API'
import type { MensagemDTO, PostDetailsDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import { createFileRoute, Link } from '@tanstack/react-router'
import { useEffect, useMemo, useRef, useState, type ChangeEvent, type MouseEvent } from 'react'
import { PostMediaComponent } from './post/$post_id'
import ProfilePicture from '#/components/ui/ProfilePicture'
import Input from '#/components/ui/Input'

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

export const Route = createFileRoute('/feed')({
  component: RouteComponent,
})

const POSICOES = [
  'Goleiro', 'Zagueiro Central', 'Zagueiro Direito', 'Zagueiro Esquerdo',
  'Lateral Direito', 'Lateral Esquerdo', 'Volante Direito', 'Volante Esquerdo',
  'Meio-campista Central', 'Meio-campista Direito', 'Meio-campista Esquerdo',
  'Segundo Atacante', 'Ponta Direita', 'Ponta Esquerda', 'Centroavante',
]

// ── AI Chat ───────────────────────────────────────────────────────────────────

const MENSAGEM_BOAS_VINDAS: MensagemDTO = {
  autor: 'IA',
  texto: 'Olá! Sou o Especialista ScoutPlay. Posso te ajudar a buscar atletas por posição, pé dominante ou clube, comparar jogadores cadastrados e responder perguntas táticas sobre futebol. Experimente perguntar: "Quais atacantes estão cadastrados?" ou "Me fale sobre os goleiros disponíveis."',
}

function AIChatComponent() {
  const [aberto, setAberto] = useState(true)
  const [mensagens, definirMensagens] = useState<MensagemDTO[]>([MENSAGEM_BOAS_VINDAS])
  const [texto, setTexto] = useState('')
  const [carregando, setCarregando] = useState(false)
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [mensagens, carregando])

  const enviar = async () => {
    const pergunta = texto.trim()
    if (!pergunta || carregando) return
    setTexto('')
    definirMensagens(prev => [...prev, { autor: 'USUARIO', texto: pergunta }])
    setCarregando(true)
    try {
      const resposta = await api.chatIA(pergunta)
      definirMensagens(prev => [...prev, resposta])
    } catch {
      definirMensagens(prev => [...prev, { autor: 'IA', texto: 'Serviço de IA temporariamente indisponível.' }])
    } finally {
      setCarregando(false)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      enviar()
    }
  }

  return (
    <div className='fixed bottom-16 end-4 z-40 flex flex-col items-end gap-2'>
      {/* Chat bubble */}
      {aberto && (
        <div className='rounded-2xl shadow-2xl border border-slate-200 bg-white overflow-hidden w-80 md:w-96 flex flex-col'>
          <div className='px-4 py-3 bg-gradient-to-r from-sky-600 to-sky-500 flex items-center justify-between'>
            <div className='flex items-center gap-2'>
              <span className='material-symbols-outlined text-white' style={{ fontSize: '1.1rem' }}>smart_toy</span>
              <span className='font-bold text-white text-sm'>O Espec<span className='text-sky-200'>IA</span>lista</span>
            </div>
            <button onClick={() => setAberto(false)} className='text-white/70 hover:text-white transition cursor-pointer'>
              <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>close</span>
            </button>
          </div>
          <div className='h-64 overflow-y-auto p-3 flex flex-col gap-2 bg-slate-50'>
            {mensagens.map((m, i) =>
              m.autor === 'USUARIO'
                ? (
                  <div key={i} className='flex justify-end'>
                    <span className='p-2 px-3 rounded-2xl rounded-br-sm bg-sky-600 text-white max-w-[85%] text-sm'>{m.texto}</span>
                  </div>
                )
                : (
                  <div key={i} className='flex justify-start'>
                    <span className='p-2 px-3 rounded-2xl rounded-bl-sm bg-white border border-slate-200 shadow-sm text-slate-700 max-w-[85%] text-sm'>{m.texto}</span>
                  </div>
                )
            )}
            {carregando && (
              <div className='flex justify-start'>
                <span className='p-2 px-3 rounded-2xl rounded-bl-sm bg-white border border-slate-200 text-slate-400 text-sm italic'>Digitando...</span>
              </div>
            )}
            <div ref={bottomRef} />
          </div>
          <div className='p-2 flex gap-1 border-t border-slate-200 bg-white'>
            <textarea
              value={texto}
              onChange={e => setTexto(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder='Pergunte sobre atletas... (Enter)'
              rows={2}
              disabled={carregando}
              className='outline-none w-full p-2 border border-slate-200 rounded-xl hover:border-slate-300 focus:border-sky-400 resize-none text-sm disabled:opacity-50 bg-slate-50'
            />
            <button
              onClick={enviar}
              disabled={carregando || !texto.trim()}
              className='flex items-center justify-center w-10 h-10 bg-sky-600 hover:bg-sky-700 text-white transition cursor-pointer rounded-full self-end disabled:opacity-40 disabled:cursor-default shrink-0'
            >
              <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>send</span>
            </button>
          </div>
        </div>
      )}
      {/* Toggle button */}
      <button
        onClick={() => setAberto(v => !v)}
        className='w-12 h-12 rounded-full bg-sky-600 hover:bg-sky-700 text-white shadow-lg transition flex items-center justify-center cursor-pointer'
        title='Especialista IA'
      >
        <span className='material-symbols-outlined' style={{ fontSize: '1.4rem' }}>{aberto ? 'close' : 'smart_toy'}</span>
      </button>
    </div>
  )
}

// ── Follow Button ─────────────────────────────────────────────────────────────

function BotaoSeguir({ seguindo, loading, onSeguir, onParar }: {
  seguindo: boolean; loading: boolean
  onSeguir: () => void; onParar: () => void
}) {
  return seguindo
    ? (
      <button onClick={onParar} disabled={loading} className='text-xs font-semibold px-3 py-0.5 rounded-full border border-slate-300 text-slate-600 hover:bg-slate-100 transition cursor-pointer disabled:opacity-40'>
        Seguindo
      </button>
    )
    : (
      <button onClick={onSeguir} disabled={loading} className='text-xs font-semibold px-3 py-0.5 rounded-full bg-sky-600 text-white hover:bg-sky-700 transition cursor-pointer disabled:opacity-40'>
        Seguir
      </button>
    )
}

// ── Post Card ─────────────────────────────────────────────────────────────────

function PostFeedComponent({ post, isOwn, seguindo, onSeguir, onParar }: {
  post: PostDetailsDTO
  isOwn: boolean
  seguindo: boolean
  onSeguir: () => void
  onParar: () => void
}) {
  const [loadingFollow, setLoadingFollow] = useState(false)
  const [likes, setLikes] = useState(post.interacoes?.quantidadeLike ?? 0)
  const [deuLike, setDeuLike] = useState(post.interacoes?.deuLike ?? false)
  const [loadingLike, setLoadingLike] = useState(false)

  const seguir = async () => {
    if (loadingFollow) return
    setLoadingFollow(true)
    const ok = await api.seguir(post.autor.username)
    setLoadingFollow(false)
    if (ok) onSeguir()
  }
  const parar = async () => {
    if (loadingFollow) return
    setLoadingFollow(true)
    const ok = await api.pararDeSeguir(post.autor.username)
    setLoadingFollow(false)
    if (ok) onParar()
  }

  const toggleLike = async (e: MouseEvent) => {
    e.preventDefault()
    if (loadingLike) return
    setLoadingLike(true)
    if (deuLike) {
      const ok = await api.darDislikeEmPost(post.url)
      if (ok) { setLikes(l => l - 1); setDeuLike(false) }
    } else {
      const ok = await api.darLikeEmPost(post.url)
      if (ok) { setLikes(l => l + 1); setDeuLike(true) }
    }
    setLoadingLike(false)
  }

  return (
    <article className='bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden hover:shadow-md transition-shadow'>
      {/* Author header */}
      <div className='flex items-center gap-3 px-4 py-3 border-b border-slate-50'>
        <Link to='/user/$user' params={{ user: post.autor.username }} className='shrink-0'>
          <div className='w-9 h-9'>
            <ProfilePicture user={post.autor} />
          </div>
        </Link>
        <Link to='/user/$user' params={{ user: post.autor.username }} className='flex flex-col min-w-0 flex-1 hover:opacity-80 transition'>
          <div className='flex items-center gap-1.5'>
            <span className='text-sm font-semibold text-slate-900 leading-tight'>{post.autor.nome}</span>
            <span className='text-xs text-slate-400'>·</span>
            <span className='text-xs text-slate-400'>{tempoRelativo(post.criadoEm)}</span>
          </div>
          {post.autor.posicao
            ? <span className='text-xs text-sky-600 font-medium'>{post.autor.posicao}</span>
            : <span className='text-xs text-slate-400'>@{post.autor.username}</span>
          }
        </Link>
        {!isOwn && (
          <BotaoSeguir seguindo={seguindo} loading={loadingFollow} onSeguir={seguir} onParar={parar} />
        )}
      </div>

      {/* Media */}
      <Link to='/post/$post_id' params={{ post_id: post.url }} className='block aspect-square overflow-hidden bg-slate-900'>
        <PostMediaComponent post={post} className='w-full h-full' showLike={false} />
      </Link>

      {/* Actions & caption */}
      <div className='px-4 py-3 flex flex-col gap-1.5'>
        <div className='flex items-center gap-3'>
          <button
            onClick={toggleLike}
            disabled={loadingLike}
            className={`flex items-center gap-1.5 transition cursor-pointer disabled:opacity-50 ${
              deuLike ? 'text-sky-600' : 'text-slate-400 hover:text-sky-600'
            }`}
          >
            <span className='material-symbols-outlined' style={{ fontSize: '1.3rem', fontVariationSettings: deuLike ? "'FILL' 1" : "'FILL' 0" }}>
              thumb_up
            </span>
            <span className='text-sm font-semibold'>{likes}</span>
          </button>
          <Link
            to='/post/$post_id'
            params={{ post_id: post.url }}
            className='flex items-center gap-1.5 text-slate-400 hover:text-sky-600 transition'
          >
            <span className='material-symbols-outlined' style={{ fontSize: '1.3rem' }}>chat_bubble_outline</span>
          </Link>
        </div>
        {post.titulo && (
          <Link to='/post/$post_id' params={{ post_id: post.url }}>
            <p className='text-sm text-slate-800 leading-snug'>
              <span className='font-semibold'>{post.autor.nome}</span>{' '}
              {post.titulo}
            </p>
          </Link>
        )}
        {post.descricao && (
          <p className='text-xs text-slate-400 truncate'>{post.descricao}</p>
        )}
      </div>
    </article>
  )
}

// ── Dialog Create Post ────────────────────────────────────────────────────────

function DialogPostCreateComponent({ onClose, onPostCreated }: { onClose: () => void; onPostCreated: () => void }) {
  const [mediaPreview, setMediaPreview] = useState('')

  const criarPost = async (form: FormData) => {
    const titulo = form.get('titulo')?.toString()
    if (!titulo) return
    const arquivo = form.get('media') as File
    if (!arquivo || arquivo.size === 0) return
    const descricao = form.get('descricao')?.toString() ?? ''
    const result = await api.criarPost({ arquivo, titulo, descricao })
    if (!result) return
    onClose()
    onPostCreated()
  }

  const previewMedia = ({ target }: ChangeEvent<HTMLInputElement>) => {
    if (!target.files?.length) return
    setMediaPreview(URL.createObjectURL(target.files[0]))
  }

  return (
    <div onClick={onClose} className='fixed flex items-center justify-center top-0 start-0 end-0 bottom-0 z-50 bg-slate-900/60 backdrop-blur-sm'>
      <form action={criarPost} onClick={e => e.stopPropagation()} className='h-128 flex gap-2 w-full max-w-256 bg-white rounded-2xl overflow-hidden mx-4 shadow-2xl'>
        <div
          style={{ backgroundImage: mediaPreview ? `url(${mediaPreview})` : undefined }}
          className='w-full h-full grow bg-slate-100 aspect-square relative bg-center bg-contain bg-no-repeat flex flex-col gap-4 items-center justify-center transition hover:bg-slate-200'
        >
          {!mediaPreview && (
            <>
              <span className='material-symbols-outlined text-slate-400' style={{ fontSize: '2.5em' }}>image_arrow_up</span>
              <span className='text-slate-500 text-sm'>Faça o upload de um arquivo</span>
            </>
          )}
          <label className='w-full h-full absolute cursor-pointer'>
            <input type='file' className='hidden' onChange={previewMedia} name='media' />
          </label>
        </div>
        <div className='w-3/4 text-start flex flex-col gap-4 p-6'>
          <h2 className='font-bold text-slate-800 text-lg'>Nova publicação</h2>
          <Input fieldName='titulo' label='Título' />
          <Input fieldName='descricao' label='Descrição' />
          <button type='submit' className='self-start px-6 py-2 rounded-full bg-sky-600 hover:bg-sky-700 text-white text-sm font-semibold cursor-pointer transition'>
            Publicar
          </button>
        </div>
      </form>
    </div>
  )
}

// ── Main Feed ─────────────────────────────────────────────────────────────────

export function RouteComponent() {
  const [posts, setPosts] = useState<PostDetailsDTO[]>([])
  const [showDialog, setShowDialog] = useState(false)
  const [filtros, setFiltros] = useState({ posicao: '', tipoConta: '' })
  const [seguindoSet, setSeguindoSet] = useState<Set<string>>(new Set())
  const loggedUsername = api.obterDadosUsuarioNoNavegador()

  const obterPostsParaFeed = async (page = 0) => {
    const { data } = await api.obterPostsFeed({ page })
    setPosts(data)
    setSeguindoSet(prev => {
      const next = new Set(prev)
      data.forEach(p => { if (p.metadados?.segueConta) next.add(p.autor.username) })
      return next
    })
  }

  useEffect(() => { obterPostsParaFeed() }, [])

  const filteredPosts = useMemo(() => posts.filter(post => {
    const posicaoOk = !filtros.posicao || post.autor.posicao === filtros.posicao
    const tipoOk = !filtros.tipoConta || (post.autor.tipoConta?.includes(filtros.tipoConta) ?? false)
    return posicaoOk && tipoOk
  }), [posts, filtros])

  return (
    <div className='min-h-screen flex flex-col bg-slate-50'>
      <LoggedHeader />

      <div className='container mx-auto grow grid grid-cols-12 gap-6 px-4 py-6 max-w-5xl'>

        {/* Left sidebar */}
        <aside className='hidden lg:block col-span-3'>
          <div className='sticky top-20 flex flex-col gap-3'>
            <div className='bg-white rounded-2xl shadow-sm border border-slate-100 p-4'>
              <h3 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-3'>Filtrar feed</h3>
              <div className='flex flex-col gap-3'>
                <div className='flex flex-col gap-1'>
                  <label className='text-xs font-semibold text-slate-600'>Posição</label>
                  <select
                    value={filtros.posicao}
                    onChange={e => setFiltros(f => ({ ...f, posicao: e.target.value }))}
                    className='w-full py-1.5 px-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-sky-400 bg-white'
                  >
                    <option value=''>Todas as posições</option>
                    {POSICOES.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
                <div className='flex flex-col gap-1'>
                  <label className='text-xs font-semibold text-slate-600'>Tipo de conta</label>
                  <select
                    value={filtros.tipoConta}
                    onChange={e => setFiltros(f => ({ ...f, tipoConta: e.target.value }))}
                    className='w-full py-1.5 px-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:border-sky-400 bg-white'
                  >
                    <option value=''>Todos</option>
                    <option value='ATLETA'>Atleta</option>
                    <option value='OLHEIRO'>Olheiro</option>
                  </select>
                </div>
                {(filtros.posicao || filtros.tipoConta) && (
                  <button
                    onClick={() => setFiltros({ posicao: '', tipoConta: '' })}
                    className='text-xs text-sky-600 hover:text-sky-800 underline cursor-pointer text-left'
                  >
                    Limpar filtros
                  </button>
                )}
              </div>
            </div>
          </div>
        </aside>

        {/* Feed */}
        <main className='col-span-12 lg:col-span-6 flex flex-col gap-4'>
          {filteredPosts.length === 0 && (
            <div className='flex flex-col items-center justify-center h-48 text-slate-400 gap-2'>
              <span className='material-symbols-outlined' style={{ fontSize: '2rem' }}>sports_soccer</span>
              <span className='text-sm'>Nenhuma publicação encontrada.</span>
            </div>
          )}
          {filteredPosts.map(post => (
            <PostFeedComponent
              key={post.url}
              post={post}
              isOwn={post.autor.username === loggedUsername}
              seguindo={seguindoSet.has(post.autor.username)}
              onSeguir={() => setSeguindoSet(prev => new Set([...prev, post.autor.username]))}
              onParar={() => setSeguindoSet(prev => { const s = new Set(prev); s.delete(post.autor.username); return s })}
            />
          ))}
        </main>

        {/* Right sidebar */}
        <aside className='hidden lg:block col-span-3'>
          <div className='sticky top-20 flex flex-col gap-3'>
            <button
              onClick={() => setShowDialog(true)}
              className='w-full py-2.5 px-4 flex items-center justify-center gap-2 rounded-2xl bg-sky-600 text-white hover:bg-sky-700 cursor-pointer transition font-semibold text-sm shadow-sm'
            >
              <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>add_circle</span>
              Nova publicação
            </button>
          </div>
        </aside>

        {/* Mobile create button */}
        <div className='lg:hidden fixed bottom-20 start-4 z-30'>
          <button
            onClick={() => setShowDialog(true)}
            className='w-12 h-12 rounded-full bg-sky-600 text-white shadow-lg hover:bg-sky-700 transition flex items-center justify-center cursor-pointer'
          >
            <span className='material-symbols-outlined' style={{ fontSize: '1.4rem' }}>add</span>
          </button>
        </div>

      </div>

      {showDialog && (
        <DialogPostCreateComponent
          onClose={() => setShowDialog(false)}
          onPostCreated={() => obterPostsParaFeed(0)}
        />
      )}
      <AIChatComponent />
      <Footer />
    </div>
  )
}
