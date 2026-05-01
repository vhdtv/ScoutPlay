import Input from "#/components/ui/Input"
import { Link } from "@tanstack/react-router"

export default function LoginForm() {
  return (
    <form action="" className="flex flex-col w-full">
      <Input fieldName="E-Mail" type="email" placeholder="Insira o email" className="mb-2"/>
      <Input fieldName="Senha" type="password" placeholder="Insira sua senha" className="mb-2"/>
      <div className="my-4">
        <button className="block button primary w-full mb-2 rounded-full" type="submit">Entrar</button>
        <Link className="block button ghost w-full rounded-full" to="/forgot-password">Esqueci minha senha</Link>
      </div>
    </form>
  )
}
