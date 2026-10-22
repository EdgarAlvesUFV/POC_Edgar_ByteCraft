"use client";

import React, { useEffect, useRef } from "react";
import Image from "next/image";
import { motion } from "framer-motion";
import { Tip } from "@/components/ui/Tip";
import { Pause } from "@/components/ui/Pause";
import { DialogBox } from "@/components/ui/DialogBox";
import { Button3D } from "@/components/ui/Button3D";

const formatTime = (milliseconds: number) => {
    const totalSeconds = Math.max(0, Math.ceil(milliseconds / 1000));
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(
        2,
        "0"
    )}`;
};

// --- 1. COMPONENTE HUD ---
interface HUDProps {
    lives: number;
    score: number;
    remainingTime: number;
    onTipClick: () => void;
    onPauseOpen: () => void;
    onPauseClose: () => void;
}

const MAX_LIVES = 3;

export const HUD: React.FC<HUDProps> = ({
                                            lives,
                                            score,
                                            remainingTime,
                                            onTipClick,
                                            onPauseOpen,
                                            onPauseClose,
                                        }) => {
    return (
        <header className="absolute top-4 left-0 right-0 z-50 flex w-full items-center justify-between px-4 md:px-8">
            <Tip
                onClick={onTipClick}
                className="!static !relative !top-auto !left-auto z-[100]
                   h-16 w-16
                   md:h-24 md:w-24"
            />
            <div
                className="
          flex flex-1 items-center
          justify-between
          rounded-3xl
          bg-white/30
          py-4 px-6 shadow-lg
          backdrop-blur-md
          mx-4 md:mx-6
        "
            >
                {/* Vidas */}
                <div className="flex items-center gap-4 md:gap-5">
          <span className="text-4xl font-bold text-white md:text-6xl">
            VIDAS:
          </span>
                    <div className="flex gap-2 md:gap-3">
                        {[...Array(MAX_LIVES)].map((_, i) => (
                            <Image
                                key={i}
                                src={
                                    i < lives
                                        ? "/image/stage-4/lifeOn.svg"
                                        : "/image/stage-4/lifeOff.svg"
                                }
                                alt="Vida"
                                width={56}
                                height={56}
                                className="h-12 w-12 md:h-16 md:w-16"
                            />
                        ))}
                    </div>
                </div>

                {/* Cronômetro */}
                <div className="flex items-center gap-4 md:gap-5">
          <span className="text-4xl font-bold text-white md:text-6xl">
            {formatTime(remainingTime)}
          </span>
                </div>

                {/* Pontos */}
                <div className="flex items-center gap-4 md:gap-5">
          <span className="text-4xl font-bold text-white md:text-6xl">
            PONTOS: {score}
          </span>
                    <Image
                        src="/image/stage-4/shine.svg"
                        alt="Pontos"
                        width={64}
                        height={64}
                        className="h-12 w-12 md:h-16 md:w-16"
                    />
                </div>
            </div>
            <Pause
                onOpen={onPauseOpen}
                onClose={onPauseClose}
                className="!static !relative !top-auto !left-auto z-[100]
                   h-16 w-16
                   md:h-24 md:w-24"
            />
        </header>
    );
};

// --- 2. COMPONENTE MODAL DE DICAS ---

interface TipDialogProps {
    onClose: () => void;
}

export const TipDialog: React.FC<TipDialogProps> = ({ onClose }) => {
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const timeoutRef = useRef<NodeJS.Timeout | null>(null);
    const handlePlaySound = async () => {
        try {
            if (audioRef.current) {
                audioRef.current.pause();
                audioRef.current.currentTime = 0;
            }

            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current);
            }

            const audio = new Audio(
                "audios/falas-pingo/falas-doc/fase-4/fase-4-5.m4a"
            );
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
        <div className="fixed inset-0 z-[200] flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <motion.div
                initial={{ scale: 0.5, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ duration: 0.3, ease: "easeOut" }}
                className="w-full h-full flex items-center justify-center"
            >
                <DialogBox
                    variant="dialog"
                    className="!p-4 bg-white/95 backdrop-blur-sm !rounded-[2rem] shadow-2xl
                     w-[45vw] h-[90vh] flex items-center justify-center"
                >
                    <DialogBox
                        variant="dialog"
                        className="!p-10 md:!p-16 text-center text-[var(--brand-midnight)] !rounded-[1.5rem] relative
                       w-full max-w-3xl"
                    >
                        <Image
                            src="/image/common/sound.svg"
                            alt="Som"
                            width={60}
                            height={60}
                            className="absolute left-8 top-8 h-10 w-10 md:h-14 md:w-14"
                            onClick={handlePlaySound}
                        />

                        <h3 className="mb-8 text-2xl md:text-4xl font-bold uppercase leading-snug">
                            AGORA SUA MENSAGEM VAI SEGUIR PELO CAMINHO QUE VOCÊ CRIOU,
                            VIAJANDO PELA{" "}
                            <span className="text-[var(--brand-magenta)]">REDE</span>!
                        </h3>

                        <div className="flex flex-col items-start gap-8 px-4 md:px-8">
                            <div className="flex items-center gap-6">
                                <Image
                                    src="/image/stage-4/envelope.svg"
                                    alt="Envelope"
                                    width={80}
                                    height={80}
                                />
                                <span className="text-2xl md:text-3xl font-bold">
                  GUIE A MENSAGEM
                </span>
                            </div>
                            <div className="flex items-center gap-6">
                                <Image
                                    src="/image/stage-4/shine.svg"
                                    alt="Brilho"
                                    width={80}
                                    height={80}
                                />
                                <span className="text-2xl md:text-3xl font-bold">
                  COLETE PONTOS
                </span>
                            </div>
                            <div className="flex items-center gap-6">
                                <Image
                                    src="/image/stage-4/noiseYellow.svg"
                                    alt="Ruído"
                                    width={80}
                                    height={80}
                                />
                                <span className="text-2xl md:text-3xl font-bold">
                  DESVIE DE RUÍDOS
                </span>
                            </div>
                        </div>

                        <div className="flex justify-center mt-12">
                            <Button3D
                                size="large"
                                variant="flatShadow"
                                onClick={onClose}
                                className="px-16 text-4xl font-bold"
                            >
                                CONTINUAR
                            </Button3D>
                        </div>
                    </DialogBox>
                </DialogBox>
            </motion.div>
        </div>
    );
};

// --- 3. COMPONENTE SELEÇÃO DE DIFICULDADE ---

interface DifficultySelectProps {
    onSelect: (difficulty: "easy" | "medium" | "hard") => void;
    onExit: () => void;
}

export const DifficultySelect: React.FC<DifficultySelectProps> = ({
                                                                      onSelect,
                                                                      onExit,
                                                                  }) => {
    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <motion.div
                initial={{ scale: 0.5, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                transition={{ duration: 0.3, ease: "easeOut" }}
                className="w-full h-full flex items-center justify-center"
            >
                <DialogBox
                    variant="dialog"
                    className="
                        !p-8 md:!p-12 text-center text-[var(--brand-midnight)]
                        flex flex-col gap-8
                        !rounded-[2rem]      /* Borda do DialogBox exterior */
                        bg-white/95         /* Fundo do DialogBox exterior */
                        backdrop-blur-md    /* Efeito do DialogBox exterior */
                        shadow-2xl          /* Sombra do DialogBox exterior */
                        w-full max-w-2xl    /* Largura do DialogBox interior */
                    "
                >
                    <h2 className="text-center text-4xl md:text-5xl font-bold text-[var(--brand-midnight)]">
                        SELECIONE A DIFICULDADE
                    </h2>

                    <Button3D
                        variant="secondary"
                        size="large"
                        onClick={() => onSelect("easy")}
                        className="w-full text-4xl font-bold"
                    >
                        FÁCIL
                    </Button3D>
                    <Button3D
                        variant="primary"
                        size="large"
                        onClick={() => onSelect("medium")}
                        className="w-full text-4xl font-bold"
                    >
                        MÉDIO
                    </Button3D>
                    <Button3D
                        variant="primary"
                        size="large"
                        onClick={() => onSelect("hard")}
                        className="w-full text-4xl !bg-[var(--brand-pink-hot)] font-bold"
                    >
                        DIFÍCIL
                    </Button3D>

                    <Button3D
                        variant="flatShadow"
                        size="large"
                        onClick={onExit}
                        className=" w-full text-4xl font-bold"
                    >
                        VOLTAR
                    </Button3D>
                </DialogBox>
            </motion.div>
        </div>
    );
};