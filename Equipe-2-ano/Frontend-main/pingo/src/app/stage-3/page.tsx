"use client";
import StageThreeScene from "@/components/decorations/StageThreeScene";
import { StageCompleteComponent } from "@/components/game/StageCompleteComponent";
import { Pause } from "@/components/ui/Pause";
import { PingoDialog } from "@/components/ui/PingoDialog";
import { usePhaseSetup } from "@/domain/user/hooks/usePhaseSetup";
import { useProgress } from "@/domain/user/hooks/useProgress";
import { delay } from "framer-motion";
import { useRouter } from "next/navigation";
import { useEffect, useReducer, useRef } from "react";

// --- ENUMS E TIPOS ---
export enum StageState {
  START_GAME = "START_GAME",
  PLAYING = "PLAYING",
  SUBMITING = "SUBMITING",
  COMPLETED = "COMPLETED",
  SHOW_COMPLETION_SCREEN = "SHOW_COMPLETION_SCREEN",
}

enum ActionType {
  NEXT_DIALOG = "NEXT_DIALOG",
  NOT_CONNECTED = "NOT_CONNECTED",
  CLICK_DISTANCE = "CLICK_DISTANCE",
  FIRST_NOT_MODEM = "FIRST_NOT_MODEM",
  END_NOT_MODEM = "END_NOT_MODEM",
  NOT_SWITCH = "NOT_SWITCH",
  ON_DEFFECT = "ON_DEFFECT",
  BACK_TO_PLAYING = "BACK_TO_PLAYING",
  SUBMIT = "SUBMIT",
  COMPLETE = "COMPLETE",
}

interface GameState {
  stage: StageState;
  dialogText: string | null;
  currentDialogIndex: number;
  delay: number;
  isDialogOpen: boolean;
  currentSoundPath: string | null;
  textSpeed: number;
  soundDelay: number;
}

const DIALOGS = {
  COMPLETED: "PARABÉNS! FASE 3 COMPLETA.",
  INITIAL_DIALOG: "VAMOS CRIAR O MELHOR CAMINHO PARA SUA MENSAGEM CHEGAR AO DESTINO!",
  INITIAL_DIALOG_2: "BASTA CLICAR EM *DOIS APARELHOS PRÓXIMOS* E A CONEXÃO SERÁ FEITA!",
  NOT_CONNECTED: "PARA QUE A MENSAGEM CONSIGA SEGUIR UM CAMINHO, ELE PRECISA EXISTIR! *INTERLIGUE* OS APARELHOS!",
  CLICK_DISTANCE: "EI! NÃO DÁ PARA LIGAR OS APARELHOS TÃO *LONGE* ASSIM!",
  FIRST_NOT_MODEM: "OPA! PARA A COMUNICAÇÃO FUNCIONAR, A MENSAGEM *PRECISA* ESTAR LIGADA EM UM *ROTEADOR*!",
  ON_DEFFECT: "OH NÃO! HÁ UM APARELHO COM DEFEITO NO CAMINHO E A MENSAGEM SE *PERDEU*...",
  END_NOT_MODEM: "OPA! PARA A COMUNICAÇÃO FUNCIONAR, O *COMPUTADOR* DE DESTINO PRECISA RECEBER A MENSAGEM POR UM *ROTEADOR*!",
  NOT_SWITCH: "IXI... O *SWITCH* NÃO ESTÁ NO CAMINHO PARA ORGANIZAR A ROTA DA MENSAGEM!",
};

const stageReducer = (
  state: GameState,
  action: { type: ActionType }
): GameState => {
  switch (action.type) {
    case ActionType.NEXT_DIALOG:
      if (state.currentDialogIndex === 0) {
        return {
          ...state,
          dialogText: DIALOGS.INITIAL_DIALOG_2,
          stage: StageState.PLAYING,
          textSpeed: 75,
          delay: 0,
          currentDialogIndex: state.currentDialogIndex + 1,
          currentSoundPath: "audios/falas-pingo/escrito-no-jogo/fase-3/fase-3-2.m4a",
          isDialogOpen: false,
        };
      }
      return {
        ...state,
        isDialogOpen: false,
      };

    case ActionType.NOT_CONNECTED:
      return {
        ...state,
        dialogText: DIALOGS.NOT_CONNECTED,
        currentDialogIndex: state.currentDialogIndex + 1,
        delay: 0,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-6.m4a",
        textSpeed: 60,
        isDialogOpen: true,
      };
    case ActionType.CLICK_DISTANCE:
      return {
        ...state,
        dialogText: DIALOGS.CLICK_DISTANCE,
        currentDialogIndex: state.currentDialogIndex + 1,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-9.m4a",
        delay: 0,
        textSpeed: 65,
        isDialogOpen: true,
      };

    case ActionType.FIRST_NOT_MODEM:
      return {
        ...state,
        dialogText: DIALOGS.FIRST_NOT_MODEM,
        currentDialogIndex: state.currentDialogIndex + 1,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-7.m4a",
        delay: 0,
        textSpeed: 65,
        isDialogOpen: true,
      };

    case ActionType.END_NOT_MODEM:
      return {
        ...state,
        dialogText: DIALOGS.END_NOT_MODEM,
        currentDialogIndex: state.currentDialogIndex + 1,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-8.m4a",
        delay: 0,
        textSpeed: 65,
        isDialogOpen: true,
      };

    case ActionType.ON_DEFFECT:
      return {
        ...state,
        dialogText: DIALOGS.ON_DEFFECT,
        currentDialogIndex: state.currentDialogIndex + 1,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-11.m4a",
        delay: 0,
        textSpeed: 70,
        isDialogOpen: true,
      };

    case ActionType.BACK_TO_PLAYING:
      if (state.stage != StageState.PLAYING) {
        return {
          ...state,
          stage: StageState.PLAYING,
        };
      }
      return state;
    case ActionType.NOT_SWITCH:
      return {
        ...state,
        dialogText: DIALOGS.NOT_SWITCH,
        currentDialogIndex: state.currentDialogIndex + 1,
        currentSoundPath: "audios/falas-pingo/falas-doc/fase-3/fase-3-10.m4a",
        delay: 0,
        textSpeed: 90,
        isDialogOpen: true,
      };
    case ActionType.SUBMIT:
      return {
        ...state,
        dialogText: null,
        stage: StageState.SUBMITING,
        isDialogOpen: false,
      };
    case ActionType.COMPLETE:
      return {
        ...state,
        stage: StageState.SHOW_COMPLETION_SCREEN,
      };

    default:
      return state;
  }
};

