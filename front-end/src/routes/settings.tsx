import API from '#/api/API'
import type { ProfileUpdateInputDTO, UserProfileDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import Input from '#/components/ui/Input'
import ProfilePicture from '#/components/ui/ProfilePicture'
import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState, type ChangeEvent } from 'react'

const api = new API;

export const Route = createFileRoute('/settings')({
  component: RouteComponent,
})

function RouteComponent() {
  const [usuario, definirUsuario] = useState({} as UserProfileDTO);
  useEffect(() => {
    api.obterDadosDoPerfil()
      .then(data => definirUsuario(data))
      .catch(e => console.log(e))
  }, []);
  
  function UserProfilePicture() {
    const previewMedia = ({target}: ChangeEvent<HTMLInputElement>) => {
        if(!target.files) return;
        const [ file ] = target.files;
        return URL.createObjectURL(file);
      }

    return (
      <div className='w-48 cursor-pointer'>
        <label onClick={() => (document.querySelector("input[name=profile_picture") as HTMLInputElement).click()} htmlFor="profile_picture">
          <Input type='file' hideLabel={true} fieldName='profile_picture' className='hidden' />
          <ProfilePicture user={usuario} />
        </label>
      </div>
    )
  }

  const atualizarDadosPerfil = async (form: FormData) => {
    const rawData: ProfileUpdateInputDTO = {
      nome: form.get("nome")?.toString(),
      sobrenome: form.get("sobrenome")?.toString(),
      username: form.get("username")?.toString(),
      fotoPerfil: (form.get('foto_perfil') as File) || undefined,
    }
    const data: ProfileUpdateInputDTO = Object.fromEntries(Object.entries(rawData).filter(([_, value]) => value !== null));

    const dadosAtualizados = await api.atualizarPerfil(data);
    definirUsuario(dadosAtualizados)
  }

  return (
    <div className='page-noscroll flex flex-col'>
      <LoggedHeader/>
      <div className="container grow-1 mx-auto flex flex-col items-start">
        <h1 className='text-3xl font-bold text-slate-700 opacity-40 my-4'>Configurações</h1>
        <form className='flex flex-col gap-4 w-full' action={atualizarDadosPerfil}>
          <Input type='file' hideLabel={true} fieldName='foto_perfil' />
          <div className="flex gap-2">
            <Input defaultValue={usuario.nome} fieldName='nome' />
            <Input defaultValue={usuario.sobrenome ?? ""} fieldName='sobrenome' />
          </div>
          <Input label="Nome de Usuário" fieldName='username' />
          <button className='p-2 px-4 self-start rounded-md cursor-pointer text-white bg-sky-700 hover:bg-sky-800' type="submit">Atualizar</button>
        </form>
      </div>
      <Footer/>
    </div>
  )
}
