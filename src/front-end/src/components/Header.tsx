import { Link } from '@tanstack/react-router'

export default function Header() {
  return (
    <header className='flex align-start w-full max-h-16'>
      <Link to="/">
        <img src="/logo-scoutplay.png" alt="Logo Scoutplay" className='h-full' />
      </Link>
    </header>
  )
}
