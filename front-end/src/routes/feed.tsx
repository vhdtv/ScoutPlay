import API from '#/api/API'
import type { PostDetailsDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import Header from '#/components/Header'
import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import PostComponent from '#/components/PostComponent'
import CreatePostComponent from '#/components/CreatePostComponent'

const api = new API

export const Route = createFileRoute('/feed')({
  component: RouteComponent,
})

export function RouteComponent() {
  const [page, setPage] = useState(0);
  const [postsInView, setPostsInView] = useState([] as PostDetailsDTO[]);

useEffect(() => {
  api.obterPostsFeed({page})
  .then(({data}) => {
    setPage(old => old + 1);
    setPostsInView([...data]);
  });
}, [])

return (
    <div className='min-h-screen bg-pattern field-pattern flex flex-col relative'>
      <Header />
      <main className='grow-1 flex container mx-auto pb-8'>
        <div className='px-4 py-12 flex flex-col gap-5 flex items-end grow lg:basis-8/12 lg:ms-auto'>
          <CreatePostComponent className="w-full lg:max-w-[35vw]" />
          {postsInView.map((post, index) => <PostComponent key={index} className="w-full lg:max-w-[35vw]" post={post} />)}
        </div>
        <div className='px-4 hidden lg:block pt-22 grow basis-4/12 me-auto'>
          <div className='card p-6 max-w-[20vw]'>
            <h3 className='text-xl mb-4'>Para você</h3>
            {Array(0).fill("").map(_ => (
              <div className='user-card border border-mist-200 p-4 rounded-2xl flex gap-4 items-center mb-3'>
                <img src="https://unsplash.it/100" className='w-12 rounded-full' />
                <div className='flex flex-col justify-center grow items-start'>
                  <span className='font-semibold'>Nome Usuario</span>
                  <span className='text-mist-400 text-sm/5 h6  tracking-wide'>@username</span>
                </div>
                {/* <button className='text-sm/4 flex items-center py-2 px-3 rounded-full transition cursor-pointer outline-none bg-blue-200 text-blue-900 font-semibold hover:bg-mist-100 hover:text-mist-600 focus:bg-mist-100 focus:text-mist-600'>Seguindo</button> */}
                <button className='text-sm/4 flex items-center py-2 px-3 rounded-full transition cursor-pointer outline-none font-semibold hover:bg-mist-100 focus:bg-mist-100'>Seguir</button>
              </div>
            ))}
          </div>
        </div>
      </main>
      <Footer />
    </div>
  )
}
