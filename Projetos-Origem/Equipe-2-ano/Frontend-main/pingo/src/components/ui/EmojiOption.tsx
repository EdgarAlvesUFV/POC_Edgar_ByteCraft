'use client'

import Image from "next/image";

interface ContactProps {
  className?: string;
  number?: number;
  color?: "magenta" | "violet";
  onClick?: (number: number, color: "magenta" | "violet") => void;
}

const variants = {
  magenta: "bg-[var(--brand-magenta)]",
  violet: "bg-[var(--brand-indigo-deep)]",
};
export const EmojiOption = ({
  className = "",
  color = "magenta",
  number = 1,
  onClick,
}: ContactProps) => {
  const variant = variants[color];
  const path = `/image/stage-2/emoji/emoji-${number}.svg`;
  return (
    <div
      onClick={() => onClick?.(number, color)}
      className={`
        relative
        w-[16%]
        h-[95%]
        cursor-pointer
        transition-all
        duration-100
        rounded-lg
        md:rounded-2xl
        ${variant}
        hover:brightness-110
        ${className}
      `}
    >
      <Image
        src={path}
        alt="emoji"
        width={50}
        height={50}
        className="absolute w-[70%] h-[70%] top-[50%] left-[50%] -translate-y-[50%] -translate-x-[50%]"
      />
    </div>
  );
};