const initialGameState: GameState = {
  stage: StageState.START_GAME,
  dialogText: DIALOGS.INITIAL_DIALOG,
  delay: 1000,
  currentDialogIndex: 0,
  isDialogOpen: false,
  currentSoundPath: "audios/falas-pingo/escrito-no-jogo/fase-3/fase-3-1.m4a",
  textSpeed: 70,
  soundDelay: 0,
};

export default function StageThreePage() {
  const [state, dispatch] = useReducer(stageReducer, initialGameState);
  const router = useRouter();

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
        audioRef.current.pause();
        audioRef.current.currentTime = 0;
      }
    };
  }, []);

  const { devices, correctPaths } = usePhaseSetup();
  const { nickname, progress, updateProgress } = useProgress();

  useEffect(() => {
    if (state.stage === StageState.SHOW_COMPLETION_SCREEN) {
      if (progress == 3) {
        updateProgress(4);
      }
    }
  }, [state.stage]);

  const handleAction = (action: string): void => {
    switch (action) {
      case "NOT_CONNECTED":
        dispatch({ type: ActionType.NOT_CONNECTED });
        break;
      case "CLICK_DISTANCE":
        dispatch({ type: ActionType.CLICK_DISTANCE });
        break;
      case "FIRST_NOT_MODEM":
        dispatch({ type: ActionType.FIRST_NOT_MODEM });
        break;
      case "END_NOT_MODEM":
        dispatch({ type: ActionType.END_NOT_MODEM });
        break;
      case "NOT_SWITCH":
        dispatch({ type: ActionType.NOT_SWITCH });
        break;
      case "BACK_TO_PLAYING":
        dispatch({ type: ActionType.BACK_TO_PLAYING });
        break;
      case "ON_DEFFECT":
        dispatch({ type: ActionType.ON_DEFFECT });
        break;
      case "SUBMIT":
        dispatch({ type: ActionType.SUBMIT });
        break;
      case "COMPLETE":
        dispatch({ type: ActionType.COMPLETE });
        break;
    }
  };

  const handleCloseDialog = async () => {
    dispatch({ type: ActionType.NEXT_DIALOG });
    state.isDialogOpen = false;
  };
  const currentText = state.dialogText ? state.dialogText : null;
  return (
    <main className="relative w-screen h-screen bg-gradient-to-t from-[var(--brand-indigo-darker)] to-[var(--brand-violet)]">
      <audio
        ref={audioRef}
        src="/audios-stages/Level-3-Soundtrack.mp3"
        loop
        preload="auto"
      />
      <StageThreeScene
        devices={devices || []}
        correctPaths={correctPaths || [[[]]]}
        state={state.stage}
        isPaused={state.isDialogOpen}
        onNotConnected={handleAction}
        onClickDistance={handleAction}
        onFirstNotModem={handleAction}
        onDeffect={handleAction}
        onEndNotModem={handleAction}
        notSwitch={handleAction}
        onBackPlaying={handleAction}
        onSubmit={handleAction}
        onComplete={handleAction}
      />

      <Pause className="top-4 left-4 z-[100] pointer-events-auto" />

      {currentText && state.stage != StageState.SHOW_COMPLETION_SCREEN && (
        <PingoDialog
          key={`${state.currentDialogIndex}-${currentText}`}
          text={currentText}
          delay={state.delay}
          isBlocked={true}
          soundPath={state.currentSoundPath}
          soundDelay={state.soundDelay}
          textSpeed={state.textSpeed}
          onClose={handleCloseDialog}
        />
      )}

      {state.stage === StageState.SHOW_COMPLETION_SCREEN && (
        <StageCompleteComponent
          onPlayNext={() => router.push("/stage-4")}
          onRestart={() => window.location.reload()}
          onExit={() => router.push("/stage-list")}
          stageText="CONSEGUIMOS DEFINIR O CAMINHO!"
        />
      )}
    </main>
  );
}
