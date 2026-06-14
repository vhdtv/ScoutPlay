import API from '#/api/API'
import type { ConviteDTO, ProfileUpdateInputDTO, UserProfileDTO } from '#/api/tipos'
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
  const [convites, definirConvites] = useState([] as ConviteDTO[]);

  useEffect(() => {
    api.obterDadosDoPerfil()
      .then(data => definirUsuario(data))
      .catch(e => console.log(e))
    api.obterConvites()
    .then(data => definirConvites(data))
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

  const criarConviteMock = async () => {
    const result = await api.mandarConvite(usuario.username, "Oi")
    console.log({result})
  }

  const aceitarConvite = async (convite: ConviteDTO) => {
    const data = await api.aceitarConvite(convite.id)
    console.log({aceito: data})
  }

  const recusarConvite = async (convite: ConviteDTO) => {
    const data = await api.recusarConvite(convite.id)
    console.log({recusado: data})
  }

  return (
    <div className='page-noscroll flex flex-col'>
      <LoggedHeader/>
      <div className="container grow-1 mx-auto flex flex-col items-start">
        <h1 className='text-3xl font-bold text-slate-700 opacity-40 my-4'>Configurações</h1>
        <button onClick={criarConviteMock}>Criar Convite Mock</button>
        { 
          convites.map(convite => (
            <div className='flex flex-wrap border-red-200 bg-red-100 border border-red-300 rounded-2xl p-4'>
              <span className='w-full'>{convite.mensagem} <br /> por: {convite.remetente.nome}</span>
              <div className='w-full flex gap-2'>
                <button onClick={() => aceitarConvite(convite)} className='p-2 grow border-2 bg-green-500'>Aceitar Convite</button>
                <button onClick={() => recusarConvite(convite)} className='p-2 grow border-2 bg-red-500'>Recusar Convite</button>
              </div>
            </div>
          )) 
        }
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
