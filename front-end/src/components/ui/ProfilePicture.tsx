import API from "#/api/API";
import type { UserSummaryDTO } from "#/api/tipos";
const api = new API

export default function({user, ...props}: {user: UserSummaryDTO, className?: string}) {
    const classResolve = () => {
        return `${props.className} ${props.className?.indexOf(" h-") != -1 ? "" : "h-full"}`
    }
    return (
        <div className={`flex justify-center items-center aspect-square overflow-hidden rounded-full border border-mist-400 bg-slate-50 ${classResolve()}`}>
            {
                user.fotoPerfil
                ? <img src={api.obterMidia(user.fotoPerfil)} className='w-full h-full' alt={`Foto de Perfil do usuario ${user.nome}`} />
                : <span className='w-full h-full flex items-center justify-center font-bold bg-mist-300 text-mist-600'>{user.iniciais}</span>
            }
        </div>
    )
}