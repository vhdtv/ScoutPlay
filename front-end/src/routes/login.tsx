import Footer from '#/components/Footer'
import LoginForm from '#/components/LoginForm'
import { createFileRoute, Link } from '@tanstack/react-router'

export const Route = createFileRoute('/login')({
  component: RouteComponent,
})

export function RouteComponent() {
  return <div className='page-noscroll flex flex-col'>
    <section className="content grow-1 flex flex-row">
      <div className='grow-1 hidden md:flex border-e-2 border-gray-200 p-3 pt-12'>
        <section className='logo'>
          <div className='flex'>
            <img src="/assets/logo.svg" className='h-16 m-2' />
            <div className='grow-1 items-center content-center'>
              <img src="/assets/text-logo.png" className='h-10'/>
              <h2 className='font-medium'>O meio-de-campo entre atletas e olheiros</h2>
            </div>
          </div>
        </section>
        <section className="carousel"></section>
      </div>
      <div className='shrink-0 sm:grow-0 flex flex-col flex-wrap items-center justify-center'>
        <div className='border-b-2 border-gray-200 p-3 min-w-xl'>
          <h3 className='text-sky-950 font-extrabold text-2xl mb-3'>Entre na sua conta</h3>
          <LoginForm />
        </div>
        <div className='text-center py-12 px-3 w-full'>
          <h4 className='text-2xl font-bold text-sky-900'>É novo por aqui?</h4>
          <p className='mb-6 text-xl text-sky-800'>Crie uma conta especializada</p>
          <div className='flex w-full overflow-hidden border-1 border-sky-700 rounded-full'>
            <Link className="button primary-outlined grow-1" to="/signup" search={{type:'atleta'}}>Sou atleta</Link>
            <div className='h-auto flex self-stretch w-[1px] bg-sky-700'></div>
            <Link className="button primary-outlined grow-1" to="/signup" search={{type:'olheiro'}}>Sou olheiro</Link>
          </div>
        </div>
      </div>
    </section>
    <Footer />
  </div>
}
