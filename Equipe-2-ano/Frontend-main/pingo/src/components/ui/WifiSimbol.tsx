import Image from "next/image";

interface WifiProps {
  className?: string;
  isOn?: boolean;
  onClick?: () => void;
}

export const WifiSimbol = ({ isOn = false, className = "", onClick }: WifiProps) => {
  let path: string;
  if (!isOn) {
      path = "/image/stage-2/wifi-signal-off.svg";
  } else {
      path = "/image/stage-2/wifi-signal-on.svg";
  }
  return (
    <div
      onClick={onClick}
      className={`
        absolute
        cursor-pointer
        w-[4%] auto max-w-[40px] aspect-square
        transition-all duration-300 ease-in-out
        ${className}
      `}
    >
      <Image
        src={path}
        alt="buttom-system"
        width={0}
        height={0}
        className="absolute left-1/2 -translate-x-1/2 w-[45%] h-full"
      />
    </div>
  );
};
