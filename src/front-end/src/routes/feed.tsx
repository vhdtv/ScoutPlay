import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import { createFileRoute, Link } from '@tanstack/react-router'

export const Route = createFileRoute('/feed')({
  component: RouteComponent,
})

function Post({data}: any) {
  return (
    <div className='bg-slate-100 overflow-hidden rounded-xl max-w-96'>
      <Link to="/user/$user" params={{user: data.user.url}} className='text-sm bg-slate-200 flex flex-col'>
        <div className='flex gap-2 p-2 items-center'>
          <div className='w-8 rounded-full overflow-hidden'>
            {
              data.user.profile?.img
              ? <img src={data.user.profile.img} className='w-full h-full' alt="User profile" />
              : <span className='w-full h-full flex items-center justify-center text-4xl font-bold bg-slate-500 text-slate-50'>{data.user.initials}</span>
            }
          </div>
          <span>{data.user.name}</span>
        </div>
      </Link>
      <video src={data.media.src} className='w-full'></video>
    </div>
  )
}

export function RouteComponent() {
  let feedPosts = Array(12).fill("");
  feedPosts = [
    {
      user: {
        name: "Teste",
        initials: "T",
        url: "teste_slug",
        profile: {
          img: "https://unsplash.it/100"
        }
      },
      title: "Um teste aqui",
      media: {
        type: "video",
        src: "/assets/videos/15750073_2160_3840_60fps.mp4",
      }
    },
    {
      user: {
        name: "Teste",
        initials: "T",
        url: "teste_slug",
        profile: {
          img: "https://unsplash.it/100"
        }
      },
      title: "Um teste aqui",
      media: {
        type: "video",
        src: "/assets/videos/15750079_2160_3840_60fps.mp4",
      }
    }
  ]
  return (
    <div className='min-h-screen flex flex-col'>
          <LoggedHeader/>
          <div className="container grow-1 mx-auto flex justify-center grid grid-cols-12 gap-2">
            <div className='col-span-12 md:col-span-3 xl:col-span-4 relative'>
              <div className='top-12 sticky bg-white px-6 py-4 mt-12 rounded-xl shadow-xl flex flex-col gap-3'>
                <h3 className='text-slate-900 opacity-60 text-2xl'>Filtros</h3>
                <button className='transition outline-none rounded-md px-5 text-sm py-2 text-slate-700 cursor-pointer self-end bg-white border border-slate-700 hover:bg-slate-700 hover:text-white focus:bg-slate-700 focus:text-white'>Aplicar Filtros</button>
              </div>
            </div>
            <div className='col-span-12 md:col-span-6 xl:col-span-4 flex flex-col gap-4 items-center p-4'>
              {feedPosts.map(postData => (<Post data={postData} />))}
            </div>
            <div className='col-span-12 md:col-span-3 xl:col-span-4'></div>
          </div>
          <Footer/>
    </div>
  )
}
