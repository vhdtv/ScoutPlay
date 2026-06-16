import API from '#/api/API';
import { Link, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import InputField from './ui/InputField';

const api = new API;

export default function() {
  const [username, setUsername] = useState("");
  const navigate = useNavigate();
  useEffect(() => {
    const username = api.obterDadosUsuarioNoNavegador();
    if(!username || username == "") {
      navigate({to: "/login"})
      return
    }
    setUsername(username)
  }, [])

  const makeSearch = (form: FormData) => {
    const query = form.get("query")?.toString();
    if(!query) return;
    navigate({ to:"/search", search: {query} });
  }

  const logout = async () => {
    await api.fazerLogout();
    navigate({to: "/login"})
  }

  const linkStyle = `transition flex items-center gap-1 font-semibold text-sm tracking-wide rounded-md outline-0 py-2 px-4 text-mist-500 focus:bg-sky-100 focus:text-sky-700 hover:bg-sky-100 hover:text-sky-700`
  return (
    <header className='h-14 element flex items-center shrink-0 gap-3 px-5 sticky top-0'>
      <div className='flex items-center gap-2 grow'>
        <Link to="/feed" className="p-2 rounded-full focus:bg-mist-50 grayscale hover:grayscale-0 focus:grayscale-0 transition outline-sky-300"><img src="/assets/icon.svg" className='w-8' /></Link>
        <form action={makeSearch} className='w-full max-w-[25em]'>
          <div className='relative'>
            <InputField className='w-full ps-9' fieldName="query" placeholder='Buscar por...' />
            <span className='absolute start-3 top-1 text-mist-500 text-lg'>
              <i className="ri-search-line"></i>
            </span>
          </div>
        </form>
      </div>
      <div className='flex gap-2 justify-end'>
        <Link to="/user/$user" params={{user: username}} className={linkStyle}>Perfil</Link>
        <Link to="/settings" className={linkStyle}>Configurações</Link>
        <button onClick={logout} className={`${linkStyle} cursor-pointer`}>
          <span>Sair</span>
          <i className="ri-logout-box-r-line"></i>
        </button>
      </div>
    </header>
  )
}