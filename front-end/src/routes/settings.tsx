import API from '#/api/API'
import type { UserProfileDTO } from '#/api/tipos'
import Footer from '#/components/Footer'
import { LoggedHeader } from '#/components/Header'
import Input from '#/components/ui/Input'
import ProfilePicture from '#/components/ui/ProfilePicture'
import { createFileRoute, redirect } from '@tanstack/react-router'
import { useRef, useState } from 'react'

const api = new API

export const Route = createFileRoute('/settings')({
  beforeLoad: () => {
    const api = new API()
    if (!api.obterDadosUsuarioNoNavegador()) throw redirect({ to: '/login' })
  },
  component: RouteComponent,
  loader: async () => {
    try {
      return { perfil: await api.obterDadosDoPerfil() }
    } catch {
      return { perfil: {} as UserProfileDTO }
    }
  },
})

type Section = 'perfil' | 'esportivos' | 'privacidade' | 'conta'

const POSICOES = [
  'Goleiro', 'Zagueiro Central', 'Zagueiro Direito', 'Zagueiro Esquerdo',
  'Lateral Direito', 'Lateral Esquerdo', 'Volante Direito', 'Volante Esquerdo',
  'Meio-campista Central', 'Meio-campista Direito', 'Meio-campista Esquerdo',
  'Segundo Atacante', 'Ponta Direita', 'Ponta Esquerda', 'Centroavante',
]

const NAV_ITEMS: { id: Section; label: string; icon: string }[] = [
  { id: 'perfil', label: 'Perfil', icon: 'person' },
  { id: 'esportivos', label: 'Dados Esportivos', icon: 'sports_soccer' },
  { id: 'privacidade', label: 'Privacidade', icon: 'lock' },
  { id: 'conta', label: 'Conta', icon: 'manage_accounts' },
]

