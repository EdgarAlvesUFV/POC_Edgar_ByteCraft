// src/context/SoundContext.tsx
import { createContext, useContext, useRef, useState, useEffect, ReactNode } from "react";
import backgroundMusic from "../assets/sounds/Bytecraft_background_music.mp3";

interface SoundContextType {
  isMuted: boolean;
  toggleMute: () => void;
  playSound: (src: string, volume?: number) => void;
}

const SoundContext = createContext<SoundContextType>({
  isMuted: false,
  toggleMute: () => {},
  playSound: () => {},
});

export function SoundProvider({ children }: { children: ReactNode }) {
  const [isMuted, setIsMuted] = useState(() => {
    // Persiste entre páginas
    return localStorage.getItem("isMuted") === "true";
  });

  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    audio.loop = true;
    audio.volume = isMuted ? 0 : 0.5;

    // Tenta tocar — se bloqueado, espera interação do usuário
    const playMusic = () => {
      if (!isMuted) {
        audio.play().catch(() => {
          console.log("🔇 Autoplay bloqueado até interação.");
        });
      }
      window.removeEventListener("click", playMusic);
    };

    audio.play().catch(() => {
      window.addEventListener("click", playMusic);
    });
  }, [isMuted]);

  const toggleMute = () => {
    setIsMuted((prev) => {
      localStorage.setItem("isMuted", String(!prev));
      return !prev;
    });
  };

  const playSound = (src: string, volume = 0.5) => {
    if (isMuted) return;
    const audio = new Audio(src);
    audio.volume = volume;
    audio.play().catch(() => {});
  };

  return (
    <SoundContext.Provider value={{ isMuted, toggleMute, playSound }}>
      <audio ref={audioRef} src={backgroundMusic} />
      {children}
    </SoundContext.Provider>
  );
}

export const useSoundContext = () => useContext(SoundContext);
