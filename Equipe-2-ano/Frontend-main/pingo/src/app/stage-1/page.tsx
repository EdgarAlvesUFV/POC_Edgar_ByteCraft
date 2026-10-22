//
"use client";

import React, { useReducer, useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import StageOneScene from "@/components/decorations/StageOneScene";
import { Pause } from "@/components/ui/Pause";
import { Tip } from "@/components/ui/Tip";
import { PingoDialog } from "@/components/ui/PingoDialog";
import { InitialDialogComponent } from "@/components/game/InitialDialogComponent";
import { StageCompleteComponent } from "@/components/game/StageCompleteComponent";
import { useProgress } from "@/domain/user/hooks/useProgress";

// ENUMS E TIPOS
enum StageState {
    INITIAL_DIALOG = "INITIAL_DIALOG",
    INTRO_DIALOG_1 = "INTRO_DIALOG_1",
    INTRO_DIALOG_2 = "INTRO_DIALOG_2",
    TASK_1_PROMPT = "TASK_1_PROMPT",
    TASK_1_STEP_1_GRAB_CABLE = "TASK_1_STEP_1_GRAB_CABLE",
    TASK_1_STEP_2_PLUG_CABLE = "TASK_1_STEP_2_PLUG_CABLE",
    TASK_1_STEP_3_PRESS_TOWER = "TASK_1_STEP_3_PRESS_TOWER",
    TASK_2_PROMPT = "TASK_2_PROMPT",
    TASK_2_STEP_1_GRAB_CABLE = "TASK_2_STEP_1_GRAB_CABLE",
    TASK_2_STEP_2_PLUG_CABLE = "TASK_2_STEP_2_PLUG_CABLE",
    TASK_3_PROMPT = "TASK_3_PROMPT",
    TASK_3_STEP_1_PRESS_ROUTER = "TASK_3_STEP_1_PRESS_ROUTER",
    COMPLETED = "COMPLETED",
    SHOW_COMPLETION_SCREEN = "SHOW_COMPLETION_SCREEN",
}

type HintState =
    | "TASK_1_STEP_1_GRAB_CABLE"
    | "TASK_1_STEP_2_PLUG_CABLE"
    | "TASK_1_STEP_3_PRESS_TOWER"
    | "TASK_2_STEP_1_GRAB_CABLE"
    | "TASK_2_STEP_2_PLUG_CABLE"
    | "TASK_3_STEP_1_PRESS_ROUTER"
    | null;

enum ActionType {
    START_GAME = "START_GAME",
    GO_TO_STAGE_LIST = "GO_TO_STAGE_LIST",
    NEXT_DIALOG = "NEXT_DIALOG",

    CLICK_PC_CABLE = "CLICK_PC_CABLE",
    CLICK_RIGHT_OUTLET = "CLICK_RIGHT_OUTLET",
    CLICK_TOWER = "CLICK_TOWER",
    CLICK_ROUTER_CABLE = "CLICK_ROUTER_CABLE",
    CLICK_LEFT_OUTLET = "CLICK_LEFT_OUTLET",
    CLICK_ROUTER_DEVICE = "CLICK_ROUTER_DEVICE",
    CLICK_WRONG = "CLICK_WRONG",
}

interface GameState {
    stage: StageState;
    // Estado do PC
    isHoldingPCCable: boolean; // Segurando o cabo do PC
    isTowerPlugged: boolean; // Cabo do PC conectado na tomada
    isTowerOn: boolean; // Botão da Torre apertado
    isComputerOn: boolean; // Monitor ligado (igual a isTowerOn)
    // Estado do Roteador
    isHoldingRouterCable: boolean; // Segurando o cabo do roteador
    isRouterPlugged: boolean; // Cabo do roteador conectado
    isRouterOn: boolean; // Botão do roteador apertado

    dialogText: string[] | null;
    currentDialogIndex: number;
    currentSoundPath: string | null;
    textSpeed: number;
    soundDelay: number;
}

const DIALOGS = {
    INTRO_1: [
        "OLÁ! EU SOU O PINGO! TODA JORNADA NA INTERNET COMEÇA COM UMA /CONEXÃO/. VAMOS INICIAR A NOSSA?",
    ],
    INTRO_2: ["PRECISO QUE ME AJUDE A ENVIAR UMA MENSAGEM PELA /INTERNET/!"],

    TASK_1_PROMPT: [
        "PRIMEIRO, VAMOS LIGAR O *COMPUTADOR*. PEGUE O CABO DE ENERGIA!",
    ],
    TASK_1_STEP_2: ["ÓTIMO! AGORA CONECTE O CABO NA TOMADA DA PAREDE."],
    TASK_1_STEP_3: [
        "PERFEITO! O COMPUTADOR ESTÁ CONECTADO. AGORA, APERTE O BOTÃO PARA LIGAR!",
    ],
    TASK_2_PROMPT: [
        "AGORA VAMOS LIGAR UM APARELHO QUE MANDA INTERNET PARA O CELULAR, TABLET OU COMPUTADOR, O *ROTEADOR*!",
        "PEGUE O CABO DE ENERGIA DO *ROTEADOR*!",
    ],
    TASK_2_STEP_2: ["ISSO! CONECTE ESTE CABO NA OUTRA TOMADA."],

    TASK_3_PROMPT: ["O *ROTEADOR* ESTÁ LIGADO?", "VEJA AS LUZINHAS!"],

    COMPLETED: ["PARABÉNS! FASE 1 COMPLETA."],
    WRONG: ["OPA! TENTE DE NOVO.", "SIGA AS SETAS E AS DICAS DO PINGO."],
    WRONG_OUTLET: ["OPA! TENTE A OUTRA TOMADA."],
};

const createStageReducer =
    (hasPlayedBefore: boolean) =>
        (state: GameState, action: { type: ActionType }): GameState => {
            const isTaskActive = state.stage.includes("STEP");

            switch (action.type) {
                case ActionType.START_GAME:
                    if (hasPlayedBefore) {
                        return {
                            ...state,
                            stage: StageState.TASK_1_STEP_1_GRAB_CABLE,
                            dialogText: DIALOGS.TASK_1_PROMPT,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-1.m4a",
                            textSpeed: 100,
                        };
                    }
                    return {
                        ...state,
                        stage: StageState.INTRO_DIALOG_1,
                        dialogText: DIALOGS.INTRO_1,
                        currentDialogIndex: 0,
                        currentSoundPath: "/audios/falas-pingo/falas-doc/fase-1/fase-1-2.m4a",
                        textSpeed: 120,
                    };

                case ActionType.NEXT_DIALOG:
                    if (
                        state.dialogText
                        && state.currentDialogIndex < state.dialogText.length - 1
                    ) {
                        if (state.stage === StageState.TASK_2_PROMPT) {
                            return {
                                ...state,
                                currentDialogIndex: state.currentDialogIndex + 1,
                                currentSoundPath:
                                    "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-5.m4a",
                                textSpeed: 95,
                            };
                        } else if (state.stage === StageState.TASK_3_PROMPT || state.stage === StageState.TASK_3_STEP_1_PRESS_ROUTER) {
                            return {
                                ...state,
                                currentDialogIndex: state.currentDialogIndex + 1,
                                currentSoundPath:
                                    "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-8.m4a",
                                textSpeed: 120,
                            };
                        } else if (state.dialogText == DIALOGS.WRONG) {
                            return {
                                ...state,
                                currentDialogIndex: state.currentDialogIndex + 1,
                                currentSoundPath:
                                    "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-setas.m4a",
                                textSpeed: 120,
                            };
                        }
                        return { ...state, currentDialogIndex: state.currentDialogIndex + 1 };
                    }

                    if (state.stage === StageState.INTRO_DIALOG_1)
                        return {
                            ...state,
                            stage: StageState.INTRO_DIALOG_2,
                            dialogText: DIALOGS.INTRO_2,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/falas-doc/fase-1/fase-1-3.m4a",
                            textSpeed: 75,
                        };
                    if (state.stage === StageState.INTRO_DIALOG_2)
                        return {
                            ...state,
                            stage: StageState.TASK_1_PROMPT,
                            dialogText: DIALOGS.TASK_1_PROMPT,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-1.m4a",
                            textSpeed: 100,
                        };
                    if (state.stage === StageState.TASK_1_PROMPT)
                        return {
                            ...state,
                            stage: StageState.TASK_1_STEP_1_GRAB_CABLE,
                            dialogText: null,
                            currentDialogIndex: 0,
                        };
                    if (state.stage === StageState.TASK_2_PROMPT)
                        return {
                            ...state,
                            stage: StageState.TASK_2_STEP_1_GRAB_CABLE,
                            dialogText: null,
                            currentDialogIndex: 0,
                        };
                    if (state.stage === StageState.TASK_3_PROMPT)
                        return {
                            ...state,
                            stage: StageState.TASK_3_STEP_1_PRESS_ROUTER,
                            dialogText: null,
                            currentDialogIndex: 0,
                        };
                    if (state.stage === StageState.COMPLETED) {
                        return {
                            ...state,
                            stage: StageState.SHOW_COMPLETION_SCREEN,
                            dialogText: null,
                            currentDialogIndex: 0,
                        };
                    }
                    if (isTaskActive)
                        return { ...state, dialogText: null, currentDialogIndex: 0 };

                    return state;

                case ActionType.CLICK_PC_CABLE:
                    if (
                        state.stage === StageState.TASK_1_STEP_1_GRAB_CABLE ||
                        state.stage === StageState.TASK_1_PROMPT
                    ) {
                        return {
                            ...state,
                            isHoldingPCCable: true,
                            stage: StageState.TASK_1_STEP_2_PLUG_CABLE,
                            dialogText: DIALOGS.TASK_1_STEP_2,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-2.m4a",
                            textSpeed: 105,
                        };
                    }
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_RIGHT_OUTLET:
                    if (state.stage === StageState.TASK_1_STEP_2_PLUG_CABLE) {
                        return {
                            ...state,
                            isHoldingPCCable: false,
                            isTowerPlugged: true,
                            stage: StageState.TASK_1_STEP_3_PRESS_TOWER,
                            dialogText: DIALOGS.TASK_1_STEP_3,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-3.m4a",
                        };
                    }
                    if (state.stage === StageState.TASK_2_STEP_2_PLUG_CABLE)
                        return {
                            ...state,
                            dialogText: DIALOGS.WRONG_OUTLET,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-tomada.m4a",
                        };
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_TOWER:
                    if (state.stage === StageState.TASK_1_STEP_3_PRESS_TOWER) {
                        return {
                            ...state,
                            isTowerOn: true,
                            isComputerOn: true,
                            stage: StageState.TASK_2_PROMPT,
                            dialogText: DIALOGS.TASK_2_PROMPT,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-4.m4a",
                            textSpeed: 85,
                        };
                    }
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_ROUTER_CABLE:
                    if (
                        state.stage === StageState.TASK_2_STEP_1_GRAB_CABLE ||
                        state.stage === StageState.TASK_2_PROMPT
                    ) {
                        return {
                            ...state,
                            isHoldingRouterCable: true,
                            stage: StageState.TASK_2_STEP_2_PLUG_CABLE,
                            dialogText: DIALOGS.TASK_2_STEP_2,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-6.m4a",
                            textSpeed: 115,
                        };
                    }
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_LEFT_OUTLET:
                    if (state.stage === StageState.TASK_2_STEP_2_PLUG_CABLE) {
                        if (hasPlayedBefore) {
                            return {
                                ...state,
                                isHoldingRouterCable: false,
                                isRouterPlugged: true,
                                stage: StageState.TASK_3_STEP_1_PRESS_ROUTER, // Estado da AÇÃO
                                dialogText: DIALOGS.TASK_3_PROMPT, // Diálogo do PROMPT
                                currentDialogIndex: 0,
                                currentSoundPath:
                                    "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-7.m4a",
                            };
                        }
                        // Primeira vez
                        return {
                            ...state,
                            isHoldingRouterCable: false,
                            isRouterPlugged: true,
                            stage: StageState.TASK_3_PROMPT,
                            dialogText: DIALOGS.TASK_3_PROMPT,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-7.m4a",
                        };
                    }
                    if (state.stage === StageState.TASK_1_STEP_2_PLUG_CABLE)
                        return {
                            ...state,
                            dialogText: DIALOGS.WRONG_OUTLET,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-tomada.m4a",
                            currentDialogIndex: 0,
                        };
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_ROUTER_DEVICE:
                    if (state.stage === StageState.TASK_3_STEP_1_PRESS_ROUTER) {
                        return {
                            ...state,
                            isRouterOn: true,
                            stage: StageState.COMPLETED,
                            dialogText: DIALOGS.COMPLETED,
                            currentDialogIndex: 0,
                            currentSoundPath:
                                "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-9.m4a",
                            textSpeed: 120,
                        };
                    }
                    return createStageReducer(hasPlayedBefore)(state, {
                        type: ActionType.CLICK_WRONG,
                    });

                case ActionType.CLICK_WRONG:
                    if (isTaskActive && !state.dialogText) {
                        return {
                            ...state,
                            dialogText: DIALOGS.WRONG,
                            currentDialogIndex: 0,
                            textSpeed: 120,
                            currentSoundPath: "/audios/falas-pingo/escrito-no-jogo/fase-1/fase-1-tente.m4a",
                        };
                    }
                    return state;

                case ActionType.GO_TO_STAGE_LIST:
                    return state;

                default:
                    return state;
            }
        };

const initialGameState: GameState = {
    stage: StageState.INITIAL_DIALOG,
    isHoldingPCCable: false,
    isTowerPlugged: false,
    isTowerOn: false,
    isComputerOn: false,
    isHoldingRouterCable: false,
    isRouterPlugged: false,
    isRouterOn: false,
    dialogText: null,
    currentDialogIndex: 0,
    currentSoundPath: null,
    textSpeed: 100,
    soundDelay: 0,
};

export default function StageOnePage() {
    const router = useRouter();
    const { nickname, progress, updateProgress } = useProgress();

    const hasPlayedBefore = progress > 1;

    const [infoScreen, setInfoScreen] = useState<"computer" | "modem" | null>(null);

    const stageReducer = createStageReducer(hasPlayedBefore);
    const [state, dispatch] = useReducer(stageReducer, initialGameState);
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

    useEffect(() => {
        if (state.stage === StageState.SHOW_COMPLETION_SCREEN) {
            if (progress === 1) {
                updateProgress(2);
            }
        }
    }, [state.stage, progress, updateProgress]);

    const handleInitialStart = () => dispatch({ type: ActionType.START_GAME });
    const handleNextDialog = () => dispatch({ type: ActionType.NEXT_DIALOG });
    const handleExitGame = () => {
        if (audioRef.current) {
            audioRef.current.pause();
            audioRef.current.currentTime = 0;
        }
        router.push("/stage-list");
    };

    const handleDialogClose = () => {
        dispatch({ type: ActionType.NEXT_DIALOG });
    };

    const handleSceneClick = (component: string): void => {
        if (
            state.dialogText &&
            (state.dialogText === DIALOGS.WRONG ||
                state.dialogText === DIALOGS.WRONG_OUTLET)
        ) {
            dispatch({ type: ActionType.NEXT_DIALOG });
            return;
        }

        switch (component) {
            case "pc_cable":
                return dispatch({ type: ActionType.CLICK_PC_CABLE });
            case "right_outlet":
                return dispatch({ type: ActionType.CLICK_RIGHT_OUTLET });
            case "tower":
                return dispatch({ type: ActionType.CLICK_TOWER });
            case "router_cable":
                return dispatch({ type: ActionType.CLICK_ROUTER_CABLE });
            case "left_outlet":
                return dispatch({ type: ActionType.CLICK_LEFT_OUTLET });
            case "router_device":
                return dispatch({ type: ActionType.CLICK_ROUTER_DEVICE });
            default:
                return dispatch({ type: ActionType.CLICK_WRONG });
        }
    };

    const getActiveHint = (): HintState => {
        if (state.stage === StageState.TASK_1_PROMPT)
            return "TASK_1_STEP_1_GRAB_CABLE";
        if (state.stage === StageState.TASK_2_PROMPT)
            return "TASK_2_STEP_1_GRAB_CABLE";
        if (state.stage === StageState.TASK_3_PROMPT)
            return "TASK_3_STEP_1_PRESS_ROUTER";

        switch (state.stage) {
            case StageState.TASK_1_STEP_1_GRAB_CABLE:
            case StageState.TASK_1_STEP_2_PLUG_CABLE:
            case StageState.TASK_1_STEP_3_PRESS_TOWER:
            case StageState.TASK_2_STEP_1_GRAB_CABLE:
            case StageState.TASK_2_STEP_2_PLUG_CABLE:
            case StageState.TASK_3_STEP_1_PRESS_ROUTER:
                return state.stage;
            default:
                return null;
        }
    };

    const currentText = state.dialogText
        ? state.dialogText[state.currentDialogIndex]
        : null;

    return (
        <main className="relative w-screen h-screen bg-[var(--brand-indigo-deep)] overflow-hidden">
            <audio
                ref={audioRef}
                src="/audios-stages/Level-1-Soundtrack.mp3"
                loop
                preload="auto"
            />
            <StageOneScene
                onClickPC_Cable={() => handleSceneClick("pc_cable")}
                onClickRightOutlet={() => handleSceneClick("right_outlet")}
                onClickTower={() => handleSceneClick("tower")}
                onClickRouterCable={() => handleSceneClick("router_cable")}
                onClickLeftOutlet={() => handleSceneClick("left_outlet")}
                onClickRouterDevice={() => handleSceneClick("router_device")}
                onWrongClick={() => handleSceneClick("wrong_click")}
                isTowerPlugged={state.isTowerPlugged}
                isTowerOn={state.isTowerOn}
                isComputerOn={state.isComputerOn}
                isRouterPlugged={state.isRouterPlugged}
                isRouterOn={state.isRouterOn}
                isHoldingPCCable={state.isHoldingPCCable}
                isHoldingRouterCable={state.isHoldingRouterCable}
                activeHint={getActiveHint()}
                infoScreen={infoScreen}
                setInfoScreen={setInfoScreen}
            />

            <Pause className="top-4 left-4 z-[100] pointer-events-auto" />
            <Tip
                className="top-4 right-4 z-[100] pointer-events-auto"
                onClick={() => dispatch({ type: ActionType.CLICK_WRONG })}
            />

            {state.stage === StageState.INITIAL_DIALOG && (
                <InitialDialogComponent
                    onStart={handleInitialStart}
                    onExit={handleExitGame}
                />
            )}

            {currentText && state.stage !== StageState.INITIAL_DIALOG && !infoScreen && (
                <PingoDialog
                    key={`${state.stage}-${state.currentDialogIndex}-${currentText}`}
                    text={currentText}
                    onClose={handleNextDialog}
                    isBlocked={!hasPlayedBefore}
                    delay={0}
                    soundPath={state.currentSoundPath}
                    soundDelay={state.soundDelay}
                    textSpeed={state.textSpeed}
                />
            )}

            {state.stage === StageState.SHOW_COMPLETION_SCREEN && (
                <StageCompleteComponent
                    onPlayNext={() => router.push("/stage-2")}
                    onRestart={() => window.location.reload()}
                    onExit={handleExitGame}
                    stageText="FASE 1 COMPLETA!"
                />
            )}
        </main>
    );
}