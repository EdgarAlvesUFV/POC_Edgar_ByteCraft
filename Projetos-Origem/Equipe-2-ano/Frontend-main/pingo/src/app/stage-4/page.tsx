"use client";

import React, {
    useState,
    useReducer,
    useEffect,
    useCallback,
    useRef,
} from "react";
import { useRouter } from "next/navigation";

import { useProgress } from "@/domain/user/hooks/useProgress";

import StageFourScene from "@/components/decorations/StageFourScene";

import {
    HUD,
    TipDialog,
    DifficultySelect,
} from "@/components/game/StageFourUI";
import { StageFourComplete } from "@/components/game/StageFourComplete";
import { PingoDialog } from "@/components/ui/PingoDialog";
import {
    GameplayComponent,
    DifficultyConfig,
} from "@/components/game/GameplayComponent";
import { createScore, updateScore } from "@/domain/user/service/scoreService";
import { ScoreUpdateRequest, ScoreCreateRequest } from "@/domain/user/types";

// --- TIPOS ---
type GameStage =
    | "SELECT_DIFFICULTY"
    | "TUTORIAL"
    | "PLAYING"
    | "GAME_OVER"
    | "COMPLETED";

type TutorialStage =
    | "DIALOG_1_PATH"
    | "DIALOG_2_NOISES"
    | "DIALOG_3_HIT_TUTORIAL"
    | "DIALOG_4_SHINE_TUTORIAL"
    | "READY_FOR_DIFFICULTY_SELECT";

interface GameState {
    tutorialStage: TutorialStage;
    lives: number;
    score: number;
    currentSoundPath: string | null;
    textSpeed: number;
    soundDelay: number;
}

type GameAction = { type: "NEXT_TUTORIAL_STEP" } | { type: "RESET_TUTORIAL" };

const DIALOG_TEXTS: Record<TutorialStage, string> = {
    DIALOG_1_PATH:
        "AGORA QUE TEMOS NOSSO CAMINHO DEFINIDO, É HORA DE SEGUIRMOS ELE!",
    DIALOG_2_NOISES:
        "OH NÃO! TEM *RUÍDOS* NO CAMINHO! ELES NOS ATRAPALHAM A CHEGAR NO NOSSO DESTINO",
    DIALOG_3_HIT_TUTORIAL:
        "AO BATER EM UM RUÍDO, *PERDEMOS* PARTE DA NOSSA MENSAGEM! SENDO ASSIM, *DESVIE* DE TODOS ELES.",
    DIALOG_4_SHINE_TUTORIAL:
        "GUIE A MENSAGEM E COLETE OS *PONTOS* DURANTE O TRAJETO!",
    READY_FOR_DIFFICULTY_SELECT: "",
};

const initialGameState: GameState = {
    tutorialStage: "DIALOG_1_PATH",
    lives: 3,
    score: 0,
    currentSoundPath: "audios/falas-pingo/falas-doc/fase-4/fase-4-1.m4a",
    textSpeed: 70,
    soundDelay: 0,
};

// --- REDUCER ---
function gameReducer(state: GameState, action: GameAction): GameState {
    switch (action.type) {
        case "RESET_TUTORIAL":
            return initialGameState;
        case "NEXT_TUTORIAL_STEP":
            switch (state.tutorialStage) {
                case "DIALOG_1_PATH":
                    return {
                        ...state,
                        tutorialStage: "DIALOG_2_NOISES",
                        currentSoundPath:
                            "audios/falas-pingo/falas-doc/fase-4/fase-4-2.m4a",
                        textSpeed: 90,
                    };
                case "DIALOG_2_NOISES":
                    return {
                        ...state,
                        tutorialStage: "DIALOG_3_HIT_TUTORIAL",
                        currentSoundPath:
                            "audios/falas-pingo/falas-doc/fase-4/fase-4-3.m4a",
                        textSpeed: 80,
                    };
                case "DIALOG_3_HIT_TUTORIAL":
                    return {
                        ...state,
                        tutorialStage: "DIALOG_4_SHINE_TUTORIAL",
                        currentSoundPath:
                            "audios/falas-pingo/falas-doc/fase-4/fase-4-4.m4a",
                        textSpeed: 80,
                        lives: state.lives - 1,
                    };
                case "DIALOG_4_SHINE_TUTORIAL":
                    return {
                        ...state,
                        tutorialStage: "READY_FOR_DIFFICULTY_SELECT",
                        score: state.score + 2,
                    };
                default:
                    return state;
            }
        default:
            return state;
    }
}

