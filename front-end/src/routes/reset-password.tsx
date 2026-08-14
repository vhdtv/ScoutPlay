import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import API from "#/api/API";

const api = new API();

export const Route = createFileRoute("/reset-password")({
	validateSearch: (search: Record<string, unknown>) => ({
		token: typeof search.token === "string" ? search.token : "",
	}),
	component: ResetPasswordPage,
});

function ResetPasswordPage() {
	const { token } = Route.useSearch();
	const [senha, setSenha] = useState("");
	const [confirmacao, setConfirmacao] = useState("");
	const [erro, setErro] = useState("");
	const [concluido, setConcluido] = useState(false);
	const [carregando, setCarregando] = useState(false);

	const enviar = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		setErro("");
		if (!token) return setErro("Link de recuperação inválido.");
		if (senha.length < 8)
			return setErro("A senha deve ter pelo menos 8 caracteres.");
		if (senha !== confirmacao) return setErro("As senhas não coincidem.");

		setCarregando(true);
		try {
			await api.redefinirSenha(token, senha);
			setConcluido(true);
		} catch (error) {
			setErro(
				error instanceof Error ? error.message : "Link inválido ou expirado.",
			);
		} finally {
			setCarregando(false);
		}
	};

	return (
		<main className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
			<section className="w-full max-w-md bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
				<h1 className="text-2xl font-extrabold text-sky-950 mb-2">
					Criar nova senha
				</h1>
				{concluido ? (
					<div className="space-y-4">
						<p className="text-green-700">Senha redefinida com sucesso.</p>
						<Link
							to="/login"
							className="button primary rounded-full block text-center"
						>
							Entrar
						</Link>
					</div>
				) : (
					<form onSubmit={enviar} className="space-y-4">
						<label className="block text-sm font-medium text-slate-700">
							Nova senha
							<input
								className="mt-1 w-full rounded-lg border border-slate-300 p-3"
								type="password"
								value={senha}
								onChange={(event) => setSenha(event.target.value)}
								autoComplete="new-password"
							/>
						</label>
						<label className="block text-sm font-medium text-slate-700">
							Confirmar senha
							<input
								className="mt-1 w-full rounded-lg border border-slate-300 p-3"
								type="password"
								value={confirmacao}
								onChange={(event) => setConfirmacao(event.target.value)}
								autoComplete="new-password"
							/>
						</label>
						{erro && <p className="text-sm text-red-600">{erro}</p>}
						<button
							className="button primary rounded-full w-full"
							type="submit"
							disabled={carregando}
						>
							{carregando ? "Salvando..." : "Redefinir senha"}
						</button>
					</form>
				)}
			</section>
		</main>
	);
}
