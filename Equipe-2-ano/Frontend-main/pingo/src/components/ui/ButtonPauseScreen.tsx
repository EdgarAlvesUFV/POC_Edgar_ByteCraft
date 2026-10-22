'use client'

import Image from "next/image";
import React from "react";

interface ButtonPauseScreenProps {
  children: React.ReactNode;
  className?: string;
  src: string;
  color?: "cyan" | "violet" | "magenta";
  onClick?: () => void;
}

const variants = {
  violet: "bg-[var(--brand-violet)]",
  cyan: "bg-[var(--brand-cyan-dark)]",
  magenta: "bg-[var(--brand-magenta)]",
};

export const ButtonPauseScreen = ({
  children,
  color = "cyan",
  src = "",
  className = "",
  onClick,
  ...props
}: ButtonPauseScreenProps) => {
  return (
    <button
      onClick={onClick}
      {...props}
      className={`
        relative group cursor-pointer
        w-[70%] h-[17%] ${className}
      `}
    >
      {/* sombra */}
      <span
        className={`
          absolute w-full h-[90%] top-[20%] left-0
          rounded-full bg-[var(--brand-midnight)]
          border-[3px] border-[var(--brand-midnight)]
          transition-transform duration-150 ease-in-out
          group-hover:-translate-y-[2px]
          group-active:translate-y-[4px]
        `}
      />

      {/* botão principal */}
      <span
        className={`
          relative z-10 flex flex-row items-center justify-center
          w-full h-full rounded-full
          border-[3px] border-[var(--brand-midnight)]
          text-white font-extrabold uppercase tracking-wide
          ${variants[color]}
          transition-transform duration-150 ease-in-out
          group-hover:-translate-y-[2px]
          group-active:translate-y-[4px]
        `}
      >
        <div className="h-full w-[30%] flex justify-end items-center">
            <Image
                src={src}
                alt="play"
                width={50}
                height={50}
                className="h-[60%] w-[42%]" // mantém proporção
            />
        </div>
        
        <p className="
            w-[70%] 
            text-left
            pl-[8%]
            text-[0.8rem]
            sm:text-[1rem]
            md:text-[1.2rem]
            lg:text-[2.5rem]
        ">{children}</p>
      </span>
    </button>
  );
};
