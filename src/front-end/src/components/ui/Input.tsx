import type { InputHTMLAttributes } from "react"

export default function Input({fieldName, hideLabel, ...props}: IExtendedProps) {
  return (
    <>
      <label className="w-full">
        {
          fieldName && (hideLabel != true) 
            ? <p className="uppercase font-extrabold mb-1 text-indigo-900 cursor-pointer">
                { fieldName || "" }
              </p>
            : null
        }
        <input type={props.type ?? "text"} placeholder={props.placeholder} className={`py-3 px-5 border-1 border-gray-200 focus:outline-none focus:border-slate-400 rounded-md placeholder-gray-300 focus:bg-slate-100 text-lg w-full ${props.className}`} name={fieldName} />
      </label>
    </>
  )
}

interface IExtendedProps extends InputHTMLAttributes<HTMLInputElement> {
  fieldName: string,
  hideLabel?: boolean
}