import API from '#/api/API'
import type { UserProfileDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import Input from '#/components/ui/Input'
import ProfilePicture from '#/components/ui/ProfilePicture'
import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'

const api = new API;

export const Route = createFileRoute('/settings')({
  component: RouteComponent,
  loader: async ({params}) => {
    try {
      const perfil = await api.obterDadosDoPerfil();
      return { perfil }
    }
    catch(e) {
      return { perfil: {} }
    }
  }
})

function RouteComponent() {
  // const { perfil } = Route.useLoaderData();
  const [usuario, definirUsuario] = useState({} as UserProfileDTO);
  useEffect(() => {
    api.obterDadosDoPerfil()
      .then(data => definirUsuario(data))
      .catch(e => console.log(e))
  }, []);
  
  function ProfileDetailComponent() {
    const updateProfileDetail = async () => {
      const response = await api.definirDetalhePerfil("PE_DOMINANTE", "AMBOS")
      console.log(response)
    }

    const removeProfileDetail = async () => {
      const response = await api.removerDetalhePerfil("PE_DOMINANTE")
      console.log(response)
    }

    return (
      <>
        <button className='mt-2 p-2 px-4 self-start rounded-md cursor-pointer text-white bg-red-700 hover:bg-red-800' onClick={updateProfileDetail}>Add</button>
        <button className='mt-2 p-2 px-4 self-start rounded-md cursor-pointer text-white bg-red-700 hover:bg-red-800' onClick={removeProfileDetail}>Remove</button>
      </>
    )
  }

  function UserProfilePicture() {
    return (
      <div className='w-48 cursor-pointer'>
        <label onClick={() => (document.querySelector("input[name=profile_picture") as HTMLInputElement).click()} htmlFor="profile_picture">
          <Input onChange={() => console.log("asd")} type='file' hideLabel={true} fieldName='profile_picture' className='hidden' />
          <ProfilePicture user={usuario} />
        </label>
      </div>
    )
  }

  const atualizarDados = async (form: FormData) => {
    const nome = form.get("nome")?.toString();
    if(!nome) return;
    definirUsuario({
      ...usuario,
      nome: nome
    })
  }

  return (
    <div className='page-noscroll flex flex-col'>
      <LoggedHeader/>
      <div className="container grow-1 mx-auto flex flex-col items-start">
        <h1 className='text-3xl font-bold text-slate-700 opacity-40 my-4'>Configurações</h1>
        <form className='flex flex-col gap-4 w-full' action={atualizarDados}>
          <UserProfilePicture />
          <div className="flex gap-2">
            <Input defaultValue={usuario.nome} fieldName='nome' />
            <Input defaultValue={usuario.sobrenome ?? ""} fieldName='sobrenome' />
          </div>
          <Input label="Nome de Usuário" fieldName='username' />
          <button className='p-2 px-4 self-start rounded-md cursor-pointer text-white bg-sky-700 hover:bg-sky-800' type="submit">Atualizar</button>
        </form>
        <ProfileDetailComponent />
      </div>
      <Footer/>
    </div>
  )
}
