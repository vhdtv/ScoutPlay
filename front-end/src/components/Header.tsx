import { Link, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'

export default function Header() {
  return (
    <header className='flex justify-start w-full max-h-16'>
      <Link to="/">
        <img src="/logo-scoutplay.png" alt="Logo Scoutplay" className='h-full' />
      </Link>
    </header>
  )
}

export function LoggedHeader() {
  const [username, setUsername] = useState("");
  const navigate = useNavigate();
  useEffect(() => {
    const username = localStorage.getItem("username");
    if(!username || username == "") {
      navigate({to: "/login"})
      return
    }
    setUsername(username)
  }, [])
  return (
    <header className='flex justify-end items-center bg-slate-100 border-b-1 border-slate-300 w-full max-h-16'>
      <Link to="/feed" className='px-6 py-3 text-2xs hover:bg-slate-200 cursor-pointer'>Feed</Link>
      <Link to="/user/$user" params={{user: username}} className='px-6 py-3 text-2xs hover:bg-slate-200 cursor-pointer'>Perfil</Link>
      <Link to="/settings" className='px-6 py-3 text-2xs hover:bg-slate-200 cursor-pointer'>Configurações</Link>
    </header>
  )
}