'use client'

import Image from "next/image";

interface TipProps {
  className?: string;
  onClick?: () => void;
}

export const Tip = ({ className = "", onClick}: TipProps) => {
  return (
    <div
        onClick={onClick}
        className={`
            fixed
            cursor-pointer
            z-[10000]
            h-[35px] w-[35px]
            sm:h-[40px] sm:w-[40px]
            md:h-[50px] md:w-[50px]
            lg:h-[60px] lg:w-[60px]
            xl:h-[70px] xl:w-[70px]
            ${className}
        `}
        >
        <Image
            src="/image/common/tip.svg"
            alt="pause"
            width={50}
            height={50}
            className="h-full w-full hover:brightness-110"
        />
    </div>
  );
};
