"use client";

import Image from "next/image";
import { useEffect, useRef } from "react";
import { createPortal } from "react-dom";

interface InfoScreenProps {
  className?: string;
  type?: string;
  onClick: () => void;
}

export const InfoScreen = ({
  className = "",
  type = "modem",
  onClick,
}: InfoScreenProps) => {
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  let text = "";
  let description = "";
  let soundPath = "";

  switch (type) {
    case "modem":
      text = "ROTEADOR";
      description =
        "É O APARELHO QUE ESPALHA A INTERNET PELA CASA, PARA QUE O COMPUTADOR, O CELULAR E O TABLET CONSIGAM SE CONECTAR.";
        soundPath =
        "audios/falas-pingo/escrito-no-jogo/fase-3/fase-3-roteador.m4a";
      break;
    case "switch":
      text = "SWITCH";
      description =
        "É UM AJUDANTE DO ROTEADOR QUE ORGANIZA O CAMINHO DAS MENSAGENS, PARA CADA UMA IR PARA O SEU DESTINO.";
      soundPath =
        "audios/falas-pingo/escrito-no-jogo/fase-3/fase-3-switch.m4a";
      break;
    case "server":
      text = "SERVIDOR";
      description =
        "É UM SUPERCOMPUTADOR QUE GUARDA E ENVIA AS INFORMAÇÕES PARA O LUGAR CERTO QUANDO ALGUÉM USA A INTERNET.";
      soundPath = "audios/falas-pingo/falas-doc/fase-3/fase-3-3.m4a";
      break;
    case "notebook":
      text = "NOTEBOOK";
      description =
        "É UM COMPUTADOR DE COLO! FAZ TUDO O QUE UM COMPUTADOR FAZ, MAS É MENOR E DÁ PRA LEVAR NA MOCHILA.";
      soundPath = "audios/falas-pingo/falas-doc/fase-3/fase-3-1.m4a";
      break;
    case "computer":
      text = "COMPUTADOR";
      description =
        "É UMA MÁQUINA QUE A GENTE USA PARA ESCREVER, DESENHAR E CONVERSAR COM OS AMIGOS PELA INTERNET.";
      soundPath =
        "audios/falas-pingo/escrito-no-jogo/fase-3/fase-3-computador.m4a";
      break;
    case "tablet":
      text = "TABLET";
      description =
        "PARECE UM CELULAR GRANDÃO, ÓTIMO PRA ASSISTIR VÍDEOS, JOGAR E MANDAR MENSAGENS!";
      soundPath = "audios/falas-pingo/falas-doc/fase-3/fase-3-5.m4a";
      break;
    case "smartphone":
      text = "CELULAR";
      description =
        "É UM APARELHO QUE CABE NO BOLSO, A GENTE USA PRA LIGAR, MANDAR MENSAGENS E USAR A INTERNET.";
      soundPath = "audios/falas-pingo/falas-doc/fase-3/fase-3-2.m4a";
      break;
    case "antenna":
      text = "ANTENA";
      type = "antenna-2";
      description =
        "É QUEM MANDA E RECEBE OS SINAIS INVISÍVEIS QUE FAZEM A INTERNET CHEGAR ATÉ O ROTEADOR.";
      soundPath = "audios/falas-pingo/falas-doc/fase-3/fase-3-4.m4a";
      break;
  }

  const handlePlaySound = async () => {
    if (!soundPath) return;

    try {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }

      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }

      const audio = new Audio(soundPath);
      audioRef.current = audio;

      timeoutRef.current = setTimeout(async () => {
        try {
          await audio.play();
        } catch (err) {
          console.error("Erro ao tentar tocar o som:", err);
        }
      }, 0);
    } catch (error) {
      console.error("Erro ao carregar o som:", error);
    }
  };

  useEffect(() => {
    handlePlaySound();
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  return (
    <>
      {createPortal(
        <div
          className="
                fixed inset-0
                bg-[var(--brand-gray-dark)]/40
                z-[9999]
                flex items-center justify-center
            "
          onClick={onClick}
        >
          <div
            className="
                fixed
                bg-white
                rounded-[15%]
                shadow-lg
                font-bold
                flex flex-col gap-[4%]
                w-[50%] max-w-[500px]
                aspect-[800/689] 
                sm:max-w-[500px]
                md:max-w-[500px]
                lg:max-w-[550px]
                "
            onClick={(e) => e.stopPropagation()} // evita fechar ao clicar dentro
          >
            <Image
              src="/image/common/cancel.svg"
              alt="cancel"
              width={0}
              height={0}
              onClick={onClick}
              className="
                        absolute 
                        cursor-pointer
                        w-[15%] h-[15%] 
                        right-[5%] top-[4%]
                        transition-transform
                        hover:scale-110
                        active:scale-95
                    "
            />

            <Image
              src="/image/common/sound.svg"
              alt="sound"
              width={0}
              height={0}
              onClick={handlePlaySound}
              className="
                        absolute
                        cursor-pointer
                        w-[11%] h-[11%]
                        left-[12%] top-[10%]
                        transition-transform
                        hover:scale-110
                        active:scale-95
                    "
            />

            <p
              className="
                    relative
                    left-[23%]
                    text-[1.1rem]
                    sm:text-[1.4rem]
                    md:text-[1.8rem]
                    lg:text-[2.6rem]
                    pt-[8%]
                    h-[20%]
                    w-[55%]
                    "
            >
              {text}
            </p>
            <Image
              src={`/image/stage-3/network-device/${type}.svg`}
              alt="network-device"
              width={0}
              height={0}
              className="
                        relative
                        w-[30%] h-[30%]
                        left-[50%] -translate-x-1/2
                    "
            />
            <p
              className="
                    relative
                    left-1/2
                    -translate-x-1/2
                    text-[0.5rem]
                    sm:text-[0.8rem]
                    md:text-[1.2rem]
                    lg:text-[1.6rem]
                    h-[40%]
                    w-[75%]
                    text-justify
                    "
            >
              {description}
            </p>
          </div>
        </div>,
        document.body
      )}
    </>
  );
};
