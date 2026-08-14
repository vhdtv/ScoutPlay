import type { UserSummaryDTO } from "#/api/tipos";
import { BACKEND_ENDPOINT } from "#/components/variables";

function resolveAvatarUrl(fotoPerfil: string): string {
	if (fotoPerfil.startsWith("http")) return fotoPerfil;
	return `${BACKEND_ENDPOINT}/api/avatar/${encodeURIComponent(fotoPerfil)}`;
}

export default function ProfilePicture({
	user,
	...props
}: {
	user:
		| UserSummaryDTO
		| { nome?: string; iniciais?: string; fotoPerfil?: string | null };
	className?: string;
}) {
	const hasHeight =
		props.className?.includes(" h-") || props.className?.startsWith("h-");
	const sizeClass = `${props.className ?? ""} ${hasHeight ? "" : "h-full"}`;
	return (
		<div
			className={`flex justify-center items-center aspect-square overflow-hidden rounded-full border border-slate-200 bg-slate-100 ${sizeClass}`}
		>
			{(user as any).fotoPerfil ? (
				<img
					src={resolveAvatarUrl((user as any).fotoPerfil)}
					className="w-full h-full object-cover"
					alt={`Foto de ${(user as any).nome ?? ""}`}
				/>
			) : (
				<span className="w-full h-full flex items-center justify-center text-2xl font-bold bg-slate-500 text-slate-50">
					{(user as any).iniciais ?? "?"}
				</span>
			)}
		</div>
	);
}
