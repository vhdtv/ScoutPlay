import type { PostDTO } from "#/api/tipos";
import { Link } from "@tanstack/react-router";

export default function ProfilePostList({posts}: {posts: PostDTO[]}) {
    return (
        <div className="grid grid-cols-2 md:grid-cols-3 gap-1">
            {
                posts.map((post, index) => {
                    return <Link to="/post/$post_id" params={{post_id: post.url}} key={index} className="bg-slate-300 h-80 relative">
                        <div className="absolute top-0 start-0 end-0 bottom-0 opacity-0 hover:opacity-80 transition-all overflow-hidden">
                            <div className="absolute top-0 start-0 end-0 bottom-0 bg-linear-to-b from-slate-0 to-slate-950"></div>
                            <div className="text-white absolute top-0 left-0 w-full h-full flex items-center justify-center">
                                <span className="material-symbols-outlined" style={{fontSize: "3.2rem"}}> play_circle </span>
                            </div>
                            <div className="text-white absolute top-0 left-0 w-full h-full leading-6 p-5 flex flex-col items-start justify-end">
                                { post.interacoes?.quantidadeLike && 
                                    <div className="text-xs p-2 border-1 rounded-full flex gap-1 items-center justify-center">
                                        <span style={{fontSize: "1rem"}} className="material-symbols-outlined"> thumb_up </span>
                                        <span>{post.interacoes.quantidadeLike}</span>
                                    </div>
                                }
                                <h4 className="text-xl text-white">{post.titulo}</h4>
                            </div>
                        </div>
                        <video preload="lazy" muted loop className="w-full h-full object-cover object-center">
                            <source type={post.media.tipo} src={post.media.src} />
                        </video>
                    </Link>
                })
            }
        </div>
    )
}