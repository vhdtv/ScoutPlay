import { createFileRoute, Link } from '@tanstack/react-router'
import Footer from '../../components/Footer'
import ProfilePostList from '#/components/ProfilePostList'
import { LoggedHeader } from '#/components/Header'
import API from '#/api/API'
import ProfilePicture from '#/components/ui/ProfilePicture'
import { useEffect, useState } from 'react'
import type { AvaliacaoDTO, PostDTO } from '#/api/tipos'

const api = new API

export const Route = createFileRoute('/user/$user')({
  component: RouteComponent,
  loader: async ({ params }) => ({
    user: await api.obterDadosDoPerfil(params.user)
  }),
})

const TIPO_CONTA_LABELS: Record<string, string> = {
  ATLETA: 'Atleta',
  OLHEIRO: 'Olheiro',
  RESPONSAVEL: 'Responsável',
  REPRESENTANTE_CLUBE: 'Repr. de Clube',
}

function StatCard({ label, value }: { label: string; value?: string | number | null }) {
  if (!value) return null
  return (
    <div className='flex flex-col items-center gap-0.5 bg-slate-50 border border-slate-100 rounded-xl px-4 py-3 min-w-[72px]'>
      <span className='text-base font-bold text-slate-800'>{value}</span>
      <span className='text-xs text-slate-400 uppercase tracking-wide'>{label}</span>
    </div>
  )
}

