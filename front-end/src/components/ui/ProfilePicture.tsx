import type { UserSummaryDTO } from "#/api/tipos";

export default function({user, ...props}: {user: UserSummaryDTO, className?: string}) {
    return (
        <div className={`flex justify-center items-center h-full aspect-square overflow-hidden rounded-full border border-red-100 bg-slate-50 ${props.className}`}>
            {
                user.fotoPerfil
                ? <img src={user.fotoPerfil} className='w-full h-full' alt={`Foto de Perfil do usuario ${user.nome}`} />
                : <span className='w-full h-full flex items-center justify-center text-4xl font-bold bg-slate-500 text-slate-50'>{user.iniciais}</span>
            }
        </div>
    )
}