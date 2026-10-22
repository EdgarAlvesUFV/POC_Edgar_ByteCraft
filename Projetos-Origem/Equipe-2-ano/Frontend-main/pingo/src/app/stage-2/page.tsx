"use client";
import { StageCompleteComponent } from "@/components/game/StageCompleteComponent";
import { AppDesktop } from "@/components/ui/AppDesktop";
import { AppScreen } from "@/components/ui/AppScreen";
import { ButtomSystem } from "@/components/ui/ButtomSystem";
import { Clock } from "@/components/ui/Clock";
import { Pause } from "@/components/ui/Pause";
import { PingoDialog } from "@/components/ui/PingoDialog";
import { WifiScreen } from "@/components/ui/WifiScreen";
import { WifiSimbol } from "@/components/ui/WifiSimbol";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { useProgress } from "@/domain/user/hooks/useProgress";

const ArrowHint: React.FC<{ className?: string }> = ({ className = "" }) => (
  <div className={`absolute z-50 animate-bounce ${className}`}>
    <Image
      src="/image/stage-1/arrow.svg"
      alt="Seta de Dica"
      fill
      className="object-contain"
    />
  </div>
);

const StageTwoPage: React.FC = () => {
  const router = useRouter();

  const [showAppScreen, setShowAppScreen] = useState(false);
  const [showWifiScreen, setShowWifiScreen] = useState(false);
  const [showCompleteScreen, setShowCompleteScreen] = useState(false);
  const [isWifiOn, setIsWifiOn] = useState(false);
  const [wifiHintVisible, setWifiHintVisible] = useState(false);

  //Diálogos do Pingo:
  const [showInitDialog, setShowInitDialog] = useState(true);
  const [showPingoDialogIncorrectApp, setShowPingoDialogIncorrectApp] =
    useState(false);
  const [showPingoFinalDialog, setShowPingoFinalDialog] = useState(false);
  let isBlocked = true;
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
          document.removeEventListener("click", playOnInteraction);
        };
        document.addEventListener("click", playOnInteraction);
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
  const { nickname, progress, updateProgress } = useProgress();
  if (progress > 2) {
    isBlocked = false;
  }

  useEffect(() => {
    if (showCompleteScreen) {
      if (progress == 2) {
        updateProgress(3);
      }
    }
  }, [showCompleteScreen]);

  const handleClickIncorrectApp = () => {
    setShowInitDialog(false);
    if (!showAppScreen) setShowPingoDialogIncorrectApp(true);
  };

  const appScreenVisible = () => {
    setShowInitDialog(false);
    setShowPingoDialogIncorrectApp(false);
    setShowAppScreen(true);
  };
  const appScreenNotVisible = () => {
    setShowAppScreen(false);
  };

  const handleConnectionNeeded = () => {
    setWifiHintVisible(true);
  };

  const handleSendMessage = () => {
    setShowWifiScreen(false);
    setShowPingoFinalDialog(true);
  };
  const handleWifiConnect = () => {
    setIsWifiOn(true);
  };

  const numPastes = 7;
  return (
    <main className="relative min-h-screen  bg-[var(--brand-indigo-darker)]">
      <audio
        ref={audioRef}
        src="/audios-stages/Level-2-Soundtrack.mp3"
        loop
        preload="auto"
      />
      <div
        className="
        fixed 
        text-[20%] 
        left-[50%] 
        -translate-x-[50%] 
        bottom-[50vh] -translate-y-[-50%] 
        md:bottom-[13%] md:-translate-y-[0%] 
        aspect-[1168/812] 
        min-w-[400px] sm:min-w-[500px] md:min-w-[700px] lg:min-w-[900px]
        z-19"
      >
        <Image
          src="/image/stage-2/monitor.svg"
          alt="monitor"
          fill
          className="object-contain"
          onClick={() => setShowWifiScreen(false)}
        />

        <div
          className="
            absolute top-[10%] left-[6%]
            flex flex-col items-center justify-center
            gap-[2%]
            h-[60%] w-[4.5%] max-w-[50px]
          "
        >
          {Array.from({ length: numPastes }).map((_, i) => (
            <AppDesktop
              key={i}
              src="/image/stage-2/paste.svg"
              onClick={handleClickIncorrectApp}
            />
          ))}

          <AppDesktop
            src="/image/stage-2/app-message.svg"
            onClick={appScreenVisible}
          />
          <AppDesktop
            src="/image/stage-2/internet-simbol.svg"
            onClick={handleClickIncorrectApp}
          />
        </div>

        <ButtomSystem className="bottom-[19.5%] left-[5%]" />

        {wifiHintVisible && !showWifiScreen && !isWifiOn && (
          <ArrowHint className="bottom-[23%] right-[9%] w-[23px] sm:w-[30px] md:w-[41px] lg:w-[55px] h-auto aspect-square" />
        )}
        <WifiSimbol
          className="bottom-[18.5%] right-[10%]"
          isOn={isWifiOn}
          onClick={() => {
            if (wifiHintVisible) setShowWifiScreen(!showWifiScreen);
          }}
        />

        <Clock
          className="
          bottom-[20%]
          right-[5%]"
        />

        {showAppScreen && (
          <AppScreen
            isBlocked={isBlocked}
            isWifiOn={isWifiOn}
            onClose={appScreenNotVisible}
            onConnectionNeeded={handleConnectionNeeded}
            onSendMessage={handleSendMessage}
            onClick={() => setShowWifiScreen(false)}
          />
        )}
        {showWifiScreen && wifiHintVisible && (
          <WifiScreen
            isConnected={isWifiOn}
            onWifiConnect={handleWifiConnect}
          />
        )}
      </div>

      <div
        className="
        absolute 
        h-20
        bottom-[50vh] 
        -translate-y-[-240%] 
        sm:-translate-y-[-280%] 
        md:bottom-4 md:-translate-y-0
        md:h-[18vh] 
        left-0 w-full 
        bg-[var(--brand-white)] 
        z-10"
      />

      {showInitDialog && (
        <PingoDialog
          className="z-30"
          text="VAMOS ABRIR O *APLICATIVO DE MENSAGENS*!"
          isBlocked={isBlocked}
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-1.m4a"}
          textSpeed={100}
          delay={2000}
        />
      )}
      {showPingoDialogIncorrectApp && (
        <PingoDialog
          className="z-30"
          text="*APLICATIVO INCORRETO*! TENTE NOVAMENTE!"
          onClose={() => setShowPingoDialogIncorrectApp(false)}
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-2.m4a"}
          textSpeed={100}
          isBlocked={isBlocked}
        />
      )}
      {isWifiOn && !showPingoFinalDialog && (
        <PingoDialog
          className="z-30"
          text="LEGAL!
            AGORA ESTAMOS CONECTADOS À /INTERNET/ E PODEMOS MANDAR NOSSA *MENSAGEM*!"
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-6.m4a"}
          onClose={() => setShowPingoDialogIncorrectApp(false)}
          isBlocked={isBlocked}
          textSpeed={80}
        />
      )}
      {showPingoFinalDialog && (
        <PingoDialog
          className="z-30"
          text="LEGAL!
           NOSSA _MENSAGEM_ FOI ENVIADA! O PRÓXIMO PASSO É AJUDAR A _MENSAGEM_ A CHEGAR ATÉ O NOSSO AMIGO!"
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-7.m4a"}
          textSpeed={90}
          onClose={() => setShowCompleteScreen(true)}
          isBlocked={isBlocked}
        />
      )}
      {showCompleteScreen && (
        <StageCompleteComponent
          onPlayNext={() => router.push("/stage-3")}
          onRestart={() => window.location.reload()}
          onExit={() => router.push("/stage-list")}
          stageText="CONSEGUIMOS ENVIAR A MENSAGEM."
        />
      )}
      <Pause />
    </main>
  );
};

export default StageTwoPage;
