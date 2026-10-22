'use client'

import Image from "next/image";

interface ContactSelectedProps {
  className?: string;
  name: string;
  isSelected?: boolean;
}

export const ContactSelected = ({
  className = "",
  name
}: ContactSelectedProps) => {
  return (
    <div
      className={`
        absolute
        w-[32%]
        h-[7%]
        top-[3%]
        left-[2%]
        rounded-none
        md:rounded-sm
        bg-[var(--brand-cyan-bright)] 
        ${className}
      `}
    >
      <Image
        src="/image/stage-2/icon-contact.svg"
        alt="contact"
        width={50}
        height={50}
        className="absolute w-[18%] h-[90%] top-[50%] left-[2%] translate-y-[-50%]"
      />
      <div className="absolute w-[0.8%] h-[100%] left-[22%] bg-[var(--brand-navy)] rounded-md" />
      <p className="absolute top-[50%] left-[25%] -translate-y-[50%] text-center font-bold justify-center
          text-[0.6rem]
          sm:text-[0.8rem]
          md:text-[0.9rem]
          lg:text-[1.3rem] text-[var(--brand-midnight)]">
        {name}
      </p>
    </div>
  );
};
