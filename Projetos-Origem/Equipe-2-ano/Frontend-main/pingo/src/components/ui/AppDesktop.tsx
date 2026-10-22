import Image from "next/image";

interface AppDesktopProps {
  className?: string;
  src?: string;
  onClick?: () => void;
}

export const AppDesktop = ({ className = "", src = "/image/stage-2/paste.svg" , onClick}: AppDesktopProps) => {
  return (
    <div
      onClick={onClick}
      className={`
        relative
        w-[100%] max-w-[50px] aspect-square
        transition-all duration-300 ease-in-out
        hover:brightness-125 hover:-translate-y-1
        ${className}
      `}
    >
      <Image
        src={src}
        alt="appdesktop"
        fill
        className="object-contain"
      />
    </div>
  );
};
