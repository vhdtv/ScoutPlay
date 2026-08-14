import { createFileRoute, Link, redirect } from "@tanstack/react-router";
import { useState } from "react";
import API from "#/api/API";
import type { AtletaCardDTO } from "#/api/tipos";
import Footer from "#/components/Footer";
import { LoggedHeader } from "#/components/Header";
import { BACKEND_ENDPOINT } from "#/components/variables";

const api = new API();

export const Route = createFileRoute("/minha-lista")({
	beforeLoad: async () => {
		const api = new API();
		if (!api.obterDadosUsuarioNoNavegador() || !(await api.validarSessao()))
			throw redirect({ to: "/login" });
	},
	component: MinhaListaPage,
	loader: async () => ({
		shortlist: await api.obterShortlist(),
	}),
});

function AtletaRow({
	atleta,
	onRemover,
}: {
	atleta: AtletaCardDTO;
	onRemover: (u: string) => void;
}) {
	return (
		<div className="flex items-center gap-4 p-3 rounded-xl bg-white border border-slate-100 shadow-sm hover:shadow transition">
			<div className="w-12 h-12 rounded-full overflow-hidden bg-slate-200 shrink-0 flex items-center justify-center">
				{atleta.fotoPerfil ? (
					<img
						src={`${BACKEND_ENDPOINT}/api/avatar/${encodeURIComponent(atleta.fotoPerfil)}`}
						className="w-full h-full object-cover"
						alt={atleta.nome}
					/>
				) : (
					<span className="text-sm font-bold text-slate-500">
						{atleta.iniciais}
					</span>
				)}
			</div>
			<div className="flex-1 min-w-0">
				<Link
					to="/user/$user"
					params={{ user: atleta.username }}
					className="text-sm font-bold text-slate-900 hover:text-sky-700 truncate block"
				>
					{atleta.nome} {atleta.sobrenome}
				</Link>
				<div className="flex items-center gap-2 mt-0.5 flex-wrap">
					<span className="text-xs text-slate-400">@{atleta.username}</span>
					{atleta.posicao && (
						<span className="text-xs px-2 py-0.5 rounded-full bg-sky-50 text-sky-700">
							{atleta.posicao}
						</span>
					)}
					{atleta.idade != null && (
						<span className="text-xs text-slate-500">{atleta.idade} anos</span>
					)}
					{atleta.peDominante && (
						<span className="text-xs text-slate-500">
							Pé {atleta.peDominante.toLowerCase()}
						</span>
					)}
				</div>
				{atleta.clubesAnteriores && (
					<p className="text-xs text-slate-400 truncate mt-0.5">
						Clubes: {atleta.clubesAnteriores}
					</p>
				)}
			</div>
			<div className="flex items-center gap-2 shrink-0">
				<Link
					to="/user/$user"
					params={{ user: atleta.username }}
					className="px-3 py-1.5 text-xs border border-sky-300 text-sky-700 rounded-full hover:bg-sky-50 transition font-medium"
				>
					Ver perfil
				</Link>
				<button
					onClick={() => onRemover(atleta.username)}
					title="Remover da lista"
					className="flex items-center gap-1 px-3 py-1.5 text-xs border border-red-200 text-red-500 rounded-full hover:bg-red-50 transition font-medium cursor-pointer"
				>
					<span
						className="material-symbols-outlined"
						style={{ fontSize: "0.9rem" }}
					>
						delete
					</span>
					Remover
				</button>
			</div>
		</div>
	);
}

function MinhaListaPage() {
	const { shortlist: initial } = Route.useLoaderData();
	const [lista, setLista] = useState<AtletaCardDTO[]>(initial);

	const remover = async (username: string) => {
		await api.removerDaShortlist(username);
		setLista((prev) => prev.filter((a) => a.username !== username));
	};

	return (
		<div className="min-h-screen flex flex-col bg-slate-50">
			<LoggedHeader />
			<div className="container mx-auto px-4 py-8 max-w-3xl flex-1">
				<div className="flex items-center justify-between mb-6">
					<h1 className="text-2xl font-bold text-slate-900">Minha Lista</h1>
					<span className="text-sm text-slate-400">
						{lista.length} atleta{lista.length !== 1 ? "s" : ""}
					</span>
				</div>

				{lista.length === 0 ? (
					<div className="flex flex-col items-center justify-center h-48 text-slate-400 gap-3">
						<span className="material-symbols-outlined text-5xl">
							bookmark_border
						</span>
						<p className="text-sm">Sua lista está vazia.</p>
						<Link
							to="/atletas"
							className="text-sm text-sky-600 hover:underline font-medium"
						>
							Buscar atletas
						</Link>
					</div>
				) : (
					<div className="flex flex-col gap-3">
						{lista.map((a) => (
							<AtletaRow key={a.username} atleta={a} onRemover={remover} />
						))}
					</div>
				)}
			</div>
			<Footer />
		</div>
	);
}
