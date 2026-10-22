import React from "react";

//props DialogBox
interface DialogBoxProps extends React.HTMLAttributes<HTMLDivElement> {
    variant?: "dialog" | "instruct" | "squareCenter";
}

// pre definições de DialogBox
const variants = {
    dialog: "p-8 text-center",
    instruct: "p-6 text-left",
    squareCenter: "p-12 text-center",
};

export const DialogBox = ({ children, variant = "dialog", className = "", ...props }: DialogBoxProps) => {
    const variantClasses = variants[variant];

    return (
        // O componente agora é apenas um único div que combina todas as classes.
        <div {...props} className={`bg-[var(--brand-white)] rounded-2xl shadow-2xl ${variantClasses} ${className}`}>
            {children}
        </div>
    );
};