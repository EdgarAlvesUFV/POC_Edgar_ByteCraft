'use client'

import Image from "next/image";

interface MessageProps {
  className?: string;
  emoji: number;
  isSent: number;
}

export const Message = ({
  className = "",
  emoji,
  isSent 
}: MessageProps) => {
  const path = `/image/stage-2/emoji/emoji-${emoji}.svg`
  if (isSent === 0) return null;
  return (
        <>
        {isSent == 1 ? (
            <div
            className={`absolute flex flex-col justify-end items-end w-full h-[20%] bottom-[20%]
                ${className}
            `}>
                <div className="relative h-[80%] w-auto right-[6%]">
                    <Image
                        src="/image/stage-2/message-green.svg"
                        alt="message"
                        width={50}
                        height={50}
                        className="h-full w-auto"
                    />
                    <Image
                        src={path}
                        alt="emoji"
                        width={50}
                        height={50}
                        className="absolute top-[50%] translate-y-[-50%] left-[17%] h-[80%] w-auto"
                    />
                </div>
                <p className="
                    relative right-[6%] 
                    text-[var(--brand-cyan-dark)]
                    font-bold 
                    text-[0.5rem]
                    sm:text-[0.7rem]
                    md:text-[0.8rem]
                    lg:text-[0.9rem]
                ">
                    MENSAGEM ENVIADA
                </p>
            </div>
        ) : (
            <div
            className={`absolute flex flex-col justify-end items-end w-full h-[20%] bottom-[20%]
                ${className}
            `}>
                <div className="relative h-[80%] w-auto right-[6%]">
                    <div className="relative w-full h-full">
                      <Image
                          src="/image/stage-2/message-red.svg"
                          alt="message"
                          width={50}
                          height={50}
                          className="h-full w-auto relative z-10"
                      />
                      
                      <Image
                          src={path}
                          alt="emoji"
                          width={50}
                          height={50}
                          className="absolute top-[50%] translate-y-[-50%] left-[17%] h-[80%] w-auto z-19"
                      />
                    </div>
                </div>
                <p className="
                    relative right-[6%] 
                    text-[var(--brand-coral)]
                    font-bold 
                    text-[0.5rem]
                    sm:text-[0.7rem]
                    md:text-[0.8rem]
                    lg:text-[0.9rem]
                ">
                    A MENSAGEM NÃO FOI ENVIADA
                </p>
            </div>
        )}
        </>
  );
};