function RouteComponent() {
  const { user } = Route.useLoaderData()
  const [seguidores, setSeguidores] = useState(user.seguidores ?? 0)
  const [souSeguidor, setSouSeguidor] = useState(user.souSeguidor ?? false)
  const [loadingFollow, setLoadingFollow] = useState(false)
  const [posts, setPosts] = useState<PostDTO[]>((user.posts ?? []) as PostDTO[])
  const [isOlheiro, setIsOlheiro] = useState(false)
  const [naShortlist, setNaShortlist] = useState(false)
  const [avaliacoes, setAvaliacoes] = useState<AvaliacaoDTO[]>([])
  const [nota, setNota] = useState(7)
  const [comentario, setComentario] = useState('')
  const [enviandoAvaliacao, setEnviandoAvaliacao] = useState(false)

  const isAtleta = (user.tipoConta as any[])?.some(
    (t: string) => t === 'ATLETA' || t === 'Atleta'
  )

  useEffect(() => {
    setIsOlheiro(api.isOlheiro())
    if (isAtleta) {
      api.listarAvaliacoes(user.username).then(setAvaliacoes).catch(() => {})
    }
  }, [])

  const toggleShortlist = async () => {
    if (naShortlist) {
      await api.removerDaShortlist(user.username)
      setNaShortlist(false)
    } else {
      await api.adicionarAShortlist(user.username)
      setNaShortlist(true)
    }
  }

  const enviarAvaliacao = async () => {
    if (enviandoAvaliacao) return
    setEnviandoAvaliacao(true)
    try {
      const nova = await api.avaliarAtleta(user.username, nota, comentario || undefined)
      setAvaliacoes(prev => [nova, ...prev])
      setComentario('')
      setNota(7)
    } catch {}
    setEnviandoAvaliacao(false)
  }

  const handleDeletePost = (url: string) => setPosts(prev => prev.filter(p => p.url !== url))

  const toggleSeguir = async () => {
    if (loadingFollow) return
    setLoadingFollow(true)
    if (souSeguidor) {
      const ok = await api.pararDeSeguir(user.username)
      if (ok) { setSouSeguidor(false); setSeguidores((n: number) => Math.max(0, n - 1)) }
    } else {
      const ok = await api.seguir(user.username)
      if (ok) { setSouSeguidor(true); setSeguidores((n: number) => n + 1) }
    }
    setLoadingFollow(false)
  }

  const raw = (user.detalhesPerfil ?? {}) as Record<string, any>
  // normalise keys — old users have POSICAO/PE_DOMINANTE, new have posicao/peDominante
  const dp: Record<string, any> = {
    posicao: raw.posicao ?? raw.POSICAO,
    peDominante: raw.peDominante ?? raw.PE_DOMINANTE,
    peso: raw.peso,
    altura: raw.altura,
    clubesAnteriores: raw.clubesAnteriores ?? raw.CLUBE_QUE_PARTICIPOU,
    RESPONSAVEL: raw.RESPONSAVEL,
    ...raw,
  }

  return (
    <div className='min-h-screen flex flex-col bg-slate-50'>
      <LoggedHeader />

      {/* Banner */}
      <div className='h-36 bg-gradient-to-br from-sky-500 via-indigo-600 to-slate-800' />

      {/* Profile header */}
      <div className='container mx-auto px-4'>
        <div className='relative -mt-12 flex flex-col sm:flex-row sm:items-end gap-4 pb-4'>
          <div className='w-24 h-24 rounded-full border-4 border-white shadow-lg overflow-hidden bg-slate-200 shrink-0'>
            <ProfilePicture user={user} className='w-full h-full' />
          </div>
          <div className='flex flex-col gap-1 flex-1'>
            <div className='flex flex-wrap items-center gap-2'>
              <h1 className='text-2xl font-bold text-slate-900'>{user.nome} {user.sobrenome}</h1>
              {user.idade != null && <span className='text-slate-500'>· {user.idade} anos</span>}
            </div>
            <div className='flex flex-wrap gap-1.5'>
              {(user.tipoConta as any[])?.map((t: string) => (
                <span key={t} className='text-xs px-2.5 py-0.5 rounded-full bg-sky-100 text-sky-700 font-semibold'>
                  {TIPO_CONTA_LABELS[t] ?? t}
                </span>
              ))}
            </div>
            <span className='text-sm text-slate-500'>@{user.username}</span>
          </div>
          <div className='flex items-center gap-4 ml-auto shrink-0'>
            <div className='text-center'>
              <p className='text-lg font-bold text-slate-800'>{seguidores}</p>
              <p className='text-xs text-slate-400'>Seguidores</p>
            </div>
            <div className='text-center'>
              <p className='text-lg font-bold text-slate-800'>{user.seguindo ?? 0}</p>
              <p className='text-xs text-slate-400'>Seguindo</p>
            </div>
            {user.souEu ? (
              <Link to='/settings' className='px-5 py-2 rounded-full border border-sky-600 text-sky-600 hover:bg-sky-600 hover:text-white text-sm font-semibold transition'>
                Editar perfil
              </Link>
            ) : (
              <>
                <button
                  onClick={toggleSeguir}
                  disabled={loadingFollow}
                  className={`px-5 py-2 rounded-full text-sm font-semibold transition cursor-pointer disabled:opacity-50 ${
                    souSeguidor
                      ? 'border border-slate-300 text-slate-600 hover:border-red-400 hover:text-red-500'
                      : 'bg-sky-600 text-white hover:bg-sky-800'
                  }`}
                >
                  {souSeguidor ? 'Seguindo' : 'Seguir'}
                </button>
                {isOlheiro && isAtleta && (
                  <button
                    onClick={toggleShortlist}
                    title={naShortlist ? 'Remover da Minha Lista' : 'Adicionar à Minha Lista'}
                    className={`flex items-center gap-1.5 px-3 py-2 rounded-full text-sm font-semibold border transition cursor-pointer ${
                      naShortlist
                        ? 'bg-amber-500 text-white border-amber-500 hover:bg-amber-600'
                        : 'border-amber-400 text-amber-600 hover:bg-amber-50'
                    }`}
                  >
                    <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>
                      {naShortlist ? 'bookmark' : 'bookmark_add'}
                    </span>
                    <span className='hidden sm:inline'>{naShortlist ? 'Na lista' : 'Minha lista'}</span>
                  </button>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      <div className='container mx-auto px-4 grid grid-cols-12 gap-4 pb-10'>

        {/* Left: stats + info */}
        <div className='col-span-12 lg:col-span-4 flex flex-col gap-4'>

          {(dp.posicao || dp.altura || dp.peso || dp.peDominante) && (
            <div className='bg-white rounded-xl shadow p-4'>
              <h2 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-3'>Dados Esportivos</h2>
              <div className='flex flex-wrap gap-2'>
                <StatCard label='Posição' value={dp.posicao} />
                <StatCard label='Altura' value={dp.altura ? `${dp.altura} cm` : null} />
                <StatCard label='Peso' value={dp.peso ? `${dp.peso} kg` : null} />
                <StatCard label='Pé dom.' value={dp.peDominante} />
              </div>
            </div>
          )}

          {dp.clubesAnteriores && (
            <div className='bg-white rounded-xl shadow p-4'>
              <h2 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-2'>Clubes</h2>
              <p className='text-slate-700 text-sm'>{dp.clubesAnteriores}</p>
            </div>
          )}

          {dp.RESPONSAVEL && Array.isArray(dp.RESPONSAVEL) && dp.RESPONSAVEL.length > 0 && (
            <div className='bg-white rounded-xl shadow p-4'>
              <h2 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-3'>Responsável</h2>
              {dp.RESPONSAVEL.map((r: any, i: number) => (
                <div key={i} className='flex items-center gap-3 bg-slate-50 rounded-lg p-2'>
                  <div className='w-10 h-10 rounded-full overflow-hidden bg-slate-200 shrink-0 flex items-center justify-center'>
                    {r.fotoPerfil
                      ? <img src={r.fotoPerfil} className='w-full h-full object-cover' alt={r.nome} />
                      : <span className='text-xs font-bold text-slate-500'>{r.iniciais}</span>
                    }
                  </div>
                  <div>
                    <p className='text-sm font-semibold text-slate-800'>{r.nome}</p>
                    <p className='text-xs text-slate-400'>Responsável</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right: posts + evaluations */}
        <div className='col-span-12 lg:col-span-8 flex flex-col gap-4'>
          <div className='bg-white rounded-xl shadow p-4'>
            <h2 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-4'>Publicações</h2>
            {posts.length
              ? <ProfilePostList posts={posts} souEu={user.souEu} onDelete={handleDeletePost} />
              : <div className='flex items-center justify-center h-32 text-slate-400 text-sm'>Nenhuma publicação ainda.</div>
            }
          </div>

          {isAtleta && (
            <div className='bg-white rounded-xl shadow p-4'>
              <h2 className='text-xs font-bold uppercase tracking-wide text-slate-400 mb-4'>Avaliações de Olheiros</h2>

              {isOlheiro && !user.souEu && (
                <div className='mb-5 p-3 bg-slate-50 rounded-xl border border-slate-200'>
                  <p className='text-xs font-semibold text-slate-500 mb-2'>Sua avaliação</p>
                  <div className='flex items-center gap-3 mb-2'>
                    <label className='text-sm text-slate-600 w-16 shrink-0'>Nota (1-10)</label>
                    <input
                      type='range' min={1} max={10} value={nota}
                      onChange={e => setNota(Number(e.target.value))}
                      className='flex-1'
                    />
                    <span className='text-sm font-bold text-sky-700 w-6 text-right'>{nota}</span>
                  </div>
                  <textarea
                    value={comentario}
                    onChange={e => setComentario(e.target.value)}
                    placeholder='Comentário (opcional)...'
                    rows={2}
                    className='w-full text-sm border border-slate-200 rounded-lg px-3 py-2 resize-none focus:outline-none focus:ring-2 focus:ring-sky-300 mb-2'
                  />
                  <button
                    onClick={enviarAvaliacao}
                    disabled={enviandoAvaliacao}
                    className='px-4 py-1.5 bg-sky-600 text-white text-sm rounded-full font-semibold hover:bg-sky-700 disabled:opacity-50 transition cursor-pointer'
                  >
                    {enviandoAvaliacao ? 'Enviando...' : 'Enviar avaliação'}
                  </button>
                </div>
              )}

              {avaliacoes.length === 0 ? (
                <div className='flex items-center justify-center h-20 text-slate-400 text-sm'>Nenhuma avaliação ainda.</div>
              ) : (
                <div className='flex flex-col gap-3'>
                  {avaliacoes.map(av => (
                    <div key={av.id} className='flex gap-3 items-start'>
                      <div className='w-9 h-9 rounded-full bg-slate-200 flex items-center justify-center shrink-0'>
                        <span className='text-xs font-bold text-slate-500'>{av.olheiro.iniciais}</span>
                      </div>
                      <div className='flex-1'>
                        <div className='flex items-center gap-2'>
                          <span className='text-sm font-semibold text-slate-800'>{av.olheiro.nome}</span>
                          <span className='text-xs text-slate-400'>@{av.olheiro.username}</span>
                          <span className='ml-auto text-sm font-bold text-sky-700'>{av.nota}/10</span>
                        </div>
                        {av.comentario && <p className='text-sm text-slate-600 mt-0.5'>{av.comentario}</p>}
                        <p className='text-xs text-slate-400 mt-0.5'>{av.criadoEm}</p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

      </div>
      <Footer />
    </div>
  )
}
