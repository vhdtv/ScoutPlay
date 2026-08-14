import { createFileRoute, redirect } from "@tanstack/react-router";
import API from "#/api/API";

export const Route = createFileRoute("/")({
	beforeLoad: async () => {
		const api = new API();
		if (api.obterDadosUsuarioNoNavegador() && (await api.validarSessao())) {
			throw redirect({ to: "/feed" });
		}
		throw redirect({ to: "/login" });
	},
});
