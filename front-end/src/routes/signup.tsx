import API from '#/api/API';
import Footer from '#/components/Footer'
import Input from '#/components/ui/Input';
import InputDate from '#/components/ui/InputDate';
import InputStateField from '#/components/ui/InputStateField';
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router'
import { useState } from 'react';

enum STEPS {ABOUT_YOU, PROFISSIONAL_HISTORY, ACCESS_FORM};

const api = new API;

export const Route = createFileRoute('/signup')({
  component: RouteComponent,
})

function RouteComponent() {
  const SECTIONS = ["Sobre Você", "Formação Profissional", "Forma de Acesso"]
  let [currentSection, setCurrentSection] = useState(STEPS.ABOUT_YOU);
  const navigate = useNavigate();
  
  const savePersonalInfo = (form: FormData) => {
    console.log(form.get("Estado"))
    setCurrentSection(STEPS.PROFISSIONAL_HISTORY)
  }
  
  const saveProfissionalHistoryInfo = async (form: FormData) => {
    const data = await api.fazerCadastro({
      cpf: "999999999-00",
      dataNascimento: new Date("12-24-00"),
      username: "masterYii",
      email: "master@chief.com",
      nome: "Master",
      sobrenome: "Chief",
      senha: "ala",
      tipoConta: "atleta",
    })
    console.log({data})
  }
  const renderSwitch = () => {
    switch(currentSection) {
      case STEPS.ABOUT_YOU:
      default:
        return <form action={savePersonalInfo} className='w-full md:min-w-[670px]'>
          <section className='shadow-lg rounded-lg mb-4 p-6'>
            <Input fieldName='Seu Nome' className='mb-3'/>
            <InputDate fieldName='Sua Data de Nascimento' className='mb-3' />
            <div className='flex gap-3'>
              <InputStateField fieldName='Estado' />
              <Input fieldName='Cidade' />
            </div>
          </section>
          <div className='flex justify-end items-center'>
            <button className='button primary rounded-full px-12' type='submit'>Próximo</button>
          </div>
        </form>
      case STEPS.PROFISSIONAL_HISTORY:
        return <form action={saveProfissionalHistoryInfo} className='w-full md:min-w-[670px]'>
          <section className='shadow-lg rounded-lg mb-4 p-6'>
            <Input fieldName='Seu Nome' className='mb-3'/>
            <InputDate fieldName='Sua Data de Nascimento' className='mb-3' />
            <div className='flex gap-3'>
              <InputStateField fieldName='Estado' />
              <Input fieldName='Cidade' />
            </div>
          </section>
          <div className='flex justify-end items-center gap-4'>
            <button className='button primary-outlined border rounded-full px-8' type='submit'>Pular</button>
            <button className='button primary rounded-full px-12' type='submit'>Próximo</button>
          </div>
        </form>
      case STEPS.ACCESS_FORM:
        return <form action={saveProfissionalHistoryInfo} className='w-full md:min-w-[670px]'>
          <section className='shadow-lg rounded-lg mb-4 p-6'>
            <Input type="email" fieldName='E-Mail' className='mb-3'/>
            <Input type="password" fieldName='Senha' className='mb-3'/>
            <Input type="password" fieldName='Confirmar senha' className='mb-3'/>
          </section>
          <div className='flex justify-end items-center'>
            <button className='button primary rounded-full px-12' type='submit'>Criar Conta</button>
          </div>
        </form>
        break;
    }
  }


  return <div className='page-noscroll flex flex-col'>
      <div className='max-w-[1024px] mx-auto content grow-1 flex'>
        <section className="flex flex-col p-4 justify-stretch w-full">
          <div className='mt-16'>
            <Link className="text-sm primary-outlined inline-flex items-center gap-2 transition transition-discrete text-sky-600 hover:text-sky-900 focus:text-sky-900" to="/login">
              <span className="material-symbols-outlined">arrow_back</span>
              <span>Voltar para Login</span>
            </Link>
            <h1 className='text-sky-950 font-extrabold text-2xl'>Crie sua conta</h1>
            <p className='text-sky-950 font-extrabold mb-3'>{`${SECTIONS[currentSection]} (${currentSection+1}/${SECTIONS.length})`}</p>
          </div>
          { renderSwitch() }
        </section>
      </div>
      <Footer />
    </div>
}
