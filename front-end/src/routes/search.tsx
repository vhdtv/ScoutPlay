import API from '#/api/API'
import type { MensagemDTO, PostDetailsDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import Header from '#/components/Header'
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { useEffect, useState, type ChangeEvent } from 'react'
// import { PostMediaComponent } from './post/$post_id'
// import ProfilePicture from '#/components/ui/ProfilePicture'
import Input from '#/components/ui/InputField'

const api = new API

export const Route = createFileRoute('/search')({
  component: RouteComponent,
})

export function RouteComponent() {
  const postsCache: PostDetailsDTO[] = []
  const [postsInView, setPostsInView] = useState([] as PostDetailsDTO[]);
  const [dialogPostCreate, setDialogPostCreate] = useState(false);
  const navigate = useNavigate();
  
  function DialogPostCreateComponent() {
    const [newPostMedia, setNewPostMedia] = useState("");
    const criarPost = async (form: FormData) => {
      const titulo = form.get("titulo")?.toString();
      if(!titulo) return;
      const arquivo = form.get("media") as File;
      if(!arquivo) return;
      let descricao = form.get("descricao")?.toString() ?? "";

      const post = await api.criarPost({arquivo, titulo, descricao});
      if(!post) return;
      setDialogPostCreate(false)
      navigate({to: "/post/$post_id", params: {post_id: post.url}})

    }

    const previewMedia = ({target}: ChangeEvent<HTMLInputElement>) => {
      if(!target.files) return;
      const [ file ] = target.files;
      setNewPostMedia(URL.createObjectURL(file));
    }
    
    return (
      <div onClick={() => setDialogPostCreate(false)} className='absolute flex items-center justify-center fixed top-0 start-0 end-0 bottom-0 z-3 outline-hidden bg-slate-900/50 backdrop-blur-xs'>
      <form action={criarPost} onClick={(e) => e.stopPropagation()} className='h-128 flex gap-2 w-full max-w-256 bg-white rounded-xl overflow-hidden mx-4'>
        <div style={{backgroundImage: `url(${newPostMedia})`}} className='w-full h-full grow bg-slate-100 aspect-square relative bg-center bg-contain bg-no-repeat flex flex-col gap-4 items-center justify-center transition hover:bg-slate-200'>
          {
            !newPostMedia && (
              <>
                <span className="material-symbols-outlined" style={{fontSize: "2em"}}> image_arrow_up </span>
                <span>Faça o upload de um arquivo</span>
              </>
             )
          }
          <label className='w-full h-full absolute cursor-pointer'>
            <input type="file" className='hidden' onChange={previewMedia} name="media" />
          </label>
        </div>
        <div className='w-3/4 text-start flex flex-col gap-4 p-4'>
          <Input fieldName='titulo' label='Título' />
          <Input fieldName='descricao' label='Descrição' />
          <button type='submit' className='self-start p-2 px-8 rounded-full bg-sky-400 hover:bg-sky-700 text-white cursor-pointer'>Postar</button>
        </div>
      </form>
    </div>
  );
}

const obterPostsParaFeed = async (page = 0) => {
  const { data } = await api.obterPostsFeed({page});
  setPostsInView(data);
  postsCache.push(...data);
}
useEffect(() => {
  obterPostsParaFeed()
}, [])

return (
    <div className='w-screen h-screen bg-pattern field-pattern flex flex-col relative'>
      <Header />
      <main className='grow-1 flex container mx-auto'>
        <div className='w-full grow px-4 py-12 flex flex-col gap-5'>
        </div>
        <div className='w-full grow px-4 pt-32 max-w-[500px]'>
        </div>
      </main>
      <Footer />
    </div>
  )
}
