import Image from "next/image";

interface ButtomSystemProps {
  className?: string;
}

export const ButtomSystem = ({ className = "" }: ButtomSystemProps) => {
  return (
    <div
      className={`
        absolute
        w-[2.5%] max-w-[25px] aspect-square
        transition-all duration-300 ease-in-out
        hover:brightness-125
        ${className}
      `}
    >
      <Image
        src="/image/stage-2/buttom-system.svg"
        alt="buttom-system"
        fill
        className="object-contain"
      />
    </div>
  );
};
