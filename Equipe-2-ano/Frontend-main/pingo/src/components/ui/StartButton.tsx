import Image from "next/image";
import React from "react";

// props flat
interface StartButtonProps {
    children?: React.ReactNode;
    className?: string;
    onClick?: () => void;
}

//definição de um button
export const StartButton = ({ children, className = "", onClick}: StartButtonProps) => {
    const baseClasses = `
        rounded-full
        border-[4px] 
        border-[var(--brand-midnight)]
        bg-[var(--brand-magenta)]
        uppercase 
        cursor-pointer 
        transition-all duration-150 ease-in-out
        flex items-center justify-center
    `;

    const responsive = `
        w-15 h-15
        sm:w-16 sm:h-16
        md:w-20 md:h-20
        lg:w-22 lg:h-22
        xl:w-28 xl:h-28
    `;

    const hoverActiveClasses = "hover:brightness-40 active:scale-[0.98] active:brightness-75";

    return (
        <button
            className={`${baseClasses} ${hoverActiveClasses} ${responsive} ${className}`}
            onClick={onClick}
        >
            <Image
                src="/image/stagesScreen/start.svg"
                alt="start"
                width={0}
                height={0}
                className="w-1/2 h-1/2 translate-x-1"
            />
        </button>
    );
};
