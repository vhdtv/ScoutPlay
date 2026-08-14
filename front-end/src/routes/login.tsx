import { createFileRoute, Link } from "@tanstack/react-router";
import Footer from "#/components/Footer";
import LoginForm from "#/components/LoginForm";

export const Route = createFileRoute("/login")({
	component: RouteComponent,
});

export function RouteComponent() {
	return (
		<div className="page-noscroll flex flex-col">
			<section className="content grow-1 min-h-0 flex flex-row">
				<div className="grow-1 min-h-0 hidden md:flex flex-col border-e-2 border-gray-200 p-6 pt-10 gap-4 overflow-hidden">
					<section className="logo shrink-0">
						<div className="flex">
							<img src="/assets/icon.svg" className="h-20 m-2" />
							<div className="grow-1 items-center content-center">
								<h1 className="text-5xl/9 font-semibold text-slate-800 scoutplay-font">
									ScoutPlay
								</h1>
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
				<div className="shrink-0 sm:grow-0 flex flex-col flex-wrap items-center justify-center">
					<div className="border-b-2 border-gray-200 p-3 min-w-xl">
						<h3 className="text-sky-950 font-extrabold text-2xl mb-3">
							Entre na sua conta
						</h3>
						<LoginForm />
					</div>
					<div className="text-center py-12 px-3 w-full">
						<h4 className="text-2xl font-bold text-sky-900">
							É novo por aqui?
						</h4>
						<p className="mb-6 text-xl text-sky-800">
							Crie uma conta especializada
						</p>
						<div className="flex w-full overflow-hidden border-1 border-sky-700 rounded-full">
							<Link
								className="button primary-outlined grow-1"
								to="/signup"
								search={{ type: "atleta" }}
							>
								Sou atleta
							</Link>
							<div className="h-auto flex self-stretch w-[1px] bg-sky-700"></div>
							<Link
								className="button primary-outlined grow-1"
								to="/signup"
								search={{ type: "olheiro" }}
							>
								Sou olheiro
							</Link>
							<div className="h-auto flex self-stretch w-[1px] bg-sky-700"></div>
							<Link
								className="button primary-outlined grow-1"
								to="/signup"
								search={{ type: "responsavel" }}
							>
								Sou responsável
							</Link>
						</div>
					</div>
				</div>
			</section>
			<Footer />
		</div>
	);
}
