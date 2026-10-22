'use client'

import Image from "next/image";
import React from "react";

interface ButtonSendProps {
  children: React.ReactNode;
  className?: string;
  onClick?: () => void;
}


export const ButtonSend = ({
  children,
  className = "",
  onClick,
  ...props
}: ButtonSendProps) => {
  return (
    <button
      onClick={onClick}
      {...props}
      className={`
        fixed cursor-pointer
        w-[80px] h-[25px]
        sm:w-[90px] sm:h-[30px]
        md:w-[125px] md:h-[35px]
        lg:w-[135px] lg:h-[40px]
        xl:w-[165px] xl:h-[50px]
        transition-transform
        hover:scale-110
        active:scale-95
        ${className}
      `}
    >
      {/* sombra */}
      <span
        className={`
          absolute w-full h-[90%] 
          top-[15%] left-0
          sm:top-[20%]
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
            bg-[var(--brand-cyan-dark)]
          text-white font-extrabold uppercase tracking-wide
          transition-transform duration-150 ease-in-out
          group-hover:-translate-y-[2px]
          group-active:translate-y-[4px]
        `}
      >
        
        <p className="
            w-[70%] 
            text-center
            pl-[8%]
            text-[0.7rem]
            sm:text-[0.8rem]
            md:text-[1.2rem]
            lg:text-[1.4rem]
            xl:text-[1.7rem]
        ">{children}</p>
        
        <div className="h-full w-[30%] flex justify-center items-center">
            <Image
                src="/image/common/play.svg"
                alt="play"
                width={50}
                height={50}
                className="h-[60%] w-auto"
            />
        </div>
      </span>
    </button>
  );
};