// --- CONFIGURAÇÕES DE DIFICULDADE ---
const difficultySettings: Record<string, DifficultyConfig> = {
    easy: {
        gameSpeed: 1.6,
        playerSpeed: 1,
        noiseAmount: 2,
        pointAmount: 3,
        winTime: 60000,
    },
    medium: {
        gameSpeed: 1.8,
        playerSpeed: 1,
        noiseAmount: 3,
        pointAmount: 2,
        winTime: 60000,
    },
    hard: {
        gameSpeed: 2.0,
        playerSpeed: 1,
        noiseAmount: 4,
        pointAmount: 2,
        winTime: 60000,
    },
};

const fetchDifficultyConfig = async (
    level: "easy" | "medium" | "hard"
): Promise<DifficultyConfig> => {
    return Promise.resolve(difficultySettings[level]);
};

const INITIAL_GAME_LIVES = 3;

// --- COMPONENTE PRINCIPAL ---
export default function StageFourPage() {
    const router = useRouter();
    const { progress, updateProgress, userId } = useProgress();

    // Estados do Jogo
    const [gameStage, setGameStage] = useState<GameStage>("SELECT_DIFFICULTY");
    const [tutorialState, dispatch] = useReducer(gameReducer, initialGameState);

    // Estados de UI/Controle
    const [isTipOpen, setIsTipOpen] = useState(false);
    const [isPaused, setIsPaused] = useState(false);

    // Configurações da partida
    const [selectedDifficulty, setSelectedDifficulty] = useState<DifficultyConfig>(difficultySettings.easy);
    const [gameplayLives, setGameplayLives] = useState(INITIAL_GAME_LIVES);
    const [gameplayScore, setGameplayScore] = useState(0);
    const [remainingTime, setRemainingTime] = useState(difficultySettings.easy.winTime);

    // Ref do Audio
    const audioRef = useRef<HTMLAudioElement>(null);

    // --- EFEITOS DE AUDIO ---
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

    // Pausa o áudio de fundo se entrar na tela de Game Over ou Vitória
    useEffect(() => {
        if ((gameStage === "COMPLETED" || gameStage === "GAME_OVER") && audioRef.current) {
            audioRef.current.pause();
        }
    }, [gameStage]);

    // --- LÓGICA DE TRANSIÇÃO DO TUTORIAL ---
    useEffect(() => {
        if (
            tutorialState.tutorialStage === "READY_FOR_DIFFICULTY_SELECT" &&
            gameStage === "TUTORIAL"
        ) {
            setGameStage("PLAYING");
        }
    }, [tutorialState.tutorialStage, gameStage]);

    // --- GAME OVER ---
    useEffect(() => {
        if (gameStage === "PLAYING" && gameplayLives <= 0) {
            setGameStage("GAME_OVER");
        }
    }, [gameplayLives, gameStage]);

    // --- HANDLERS ---
    const handleNextDialog = () => {
        dispatch({ type: "NEXT_TUTORIAL_STEP" });
    };

    const handleSelectDifficulty = async (
        difficulty: "easy" | "medium" | "hard"
    ) => {
        try {
            const config = await fetchDifficultyConfig(difficulty);
            setSelectedDifficulty(config);
            setGameplayLives(INITIAL_GAME_LIVES);
            setGameplayScore(0);
            setRemainingTime(config.winTime);
            dispatch({ type: "RESET_TUTORIAL" });
            setGameStage("TUTORIAL");
        } catch (error) {
            console.error("Falha ao carregar configuração de dificuldade:", error);
        }
    };

    const handleGameWon = useCallback(async () => {
        // 1. Atualizar Score no Backend
        if (userId > 0) {
            const STAGE_ID = 4;
            const scoreData: ScoreUpdateRequest = {
                userId: userId,
                stageId: STAGE_ID,
                points: gameplayScore,
            };

            try {
                await updateScore(scoreData);
                console.log("Score atualizado com sucesso.");
            } catch (error) {
                console.warn(
                    "Score não encontrado para atualização. Tentando criar novo registro..."
                );
                try {
                    const createData: ScoreCreateRequest = {
                        userId: userId,
                        stageId: STAGE_ID,
                    };
                    await createScore(createData);
                    await updateScore(scoreData);
                    console.log("Score criado e atualizado com sucesso.");
                } catch (createError) {
                    console.error("Erro crítico ao salvar score:", createError);
                }
            }
        }

        setGameStage("COMPLETED");
    }, [gameplayScore, userId]);

    const handleGameRestart = useCallback(() => {
        setGameplayLives(INITIAL_GAME_LIVES);
        setGameplayScore(0);
        setRemainingTime(selectedDifficulty.winTime);
        setGameStage("PLAYING");
        // Reinicia o áudio se ele estiver pausado
        if (audioRef.current) {
            audioRef.current.currentTime = 0;
            audioRef.current.play().catch(e => console.log("Erro ao reiniciar áudio:", e));
        }
    }, [selectedDifficulty]);

    const handleExit = useCallback(() => {
        if (audioRef.current) {
            audioRef.current.pause();
            audioRef.current.currentTime = 0;
        }
        router.push("/stage-list");
    }, [router]);

    const handleCredits = useCallback(() => {
        if (audioRef.current) {
            audioRef.current.pause();
            audioRef.current.currentTime = 0;
        }
        router.push("/credits");
    }, [router]);

    // --- RENDERIZACAO AUXILIAR ---
    const currentDialogText = DIALOG_TEXTS[tutorialState.tutorialStage];
    const displayLives = gameStage === "TUTORIAL" ? tutorialState.lives : gameplayLives;
    const displayScore = gameStage === "TUTORIAL" ? tutorialState.score : gameplayScore;

    return (
        <main
            className="relative h-screen w-screen overflow-hidden bg-cover bg-center"
            style={{ backgroundImage: "url('/image/stage-4/background.png')" }}
        >
            <audio
                ref={audioRef}
                src="/audios-stages/Level-4-Soundtrack.mp3"
                loop
                preload="auto"
            />

            <HUD
                lives={displayLives}
                score={displayScore}
                remainingTime={remainingTime}
                onTipClick={() => {
                    setIsTipOpen(true);
                    setIsPaused(true);
                }}
                onPauseOpen={() => setIsPaused(true)}
                onPauseClose={() => setIsPaused(false)}
            />

            {isTipOpen && (
                <TipDialog
                    onClose={() => {
                        setIsTipOpen(false);
                        setIsPaused(false);
                    }}
                />
            )}

            {gameStage === "SELECT_DIFFICULTY" && (
                <DifficultySelect
                    onSelect={handleSelectDifficulty}
                    onExit={handleExit}
                />
            )}

            {gameStage === "TUTORIAL" && (
                <>
                    <StageFourScene currentStage={tutorialState.tutorialStage} />
                    {/* AQUI ESTÁ A CORREÇÃO: !isPaused garante que o PingoDialog suma se tiver pause/dica */}
                    {currentDialogText && !isPaused && (
                        <PingoDialog
                            key={tutorialState.tutorialStage}
                            text={currentDialogText}
                            onClose={handleNextDialog}
                            soundPath={tutorialState.currentSoundPath}
                            soundDelay={tutorialState.soundDelay}
                            textSpeed={tutorialState.textSpeed}
                            delay={500}
                        />
                    )}
                </>
            )}

            {gameStage === "PLAYING" && (
                <GameplayComponent
                    difficultyConfig={selectedDifficulty}
                    setLives={setGameplayLives}
                    setScore={setGameplayScore}
                    setRemainingTime={setRemainingTime}
                    onGameWon={handleGameWon}
                    isPaused={isPaused}
                />
            )}

            {gameStage === "COMPLETED" && (
                <StageFourComplete
                    variant="victory"
                    score={displayScore}
                    onContinue={handleCredits}
                    onRestart={handleGameRestart}
                    onExit={handleExit}
                />
            )}

            {gameStage === "GAME_OVER" && (
                <StageFourComplete
                    variant="defeat"
                    score={displayScore}
                    onContinue={handleExit}
                    onRestart={handleGameRestart}
                    onExit={handleExit}
                />
            )}
        </main>
    );
}