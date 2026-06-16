import API from '#/api/API'
import { Link, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'

const api = new API

export default function Header() {
  return (
    <header className='flex items-center h-14 px-4 bg-white border-b border-slate-200 shadow-sm'>
      <Link to='/' className='flex items-center gap-2'>
        <img src='/logo-scoutplay.png' alt='Logo Scoutplay' className='h-8' />
      </Link>
    </header>
  )
}

export function LoggedHeader() {
  const [username, setUsername] = useState('')
  const [isOlheiro, setIsOlheiro] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    const u = api.obterDadosUsuarioNoNavegador()
    if (!u) { navigate({ to: '/login' }); return }
    setUsername(u)
    setIsOlheiro(api.isOlheiro())
  }, [])

  const logout = async () => {
    await api.fazerLogout()
    navigate({ to: '/login' })
  }

  return (
    <header className='flex items-center h-14 bg-white border-b border-slate-200 shadow-sm px-4 sticky top-0 z-40'>
      <Link to='/feed' className='mr-auto'>
        <img src="/assets/icon.svg" alt='Logo Scoutplay' className='h-10 grayscale transition hover:grayscale-0' />
      </Link>
      <nav className='flex items-center gap-1'>
        <Link to='/feed' className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-sky-700 hover:bg-sky-50 rounded-lg transition'>
          <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>home</span>
          <span className='hidden sm:inline'>Feed</span>
        </Link>
        {isOlheiro && (
          <Link to='/atletas' className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-sky-700 hover:bg-sky-50 rounded-lg transition'>
            <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>search</span>
            <span className='hidden sm:inline'>Atletas</span>
          </Link>
        )}
        {isOlheiro && (
          <Link to='/minha-lista' className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-sky-700 hover:bg-sky-50 rounded-lg transition'>
            <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>bookmark</span>
            <span className='hidden sm:inline'>Minha Lista</span>
          </Link>
        )}
        {username && (
          <Link to='/user/$user' params={{ user: username }} className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-sky-700 hover:bg-sky-50 rounded-lg transition'>
            <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>person</span>
            <span className='hidden sm:inline'>Perfil</span>
          </Link>
        )}
        <Link to='/settings' className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-sky-700 hover:bg-sky-50 rounded-lg transition'>
          <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>settings</span>
          <span className='hidden sm:inline'>Configurações</span>
        </Link>
        <button onClick={logout} className='flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition cursor-pointer'>
          <span className='material-symbols-outlined' style={{ fontSize: '1.35rem' }}>logout</span>
          <span className='hidden sm:inline'>Sair</span>
        </button>
      </nav>
    </header>
  )
}
