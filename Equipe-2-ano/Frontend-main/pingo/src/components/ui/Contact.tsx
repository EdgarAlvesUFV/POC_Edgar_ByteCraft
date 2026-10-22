'use client'

import Image from "next/image";

interface ContactProps {
  className?: string;
  name: string;
  isSelected?: boolean;
  onClick?: (name: string) => void;
}

export const Contact = ({
  className = "",
  name,
  isSelected = false,
  onClick,
}: ContactProps) => {
  let iamgePath = "/image/stage-2/icon-contact-white.svg";
  let color = "text-white";
  if (isSelected) {
    iamgePath = "/image/stage-2/icon-contact.svg";
    color = "text-[var(--brand-midnight)]";
  }
  return (
    <div
      onClick={() => onClick?.(name)}
      className={`
        relative
        w-[90%]
        h-[15%]
        cursor-pointer
        transition-all
        duration-200
        ${isSelected ? "bg-[var(--brand-cyan-bright)]" : "bg-[var(--brand-violet)]"}
        rounded-sm
        md:rounded-md
        hover:brightness-110
        z-21
        ${className}
      `}
    >
      <Image
        src={iamgePath}
        alt="contact"
        width={50}
        height={50}
        className="absolute w-[35%] h-[80%] top-[50%] left-[2%] -translate-y-[50%]"
      />
      <p className={`absolute top-[50%] left-[35%] -translate-y-[50%] text-center font-bold 
          text-[0.5rem]
          sm:text-[0.7rem]
          md:text-[0.8rem]
          lg:text-[1.2rem] ${color}`}>
        {name}
      </p>
    </div>
  );
};
