import { createFileRoute } from '@tanstack/react-router'
import Footer from "../../components/Footer"
import InputToggleButton from '#/components/ui/InputToggleButton'
import ProfilePostList from '#/components/ProfilePostList'
import { LoggedHeader } from '#/components/Header'
import MockAPI from '#/api/MockAPI'
import type { ConfigItemDTO, UserProfileDTO } from '#/api/tipos'
import { useState, type Dispatch, type SetStateAction } from 'react'
import ProfilePicture from '#/components/ui/ProfilePicture'

const api = new MockAPI

export const Route = createFileRoute('/user/$user')({
  component: RouteComponent,
  loader: async ({params}) => {
    return {
      user: await api.obterDadosDoPerfil(params.user)
    }
  }
})

function UserCard({user}: {user: UserProfileDTO}) {
  return (
    <div className='block'>
      <div className='h-18'></div>
      <div className="rounded-xl shadow-xl max-w-96 mx-auto w-full flex flex-col items-center justify-start bg-white">
        <div className='bg-slate-100 flex flex-col items-center w-full rounded-xl'>
          <div className='w-16 h-16 flex relative justify-center'>
            <ProfilePicture user={user} className='w-32 h-32 absolute -translate-y-16 top-0' />
          </div>
          <div className='w-full flex flex-col gap-0 items-center justify-center leading-none my-2'>
            <div className='block'>
              <h1 className='font-bold text-xl/6 inline-block me-2 text-center'>{`${user.nome} ${user.sobrenome}`}</h1>
              <span className='opacity-40 font-bold'>{user.idade}</span>
            </div>
            {user.detalhesPerfil?.CIDADE && <h2 className='inline-block text-center text-lg opacity-50'>{user.detalhesPerfil.CIDADE}</h2>}
          </div>
          <section className='flex flex-wrap gap-3 w-full items-center justify-center px-4'>
            {user.detalhesPerfil?.POSICOES && user.detalhesPerfil.POSICOES.map((posicao, index) => <span key={index} className='text-nowrap border border-slate-800 text-slate-800 px-6 leading-none text-sm py-2 rounded-full'>{posicao}</span>)}
          </section>
          {
            user.detalhesPerfil?.RESPONSAVEL
            && ( 
              <>
                <hr className='h-0.5 w-full my-4 bg-slate-200 border-0' />
                {user.detalhesPerfil?.RESPONSAVEL.map((responsavel, index) => 
                  <div className='px-3 mb-3 w-full' key={index}>
                    <section className='bg-slate-200 border border-slate-300 bg-slate-100 p-2 w-full rounded-md flex gap-2'>
                      <div className='flex jusitfy-center items-center w-12 h-12 overflow-hidden rounded-full border border-red-100 bg-slate-50'>
                        {
                          responsavel.fotoPerfil
                          ? <img src={responsavel.fotoPerfil } className='w-full h-full' alt={`Foto de Perfil do usuario ${responsavel.nome}`} />
                          : <span className='w-full h-full flex items-center justify-center text-3xs font-bold bg-slate-500 text-slate-50'>{responsavel.iniciais}</span>
                        }
                      </div>
                      <div className='flex flex-col justify-center'>
                        <h3 className='leading-none'>{responsavel.nome}</h3>
                        <h4 className='leading-none text-slate-400'>Responsável</h4>
                      </div>
                    </section>
                  </div>
                )}
              </>
            )
          }
        </div>
      </div>
    </div>
  )
}

function RouteComponent() {
  const { user } = Route.useLoaderData();
  const [olheirosPodemConectarPeloWhatsapp, setConfigOlheirosPodemConectar] = useState(user.configuracoes?.OLHEIROS_PODEM_CONECTAR_PELO_WHATSAPP)
  const [deixarDadosPessoaisPublicamente, setConfigDeixarDadosPublicos] = useState(user.configuracoes?.OCULTAR_INFORMACOES_PESSOAIS_PUBLICAMENTE)
;
  const atualizarConfiguracao = async (key: keyof ConfigItemDTO, value: boolean, setter: Dispatch<SetStateAction<boolean | undefined>>) => {
    const result = await api.atualizarConfiguracao({ [key]: value })
    if(!result) setter(!value)
    setter(value)
  }

  return <div>
    <LoggedHeader />
    <div className="container mx-auto grid grid-cols-12 gap-4">
      <div className="col-span-12 lg:col-span-3 p-4 relative">
         <div className="block lg:sticky top-4 pt-2">
            <UserCard user={user} />
         </div>
      </div>
      <div className="col-span-12 lg:col-span-6 p-4 min-h-screen">
        <div>
          <button className='px-6 py-2 text-xl border-b-4 border-slate-700 hover:bg-slate-200 cursor-pointer'>Publicações</button>
          {user?.posts && <ProfilePostList posts={user.posts} />}
        </div>
      </div>
      {
        user.souEu && (
          <div className="hidden lg:block lg:col-span-3 p-4 relative flex">
              <div className="block lg:sticky top-4 w-full pt-16">
              <h3 className='text-sky-950 font-bold text-xl mb-3'>Configurações Rápidas</h3>
              <div className='flex gap-3 items-center w-full mb-3'>
                <InputToggleButton onChange={(event: React.ChangeEvent<HTMLInputElement>) => atualizarConfiguracao("OLHEIROS_PODEM_CONECTAR_PELO_WHATSAPP", event.target.checked, setConfigOlheirosPodemConectar)} checked={olheirosPodemConectarPeloWhatsapp} fieldName='allow-whatsapp' className='shrink-0' />
                <span>Permitir que olheiros entrem em contato pelo WhatsApp</span>
              </div>
              <div className='flex gap-3 items-center w-full'>
                <InputToggleButton onChange={(event: React.ChangeEvent<HTMLInputElement>) => atualizarConfiguracao("OCULTAR_INFORMACOES_PESSOAIS_PUBLICAMENTE", event.target.checked, setConfigDeixarDadosPublicos)} checked={deixarDadosPessoaisPublicamente} fieldName='allow-info' className='shrink-0' />
                <span>Ocultar informações pessoais</span>
              </div>
              </div>
          </div>
        )
      }
      
    </div>
    <Footer />
  </div>
}
