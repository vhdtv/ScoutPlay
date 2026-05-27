import Input from "#/components/ui/Input"
import { useFormStatus } from "react-dom";
import { Link, useNavigate } from "@tanstack/react-router"
import { BACKEND_ENDPOINT } from "./variables"

const makeLoginAttempt = async (form: FormData) => {
  const email = form.get('E-Mail');
  const password = form.get('Senha');
  try {
    const backendRequest = await fetch(`${BACKEND_ENDPOINT}/auth/login`, {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify({email, password})
    })
    await backendRequest.json();
    window.location.reload();
  }
  catch(error) {
    console.error(error)
  }
}

function LoginButton() {
  const status = useFormStatus();
  console.log(status.pending)
  return (
    <button className="block button primary w-full mb-2 rounded-full" disabled={status.pending} type="submit">{status.pending ? '...' : 'Entrar'}</button>
  )
}

export default function LoginForm() {
  return (
    <form action={makeLoginAttempt} className="flex flex-col w-full">
      <Input fieldName="E-Mail" type="email" placeholder="Insira o email" className="mb-2"/>
      <Input fieldName="Senha" type="password" placeholder="Insira sua senha" className="mb-2"/>
      <div className="my-4">
        <LoginButton />
        <Link className="block button ghost w-full rounded-full" to="/forgot-password">Esqueci minha senha</Link>
      </div>
    </form>
  )
}
