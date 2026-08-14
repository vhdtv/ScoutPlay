import type { InputHTMLAttributes } from "react";

export default function InputToggleButton({
	fieldName,
	hideLabel,
	...props
}: IExtendedProps) {
	return (
		<input
			type="checkbox"
			checked={props.checked}
			onChange={props.onChange}
			placeholder={props.placeholder}
			className={`input-toggle ${props.className}`}
			name={fieldName}
		/>
	);
}

interface IExtendedProps extends InputHTMLAttributes<HTMLInputElement> {
	fieldName: string;
	hideLabel?: boolean;
}
