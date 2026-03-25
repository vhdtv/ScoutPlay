import { createFileRoute } from '@tanstack/react-router'
import Header from "#/components/Header"
import LoginForm from "#/components/LoginForm"

export const Route = createFileRoute('/')({ component: App })

function App() {
  return (
    <>
      <Header />
      <main className='bg-sky-950 text-gray-200 flex'>
        <section className="hero">
          <section className="tagline text-center py-7 px-4">
            <h1 className='text-4xl font-bold'>Conectando Atletas e Olheiros</h1>
            <p className='text-2xl'>Em busca de uma oportunidade no mundo do futebol? Iremos te ajudar!</p>
          </section>
          <section className="flex items-center grow-0 leading-6">
            <section className="about text-center p-4">
              <h2 className='text-2xl font-semibold text-gray-400 mb-3'>Sobre nós</h2>
              <p>Nossa plataforma conecta jovens jogadores de futebol com olheiros, clubes e agências esportivas, oferecendo uma oportunidade única para aqueles que sonham em seguir carreira no futebol. Para garantir a segurança dos menores de 18 anos, o cadastro é feito por pais ou responsáveis.</p>
            </section>
            <div className="vertical-line h-48 bg-sky-100/50 w-0.5"></div>
            <section className="how-works text-center p-4">
              <h2 className='text-2xl font-semibold text-gray-400 mb-3'>Como funciona?</h2>
              <p>Poste seus vídeos com seus melhores momentos, e preencha um formulário com suas qualificações como atleta! Olheiros cadastrados estarão de olho nos seus melhores lances!</p>
            </section>
          </section>
        </section>
        <section className="login-form flex items-center pe-4">
          <LoginForm />
        </section>
      </main>
    </>
  )
}
