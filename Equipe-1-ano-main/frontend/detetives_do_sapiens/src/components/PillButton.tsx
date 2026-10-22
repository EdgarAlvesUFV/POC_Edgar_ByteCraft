// src/components/PillButton.tsx
import { ButtonHTMLAttributes, forwardRef } from "react";
import { cn } from "@/lib/utils";

interface PillButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: "primary" | "secondary";
    size?: "default" | "large";
}

export const PillButton = forwardRef<HTMLButtonElement, PillButtonProps>(
    (
        {
            className,
            variant = "primary",
            size = "default",
            children,
            type = "button", // ✅ respeita o que vier do caller; default = "button"
            ...props
        },
        ref
    ) => {
        return (
            <button
                ref={ref}
                type={type}
                className={cn(
                    // Base
                    "inline-flex items-center justify-center rounded-full border-2 font-extrabold transition-all duration-200",
                    "focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2",
                    "disabled:opacity-50 disabled:cursor-not-allowed",
                    // Sombra mockup
                    "shadow-[2px_4px_0px_rgba(0,0,0,0.35)]",
                    "active:shadow-[1px_2px_0px_rgba(0,0,0,0.35)] active:translate-x-[1px] active:translate-y-[2px]",
                    // Variantes
                    variant === "primary"
                        ? "bg-pill-yellow text-pill-text border-pill-border hover:bg-pill-yellow/90"
                        : "bg-background text-pill-text border-pill-border hover:bg-content-bg",
                    // Tamanhos
                    size === "large" ? "px-12 py-4 text-lg" : "px-8 py-3 text-sm",
                    className
                )}
                {...props}
            >
                {children}
            </button>
        );
    }
);

PillButton.displayName = "PillButton";
