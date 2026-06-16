import { createFileRoute, Link } from '@tanstack/react-router'
import { LoggedHeader } from '#/components/Header'
import Footer from '#/components/Footer'
import API from '#/api/API'
import { useState } from 'react'
import type { AtletaCardDTO } from '#/api/tipos'

const api = new API

export const Route = createFileRoute('/atletas')({
  component: AtletasPage,
})

const POSICOES = [
  '', 'Goleiro', 'Zagueiro', 'Lateral Direito', 'Lateral Esquerdo',
  'Volante', 'Meia', 'Meia-atacante', 'Ponta Direita', 'Ponta Esquerda',
  'Centroavante',
]

function AtletaCard({ atleta, onShortlist }: { atleta: AtletaCardDTO; onShortlist: (u: string) => void }) {
  return (
    <div className='bg-white rounded-xl shadow-sm border border-slate-100 p-4 flex flex-col gap-3 hover:shadow-md transition'>
      <div className='flex items-center gap-3'>
        <div className='w-12 h-12 rounded-full overflow-hidden bg-slate-200 shrink-0 flex items-center justify-center'>
          {atleta.fotoPerfil
            ? <img src={`http://localhost:8080/api/media/${atleta.fotoPerfil}`} className='w-full h-full object-cover' alt={atleta.nome} />
            : <span className='text-sm font-bold text-slate-500'>{atleta.iniciais}</span>
          }
        </div>
        <div className='flex-1 min-w-0'>
          <Link to='/user/$user' params={{ user: atleta.username }} className='text-sm font-bold text-slate-900 hover:text-sky-700 truncate block'>
            {atleta.nome} {atleta.sobrenome}
          </Link>
          <span className='text-xs text-slate-400'>@{atleta.username}</span>
        </div>
      </div>
      <div className='grid grid-cols-2 gap-1.5 text-xs text-slate-600'>
        {atleta.posicao && <span className='bg-sky-50 text-sky-700 rounded-full px-2 py-0.5'>{atleta.posicao}</span>}
        {atleta.peDominante && <span className='bg-slate-50 rounded-full px-2 py-0.5'>Pé: {atleta.peDominante}</span>}
        {atleta.idade != null && <span className='bg-slate-50 rounded-full px-2 py-0.5'>{atleta.idade} anos</span>}
        <span className='bg-slate-50 rounded-full px-2 py-0.5'>{atleta.seguidores} seguidores</span>
      </div>
      {atleta.clubesAnteriores && (
        <p className='text-xs text-slate-500 truncate'>Clubes: {atleta.clubesAnteriores}</p>
      )}
      <button
        onClick={() => onShortlist(atleta.username)}
        className='flex items-center justify-center gap-1.5 w-full py-1.5 border border-amber-400 text-amber-600 hover:bg-amber-50 rounded-full text-xs font-semibold transition cursor-pointer'
      >
        <span className='material-symbols-outlined' style={{ fontSize: '1rem' }}>bookmark_add</span>
        Minha Lista
      </button>
    </div>
  )
}

function AtletasPage() {
  const [nome, setNome] = useState('')
  const [posicao, setPosicao] = useState('')
  const [peDominante, setPeDominante] = useState('')
  const [atletas, setAtletas] = useState<AtletaCardDTO[]>([])
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)
  const [hasMore, setHasMore] = useState(false)

  const buscar = async (p = 0) => {
    setLoading(true)
    try {
      const res = await api.buscarAtletas({ nome: nome || undefined, posicao: posicao || undefined, peDominante: peDominante || undefined, page: p })
      if (p === 0) setAtletas(res)
      else setAtletas(prev => [...prev, ...res])
      setHasMore(res.length === 12)
      setPage(p)
      setSearched(true)
    } catch {}
    setLoading(false)
  }

  const handleShortlist = async (username: string) => {
    await api.adicionarAShortlist(username)
  }

  return (
    <div className='min-h-screen flex flex-col bg-slate-50'>
      <LoggedHeader />
      <div className='container mx-auto px-4 py-8 max-w-5xl flex-1'>
        <h1 className='text-2xl font-bold text-slate-900 mb-6'>Buscar Atletas</h1>

        <div className='bg-white rounded-xl shadow p-4 mb-6 flex flex-col sm:flex-row gap-3'>
          <input
            type='text'
            placeholder='Nome do atleta...'
            value={nome}
            onChange={e => setNome(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && buscar(0)}
            className='flex-1 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-300'
          />
          <select
            value={posicao}
            onChange={e => setPosicao(e.target.value)}
            className='border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-300 bg-white'
          >
            {POSICOES.map(p => <option key={p} value={p}>{p || 'Todas as posições'}</option>)}
          </select>
          <select
            value={peDominante}
            onChange={e => setPeDominante(e.target.value)}
            className='border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-300 bg-white'
          >
            <option value=''>Pé dominante</option>
            <option value='DIREITO'>Direito</option>
            <option value='ESQUERDO'>Esquerdo</option>
          </select>
          <button
            onClick={() => buscar(0)}
            disabled={loading}
            className='px-6 py-2 bg-sky-600 text-white rounded-lg text-sm font-semibold hover:bg-sky-700 disabled:opacity-50 transition cursor-pointer'
          >
            {loading ? 'Buscando...' : 'Buscar'}
          </button>
        </div>

        {!searched && !loading && (
          <div className='flex items-center justify-center h-48 text-slate-400 text-sm'>
            Use os filtros acima para encontrar atletas.
          </div>
        )}

        {searched && atletas.length === 0 && !loading && (
          <div className='flex items-center justify-center h-48 text-slate-400 text-sm'>
            Nenhum atleta encontrado para os filtros selecionados.
          </div>
        )}

        <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4'>
          {atletas.map(a => (
            <AtletaCard key={a.username} atleta={a} onShortlist={handleShortlist} />
          ))}
        </div>

        {hasMore && (
          <div className='flex justify-center mt-6'>
            <button
              onClick={() => buscar(page + 1)}
              disabled={loading}
              className='px-8 py-2 border border-sky-600 text-sky-600 rounded-full text-sm font-semibold hover:bg-sky-50 disabled:opacity-50 transition cursor-pointer'
            >
              {loading ? 'Carregando...' : 'Carregar mais'}
            </button>
          </div>
        )}
      </div>
      <Footer />
    </div>
  )
}
