import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import API from "#/api/API";
import Footer from "#/components/Footer";
import Input from "#/components/ui/Input";

const api = new API();

export const Route = createFileRoute("/forgot-password")({
	component: RouteComponent,
});

function RouteComponent() {
	const [status, setStatus] = useState<
		"idle" | "loading" | "success" | "error"
	>("idle");
	const [errorMsg, setErrorMsg] = useState("");

	const sendRecoveryEmail = async (form: FormData) => {
		const email = form.get("E-Mail")?.toString().trim();
		if (!email) return;
		setStatus("loading");
		setErrorMsg("");
		try {
			await api.mandarEmailParaRecuperarSenha(email);
			setStatus("success");
		} catch {
			setStatus("error");
			setErrorMsg("Não foi possível enviar o e-mail. Tente novamente.");
		}
	};

	return (
		<div className="page-noscroll flex flex-col">
			<section className="content grow-1 min-h-0 flex flex-row">
				{/* Left — banner (igual ao login) */}
				<div className="grow-1 min-h-0 hidden md:flex flex-col border-e-2 border-gray-200 p-6 pt-10 gap-4 overflow-hidden">
					<section className="logo shrink-0">
						<div className="flex">
							<img src="/assets/logo.svg" className="h-16 m-2" />
							<div className="grow-1 items-center content-center">
								<img src="/assets/text-logo.png" className="h-10" />
								<h2 className="font-medium">
									O meio-de-campo entre atletas e olheiros
								</h2>
							</div>
						</div>
					</section>
					<section className="min-h-0 flex-1 overflow-hidden rounded-2xl mb-4">
						<img
							src="/assets/images/atleta-banner-1.webp"
							alt="Atleta em ação"
							className="w-full h-full object-cover object-top"
						/>
					</section>
				</div>

				{/* Right — form */}
				<div className="shrink-0 sm:grow-0 flex flex-col flex-wrap items-center justify-center">
					<div className="border-b-2 border-gray-200 p-3 min-w-xl grow-1 flex flex-col pt-12">
						<h3 className="text-sky-950 font-extrabold text-2xl mb-2">
							Recuperar a senha
						</h3>
						<p className="text-slate-500 text-sm mb-5">
							Informe seu e-mail cadastrado e enviaremos um link temporário de
							uso único.
						</p>

						{status === "success" ? (
							<div className="flex flex-col items-center gap-4 py-8 text-center">
								<span
									className="material-symbols-outlined text-green-500"
									style={{ fontSize: "3rem" }}
								>
									mark_email_read
								</span>
								<p className="text-green-700 font-semibold text-lg">
									E-mail enviado!
								</p>
								<p className="text-slate-500 text-sm">
									Se o endereço estiver cadastrado, você receberá um link para
									criar uma nova senha.
								</p>
								<Link
									to="/login"
									className="mt-4 button primary rounded-full px-6"
								>
									Ir para o login
								</Link>
							</div>
						) : (
							<form
								action={sendRecoveryEmail}
								className="grow-1 flex flex-col gap-4"
							>
								<Input fieldName="E-Mail" type="email" />
								{status === "error" && (
									<p className="text-red-600 text-sm">{errorMsg}</p>
								)}
								<button
									className="button primary rounded-full px-4 w-full mb-3 disabled:opacity-60"
									type="submit"
									disabled={status === "loading"}
								>
									{status === "loading" ? "Enviando..." : "Recuperar Senha"}
								</button>
							</form>
						)}
					</div>
					<div className="text-center py-12 px-3 w-full">
						<h4 className="text-xl font-bold text-sky-900 mb-1">
							Lembrou da sua senha?
						</h4>
						<Link
							className="text-underline text-sky-700 underline underline-offset-8"
							to="/login"
						>
							Voltar a página de login
						</Link>
					</div>
				</div>
			</section>
			<Footer />
		</div>
	);
}
