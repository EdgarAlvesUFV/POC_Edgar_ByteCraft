'use client'

import Image from "next/image";

interface ArrowProps {
  className?: string;
  isSelected?: boolean;
  onClick?: () => void;
}

export const Arrow = ({
  className = "",
  onClick,
}: ArrowProps) => {
  return (
    <div
      onClick={() => onClick?.()}
      className={`
        absolute
        w-[12%]
        h-[78%]
        cursor-pointer
        transition-all
        duration-100
        rounded-sm
        md:rounded-md
        bg-[var(--brand-indigo-deep)]
        hover:brightness-110
        ${className}
      `}
    >
      <Image
        src="/image/stage-2/arrow1.svg"
        alt="arrow"
        width={50}
        height={50}
        className="absolute w-[75%] h-[75%] top-[50%] left-[50%] -translate-y-[50%] -translate-x-[42%]"
      />
    </div>
  );
};
