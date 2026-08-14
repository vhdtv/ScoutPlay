import type { InputHTMLAttributes } from "react";

export default function InputStateField({
	fieldName,
	hideLabel,
	...props
}: IExtendedProps) {
	const states = [
		{ value: "ES" },
		{ value: "MG" },
		{ value: "RJ" },
		{ value: "SP" },
		{ value: "DF" },
		{ value: "GO" },
		{ value: "MS" },
		{ value: "MT" },
		{ value: "PR" },
		{ value: "RS" },
		{ value: "SC" },
		{ value: "AL" },
		{ value: "BA" },
		{ value: "CE" },
		{ value: "MA" },
		{ value: "PB" },
		{ value: "PE" },
		{ value: "PI" },
		{ value: "RN" },
		{ value: "SE" },
		{ value: "AC" },
		{ value: "AP" },
		{ value: "AM" },
		{ value: "PA" },
		{ value: "RO" },
		{ value: "RR" },
		{ value: "TO" },
	];
	return (
		<label className="w-auto">
			{fieldName && hideLabel !== true ? (
				<p className="uppercase font-extrabold mb-1 text-indigo-900 cursor-pointer">
					{fieldName || ""}
				</p>
			) : null}
			<select
				defaultValue={"DEFAULT"}
				className={`py-3 px-5 border-1 border-gray-200 focus:outline-none focus:border-slate-400 rounded-md placeholder-gray-300 focus:bg-slate-100 text-lg w-24 ${props.className}`}
				name={fieldName}
			>
				<option value="DEFAULT" disabled></option>
				{states.map(({ value }, index) => (
					<option key={index} value={value}>
						{value}
					</option>
				))}
			</select>
		</label>
	);
}

interface IExtendedProps extends InputHTMLAttributes<HTMLInputElement> {
	fieldName: string;
	hideLabel?: boolean;
}
