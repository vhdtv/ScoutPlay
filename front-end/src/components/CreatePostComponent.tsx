import API from "#/api/API";
import { useNavigate } from "@tanstack/react-router";
import { useState, type ChangeEvent, type ComponentProps } from "react";
import InputField from "./ui/InputField";
import TextField from "./ui/TextField";

const api = new API;
export default function({...props}: {} & ComponentProps<"div">) {
  const navigate = useNavigate();

  const [previewMedia, setPreviewMedia] = useState("");
  const criarPost = async (form: FormData) => {
    const titulo = form.get("titulo")?.toString();
    if(!titulo) return;
    const arquivo = form.get("media") as File;
    if(!arquivo) return;
    let descricao = form.get("descricao")?.toString() ?? "";

    const post = await api.criarPost({arquivo, titulo, descricao});
    if(!post) return;
    navigate({to: "/post/$post_id", params: {post_id: post.url}})

  }

  const createURLFromBlob = ({target}: ChangeEvent<HTMLInputElement>) => {
    if(!target.files) return;
    const [ file ] = target.files;
    setPreviewMedia(URL.createObjectURL(file));
  }

  const focusOnFileInput = () => (document.querySelector("input[name=media]") as HTMLInputElement)!.click()
  
  return (
    <form action={criarPost} className={`card p-6 flex flex-col gap-4 ${props.className}`}>
      <p className="font-semibold text-xl/6 text-mist-800">Criar Post</p>
      <div className="flex gap-4">
        <button style={{backgroundImage: `url(${previewMedia})`}} onClick={focusOnFileInput} className="w-50 bg-center bg-cover bg-no-repeat rounded-xl border border-mist-200 bg-mist-100 text-mist-400 p-2 h-full aspect-square flex items-center justify-center text-2xl cursor-pointer transition outline-none hover:bg-mist-200 hover:border-mist-400 hover:text-mist-500 focus:bg-mist-200 focus:border-mist-400 focus:text-mist-500">
          {previewMedia == "" && <i className="ri-upload-2-line"></i>}
          <input type="file" className='hidden' onChange={createURLFromBlob} name="media" />
        </button>
        <div className="flex flex-col gap-2 grow">
          <InputField fieldName="titulo" placeholder="Título da publicação" className="w-full"/>
          <TextField className="h-full" fieldName="descricao" placeholder="Descrição"/>
          <button className='grow text-white bg-indigo-600 hover:bg-indigo-800 transition focus:bg-indigo-800 outline-none rounded-full py-2 px-6 font-semibold text-sm self-end cursor-pointer'> Criar Post </button>
        </div>
      </div>
    </form>
  )
}