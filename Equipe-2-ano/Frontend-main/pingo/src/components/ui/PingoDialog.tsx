'use client'
import Image from "next/image";
import Lottie from 'react-lottie-player';
import React, { useState, useEffect, useRef, useMemo } from "react";
import { createPortal } from "react-dom";

interface PingoDialogProps {
  className?: string;
  isBlocked?: boolean;
  soundPath?: string | null;
  text: string;
  soundDelay?: number;
  textSpeed?: number;
  delay?: number;
  onClose?: () => void; 
}

// Função auxiliar (mantida igual)
const parseTextSegments = (fullText: string) => {
  const regex = /(\*[^*]+\*|-[^-]+-|\/[^/]+\/|_[^_]+_)/g;
  return fullText.split(regex).map((part) => {
    if (part.startsWith('*') && part.endsWith('*')) {
      return { text: part.slice(1, -1), className: "text-[var(--brand-magenta)]" };
    } 
    else if (part.startsWith('-') && part.endsWith('-')) {
      return { text: part.slice(1, -1), className: "text-[var(--brand-orange)]" };
    } 
    else if (part.startsWith('/') && part.endsWith('/')) {
      return { text: part.slice(1, -1), className: "text-[var(--brand-cyan-dark)]" };
    } 
    else if (part.startsWith('_') && part.endsWith('_')) {
      return { text: part.slice(1, -1), className: "text-[var(--brand-violet)]" };
    } 
    else {
      return { text: part, className: "" };
    }
  });
};

export const PingoDialog = ({
  className = "",
  isBlocked = false,
  soundPath = "",
  text = "",
  soundDelay = 0,
  textSpeed = 50,
  delay = 0,
  onClose
}: PingoDialogProps) => {
  const [displayedCharCount, setDisplayedCharCount] = useState(0);
  const [visible, setVisible] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const timeoutRef = useRef<NodeJS.Timeout | null>(null);
  const typingTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const parsedSegments = useMemo(() => parseTextSegments(text), [text]);
  
  const fullCleanText = useMemo(() => {
    return parsedSegments.map(s => s.text).join('');
  }, [parsedSegments]);

  const totalCleanLength = fullCleanText.length;

  useEffect(() => {
    const timer = setTimeout(() => setVisible(true), delay);
    return () => clearTimeout(timer);
  }, [delay]);

  const handleClose = () => {
    setVisible(false);
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
    if (onClose) onClose();
  };

  const handlePlaySound = async () => {
    if (!soundPath) return;
    try {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
      if (timeoutRef.current) clearTimeout(timeoutRef.current);

      const audio = new Audio(soundPath);
      audioRef.current = audio;

      timeoutRef.current = setTimeout(async () => {
        try {
          await audio.play();
        } catch (err) {
          console.error("Erro ao som:", err);
        }
      }, soundDelay);
    } catch (error) {
      console.error("Erro ao carregar som:", error);
    }
  };

  useEffect(() => {
    if (!visible) return;

    if (displayedCharCount < totalCleanLength) {
      typingTimeoutRef.current = setTimeout(() => {
        setDisplayedCharCount((prev) => prev + 1);
      }, textSpeed);
    }

    return () => {
      if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
    };
  }, [visible, displayedCharCount, totalCleanLength, textSpeed]);

  useEffect(() => {
    if (visible) {
      handlePlaySound();
      setDisplayedCharCount(0);
    }
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
      if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
    };
  }, [visible]);

  if(!visible) return null;
  // --- LÓGICA DE RENDERIZAÇÃO AJUSTADA ---
  const renderAnimatedText = () => {
    // Verifica se a digitação acabou completamente
    const isFinished = displayedCharCount >= totalCleanLength;

    const currentText = fullCleanText.slice(0, displayedCharCount);
    const lastChar = currentText.slice(-1);
    
    let activeWordStartIndex = -1;
    if (lastChar !== ' ' && displayedCharCount > 0) {
      activeWordStartIndex = currentText.lastIndexOf(' ') + 1;
    }

    let charsLimit = displayedCharCount;
    let globalIndexOffset = 0;

    return parsedSegments.map((segment, i) => {
      if (charsLimit <= 0) return null;

      const lengthToShow = Math.min(segment.text.length, charsLimit);
      const textSlice = segment.text.slice(0, lengthToShow);
      
      const segmentStart = globalIndexOffset;
      const segmentEnd = globalIndexOffset + lengthToShow;

      let content;
      if (!isFinished && activeWordStartIndex !== -1 && segmentEnd > activeWordStartIndex) {
        const localActiveStart = Math.max(0, activeWordStartIndex - segmentStart);

        const normalPart = textSlice.slice(0, localActiveStart);
        const activePart = textSlice.slice(localActiveStart);

        content = (
          <>
            {normalPart}
            <span className="text-[var(--brand-sky)]">{activePart}</span>
          </>
        );
      } else {
        content = textSlice;
      }

      charsLimit -= lengthToShow;
      globalIndexOffset += segment.text.length;

      return (
        <span key={i} className={segment.className}>
          {content}
        </span>
      );
    });
  };

  return createPortal(
    <>
      { isBlocked && (<div 
        className="absolute w-full h-full inset-0 z-[9998] pointer-events-auto"
        onClick={(e) => e.stopPropagation()}
      />)}
      
      <div className={`
        fixed left-[50%] -translate-x-[50%] bottom-10
        w-[90vw] h-[100px] 
        sm:w-[85vw] sm:h-[120px] 
        md:w-[85vw] md:h-[150px]
        lg:w-[50vw] lg:h-[180px] lg:min-w-[950px]
        z-[9999] pointer-events-auto
        ${className}
      `}>
        <div className="fixed top-[50%] -translate-y-[48%] left-[-9%] sm:left-[-7%] h-[180%] w-auto
          z-[10001]">
          <Lottie
            path="/animation/Pingo.json"
            play loop
            style={{ height: '100%', width: 'auto' }}
            className="object-contain"
          />
        </div>
        <div className="
          absolute w-full h-full
          rounded-md
          sm:rounded-lg
          md:rounded-2xl
          lg:rounded-3xl
          opacity-90 bg-[var(--brand-white)]
          z-[10000]
        ">
          <div className="
            fixed items-top justify-center
            w-[68%] left-[22%] sm:left-[22%] md:left-[22%] lg:left-[20%]
            h-[100%] pt-[4%] pb-[4%]
            font-bold text-[0.8rem] sm:text-[1rem] md:text-[1.2rem] lg:text-[1.5rem]
          ">
            {renderAnimatedText()}
          </div>

          <Image
            src="/image/common/sound.svg" alt="sound" width={20} height={20}
            onClick={handlePlaySound}
            className="fixed cursor-pointer top-[8%] right-[10px] h-[30%] w-auto transition-transform hover:scale-110 active:scale-95"
          />
          <Image
            src="/image/common/skip.svg" alt="skip" width={20} height={20}
            onClick={handleClose}
            className="fixed cursor-pointer bottom-[8%] right-[10px] h-[30%] w-auto transition-transform hover:scale-110 active:scale-95"
          />
          
        </div>
      </div>
    </>,
    document.body
  );
};
