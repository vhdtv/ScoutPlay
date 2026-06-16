import type { InputHTMLAttributes } from "react";

export default function({...props}: IInputField) {
  return (
    <div role='group' className={`flex flex-col gap-2 ${props.classNameContainer}`}>
      <label htmlFor={props.fieldName} className='ps-1 text-sm font-semibold text-mist-950'>{props.label}</label>
      <input type="date"  className={props.classNameInput || props.className} placeholder={props.placeholder} name={props.fieldName} defaultValue={props.defaultValue}/>
    </div>
  )
}

export interface IInputField extends InputHTMLAttributes<HTMLInputElement> {
  label?: string,
  fieldName: string,
  classNameContainer?: string,
  classNameInput?: string,
}