import { createFileRoute, Link } from '@tanstack/react-router'
import { getPostById } from '../../services/postService'
import { LoggedHeader } from '#/components/Header';
import Footer from '#/components/Footer';

const mockText = [
  "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",
  "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum",
  "Só um teste",
]

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

export const Route = createFileRoute('/post/$post_id')({
  component: RouteComponent,
  loader: async ({params}) => {
    const postData = await getPostById(params.post_id);
    return {
        postId: params.post_id
    }
  }
})

function Comment({text, user}: any) {
  return <div className='flex gap-2 p-2'>
    <Link to='/user/$user' params={{user: user.url}}>
      <div className='flex justify-center items-center w-8 overflow-hidden rounded-full border border-red-100 bg-slate-50 mb-2'>
        {
          user.profile?.img
          ? <img src={user.profile.img} className='w-full h-full' alt="User profile" />
          : <span className='w-full h-full flex items-center justify-center text-4xl font-bold bg-slate-500 text-slate-50'>{user.initials}</span>
        }
      </div>
    </Link>
    <span>{text}</span>
  </div>
}

function RouteComponent() {
  const { postId } = Route.useLoaderData();
  return (
    <div className='page-noscroll flex flex-col'>
      <LoggedHeader/>
      <div className="container grow-1 mx-auto flex items-center justify-center">
        <div className='shadow-lg grid grid-cols-12 rounded-lg overflow-hidden'>
            <div className="col-span-8 bg-slate-900 flex items-center h-full">
              <video className='object-fit object-center' loop src="/assets/videos/3560017-uhd_3840_2160_25fps.mp4"></video>
            </div>
            <div className="col-span-4 bg-slate-100 overflow-hidden flex h-full flex-col">
              <div className='flex gap-2 p-2 border-b-1 border-slate-300 bg-slate-200'> 
                <div className='w-8 h-8 text-sm rounded-full bg-white flex items-center justify-center font-bold'>T</div>
                <div className='flex flex-col justify-center'>
                  <small className='leading-4 font-bold'>Nome Usuário</small>
                  <small className='leading-none opacity-50'>2 novas publicações</small>
                </div>
              </div>
              <div className='flex flex-col overflow-auto grow-1 md:max-h-48 lg:max-h-96'>
                {Array(90).fill("").map((_, index) => <Comment user={user} text={mockText[index % mockText.length]} />)}
              </div>
              <div className='p-4 relative text-xs flex gap-2'>
                <textarea placeholder='Adicione um comentário...' className='outline-none w-full p-2 border-1 border-slate-200 rounded-md hover:border-slate-400 hover:bg-slate-200 focus:border-slate-400 focus:bg-slate-200 resize-none'> </textarea>
                <button className='flex items-center p-2 bg-white hover:bg-sky-700 m-auto hover:text-white focus:bg-sky-700 focus:text-white transition cursor-pointer rounded-full outline-none'>
                  <span className="material-symbols-outlined" style={{fontSize: "1.2rem"}}> send </span>
                </button>
              </div>
            </div>
          </div>
      </div>
      <Footer/>
    </div>
  )
}

