'use client';

import React, { useRef, useState, useEffect } from 'react';
import Image from 'next/image';
import { ButtonFlat } from '@/components/ui/ButtonFlat';
import { DialogBox } from '@/components/ui/DialogBox';

interface InitialDialogProps {
    onStart: () => void;
    onExit: () => void;
    soundPath?: string;
}

export const InitialDialogComponent = ({ onStart, onExit, soundPath = ''}: InitialDialogProps) => {
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const [isPlaying, setIsPlaying] = useState(false);

    // Lógica para tocar o áudio
    const handlePlaySound = async (e?: React.MouseEvent) => {
        if (e) e.stopPropagation();
        if (soundPath == '') return;

        if (audioRef.current) {
            if (isPlaying) {
                audioRef.current.pause();
                audioRef.current.currentTime = 0;
                setIsPlaying(false);
            } else {
                try {
                    await audioRef.current.play();
                    setIsPlaying(true);
                } catch (err) {
                    console.error("Erro ao tentar tocar o som:", err);
                    setIsPlaying(false);
                }
            }
        }
    };

    // Configuração inicial do áudio e cleanup
    useEffect(() => {
        audioRef.current = new Audio(soundPath);

        const onEnded = () => setIsPlaying(false);
        if (audioRef.current) {
            audioRef.current.addEventListener('ended', onEnded);
        }

        return () => {
            if (audioRef.current) {
                audioRef.current.removeEventListener('ended', onEnded);
                audioRef.current.pause();
            }
        };
    }, [soundPath]);

    // Garante que o áudio pare se o componente sair
    const handleExit = (e?: React.MouseEvent) => {
        if (e) e.stopPropagation();
        if (audioRef.current) {
            audioRef.current.pause();
            audioRef.current.currentTime = 0;
        }
        onExit();
    }

    // Start handler que previne bubbling
    const handleStartClick = (e: React.MouseEvent) => {
        e.stopPropagation();
        onStart();
    };

    return (
        <div className="fixed inset-0 bg-[var(--brand-gray-dark)]/40 z-[9999] flex items-center justify-center">
            <DialogBox variant="squareCenter" className="
                w-[90%] max-w-[600px] h-auto
                flex flex-col items-center gap-4 py-8 px-6
                md:w-[80%] md:max-w-[700px] md:gap-6
            ">
                <div className="relative w-full flex justify-center items-center">
                    <h2 className="
                        font-extrabold text-[var(--brand-midnight)]
                        text-2xl sm:text-3xl md:text-4xl lg:text-5xl
                        text-center
                    ">
                        PRIMEIRA FASE
                    </h2>
                </div>

                {/* Texto Simplificado e Configurável */}
                <p className="
                    text-[var(--brand-midnight)] font-medium
                    text-base sm:text-lg md:text-xl lg:text-2xl
                    text-center
                    max-w-[90%]
                ">
                    PINGO IRÁ TE GUIAR! CLIQUE EM <b
                    className="inline-block rounded-r-md rounded-l-md bg-[var(--brand-pink-hot)] px-2 py-0.5 font-bold text-[var(--brand-white)] align-middle">COMEÇAR</b> PARA INICIAR A FASE OU <b
                    className="inline-block rounded-r-md rounded-l-md bg-[var(--brand-violet)] px-2.5 py-1 font-bold text-[var(--brand-white)] align-middle">X</b> PARA
                    SAIR.
                </p>

                {/* Área de Botões */}
                <div className="flex gap-4 w-full justify-center mt-4">
                    <ButtonFlat
                        onClick={handleStartClick}
                        variant="gradient"
                        size="medium"
                        className="rounded-r-4xl rounded-l-4xl"
                    >
                        COMEÇAR
                    </ButtonFlat>

                    <ButtonFlat
                        onClick={handleExit}
                        variant="flat"
                        size="medium"
                        className="rounded-r-4xl rounded-l-4xl"
                    >
                        X
                    </ButtonFlat>
                </div>
            </DialogBox>
        </div>
    );
};
