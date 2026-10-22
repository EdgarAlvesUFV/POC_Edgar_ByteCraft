import React from "react";

interface StageTitleProps {
  text?: string;
  className?: string;
}

export const StageTitle = ({ text = "LISTA DE FASES", className = "" }: StageTitleProps) => {
  const responsive = `
    w-60 h-12 text-base gap-2
    sm:w-70 sm:h-14 sm:text-xl sm:gap-2
    md:w-90 md:h-14 md:text-xl md:gap-3
    lg:w-100 lg:h-16 lg:text-2xl lg:gap-3
    xl:w-140 xl:h-17 xl:text-4xl xl:gap-4
    2xl:w-150 2xl:h-18 2xl:text-4xl 2xl:gap-5
`;

  return (
    <div
      className={`
        flex 
        items-center 
        justify-center
        px-6 py-1
        bg-[var(--brand-magenta)]
        text-white
        text-xl
        font-extrabold
        border-black
        border-4
        rounded-full
        uppercase
        ${className}
        ${responsive}
      `}
    >
      {text}
    </div>
  );
};
