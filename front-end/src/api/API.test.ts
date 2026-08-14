// @vitest-environment jsdom

import { beforeEach, describe, expect, it, vi } from "vitest";
import API from "./API";

describe("API de autenticação", () => {
	beforeEach(() => {
		localStorage.clear();
		vi.restoreAllMocks();
	});

	it("salva somente o resumo da sessão após login", async () => {
		vi.stubGlobal(
			"fetch",
			vi.fn().mockResolvedValue(
				new Response(
					JSON.stringify({
						data: {
							nome: "Ana",
							sobrenome: "Silva",
							username: "ana_silva",
							iniciais: "AS",
							fotoPerfil: null,
							tipoConta: ["ATLETA"],
						},
					}),
					{ status: 200, headers: { "Content-Type": "application/json" } },
				),
			),
		);

		const api = new API();
		await api.fazerLogin("ana@example.com", "Senha123!");

		expect(api.obterDadosUsuarioNoNavegador()).toBe("ana_silva");
		expect(api.obterTipoContaUsuario()).toEqual(["ATLETA"]);
		expect(localStorage.getItem("access_token")).toBeNull();
	});

	it("limpa o cache local quando o servidor rejeita a sessão", async () => {
		localStorage.setItem("__usuario", "sessao_antiga");
		localStorage.setItem("__tipoConta", JSON.stringify(["OLHEIRO"]));
		vi.stubGlobal(
			"fetch",
			vi.fn().mockResolvedValue(new Response(null, { status: 401 })),
		);

		const api = new API();
		expect(await api.validarSessao()).toBe(false);
		expect(api.obterDadosUsuarioNoNavegador()).toBeNull();
		expect(api.obterTipoContaUsuario()).toEqual([]);
	});
});
