import Input from "#/components/ui/Input"
import { useFormStatus } from "react-dom";
import { Link, useNavigate } from "@tanstack/react-router"
import { useState } from "react";
import API from "#/api/API";

const api = new API

function LoginButton() {
  const status = useFormStatus();
  return (
    <button className="block button primary w-full mb-2 rounded-full" disabled={status.pending} type="submit">{status.pending ? '...' : 'Entrar'}</button>
  )
}

export default function LoginForm() {
  const navigateHook = useNavigate();
  const [loginError, setLoginError] = useState(false);
  const makeLoginAttempt = async (form: FormData) => {
    const email = form.get('E-Mail')?.toString();
    const senha = form.get('Senha')?.toString();
    if(!email) throw new Error(); // para forçar a cair no catch()
    if(!senha) throw new Error(); // para forçar a cair no catch()
    try {
      setLoginError(false)
      const data = await api.fazerLogin(email, senha);
      navigateHook({to: "/feed"})
    }
    catch(error) {
      console.error(error);
      setLoginError(true);
    }
  }
  return (
    <form action={makeLoginAttempt} className="flex flex-col w-full">
      {loginError && <span className="p-3 text-xs bg-red-100 mb-3 self-start border-1 border-red-300 rounded-md">Login ou senha incorretos</span> }
      <Input fieldName="E-Mail" type="email" placeholder="Insira o email" className="mb-2"/>
      <Input fieldName="Senha" type="password" placeholder="Insira sua senha" className="mb-2"/>
      <div className="my-4">
        <LoginButton />
        <Link className="block button ghost w-full rounded-full" to="/forgot-password">Esqueci minha senha</Link>
      </div>
    </form>
  )
}
