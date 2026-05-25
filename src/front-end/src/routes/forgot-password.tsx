import Footer from '#/components/Footer'
import Input from '#/components/ui/Input'
import { createFileRoute, Link } from '@tanstack/react-router'

export const Route = createFileRoute('/forgot-password')({
  component: RouteComponent,
})

const sendRecoveryEmail = (form: FormData) => {

}

function RouteComponent() {
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
          <div className='border-b-2 border-gray-200 p-3 min-w-xl grow-1 flex flex-col pt-12'>
            <h3 className='text-sky-950 font-extrabold text-2xl mb-5'>Recuperar a senha</h3>
            <form action={sendRecoveryEmail} className='grow-1 flex flex-col'>
              <div className='mb-auto'>
                <Input fieldName='E-Mail' />
              </div>
              <button className='button primary rounded-full px-4 w-full mb-3' type="submit">Recuperar Senha</button>
            </form>
          </div>
          <div className='text-center py-12 px-3 w-full'>
            <h4 className='text-xl font-bold text-sky-900 mb-1'>Lembrou da sua senha?</h4>
            <Link className="text-underline text-sky-700 underline underline-offset-8" to="/login">Voltar a página de login</Link>
          </div>
        </div>
      </section>
      <Footer />
    </div>
}
