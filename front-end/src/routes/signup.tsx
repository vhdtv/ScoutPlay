import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import API from "#/api/API";
import Footer from "#/components/Footer";

const api = new API();

enum STEPS {
	ABOUT_YOU,
	PROFISSIONAL_HISTORY,
	ACCESS_FORM,
}

const POSICOES = [
	"Goleiro",
	"Zagueiro Central",
	"Zagueiro Direito",
	"Zagueiro Esquerdo",
	"Lateral Direito",
	"Lateral Esquerdo",
	"Volante Direito",
	"Volante Esquerdo",
	"Meio-campista Central",
	"Meio-campista Direito",
	"Meio-campista Esquerdo",
	"Segundo Atacante",
	"Ponta Direita",
	"Ponta Esquerda",
	"Centroavante",
];

export const Route = createFileRoute("/signup")({
	component: RouteComponent,
	validateSearch: (search: Record<string, unknown>) => ({
		type: (search.type as string) ?? "atleta",
	}),
});

// ── Helpers de formatação/validação ─────────────────────────────────────────

function formatCPF(v: string) {
	const d = v.replace(/\D/g, "").slice(0, 11);
	if (d.length <= 3) return d;
	if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`;
	if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`;
	return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
}

function formatTelefone(v: string) {
	const d = v.replace(/\D/g, "").slice(0, 11);
	if (d.length <= 2) return d.length ? `(${d}` : "";
	if (d.length <= 6) return `(${d.slice(0, 2)}) ${d.slice(2)}`;
	if (d.length <= 10)
		return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
	return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
}

function formatCEP(v: string) {
	const d = v.replace(/\D/g, "").slice(0, 8);
	if (d.length <= 5) return d;
	return `${d.slice(0, 5)}-${d.slice(5)}`;
}

function onlyDigits(v: string) {
	return v.replace(/\D/g, "");
}

// ── Componente de campo com label padronizado ────────────────────────────────

function Field({
	label,
	name,
	type = "text",
	placeholder,
	value,
	onChange,
	required,
	maxLength,
	error,
	children,
}: {
	label: string;
	name?: string;
	type?: string;
	placeholder?: string;
	value?: string;
	onChange?: (v: string) => void;
	required?: boolean;
	maxLength?: number;
	error?: string;
	children?: React.ReactNode;
}) {
	return (
		<label className="w-full flex flex-col gap-0.5">
			<p className="uppercase font-extrabold text-sm text-indigo-900">
				{label}
				{required && <span className="text-red-500 ml-0.5">*</span>}
			</p>
			{children ?? (
				<input
					type={type}
					name={name ?? label}
					placeholder={placeholder}
					value={value}
					onChange={(e) => onChange?.(e.target.value)}
					maxLength={maxLength}
					className={`py-3 px-5 border rounded-md placeholder-gray-300 focus:outline-none focus:border-slate-400 focus:bg-slate-50 text-base w-full ${error ? "border-red-400 bg-red-50" : "border-gray-200"}`}
				/>
			)}
			{error && <p className="text-red-500 text-xs mt-0.5">{error}</p>}
		</label>
	);
}

function SelectField({
	label,
	name,
	value,
	onChange,
	required,
	error,
	children,
}: {
	label: string;
	name: string;
	value: string;
	onChange: (v: string) => void;
	required?: boolean;
	error?: string;
	children: React.ReactNode;
}) {
	return (
		<label className="w-full flex flex-col gap-0.5">
			<p className="uppercase font-extrabold text-sm text-indigo-900">
				{label}
				{required && <span className="text-red-500 ml-0.5">*</span>}
			</p>
			<select
				name={name}
				value={value}
				onChange={(e) => onChange(e.target.value)}
				className={`py-3 px-5 border rounded-md text-base w-full bg-white focus:outline-none focus:border-slate-400 ${error ? "border-red-400 bg-red-50" : "border-gray-200"}`}
			>
				{children}
			</select>
			{error && <p className="text-red-500 text-xs mt-0.5">{error}</p>}
		</label>
	);
}