function RouteComponent() {
  const { perfil } = Route.useLoaderData()
  const [usuario, setUsuario] = useState(perfil as UserProfileDTO)
  const [section, setSection] = useState<Section>('perfil')
  const [saved, setSaved] = useState(false)
  const [uploadingAvatar, setUploadingAvatar] = useState(false)
  const avatarInputRef = useRef<HTMLInputElement>(null)

  const raw = (usuario?.detalhesPerfil ?? {}) as Record<string, any>
  const dp: Record<string, any> = {
    posicao: raw.posicao ?? raw.POSICAO,
    peDominante: raw.peDominante ?? raw.PE_DOMINANTE,
    peso: raw.peso,
    altura: raw.altura,
    clubesAnteriores: raw.clubesAnteriores ?? raw.CLUBE_QUE_PARTICIPOU,
    ...raw,
  }

  const showSaved = () => { setSaved(true); setTimeout(() => setSaved(false), 2500) }

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    setUploadingAvatar(true)
    try {
      const filename = await api.atualizarFotoPerfil(file)
      setUsuario(u => ({ ...u, fotoPerfil: filename }))
      showSaved()
    } catch {
      // silently fail — user stays on page
    } finally {
      setUploadingAvatar(false)
    }
  }

  const salvarPerfil = async (form: FormData) => {
    const nome = form.get('nome')?.toString()
    const sobrenome = form.get('sobrenome')?.toString()
    const username = form.get('username')?.toString()
    try {
      await api.atualizarInfoPerfil({ nome, sobrenome, username })
      setUsuario(u => ({ ...u, nome: nome ?? u.nome, sobrenome: sobrenome ?? u.sobrenome, username: username ?? u.username }))
      showSaved()
    } catch {
      // silently fail — user stays on page
    }
  }

  const salvarEsportivos = async (form: FormData) => {
    const keys = ['posicao', 'peDominante', 'peso', 'altura', 'clubesAnteriores']
    for (const key of keys) {
      const val = form.get(key)?.toString()
      if (val && val.trim()) await api.definirDetalhePerfil(key, val.trim())
    }
    showSaved()
  }

  return (
    <div className='min-h-screen flex flex-col bg-slate-50'>
      <LoggedHeader />

      <div className='container mx-auto px-4 py-6 flex flex-col lg:flex-row gap-4 grow'>

        {/* Sidebar */}
        <aside className='lg:w-60 shrink-0'>
          <div className='bg-white rounded-xl shadow p-2'>
            <p className='text-xs font-bold uppercase text-slate-400 px-3 py-2 tracking-wide'>Configurações</p>
            {NAV_ITEMS.map(item => (
              <button
                key={item.id}
                onClick={() => setSection(item.id)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition cursor-pointer ${
                  section === item.id
                    ? 'bg-sky-50 text-sky-700 font-semibold'
                    : 'text-slate-600 hover:bg-slate-50'
                }`}
              >
                <span className='material-symbols-outlined' style={{ fontSize: '1.1rem' }}>{item.icon}</span>
                {item.label}
              </button>
            ))}
          </div>
        </aside>

        {/* Content */}
        <div className='flex-1 bg-white rounded-xl shadow p-6 min-h-96'>
          {saved && (
            <div className='mb-5 px-4 py-2.5 bg-green-50 border border-green-200 text-green-700 text-sm rounded-lg'>
              Alterações salvas com sucesso!
            </div>
          )}

          {section === 'perfil' && (
            <form action={salvarPerfil} className='flex flex-col gap-5'>
              <h2 className='text-xl font-bold text-slate-800 mb-1'>Perfil</h2>
              <div className='flex items-center gap-4'>
                <div className='relative group shrink-0'>
                  <div className='w-20 h-20 rounded-full overflow-hidden bg-slate-200'>
                    <ProfilePicture user={usuario} className='w-full h-full' />
                  </div>
                  <button
                    type='button'
                    onClick={() => avatarInputRef.current?.click()}
                    disabled={uploadingAvatar}
                    className='absolute inset-0 rounded-full bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition cursor-pointer disabled:cursor-wait'
                  >
                    {uploadingAvatar
                      ? <span className='material-symbols-outlined text-white' style={{fontSize:'1.4rem'}}>hourglass_top</span>
                      : <span className='material-symbols-outlined text-white' style={{fontSize:'1.4rem'}}>photo_camera</span>
                    }
                  </button>
                  <input
                    ref={avatarInputRef}
                    type='file'
                    accept='image/*'
                    className='hidden'
                    onChange={handleAvatarChange}
                  />
                </div>
                <div>
                  <p className='font-semibold text-slate-800'>{usuario?.nome} {usuario?.sobrenome}</p>
                  <p className='text-sm text-slate-500'>@{usuario?.username}</p>
                  <button
                    type='button'
                    onClick={() => avatarInputRef.current?.click()}
                    className='text-xs text-sky-600 hover:underline cursor-pointer mt-0.5'
                  >
                    Alterar foto
                  </button>
                </div>
              </div>
              <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
                <Input defaultValue={usuario?.nome ?? ''} fieldName='nome' label='Nome' />
                <Input defaultValue={usuario?.sobrenome ?? ''} fieldName='sobrenome' label='Sobrenome' />
              </div>
              <Input defaultValue={usuario?.username ?? ''} fieldName='username' label='Nome de Usuário' />
              <button type='submit' className='self-start px-6 py-2 rounded-full bg-sky-600 hover:bg-sky-800 text-white text-sm font-semibold cursor-pointer transition'>
                Salvar
              </button>
            </form>
          )}

          {section === 'esportivos' && (
            <form action={salvarEsportivos} className='flex flex-col gap-5'>
              <h2 className='text-xl font-bold text-slate-800 mb-1'>Dados Esportivos</h2>
              <div className='flex flex-col gap-1'>
                <label className='text-xs font-bold uppercase text-slate-500'>Posição</label>
                <select
                  name='posicao'
                  defaultValue={dp.posicao ?? ''}
                  className='border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-sky-400 bg-white'
                >
                  <option value=''>Selecione a posição</option>
                  {POSICOES.map(p => <option key={p} value={p}>{p}</option>)}
                </select>
              </div>
              <div className='flex flex-col gap-1'>
                <label className='text-xs font-bold uppercase text-slate-500'>Pé Dominante</label>
                <select
                  name='peDominante'
                  defaultValue={dp.peDominante ?? ''}
                  className='border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-sky-400 bg-white'
                >
                  <option value=''>Selecione</option>
                  <option value='DIREITO'>Direito</option>
                  <option value='ESQUERDO'>Esquerdo</option>
                  <option value='AMBOS'>Ambidestro</option>
                </select>
              </div>
              <div className='grid grid-cols-2 gap-4'>
                <Input defaultValue={dp.peso ?? ''} fieldName='peso' label='Peso (kg)' />
                <Input defaultValue={dp.altura ?? ''} fieldName='altura' label='Altura (cm)' />
              </div>
              <Input defaultValue={dp.clubesAnteriores ?? ''} fieldName='clubesAnteriores' label='Clubes Anteriores' />
              <button type='submit' className='self-start px-6 py-2 rounded-full bg-sky-600 hover:bg-sky-800 text-white text-sm font-semibold cursor-pointer transition'>
                Salvar
              </button>
            </form>
          )}

          {section === 'privacidade' && (
            <div className='flex flex-col gap-4'>
              <h2 className='text-xl font-bold text-slate-800 mb-1'>Privacidade</h2>
              <p className='text-slate-500 text-sm'>Configurações de privacidade estarão disponíveis em breve.</p>
            </div>
          )}

          {section === 'conta' && (
            <div className='flex flex-col gap-4'>
              <h2 className='text-xl font-bold text-slate-800 mb-1'>Conta</h2>
              <p className='text-slate-500 text-sm'>Gerenciamento de conta estará disponível em breve.</p>
            </div>
          )}
        </div>

      </div>
      <Footer />
    </div>
  )
}
