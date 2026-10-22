import React from "react";

// props flat
interface ButtonFlatProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: "flat" | "cyanFlat" | "gradient";
    size?: "small" | "medium" | "large";
    rounded?: string;
}

// tamanhos pre definidos
const sizes = {
    small: "py-2 px-5 text-xl",
    medium: "py-3 px-8 text-3xl",
    large: "py-4 px-10 text-5xl",
};

// variações
const variants = {
    flat: "bg-[var(--brand-violet)] ",
    cyanFlat: "bg-[var(--brand-cyan)]",
    gradient: "bg-gradient-to-r from-[var(--brand-violet)] to-[var(--brand-magenta)]",
};

//definição de um button
export const ButtonFlat = ({ children,  variant = "flat", rounded = "rounded-xl", size = "medium", className = "", ...props }: ButtonFlatProps) => {
    // definição de variante e tamanho
    const variantClasses = variants[variant];
    const sizeClasses = sizes[size];

    // basico do botão
    const baseClasses =
        rounded +
        " border-[3px] " +
        "border-[var(--brand-midnight)] " +
        "text-white " +
        "text-5xl "+
        "font-extrabold " +
        "uppercase " +
        "cursor-pointer " +
        "transition-all duration-150 ease-in-out ";

    //escurece o botão
    const hoverActiveClasses = "hover:brightness-90 active:scale-[0.98] active:brightness-75";

    return (
        <button
            {...props}
            className={` ${variantClasses} ${baseClasses} ${hoverActiveClasses} ${sizeClasses} ${className}`}
        >
            {children}
        </button>
    );
};