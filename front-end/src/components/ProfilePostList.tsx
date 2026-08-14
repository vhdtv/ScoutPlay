import { Link } from "@tanstack/react-router";
import { useState } from "react";
import API from "#/api/API";
import type { PostDTO } from "#/api/tipos";

const api = new API();

export default function ProfilePostList({
	posts,
	souEu = false,
	onDelete,
}: {
	posts: PostDTO[];
	souEu?: boolean;
	onDelete?: (url: string) => void;
}) {
	return (
		<div className="grid grid-cols-2 md:grid-cols-3 gap-1">
			{posts.map((post, index) => (
				<PostThumbnail
					key={post.url ?? index}
					post={post}
					souEu={souEu}
					onDelete={onDelete}
				/>
			))}
		</div>
	);
}

function PostThumbnail({
	post,
	souEu,
	onDelete,
}: {
	post: PostDTO;
	souEu: boolean;
	onDelete?: (url: string) => void;
}) {
	const [deleting, setDeleting] = useState(false);
	const isVideo = post.media?.mimeType?.startsWith("video/");
	const mediaSrc = api.obterMidia(post.media?.src);

	const handleDelete = async (e: React.MouseEvent) => {
		e.preventDefault();
		e.stopPropagation();
		if (deleting) return;
		setDeleting(true);
		const ok = await api.deletarPost(post.url);
		if (ok) onDelete?.(post.url);
		else setDeleting(false);
	};

	return (
		<Link
			to="/post/$post_id"
			params={{ post_id: post.url }}
			className="group bg-slate-300 h-64 relative overflow-hidden block"
		>
			{/* overlay on hover */}
			<div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-all z-10">
				<div className="absolute inset-0 bg-gradient-to-b from-transparent to-slate-950/80" />

				{/* centre icon */}
				<div className="absolute inset-0 flex items-center justify-center text-white">
					<span
						className="material-symbols-outlined"
						style={{ fontSize: "4rem" }}
					>
						{isVideo ? "play_circle" : "zoom_in"}
					</span>
				</div>

				{/* bottom bar */}
				<div className="absolute bottom-0 left-0 right-0 p-3 flex items-end justify-between text-white">
					<div>
						{post.interacoes?.quantidadeLike > 0 && (
							<div className="text-sm mb-1 flex gap-1 items-center">
								<span
									className="material-symbols-outlined"
									style={{ fontSize: "1.1rem" }}
								>
									thumb_up
								</span>
								<span>{post.interacoes.quantidadeLike}</span>
							</div>
						)}
						<h4 className="text-sm font-semibold truncate max-w-[140px]">
							{post.titulo}
						</h4>
					</div>

					{souEu && (
						<button
							onClick={handleDelete}
							disabled={deleting}
							title="Remover publicação"
							className="shrink-0 w-10 h-10 flex items-center justify-center rounded-full bg-red-600 hover:bg-red-700 disabled:opacity-50 transition cursor-pointer"
						>
							{deleting ? (
								<span
									className="material-symbols-outlined animate-spin"
									style={{ fontSize: "1.3rem" }}
								>
									progress_activity
								</span>
							) : (
								<span
									className="material-symbols-outlined"
									style={{ fontSize: "1.3rem" }}
								>
									delete
								</span>
							)}
						</button>
					)}
				</div>
			</div>

			{/* media */}
			{isVideo ? (
				<video
					preload="none"
					muted
					loop
					className="w-full h-full object-cover object-center"
				>
					<source type={post.media.mimeType} src={mediaSrc} />
				</video>
			) : (
				<img
					src={mediaSrc}
					alt={post.titulo}
					loading="lazy"
					className="w-full h-full object-cover object-center"
				/>
			)}
		</Link>
	);
}
