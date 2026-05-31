import Input from "#/components/ui/Input"
import { useFormStatus } from "react-dom";
import { Link, useNavigate } from "@tanstack/react-router"
import { BACKEND_ENDPOINT } from "./variables"
import { useState } from "react";

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
    const email = form.get('E-Mail');
    const password = form.get('Senha');
    try {
      setLoginError(false)
      const backendRequest = await fetch(`${BACKEND_ENDPOINT}/api/login`, {
        method: 'POST',
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({email, senha: password})
      })
      if(backendRequest.status !== 200) return setLoginError(true);
      await backendRequest.json();
      navigateHook({to: "/feed"})
    }
    catch(error) {
      console.error(error)
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
