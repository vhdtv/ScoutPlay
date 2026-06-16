import API from '#/api/API';
import Footer from '#/components/Footer';
import InputField from '#/components/ui/InputField'
import PasswordField from '#/components/ui/PasswordField'
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'

export const Route = createFileRoute('/login')({
  component: RouteComponent,
})

const api = new API;
export function RouteComponent() {
  const navigate = useNavigate();
  const fazerLogin = async(form: FormData) => {
    const login = form.get("email")?.toString();
    if(!login || login == "") return;
    const senha = form.get("senha")?.toString();
    if(!senha || senha == "") return;
    try {
      const data = await api.fazerLogin(login, senha);
      navigate({to: "/feed"})
    }
    catch(e) {
      console.error(e)
    }
  }
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
            <h1 className='text-2xl font-medium mb-4.5'>Acesse sua conta</h1>
            <form action={fazerLogin} className='flex flex-col gap-3'>
              <InputField defaultValue={"fabio@atleta.com"} type="email" fieldName='email' label="E-Mail" className='w-full' placeholder='Informe o e-mail'/>
              <div className='mb-4 flex flex-col'>
                <PasswordField defaultValue={"12345"} fieldName='senha' label="Senha" className='w-full mb-1 pe-8' placeholder='Informe a senha' />
                <Link to="/forgot-password" className='ms-auto outline-mist-300 inline-block text-xs p-1 px-3 hover:bg-mist-100 focus:bg-mist-100 rounded-full '>Esqueci a senha</Link>
              </div>
              <div className='self-start w-full flex gap-2'>
                <button className='grow text-white bg-emerald-600 hover:bg-emerald-700 focus:bg-emerald-700 outline-none rounded-full py-2 px-12 font-semibold text-sm self-end cursor-pointer'> Entrar </button>
              </div>
            </form>
            <div data-orientation="horizontal" role="none" data-slot="separator" className="shrink-0 h-[1px] my-8 grow bg-mist-300"></div>
            <h2 className='text-center text-lg/6 text-mist-700 font-semibold'>É novo por aqui?</h2>
            <p className='text-center text-sm mb-8'>Crie uma conta especializada</p>
            <div className='overflow-hidden rounded-full flex border-1 border-mist-300 font-semibold mb-3'>
                <Link to="/signup" search={{tipoConta:'atleta'}} className='text-center text-mist-600 grow hover:bg-mist-100 focus:bg-mist-100 outline-none py-2 px-5 text-sm self-end cursor-pointer border-mist-200'> Sou Atleta </Link>
                <Link to="/signup" search={{tipoConta:'olheiro'}} className='text-center text-mist-600 grow hover:bg-mist-100 focus:bg-mist-100 outline-none py-2 px-5 text-sm self-end cursor-pointer border-s-1 border-mist-200'> Sou Olheiro </Link>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  )
}