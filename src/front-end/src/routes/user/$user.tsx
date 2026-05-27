import { createFileRoute } from '@tanstack/react-router'
import Footer from "../../components/Footer"
import InputToggleButton from '#/components/ui/InputToggleButton'
import ProfilePostList from '#/components/ProfilePostList'
import { LoggedHeader } from '#/components/Header'

export const Route = createFileRoute('/user/$user')({
  component: RouteComponent,
})

function UserCard({user}: any) {
  return (
    <div className='block'>
      <div className='h-18'></div>
      <div className="rounded-xl shadow-xl max-w-96 mx-auto w-full flex flex-col items-center justify-start bg-white">
        <div className='-translate-y-16 flex flex-col items-center w-full'>
          <div className='flex justify-center items-center w-32 h-32 overflow-hidden rounded-full border border-red-100 bg-slate-50 mb-2'>
            {
              user.profile?.img
              ? <img src={user.profile.img} className='w-full h-full' alt="User profile" />
              : <span className='w-full h-full flex items-center justify-center text-4xl font-bold bg-slate-500 text-slate-50'>{user.initials}</span>
            }
          </div>
          <h1 className='font-bold block text-center text-xl'>Nome Usuário</h1>
          <h2 className='block text-center text-lg opacity-50 mb-2'>Cidade</h2>
          <section className='flex gap-3 items-centerf'>
            <span className='border border-slate-800 text-slate-800 px-6 leading-none text-sm py-2 rounded-full'>Teste</span>
            <span className='border border-slate-800 text-slate-800 px-6 leading-none text-sm py-2 rounded-full'>Teste</span>
          </section>
          {
            user.responsible
            && (
              <>
              <hr className='h-0.5 w-full my-4 bg-slate-200 border-0' />
              <div className='px-6 w-full'>
                <section className='translate-y-5 border border-slate-300 bg-slate-100 p-2 w-full rounded-md flex gap-2'>
                  <div className='flex jusitfy-center items-center w-12 h-12 overflow-hidden rounded-full border border-red-100 bg-slate-50'>
                    {
                      user.responsible.profile?.img
                      ? <img src={user.responsible.profile.img} className='w-full h-full' alt="User profile" />
                      : <span className='w-full h-full flex items-center justify-center text-3xs font-bold bg-slate-500 text-slate-50'>{user.responsible.initials}</span>
                    }
                  </div>
                  <div className='flex flex-col justify-center'>
                    <h3 className='leading-none'>{user.responsible.name}</h3>
                    <h4 className='leading-none text-slate-400'>Responsável</h4>
                  </div>
                </section>
              </div>
              </>
            )
          }
        </div>
      </div>
    </div>
  )
}

function RouteComponent() {
  const user = {
    initials: "TS",
    profile: {
      img: "https://unsplash.it/1000"
    },
    responsible: {
      name: "Marcos Teixeira",
      initials: "MT"
    }
  }
  const posts = [
    {
      url: "/assets/videos/3560017-uhd_3840_2160_25fps.mp4",
      id: "1",
    },
    {
      url: "/assets/videos/12462341-hd_1080_1920_60fps.mp4",
      id: "2",
    },
    {
      url: "/assets/videos/14621116_1920_1080_25fps.mp4",
      id: "3",
    },
    {
      url: "/assets/videos/15390776_2160_3840_60fps.mp4",
      id: "4",
    },
    {
      url: "/assets/videos/15391407_2160_3840_60fps.mp4",
      id: "5",
    },
    {
      url: "/assets/videos/15448985-hd_1920_1080_60fps.mp4",
      id: "6",
    },
    {
      url: "/assets/videos/15449387-hd_1920_1080_60fps.mp4",
      id: "7",
    },
    {
      url: "/assets/videos/15480914_2160_3840_60fps.mp4",
      id: "8",
    },
    {
      url: "/assets/videos/15750007_2160_3840_60fps.mp4",
      id: "9",
    },
    {
      url: "/assets/videos/15750073_2160_3840_60fps.mp4",
      id: "10",
    },
    {
      url: "/assets/videos/15750079_2160_3840_60fps.mp4",
      id: "",
    },
  ]
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
          <ProfilePostList posts={posts} />
        </div>
      </div>
      <div className="hidden lg:block lg:col-span-3 p-4 relative flex">
         <div className="block lg:sticky top-4 w-full pt-16">
          <h3 className='text-sky-950 font-bold text-xl mb-3'>Configurações Rápidas</h3>
          <div className='flex gap-3 items-center w-full mb-3'>
            <InputToggleButton fieldName='allow-whatsapp' className='shrink-0' />
            <span>Permitir que olheiros entrem em contato pelo WhatsApp</span>
          </div>
          <div className='flex gap-3 items-center w-full'>
            <InputToggleButton fieldName='allow-info' className='shrink-0' />
            <span>Ocultar informações pessoais</span>
          </div>
         </div>
      </div>
      
    </div>
    <Footer />
  </div>
}
