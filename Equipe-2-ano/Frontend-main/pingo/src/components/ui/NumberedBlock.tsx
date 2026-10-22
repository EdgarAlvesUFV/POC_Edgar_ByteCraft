'use client'
import Image from "next/image";
import React from "react";

interface NumberedBlockProps {
  number: number;
  progress: number | null;
  color?: "blue" | "red";
  className?: string;
  isSelected?: boolean;                // indica se o bloco está selecionado
  onSelect?: (number: number) => void; // callback para o pai
}

const colors = {
  red: {
    block: "from-[var(--brand-magenta)] to-[var(--brand-magenta-dark)]",
    shadow: "bg-[var(--brand-magenta)]",
  },
  blue: {
    block: "from-[var(--brand-cyan)] to-[var(--brand-cyan-dark)]",
    shadow: "bg-[var(--brand-cyan-dark)]",
  },
};

const stageImages: Record<number, string> = {
  1: "/image/stagesScreen/on-off.svg",
  2: "/image/stagesScreen/letter.svg",
  3: "/image/stagesScreen/world.svg",
  4: "/image/stagesScreen/router.svg",
  5: "/image/stagesScreen/blocked.svg",
};

export const NumberedBlock = ({
  number,
  progress,
  color = "blue",
  className = "",
  isSelected = false,
  onSelect,
}: NumberedBlockProps) => {
  if(progress === null) {
    return null; // ou algum outro fallback apropriado
  }
  const colorClasses = colors[color];
  const isBlocked = number > progress;

  const responsive = `
    w-24 h-24 text-3xl gap-2
    sm:w-24 sm:h-24 sm:text-3xl sm:gap-2
    md:w-32 md:h-32 md:text-4xl md:gap-3
    lg:w-40 lg:h-40 lg:text-5xl lg:gap-3
    xl:w-48 xl:h-48 xl:text-6xl xl:gap-5
    2xl:w-56 2xl:h-56 2xl:text-7xl 2xl:gap-6
  `;

  // Seleciona imagem da fase ou bloqueada
  const imageSrc = isBlocked ? stageImages[5] : stageImages[number];

  return (
    <div
      className={`relative group ${className}`}
      onClick={() => {
        if (!isBlocked && onSelect) onSelect(number);
      }}
    >
      {/* sombra do bloco */}
      <div
        className={`
          absolute
          w-full h-full
          rounded-2xl
          transition-all duration-150 ease-in-out
          ${responsive}
          ${
            isBlocked
              ? `${colorClasses.shadow} brightness-75 rotate-12` // bloqueado
              : isSelected
              ? "bg-[var(--brand-yellow-gold)] rotate-12" // selecionado
              : `${colorClasses.shadow} rotate-12 group-hover:rotate-0 group-active:rotate-0` // normal
          }
        `}
      />

      {/* bloco superior */}
      <div
        className={`
          relative
          cursor-pointer 
          bg-gradient-to-br 
          rounded-2xl 
          flex flex-col items-center justify-center 
          text-white font-extrabold 
          border-3 border-[var(--brand-midnight)]
          transition-all duration-150 ease-in-out
          ${colorClasses.block} 
          ${responsive}
          ${isBlocked ? "brightness-75 cursor-not-allowed" : ""}
        `}
      >
        <span>{number}</span>
        <Image
          src={imageSrc}
          alt={`fase ${number}`}
          className="w-9 h-9 sm:w-9 sm:h-9 md:w-13 md:h-13 lg:w-16 lg:h-16"
          width={20}
          height={20}
        />
      </div>
    </div>
  );
};
