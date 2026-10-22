"use client";

import React, {useEffect, useRef, useState} from "react";
import { createPortal } from "react-dom";
import Image from "next/image";
import { DialogBox } from "@/components/ui/DialogBox";
import { ButtonPauseScreen } from "@/components/ui/ButtonPauseScreen";
import { motion } from "framer-motion";
import Lottie from "react-lottie-player";

interface StageFourCompleteProps {
  variant: "victory" | "defeat";
  score: number;
  onRestart: () => void;
  onExit: () => void;
  onContinue: () => void;
}

const VictoryDialog: React.FC<
  Pick<StageFourCompleteProps, "score" | "onContinue">
> = ({ score, onContinue }) => {
  const [userEmoji, setUserEmoji] = useState<number>(1);
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
        "audios/falas-pingo/falas-doc/fase-4/fase-4-7.m4a"
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
  useEffect(() => {
        const savedEmoji = localStorage.getItem('userSelectedEmoji');
        if (savedEmoji) {
            setUserEmoji(parseInt(savedEmoji));
        }
  }, []);


  return (
    <DialogBox
      variant="dialog"
      className="!p-8 md:!p-12 text-center text-[var(--brand-midnight)] flex flex-col items-center gap-6 !rounded-[1.5rem] w-full max-w-lg bg-[var(--brand-white)]/95 backdrop-blur-sm"
    >
      <h2 className="text-5xl font-bold">PARABÉNS!</h2>

      <p className="text-2xl font-medium">SUA MENSAGEM FOI ENTREGUE.</p>

      <div className="flex items-center justify-center gap-2 md:gap-3 mt-[-10px] md:mt-[-10px]">
        <Image
          src="/image/stage-4/shine.svg"
          alt="shine"
          width={234}
          height={254}
          className="w-16 h-16 md:w-16 md:h-16"
        />
        <p className="text-2xl font-medium">
          VOCÊ FEZ{" "}
          <span className="font-bold text-[var(--brand-yellow-gold)] text-3xl">
            {score}
          </span>{" "}
          PONTOS!
        </p>
        <Image
          src="/image/stage-4/shine.svg"
          alt="shine"
          width={234}
          height={254}
          className="w-16 h-16 md:w-16 md:h-16"
        />
      </div>
      <div className="relative w-40 h-40 md:w-48 md:h-48 flex items-center justify-center">
          <Image
              src="/image/stage-4/envelopeSucess.svg"
              alt="Mensagem Entregue"
              fill
              className="object-contain z-10"
          />
          <Image
              src={`/image/stage-2/emoji/emoji-${userEmoji}.svg`}
              alt="Emoji escolhido"
              width={80}
              height={80}
              className="absolute z-20 w-[30%] h-[30%] top-[25%]"
          />
      </div>

      <div className="w-full px-4 flex flex-col gap-4">
        <ButtonPauseScreen
          src="/image/common/play.svg"
          color="violet"
          onClick={onContinue}
          className="!w-full"
        >
          CONTINUAR
        </ButtonPauseScreen>
      </div>
    </DialogBox>
  );
};

const DefeatDialog: React.FC<
  Pick<StageFourCompleteProps, "onRestart" | "onExit">
> = ({ onRestart, onExit }) => {
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
        "audios/falas-pingo/falas-doc/fase-4/fase-4-6.m4a"
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
    <DialogBox
      variant="dialog"
      className="!p-8 md:!p-12 text-center text-[var(--brand-midnight)] flex flex-col items-center gap-6 !rounded-[1.5rem] w-full max-w-lg"
    >
      <h2 className="text-5xl font-bold">OH NÃO!</h2>
      <p className="text-2xl font-medium">A MENSAGEM FOI PERDIDA.</p>

      <Image
        src="/image/stage-4/envelopeFailed.png"
        alt="Mensagem Perdida"
        width={200}
        height={200}
        className="w-40 h-40 md:w-48 md:h-48"
      />

      <div className="w-full px-4 flex flex-col gap-4">
        <ButtonPauseScreen
          src="/image/common/restart.svg"
          color="violet"
          onClick={onRestart}
          className="!w-full"
        >
          REINICIAR
        </ButtonPauseScreen>
        <ButtonPauseScreen
          src="/image/common/arrow_back.svg"
          color="magenta"
          onClick={onExit}
          className="!w-full"
        >
          VOLTAR
        </ButtonPauseScreen>
      </div>
    </DialogBox>
  );
};

export const StageFourComplete = ({
  variant,
  score,
  onRestart,
  onExit,
  onContinue,
}: StageFourCompleteProps) => {
  // Este é o overlay escuro
  return createPortal(
    <div
      className="
              fixed inset-0
              bg-[var(--brand-gray-dark)]/40
              z-[9998]
              flex items-center justify-center
              backdrop-blur-sm
            "
    >
      {variant === "victory" && (
        <Lottie
          path="animation/stage-1/Confetti.json"
          play
          loop={false}
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            zIndex: 9999,
            pointerEvents: "none",
          }}
        />
      )}

      <motion.div
        initial={{ scale: 0.5, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ type: "spring", stiffness: 260, damping: 20 }}
        className="w-[90%] max-w-lg relative z-[10000]"
      >
        {/* Decide qual modal renderizar */}
        {variant === "victory" ? (
          <VictoryDialog score={score} onContinue={onContinue} />
        ) : (
          <DefeatDialog onRestart={onRestart} onExit={onExit} />
        )}
      </motion.div>
    </div>,
    document.body
  );
};
