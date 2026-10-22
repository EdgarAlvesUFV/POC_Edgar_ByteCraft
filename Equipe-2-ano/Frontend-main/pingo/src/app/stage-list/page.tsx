'use client'
import CloudDecorationsStages from "@/components/decorations/CloudDecorationsStages";
import { NumberedBlock } from "@/components/ui/NumberedBlock";
import { StageTitle } from "@/components/ui/StageTitle";
import { StartButton } from "@/components/ui/StartButton";
import { useProgress } from "@/domain/user/hooks/useProgress";
import { useStage } from "@/domain/user/hooks/useStage";
import { motion } from 'framer-motion';
import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";

export default function Stages() {
  const router = useRouter();
  const [selectedBlock, setSelectedBlock] = useState<number | null>(null);
  const audioRef = useRef<HTMLAudioElement>(null);
  useEffect(() => {
          const audio = audioRef.current;
          if (!audio) return;
  
          audio.volume = 0.15;
          audio.loop = true;
          const playPromise = audio.play();
          
          if (playPromise !== undefined) {
              playPromise.catch((error) => {
                  console.log("Autoplay bloqueado pelo navegador:", error);
                  const playOnInteraction = () => {
                      audio.play();
                      document.removeEventListener('click', playOnInteraction);
                  };
                  document.addEventListener('click', playOnInteraction);
              });
          }
          return () => {
            if (audioRef.current) {
                // Pausa o áudio.
                audioRef.current.pause(); 
                // Volta o tempo para 0 (opcional, mas bom para se for tocar de novo).
                audioRef.current.currentTime = 0;
            }
        };
      }, []);
  const {
    stages
  } = useStage();

  const {
    nickname,
    progress,
    updateProgress
  } = useProgress();
  
  const handleClickStart = () => {
    if (selectedBlock != null) {
      const screen = "/stage-" + selectedBlock
      router.push(screen);
    }
  }
  return (
    <main>
      <audio 
          ref={audioRef}
          src="/audios-stages/Main-Theme.mp3"
          loop
      />
      <motion.div initial={{ y: -200, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ type: "spring", stiffness: 50, damping: 15, delay: 0.5 }}
                className="flex min-h-screen flex-col items-center justify-center gap-16 p-4 sm:p-8 bg-[var(--brand-indigo-deep)] font-[var(--font-sans)]">
      <StageTitle className="mt-6" />

      <div
        className="
          flex flex-col sm:flex-row
          gap-4 sm:gap-6 md:gap-10 lg:gap-16 xl:gap-20
          items-center justify-center
          flex-1
          sm:mt-20
          mt-0
        "
      >
        {stages.map((stage) => (
          <NumberedBlock
            key={stage.number}
            number={stage.number}
            progress={progress}
            color={stage.number % 2 === 0 ? "red" : "blue"}
            className="font-bold"
            isSelected={selectedBlock === stage.number}
            onSelect={(n) => setSelectedBlock(n)}
          />
        ))}
      </div>

      {/* Botão no fim */}
      <div className="flex justify-center 
        mb-30
      ">
        <StartButton onClick={handleClickStart} />
      </div>
      </motion.div>
      <CloudDecorationsStages />
    </main>
  );
}
