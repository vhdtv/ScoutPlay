import API from '#/api/API';
import Footer from '#/components/Footer'
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { useState } from 'react';
import InputField from '#/components/ui/InputField';
import PasswordField from '#/components/ui/PasswordField';
import DateField from '#/components/ui/DateField';
import type { ClientSignupInputDTO } from '#/api/tipos';

enum STEPS {ABOUT_YOU, PROFISSIONAL_HISTORY, ACCESS_FORM};

const api = new API;

export const Route = createFileRoute('/signup')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>) => {
    return {
      tipoConta: search?.tipoConta as string || ""
    }
  }
})

// @ts-ignore
let signupData: ClientSignupInputDTO = {};
function RouteComponent() {
  const SECTIONS = ["Sobre Você", "Formação Profissional", "Forma de Acesso"]
  const { tipoConta } = Route.useSearch();
  signupData.tipoConta = tipoConta;
  
  const [currentSection, setCurrentSection] = useState(STEPS.ABOUT_YOU);
  const navigate = useNavigate();
  
  const fazerCadastro = async () => {
    await api.fazerCadastro(signupData);
    api.deletarDadosUsuarioNoNavegador()
    await api.fazerLogin(signupData.email, signupData.senha);
    navigate({to: "/feed"});
  }

  const savePersonalData = (form: FormData) => {
      signupData.nome = form.get("nome")!.toString();
      signupData.sobrenome = form.get("sobrenome")!.toString();
      signupData.cpf = form.get("cpf")!.toString();
      signupData.dataNascimento = new Date(form.get("dataNascimento")!.toString());
    setCurrentSection(STEPS.ACCESS_FORM);
  }
  const saveLoginData = (form: FormData) => {
    signupData.email = form.get("email")!.toString();
    signupData.senha = form.get("senha")!.toString();
    fazerCadastro()
  }

  const renderCards = () => {
    switch(currentSection) {
      case STEPS.ABOUT_YOU:
      default:
        return (
          <div className="card p-6 w-full max-w-[600px] fade-in">
            <h1 className='text-2xl/7 font-medium'>Fazer Cadastro</h1>
            <h2 className='text-lg text-mist-500 font-semibold mb-6'>Dados Pessoais (1/2)</h2>
            <form action={savePersonalData} className='flex flex-col gap-6'>
              <div className='flex gap-3'>
                <InputField fieldName='nome' label="Seu Nome" placeholder='Digite aqui' classNameContainer='grow basis-2/6' />
                <InputField fieldName='sobrenome' label="Seu Sobrenome" placeholder='Digite aqui' classNameContainer='grow basis-4/6' />
              </div>
              <div className='flex gap-3'>
                <InputField fieldName='cpf' label="Seu CPF" placeholder='Digite aqui' classNameContainer='grow basis-1/2' />
                <DateField fieldName='dataNascimento' label="Data de nascimetno" />
              </div>
              <button className='grow text-white bg-emerald-600 hover:bg-emerald-700 focus:bg-emerald-700 outline-none rounded-full py-2 px-12 font-semibold text-sm self-start cursor-pointer'> Próximo </button>
            </form>
          </div>
        )
      case STEPS.ACCESS_FORM:
        return (
          <div className="card p-6 w-full max-w-[600px] fade-in">
            <h1 className='text-2xl/7 font-medium'>Fazer Cadastro</h1>
            <h2 className='text-lg text-mist-500 font-semibold mb-6'>Forma de Acesso (2/2)</h2>
            <form action={saveLoginData} className='flex flex-col gap-6'>
              <InputField fieldName='email' label="Seu email" placeholder='Digite aqui' />
              <PasswordField fieldName='senha' label="Senha" placeholder='Digite aqui' hideIcon={true} />
              <PasswordField fieldName='confirmarSenha' label="Confirmar Senha" placeholder='Digite aqui' hideIcon={true} />
              <button className='grow text-white bg-emerald-600 hover:bg-emerald-700 focus:bg-emerald-700 outline-none rounded-full py-2 px-12 font-semibold text-sm self-start cursor-pointer'> Próximo </button>
            </form>
          </div>
        )
    }
  }


  return (
      <div className='w-screen h-screen bg-pattern soccer-pattern flex flex-col relative'>
        <main className='grow-1 flex container mx-auto'>
          <section className='flex flex-col grow items-center justify-center'>
            {renderCards()}
          </section>
        </main>
        <Footer />
      </div>
    )
}
