import Input from "#/components/ui/Input"

export default function LoginForm() {
  return (
    <form action="" className="bg-stone-100 text-sky-800 p-4 rounded-xl flex flex-col">
      <Input />
      <button className="p-2 rounded bg-sky-700 text-sky-100 font-semibold px-4" type="submit">Logar</button>
    </form>
  )
}
