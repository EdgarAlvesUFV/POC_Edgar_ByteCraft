import Image from "next/image";

interface ButtonAppScreenProps {
  className?: string;
  type?: "min" | "max" | "x";
  onClick?: () => void;
}

export const ButtonAppScreen = ({ className = "", type = "min", onClick }: ButtonAppScreenProps) => {
  const defaultClass = `
    relative
    transition-all duration-300 ease-in-out
    h-[60%]
    cursor-pointer
  `;

  let src = "";
  let width = "20%"; // valor default

  if (type === "min") src = "/image/stage-2/buttom-min.svg";
  else if (type === "max") { src = "/image/stage-2/buttom-max.svg"; width = "18%"; }
  else if (type === "x") { src = "/image/stage-2/buttom-x.svg"; width = "18%"; }

  return (
    <div
      className={`${defaultClass} ${className}`}
      style={{ width }} // largura dinâmica via style
      onClick={onClick}
    >
      <Image
        src={src}
        alt={type}
        fill
        className="object-contain"
      />
    </div>
  );
};
