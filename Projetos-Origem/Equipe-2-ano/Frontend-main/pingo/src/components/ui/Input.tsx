import React from "react";

// tamnhos
const sizeStyles: Record<"small" | "medium" | "large", string> = {
    small: "h-10 px-3",
    medium: "h-14 px-4",
    large: "h-20 px-5",
};
// props de Input
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    wrapperClassName?: string;
    sizing?: "small" | "medium" | "large";
}

export const Input = ({ wrapperClassName = "",  sizing = "medium", className = "", ...props }: InputProps) => (
    <div className={`relative ${wrapperClassName}`}>
        <input
            className={`w-full
             bg-white
              border-2 
             border-[var(--brand-midnight)] 
              rounded-2xl
              resize-none
              focus:outline-none 
              focus:ring-2 
              focus:ring-[var(--brand-midnight)]
              text-brand-midnight
              ${sizeStyles[sizing]} 
              ${className}
              `}
            {...props}
        />
    </div>
);