// ── Componente principal ──────────────────────────────────────────────────────

function RouteComponent() {
	const { type } = Route.useSearch();
	const isOlheiro = type === "olheiro";
	const isResponsavel = type === "responsavel";
	const tipoConta = isOlheiro
		? "OLHEIRO"
		: isResponsavel
			? "RESPONSAVEL"
			: "ATLETA";

	const SECTIONS = [
		"Sobre Você",
		isOlheiro
			? "Dados Profissionais"
			: isResponsavel
				? "Responsável"
				: "Dados Esportivos",
		"Acesso",
	];
	const [currentSection, setCurrentSection] = useState(STEPS.ABOUT_YOU);
	const [erro, setErro] = useState("");
	const navigate = useNavigate();

	// ── Step 1: dados pessoais ────────────────────────────────────────────────
	const [nome, setNome] = useState("");
	const [dataNascimento, setDataNascimento] = useState("");
	const [cpf, setCpf] = useState("");
	const [telefone, setTelefone] = useState("");
	const [cep, setCep] = useState("");
	const [estado, setEstado] = useState("");
	const [cidade, setCidade] = useState("");
	const [errStep1, setErrStep1] = useState<Record<string, string>>({});

	// ── Step 2: dados profissionais/esportivos ────────────────────────────────
	const [posicao, setPosicao] = useState("");
	const [peDominante, setPeDominante] = useState("");
	const [peso, setPeso] = useState("");
	const [altura, setAltura] = useState("");
	const [clubesAnteriores, setClubesAnteriores] = useState("");
	// olheiro
	const [empresaOuClube, setEmpresaOuClube] = useState("");
	const [regiaoAtuacao, setRegiaoAtuacao] = useState("");
	const [anosExperiencia, setAnosExperiencia] = useState("");
	const [errStep2, setErrStep2] = useState<Record<string, string>>({});

	// ── Step 3: acesso ────────────────────────────────────────────────────────
	const [email, setEmail] = useState("");
	const [senha, setSenha] = useState("");
	const [confirmarSenha, setConfirmarSenha] = useState("");

	// ── Validação step 1 ─────────────────────────────────────────────────────
	const validarStep1 = () => {
		const errs: Record<string, string> = {};
		if (!nome.trim()) errs.nome = "Nome obrigatório.";
		else if (nome.trim().split(" ").length < 2)
			errs.nome = "Informe nome e sobrenome.";
		if (!dataNascimento)
			errs.dataNascimento = "Data de nascimento obrigatória.";
		if (!cpf) errs.cpf = "CPF obrigatório.";
		else if (onlyDigits(cpf).length !== 11)
			errs.cpf = "CPF deve ter 11 dígitos.";
		if (!telefone) errs.telefone = "Telefone obrigatório.";
		else if (onlyDigits(telefone).length < 10)
			errs.telefone = "Telefone inválido.";
		if (cep && onlyDigits(cep).length !== 8)
			errs.cep = "CEP deve ter 8 dígitos.";
		if (!estado) errs.estado = "Estado obrigatório.";
		if (!cidade.trim()) errs.cidade = "Cidade obrigatória.";
		setErrStep1(errs);
		return Object.keys(errs).length === 0;
	};

	// ── Validação step 2 (olheiro) ────────────────────────────────────────────
	const validarStep2Olheiro = () => {
		const errs: Record<string, string> = {};
		if (!empresaOuClube.trim()) errs.empresaOuClube = "Campo obrigatório.";
		if (!regiaoAtuacao.trim()) errs.regiaoAtuacao = "Campo obrigatório.";
		setErrStep2(errs);
		return Object.keys(errs).length === 0;
	};

	const avancarStep1 = (e: React.FormEvent) => {
		e.preventDefault();
		if (validarStep1())
			setCurrentSection(
				isResponsavel ? STEPS.ACCESS_FORM : STEPS.PROFISSIONAL_HISTORY,
			);
	};

	const avancarStep2 = (e: React.FormEvent, pular = false) => {
		e.preventDefault();
		if (pular || !isOlheiro) {
			setCurrentSection(STEPS.ACCESS_FORM);
			return;
		}
		if (validarStep2Olheiro()) setCurrentSection(STEPS.ACCESS_FORM);
	};

	const finalizarCadastro = async (e: React.FormEvent) => {
		e.preventDefault();
		setErro("");
		if (!email.trim()) {
			setErro("E-mail obrigatório.");
			return;
		}
		if (senha.length < 8) {
			setErro("Senha deve ter no mínimo 8 caracteres.");
			return;
		}
		if (senha !== confirmarSenha) {
			setErro("As senhas não coincidem.");
			return;
		}

		const partes = nome.trim().split(" ");
		try {
			await api.cadastrar({
				nome: partes[0],
				sobrenome: partes.slice(1).join(" "),
				email,
				senha,
				dataNascimento,
				tipoConta,
				cpf: onlyDigits(cpf) || undefined,
				telefone: onlyDigits(telefone) || undefined,
				cep: onlyDigits(cep) || undefined,
				posicao: isResponsavel ? undefined : posicao || undefined,
				peDominante: isResponsavel ? undefined : peDominante || undefined,
				peso: isResponsavel ? undefined : peso || undefined,
				altura: isResponsavel ? undefined : altura || undefined,
				clubesAnteriores: isResponsavel
					? undefined
					: isOlheiro
						? empresaOuClube
							? `${empresaOuClube} | Região: ${regiaoAtuacao}`
							: undefined
						: clubesAnteriores || undefined,
			});
			navigate({ to: "/login" });
		} catch (ex: any) {
			setErro(ex.message ?? "Erro ao criar conta.");
		}
	};

	const inputClass =
		"py-3 px-5 border border-gray-200 rounded-md placeholder-gray-300 focus:outline-none focus:border-slate-400 focus:bg-slate-50 text-base w-full";
	const selectClass =
		"py-3 px-5 border border-gray-200 rounded-md text-base w-full bg-white focus:outline-none focus:border-slate-400";

	const renderSwitch = () => {
		switch (currentSection) {
			default:
				return (
					<form
						onSubmit={avancarStep1}
						className="w-full md:min-w-[670px]"
						noValidate
					>
						<section className="shadow-lg rounded-lg mb-4 p-6 flex flex-col gap-4">
							<div className="bg-sky-50 border border-sky-200 rounded-lg px-4 py-2 text-sm text-sky-800 font-semibold">
								Cadastro de{" "}
								<span className="uppercase">
									{isOlheiro
										? "Olheiro"
										: isResponsavel
											? "Responsável"
											: "Atleta"}
								</span>
							</div>

							<Field label="Seu Nome" required error={errStep1.nome}>
								<input
									type="text"
									name="nome"
									placeholder="Nome e Sobrenome"
									value={nome}
									onChange={(e) => setNome(e.target.value)}
									className={`${inputClass} ${errStep1.nome ? "border-red-400 bg-red-50" : ""}`}
								/>
							</Field>

							<Field
								label="Data de Nascimento"
								required
								error={errStep1.dataNascimento}
							>
								<input
									type="date"
									name="dataNascimento"
									value={dataNascimento}
									onChange={(e) => setDataNascimento(e.target.value)}
									className={`${inputClass} ${errStep1.dataNascimento ? "border-red-400 bg-red-50" : ""}`}
								/>
							</Field>

							<div className="grid grid-cols-2 gap-3">
								<Field label="CPF" required error={errStep1.cpf}>
									<input
										type="text"
										name="cpf"
										placeholder="000.000.000-00"
										value={cpf}
										onChange={(e) => setCpf(formatCPF(e.target.value))}
										className={`${inputClass} ${errStep1.cpf ? "border-red-400 bg-red-50" : ""}`}
									/>
								</Field>
								<Field label="Telefone" required error={errStep1.telefone}>
									<input
										type="text"
										name="telefone"
										placeholder="(11) 99999-9999"
										value={telefone}
										onChange={(e) =>
											setTelefone(formatTelefone(e.target.value))
										}
										className={`${inputClass} ${errStep1.telefone ? "border-red-400 bg-red-50" : ""}`}
									/>
								</Field>
							</div>

							<div className="grid grid-cols-2 gap-3">
								<Field label="CEP" error={errStep1.cep}>
									<input
										type="text"
										name="cep"
										placeholder="00000-000"
										value={cep}
										onChange={(e) => setCep(formatCEP(e.target.value))}
										className={`${inputClass} ${errStep1.cep ? "border-red-400 bg-red-50" : ""}`}
									/>
								</Field>
								<div />
							</div>

							<div className="grid grid-cols-2 gap-3">
								<Field label="Estado" required error={errStep1.estado}>
									<select
										name="estado"
										value={estado}
										onChange={(e) => setEstado(e.target.value)}
										className={`${selectClass} ${errStep1.estado ? "border-red-400 bg-red-50" : ""}`}
									>
										<option value="">Selecione</option>
										{[
											"AC",
											"AL",
											"AP",
											"AM",
											"BA",
											"CE",
											"DF",
											"ES",
											"GO",
											"MA",
											"MT",
											"MS",
											"MG",
											"PA",
											"PB",
											"PR",
											"PE",
											"PI",
											"RJ",
											"RN",
											"RS",
											"RO",
											"RR",
											"SC",
											"SP",
											"SE",
											"TO",
										].map((uf) => (
											<option key={uf} value={uf}>
												{uf}
											</option>
										))}
									</select>
								</Field>
								<Field label="Cidade" required error={errStep1.cidade}>
									<input
										type="text"
										name="cidade"
										value={cidade}
										onChange={(e) => setCidade(e.target.value)}
										className={`${inputClass} ${errStep1.cidade ? "border-red-400 bg-red-50" : ""}`}
									/>
								</Field>
							</div>
						</section>
						<div className="flex justify-end">
							<button
								type="submit"
								className="button primary rounded-full px-12"
							>
								Próximo
							</button>
						</div>
					</form>
				);

			// ── STEP 2: Dados esportivos / profissionais ──────────────────────────
			case STEPS.PROFISSIONAL_HISTORY:
				return (
					<form
						onSubmit={(e) => avancarStep2(e, false)}
						className="w-full md:min-w-[670px]"
						noValidate
					>
						<section className="shadow-lg rounded-lg mb-4 p-6 flex flex-col gap-4">
							{isOlheiro ? (
								<>
									<p className="text-slate-500 text-sm">
										Dados sobre sua atuação como olheiro.
									</p>

									<Field
										label="Clube ou Empresa que Representa"
										required
										error={errStep2.empresaOuClube}
									>
										<input
											type="text"
											placeholder="Ex: Clube Atlético Mineiro"
											value={empresaOuClube}
											onChange={(e) => setEmpresaOuClube(e.target.value)}
											className={`${inputClass} ${errStep2.empresaOuClube ? "border-red-400 bg-red-50" : ""}`}
										/>
									</Field>

									<Field
										label="Região de Atuação"
										required
										error={errStep2.regiaoAtuacao}
									>
										<input
											type="text"
											placeholder="Ex: São Paulo, Sudeste"
											value={regiaoAtuacao}
											onChange={(e) => setRegiaoAtuacao(e.target.value)}
											className={`${inputClass} ${errStep2.regiaoAtuacao ? "border-red-400 bg-red-50" : ""}`}
										/>
									</Field>

									<Field label="Anos de Experiência">
										<input
											type="number"
											placeholder="Ex: 5"
											min={0}
											max={50}
											value={anosExperiencia}
											onChange={(e) => setAnosExperiencia(e.target.value)}
											className={inputClass}
										/>
									</Field>
								</>
							) : (
								<>
									<p className="text-slate-500 text-sm">
										Esta seção é opcional. Você pode preencher depois nas
										configurações.
									</p>

									<SelectField
										label="Posição que Joga"
										name="posicao"
										value={posicao}
										onChange={setPosicao}
										error={errStep2.posicao}
									>
										<option value="">Selecione uma posição</option>
										{POSICOES.map((p) => (
											<option key={p} value={p}>
												{p}
											</option>
										))}
									</SelectField>

									<SelectField
										label="Pé Dominante"
										name="peDominante"
										value={peDominante}
										onChange={setPeDominante}
									>
										<option value="">Selecione</option>
										<option value="DESTRO">Destro</option>
										<option value="CANHOTO">Canhoto</option>
										<option value="AMBIDESTRO">Ambidestro</option>
									</SelectField>

									<div className="grid grid-cols-2 gap-3">
										<Field label="Peso (kg)">
											<input
												type="number"
												placeholder="Ex: 75"
												value={peso}
												onChange={(e) => setPeso(e.target.value)}
												className={inputClass}
											/>
										</Field>
										<Field label="Altura (cm)">
											<input
												type="number"
												placeholder="Ex: 178"
												value={altura}
												onChange={(e) => setAltura(e.target.value)}
												className={inputClass}
											/>
										</Field>
									</div>

									<Field label="Clubes Anteriores">
										<input
											type="text"
											placeholder="Ex: São Paulo FC, Corinthians"
											value={clubesAnteriores}
											onChange={(e) => setClubesAnteriores(e.target.value)}
											className={inputClass}
										/>
									</Field>
								</>
							)}
						</section>
						<div className="flex justify-end items-center gap-4">
							{!isOlheiro && (
								<button
									type="button"
									onClick={(e) => avancarStep2(e as any, true)}
									className="button primary-outlined border rounded-full px-8"
								>
									Pular
								</button>
							)}
							<button
								type="submit"
								className="button primary rounded-full px-12"
							>
								Próximo
							</button>
						</div>
					</form>
				);

			// ── STEP 3: Acesso ────────────────────────────────────────────────────
			case STEPS.ACCESS_FORM:
				return (
					<form
						onSubmit={finalizarCadastro}
						className="w-full md:min-w-[670px]"
						noValidate
					>
						<section className="shadow-lg rounded-lg mb-4 p-6 flex flex-col gap-4">
							<Field label="E-Mail" required>
								<input
									type="email"
									name="email"
									placeholder="seu@email.com"
									value={email}
									onChange={(e) => setEmail(e.target.value)}
									className={inputClass}
								/>
							</Field>
							<Field label="Senha" required>
								<input
									type="password"
									name="senha"
									placeholder="Mínimo 8 caracteres"
									value={senha}
									onChange={(e) => setSenha(e.target.value)}
									className={inputClass}
								/>
							</Field>
							<Field label="Confirmar Senha" required>
								<input
									type="password"
									name="confirmarSenha"
									value={confirmarSenha}
									onChange={(e) => setConfirmarSenha(e.target.value)}
									className={inputClass}
								/>
							</Field>
							{erro && <p className="text-red-600 text-sm">{erro}</p>}
						</section>
						<div className="flex justify-end">
							<button
								type="submit"
								className="button primary rounded-full px-12"
							>
								Criar Conta
							</button>
						</div>
					</form>
				);
		}
	};

	return (
		<div className="page-noscroll flex flex-col">
			<div className="max-w-[1024px] mx-auto content grow-1 flex">
				<section className="flex flex-col p-4 justify-stretch w-full">
					<div className="mt-16">
						<Link
							className="text-sm primary-outlined inline-flex items-center gap-2 transition text-sky-600 hover:text-sky-900"
							to="/login"
						>
							<span className="material-symbols-outlined">arrow_back</span>
							<span>Voltar para Login</span>
						</Link>
						<h1 className="text-sky-950 font-extrabold text-2xl">
							Crie sua conta
						</h1>
						<p className="text-sky-950 font-extrabold mb-3">
							{`${SECTIONS[currentSection]} (${currentSection + 1}/${SECTIONS.length})`}
						</p>
					</div>
					{renderSwitch()}
				</section>
			</div>
			<Footer />
		</div>
	);
}
