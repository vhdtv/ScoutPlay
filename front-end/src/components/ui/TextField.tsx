import type { InputHTMLAttributes } from "react";

export default function({...props}: IInputField) {
  return (
    <>
      { props.label
        ? <div role='group' className={`flex flex-col gap-2 ${props.classNameContainer}`}>
            <label htmlFor={props.fieldName} className='ps-1 text-sm font-semibold text-mist-950'>{props.label}</label>
            <textarea className={props.classNameInput || props.className} placeholder={props.placeholder} name={props.fieldName} defaultValue={props.defaultValue} rows={props.rows}></textarea>
          </div>
        : <textarea className={props.classNameInput || props.className} placeholder={props.placeholder} name={props.fieldName} defaultValue={props.defaultValue} rows={props.rows}></textarea>
      }
    </>
  )
}

export interface IInputField extends InputHTMLAttributes<HTMLTextAreaElement> {
  label?: string,
  fieldName: string,
  classNameContainer?: string,
  classNameInput?: string,
  rows?: number
}