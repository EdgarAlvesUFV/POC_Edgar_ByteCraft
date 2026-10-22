'use client'

import Image from "next/image";
import { useState } from "react";
import { createPortal } from "react-dom";
import { ButtonPauseScreen } from "./ButtonPauseScreen";
import { useRouter } from 'next/navigation';

interface PauseProps {
    className?: string;
    onOpen?: () => void;
    onClose?: () => void;
}

export const Pause = ({ className = "", onOpen, onClose }: PauseProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const router = useRouter();

    const handleClick = () => {
        const newState = !isOpen;
        setIsOpen(newState);

        if (newState && onOpen) {
            onOpen();
        } else if (!newState && onClose) {
            onClose();
        }
    };

    const handleCloseModal = () => {
        setIsOpen(false);
        if (onClose) onClose();
    };

    const handleRestart = () => {
        window.location.reload();
    };

    const handleBack = () => {
        router.push("/stage-list");
    };
    return (
        <>
            {/* Botão de pausa */}
            <div
                onClick={handleClick}
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
                    src="/image/common/pause.svg"
                    alt="pause"
                    width={50}
                    height={50}
                    className="h-full w-full hover:brightness-110"
                />
            </div>

            {/* Conteúdo via Portal */}
            {isOpen &&
                createPortal(
                    <div
                        className="
              fixed inset-0
              bg-[var(--brand-gray-dark)]/40
              z-[10001]
              flex items-center justify-center
            "
                        onClick={handleCloseModal}
                    >
                        <div
                            className="
               bg-white
                rounded-[15%]
                shadow-lg
                font-bold
                flex flex-col gap-[4%] items-center justify-top
                w-[50%] max-w-[500px]
                aspect-[800/689]
                sm:max-w-[500px]
                md:max-w-[500px]
                lg:max-w-[550px]
              "
                            onClick={(e) => e.stopPropagation()} // evita fechar ao clicar dentro
                        >
                            <p className="
                text-[1.3rem]
                sm:text-[1.6rem]
                md:text-[2rem]
                lg:text-[2.8rem]
                pt-[5%]
                text-top
                h-[27%]
              ">O QUE VOCÊ DESEJA?</p>
                            <ButtonPauseScreen src="/image/common/play.svg" color="cyan" onClick={handleCloseModal}> {/* <-- MUDANÇA */}
                                JOGAR
                            </ButtonPauseScreen>
                            <ButtonPauseScreen src="/image/common/restart.svg" color="violet" onClick={handleRestart}>
                                REINICIAR
                            </ButtonPauseScreen>
                            <ButtonPauseScreen src="/image/common/arrow_back.svg" color="magenta" onClick={handleBack}>
                                VOLTAR
                            </ButtonPauseScreen>
                        </div>
                    </div>,
                    document.body
                )}
        </>
    );
};