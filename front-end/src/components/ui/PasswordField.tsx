import { useState } from "react";
import type { IInputField } from "./InputField";

export default function ({...props}: IInputField & {hideIcon?: boolean}) {
  const [type, setType] = useState("password");
  const toggleVisibility = () => {
    type == "password" 
      ? setType("text")
      : setType("password");
  }
  return (
    
    <div role='group' className='flex flex-col gap-2'>
      <label htmlFor={props.fieldName} className='ps-1 text-sm font-semibold text-mist-950'>{props.label}</label>
      <div className='relative'>
        <input type={type} name={props.fieldName} className={`w-full ${props.className}`} placeholder={props.placeholder} defaultValue={props.defaultValue}/>
        { !props.hideIcon &&
          <button type='button' onClick={toggleVisibility} className={`p-2 flex items-center rounded-full outline-mist-300 justify-center cursor-pointer absolute end-0 top-0 m-1 text-mist-600 hover:text-mist-900 focus:text-mist-900`}>
            { type == "password"
              ? <i className="ri-eye-line"></i>
              : <i className="ri-eye-off-line"></i>
            }
          </button>
        }
      </div>
    </div>
  )
}
