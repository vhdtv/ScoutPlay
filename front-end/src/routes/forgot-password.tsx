import Footer from '#/components/Footer'
import InputField from '#/components/ui/InputField'
import Input from '#/components/ui/InputField'
import PasswordField from '#/components/ui/PasswordField'
import { createFileRoute, Link } from '@tanstack/react-router'

export const Route = createFileRoute('/forgot-password')({
  component: RouteComponent,
})

const sendRecoveryEmail = (form: FormData) => {

}

function RouteComponent() {
  return (
      <div className='w-screen h-screen bg-pattern soccer-pattern flex flex-col relative'>
        <main className='grow-1 flex container mx-auto'>
          <div className='w-full grow px-4 py-12 flex flex-col gap-5'>
            <div className='flex gap-3 items-center'>
              <img src="/assets/icon.svg" className='grayscale w-16' />
              <div>
                <h3 className='text-3xl font-semibold text-mist-600'>ScoutPlay</h3>
                <p className='text-lg/5 text-mist-500'>O meio-de-campo entre atletas e olheiros</p>
              </div>
            </div>
            <div className='grow h-full  justify-center'>
              <div className='relative flex items-center justify-center'>
                <img src="/assets/images/silhouette.png" className='opacity-60 max-h-[70vh] px-50' />
                <h6 className='text-end font-semibold text-mist-700 text-5xl absolute bottom-0 end-0 max-w-[10em]'>Sua história pode começar <span className='text-emerald-600'>aqui</span></h6>
              </div>
            </div>
          </div>
          <div className='w-full grow px-4 pt-32 max-w-[500px]'>
            <div className='card p-6 inline-flex flex-col w-full shrink-0'>
              <h1 className='text-2xl font-medium mb-4.5'>Esqueci a Senha</h1>
              <form action={sendRecoveryEmail} className='flex flex-col gap-3'>
                <InputField type="email" fieldName='email' label="E-Mail" className='w-full mb-2' placeholder='Informe o e-mail cadastrado'/>
                <button className='w-full text-white bg-emerald-600 hover:bg-emerald-700 focus:bg-emerald-700 outline-none rounded-full py-2 px-12 font-semibold text-sm self-end cursor-pointer'> Enviar email de recuperação </button>
                <Link className='text-center text-mist-600 grow hover:bg-mist-100 focus:bg-mist-100 outline-none py-2 px-5 text-sm cursor-pointer rounded-full border-1 border-mist-200' to="/login">
                  <i className="ri-arrow-left-s-line me-1"></i>
                  <span>Voltar para login</span>
                </Link>
              </form>
            </div>
          </div>
        </main>
        <Footer />
      </div>
    )
}
