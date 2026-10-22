import Image from "next/image";

interface WifiScreenProps {
  className?: string;
  isConnected?: boolean;
  onWifiConnect?: () => void;
}

export const WifiScreen = ({ className = "", isConnected, onWifiConnect}: WifiScreenProps) => {
  
  const handleClick = () => {
    if (onWifiConnect) onWifiConnect();
  };

  return (
    <div
      className={`
        fixed
        w-[17%] h-[27%]
        bg-[var(--brand-cyan-bright)]
        right-[4.5%] bottom-[23%]
        shadow-[0px_0px_5px_0px_rgba(0,0,0,0.5)]
        rounded-[10%]
        z-40
        ${className}
      `}
    >
        <p className="
            relative 
            top-[8%] 
            left-[50%] 
            w-[92%]
            h-[13%]
            -translate-x-[50%]
            flex flex-row items-center justify-center
            font-bold
            text-white
            bg-[var(--brand-navy)]
            rounded-full
            shadow-[0px_0px_3px_0px_rgba(0,0,0,0.5)]
            text-[0.3rem] sm:text-[0.4rem] md:text-[0.55rem] lg:text-[0.75rem]
        ">
        REDES WIFI DISPONÍVEIS
        </p>

    
        <div
            className="
            relative
            top-[30%] 
            left-[50%] 
            w-[85%]
            h-[35%]
            -translate-y-[50%] -translate-x-[50%]
            font-bold
            text-white
            bg-[var(--brand-navy)]
            shadow-[0px_0px_3px_0px_rgba(0,0,0,0.5)]
            text-[0.3rem] sm:text-[0.4rem] md:text-[0.5rem] lg:text-[0.6rem]
            rounded-md sm:rounded-lg md:rounded-xl lg:rounded-2xl
          ">
            <div className="
              relative
              top-[15%]
              flex
              flex-row
              itens-center
              justify-center
              gap-0
              w-[100%]
              h-[40%]
            ">
              <p className="
                relative
                flex 
                items-center 
                justify-center 
                text-[0.3rem] sm:text-[0.4rem] md:text-[0.5rem] lg:text-[0.75rem]
                w-[70%] ">CASA DO ALUNO
              </p>
              <Image
                src="/image/stage-2/wifi-signal-on.svg"
                alt="buttom-system"
                width={0}
                height={0}
                className="relative filter invert h-[80%] w-[20%]"
              />
            </div>

            <div className={`
              fixed
              left-[50%]
              bottom-[15%]
              -translate-x-[50%]
              w-[65%]
              h-[30%]
              flex
              flex-row
              items-center
              justify-center
              cursor-pointer
              rounded-full
              text-[var(--brand-navy)]
              text-[0.3rem] sm:text-[0.4rem] md:text-[0.5rem] lg:text-[0.9rem]
              ${isConnected ? 'bg-[var(--brand-cyan)]' : 'bg-[var(--brand-cyan-bright)]'}
              
            `}
            onClick={handleClick}>

              <p>{isConnected ? "CONECTADO" : "CONECTAR"}</p>
            </div>
        </div>
    </div>
  );
};