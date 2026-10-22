import React from "react";

// propsButton3D
interface Button3DProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: "primary" | "secondary" | "flatShadow";
    size?: "small" | "medium" | "large";
}

// tamanhos pre definidos
const sizes = {
    small: "py-2 px-5 text-xl",
    medium: "py-3 px-8 text-3xl",
    large: "py-4 px-10 text-5xl",
};

// variantes
const variants = {
    primary: {
        button: "bg-[var(--brand-violet)]",
        shadow: "bg-[var(--brand-magenta)]",
    },
    secondary: {
        button: "bg-[var(--brand-cyan)]",
        shadow: "bg-[var(--brand-yellow-gold)]",
    },
    flatShadow: {
        button: "bg-[var(--brand-violet)]",
        shadow: "bg-[var(--brand-midnight)]",
    },
};

export const Button3D = ({ children, variant = "primary", size = "medium", className = "", ...props}: Button3DProps) => {
    // salva variante
    const variantClasses = variants[variant];
    const sizeClasses = sizes[size];

    // base do botão
    const buttonBaseClasses =
        "relative " +
        "inline-flex items-center justify-center " +
        "whitespace-nowrap " +
        "rounded-xl " +
        "border-[3px] " +
        "border-[var(--brand-midnight)] " +
        "text-white " +
        "font-extrabold " +
        "uppercase " +
        "tracking-wide " +
        "transition-transform duration-150 ease-in-out";

    // hover e active(tratamento de variante)
    const hoverActiveClasses = variant === "flatShadow"
        ?
        "group-hover:-translate-x-[2px] " +
        "group-hover:-translate-y-[2px] " +
        "group-active:translate-x-0 " +
        "group-active:translate-y-[6px]"
        :
        "group-hover:-translate-x-[2px] " +
        "group-hover:-translate-y-[2px] " +
        "group-active:translate-x-[-6px] " +
        "group-active:translate-y-[6px]";

    // sombra base
    const shadowBaseClasses =
        "border-[3px] " +
        "border-[var(--brand-midnight)] " +
        "absolute " +
        "w-full " +
        "rounded-xl " +
        "transition-all duration-150 ease-in-out";

    // definição de sombra para variante
    const shadowClasses = variant === "flatShadow"
        ?
        "bg-[var(--brand-midnight)] " +
        "left-[0px] " +
        "top-[9px] " +
        "h-full " +
        "w-full " +
        "group-hover:translate-y-[2px] " +
        "group-active:top-[6px]"
        :
        "top-[6px] " +
        "left-[-6px] " +
        "h-full " +
        "group-hover:translate-x-[-2px] " +
        "group-hover:translate-y-[2px] " +
        "group-active:translate-x-0 " +
        "group-active:translate-y-0";

    return (
        <button
            {...props}
            className={`relative inline-block cursor-pointer group w-fit-content `}
        >
            {/* sombra */}
            <span className={`${shadowBaseClasses} ${shadowClasses} ${variantClasses.shadow}`} />

            {/* button*/}
            <span className={`${buttonBaseClasses} ${hoverActiveClasses} ${sizeClasses} ${variantClasses.button} ${className}`}>
                {children}
            </span>
        </button>
    );
};