import { Link } from "@tanstack/react-router";

export default function ProfilePostList({posts}: {posts: IPostList[]}) {
    // posts = new Array(11).fill("");
    return (
        <div className="grid grid-cols-2 md:grid-cols-3 gap-1">
            {
                posts.map((post, index) => {
                    return <Link to="/post/$post_id" params={{post_id: post.id}} key={index} className="bg-slate-300 h-80 relative">
                        <div className="absolute top-0 start-0 end-0 bottom-0 opacity-0 hover:opacity-80 transition-all overflow-hidden">
                            <div className="absolute top-0 start-0 end-0 bottom-0 bg-linear-to-b from-slate-0 to-slate-950"></div>
                            <div className="text-white absolute top-0 left-0 w-full h-full flex items-center justify-center">
                                <span className="material-symbols-outlined" style={{fontSize: "3.2rem"}}> play_circle </span>
                            </div>
                            <div className="text-white absolute top-0 left-0 w-full h-full p-5 flex items-end justify-start">
                                <h4 className="text-xl text-white">Título aqui</h4>
                            </div>
                        </div>
                        <video src={post.url} preload="lazy" muted loop className="w-full h-full object-cover object-center"></video>
                    </Link>
                })
            }
        </div>
    )
}

type IPostList = {
    url: string;
    id: string;
}