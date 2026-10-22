"use client";

import React, { useState, useEffect, useRef, useCallback } from "react";
import Image from "next/image";
import { motion } from "framer-motion";

export interface DifficultyConfig {
    gameSpeed: number;
    playerSpeed: number;
    noiseAmount: number;
    pointAmount: number;
    winTime: number;
}

interface GameplayProps {
    difficultyConfig: DifficultyConfig;
    setLives: React.Dispatch<React.SetStateAction<number>>;
    setScore: React.Dispatch<React.SetStateAction<number>>;
    setRemainingTime: React.Dispatch<React.SetStateAction<number>>;
    onGameWon: () => void;
    isPaused: boolean;
}

interface GameEntity {
    id: number;
    type: "noise" | "point";
    x: number;
    y: number;
    ref: React.RefObject<HTMLDivElement | null>;
}

let entityIdCounter = 0;

// --- CONSTANTES ---
const PLAYER_X_POSITION_VW = 20;
const PLAYER_WIDTH_VW = 10;
const PLAYER_HEIGHT_VH = 10;

const POINT_WIDTH_VW = 12;
const POINT_HEIGHT_VH = 12;
const NOISE_WIDTH_VW = 15;
const NOISE_HEIGHT_VH = 15;

const PLAYER_Y_CLAMP_MIN = 20;
const PLAYER_Y_CLAMP_MAX = 90;

// --- ASSETS ---
const EntityAssetMap = {
    "noise-1": "/image/stage-4/noiseRed.svg",
    "noise-2": "/image/stage-4/noiseBlue.svg",
    "noise-3": "/image/stage-4/noisePurple.svg",
    "noise-4": "/image/stage-4/noiseYellow.svg",
    point: "/image/stage-4/shine.svg",
};

// --- COMPONENTE DE ENTIDADE ---
const EntityComponent: React.FC<{ entity: GameEntity }> = ({ entity }) => {
    const asset =
        entity.type === "point"
            ? EntityAssetMap.point
            : EntityAssetMap[
                `noise-${(entity.id % 4) + 1}` as keyof typeof EntityAssetMap
                ];

    const isNoise = entity.type === "noise";
    const styleWidth = isNoise ? NOISE_WIDTH_VW : POINT_WIDTH_VW;
    const styleHeight = isNoise ? NOISE_HEIGHT_VH : POINT_HEIGHT_VH;

    return (
        <div
            ref={entity.ref}
            className="absolute z-20 pointer-events-none"
            style={{
                left: `${entity.x}vw`,
                top: `${entity.y}vh`,
                width: `${styleWidth}vw`,
                height: `${styleHeight}vh`,
            }}
        >
            <Image src={asset} alt={entity.type} fill className="object-contain" />
        </div>
    );
};

// --- GAMEPLAY PRINCIPAL ---
export const GameplayComponent: React.FC<GameplayProps> = ({
                                                               difficultyConfig,
                                                               setLives,
                                                               setScore,
                                                               setRemainingTime,
                                                               onGameWon,
                                                               isPaused,
                                                           }) => {
    const [playerY, setPlayerY] = useState(50);
    const playerYRef = useRef(50);

    const [entities, setEntities] = useState<GameEntity[]>([]);
    const entitiesRef = useRef<GameEntity[]>([]);

    // --- REFS DO JOGO ---
    const playerRef = useRef<HTMLDivElement>(null);
    const gameLoopRef = useRef<number>(0);
    const lastTimeRef = useRef<number>(0);
    const moveDirection = useRef<"up" | "down" | "none">("none");
    const isGameOver = useRef(false);
    const isPausedRef = useRef(isPaused);

    const gameTimer = useRef(0);
    const collidedEntityIds = useRef<Set<number>>(new Set());

    // --- REFS ESTABILIZADAS ---
    const onGameWonRef = useRef(onGameWon);
    const setLivesRef = useRef(setLives);
    const setScoreRef = useRef(setScore);
    const setRemainingTimeRef = useRef(setRemainingTime);

    useEffect(() => {
        isPausedRef.current = isPaused;
    }, [isPaused]);

    // --- FUNÇÃO DE SPAWN SIMPLIFICADA ---
    const getNewSpawnPosition = useCallback(() => {
        const newX = 110 + Math.random() * 70;
        const newY = Math.random() * 80 + 10;
        return { x: newX, y: newY };
    }, []);

    const spawnNoise = useCallback((): GameEntity => {
        const { x, y } = getNewSpawnPosition();
        return {
            id: entityIdCounter++,
            type: "noise",
            x,
            y,
            ref: React.createRef<HTMLDivElement>(),
        };
    }, [getNewSpawnPosition]);

    const spawnPoint = useCallback((): GameEntity => {
        const { x, y } = getNewSpawnPosition();
        return {
            id: entityIdCounter++,
            type: "point",
            x,
            y,
            ref: React.createRef<HTMLDivElement>(),
        };
    }, [getNewSpawnPosition]);

    // --- SYNC DE PROPS ---
    useEffect(() => {
        onGameWonRef.current = onGameWon;
    }, [onGameWon]);

    useEffect(() => {
        setLivesRef.current = setLives;
    }, [setLives]);

    useEffect(() => {
        setScoreRef.current = setScore;
    }, [setScore]);

    useEffect(() => {
        setRemainingTimeRef.current = setRemainingTime;
    }, [setRemainingTime]);

    // --- COLISÃO ---
    const checkCollisions = useCallback(() => {
        if (!playerRef.current) return;

        const playerRect = playerRef.current.getBoundingClientRect();

        for (const entity of entitiesRef.current) {
            if (!entity.ref.current || collidedEntityIds.current.has(entity.id))
                continue;

            const entityRect = entity.ref.current.getBoundingClientRect();

            const PADDING_PERCENT = 0.15;
            const playerPaddingH = playerRect.width * PADDING_PERCENT;
            const playerPaddingV = playerRect.height * PADDING_PERCENT;
            const entityPaddingH = entityRect.width * PADDING_PERCENT;
            const entityPaddingV = entityRect.height * PADDING_PERCENT;

            const isColliding = !(
                playerRect.right - playerPaddingH < entityRect.left + entityPaddingH ||
                playerRect.left + playerPaddingH > entityRect.right - entityPaddingH ||
                playerRect.bottom - playerPaddingV < entityRect.top + entityPaddingV ||
                playerRect.top + playerPaddingV > entityRect.bottom - entityPaddingV
            );

            if (isColliding) {
                collidedEntityIds.current.add(entity.id);

                if (entity.type === "noise") {
                    setLivesRef.current((prev) => prev - 1);
                } else if (entity.type === "point") {
                    setScoreRef.current((prev) => prev + 10);
                }

                if (entity.ref.current) {
                    entity.ref.current.style.display = "none";
                }
            }
        }
    }, []);

    // --- GAME LOOP ---
    useEffect(() => {
        isGameOver.current = false;
        lastTimeRef.current = 0;
        gameTimer.current = 0;
        collidedEntityIds.current.clear();

        playerYRef.current = 50;
        setPlayerY(50);

        const initialNoise = Array.from(
            { length: difficultyConfig.noiseAmount },
            () => spawnNoise()
        );
        const initialPoints = Array.from(
            { length: difficultyConfig.pointAmount },
            () => spawnPoint()
        );

        const initialEntities = [...initialNoise, ...initialPoints].sort(
            () => Math.random() - 0.5
        );

        entitiesRef.current = initialEntities;
        setEntities(initialEntities);

        const gameLoop = (currentTime: number) => {
            if (isGameOver.current) return;

            if (isPausedRef.current) {
                lastTimeRef.current = 0;
                gameLoopRef.current = requestAnimationFrame(gameLoop);
                return;
            }

            if (lastTimeRef.current === 0) {
                lastTimeRef.current = currentTime;
                gameLoopRef.current = requestAnimationFrame(gameLoop);
                return;
            }

            const deltaTime = currentTime - lastTimeRef.current;
            lastTimeRef.current = currentTime;

            if (deltaTime > 100) {
                gameLoopRef.current = requestAnimationFrame(gameLoop);
                return;
            }

            const frameFactor = deltaTime / 16.67;
            const { playerSpeed, gameSpeed, winTime } = difficultyConfig;

            gameTimer.current += deltaTime;
            const remaining = Math.max(0, winTime - gameTimer.current);
            setRemainingTimeRef.current(remaining);

            if (gameTimer.current >= winTime) {
                isGameOver.current = true;
                onGameWonRef.current();
                return;
            }

            let nextY = playerYRef.current;
            if (moveDirection.current === "up") nextY -= playerSpeed * frameFactor;
            else if (moveDirection.current === "down")
                nextY += playerSpeed * frameFactor;

            nextY = Math.max(PLAYER_Y_CLAMP_MIN, Math.min(PLAYER_Y_CLAMP_MAX, nextY));

            playerYRef.current = nextY;
            setPlayerY(nextY);

            const prevEntities = entitiesRef.current;
            const newEntities: GameEntity[] = [];

            const currentNoiseCount = prevEntities.filter(
                (e) => e.type === "noise" && e.x > -15
            ).length;
            const currentPointCount = prevEntities.filter(
                (e) => e.type === "point" && e.x > -15
            ).length;

            if (currentNoiseCount < difficultyConfig.noiseAmount)
                newEntities.push(spawnNoise());
            if (currentPointCount < difficultyConfig.pointAmount)
                newEntities.push(spawnPoint());

            const movedEntities = prevEntities
                .map((entity) => {
                    const newX = entity.x - gameSpeed * frameFactor * 0.2;
                    if (newX > -15 && !collidedEntityIds.current.has(entity.id)) {
                        return { ...entity, x: newX };
                    }
                    return null;
                })
                .filter((e): e is GameEntity => e !== null);

            const all = [...movedEntities, ...newEntities];
            entitiesRef.current = all;
            setEntities(all);

            checkCollisions();

            if (!isGameOver.current) {
                gameLoopRef.current = requestAnimationFrame(gameLoop);
            }
        };

        gameLoopRef.current = requestAnimationFrame(gameLoop);

        return () => {
            isGameOver.current = true;
            cancelAnimationFrame(gameLoopRef.current);
            lastTimeRef.current = 0;
            gameTimer.current = 0;
            collidedEntityIds.current.clear();
        };
    }, [
        difficultyConfig,
        spawnNoise,
        spawnPoint,
        getNewSpawnPosition,
        checkCollisions,
    ]);

    // --- CONTROLES ---
    const handleInteractionStart = (y: number) => {
        if (isGameOver.current || isPausedRef.current) return;
        moveDirection.current = y < window.innerHeight / 2 ? "up" : "down";
    };

    const handleInteractionEnd = () => {
        moveDirection.current = "none";
    };

    const handleCursorMove = (e: React.MouseEvent) => {
        if (isPausedRef.current) {
            e.currentTarget.classList.remove("cursor-up", "cursor-down");
            return;
        }
        if (e.clientY < window.innerHeight / 2) {
            e.currentTarget.classList.add("cursor-up");
            e.currentTarget.classList.remove("cursor-down");
        } else {
            e.currentTarget.classList.add("cursor-down");
            e.currentTarget.classList.remove("cursor-up");
        }
    };

    const handleCursorLeave = (e: React.MouseEvent) => {
        e.currentTarget.classList.remove("cursor-up", "cursor-down");
    };

    return (
        <div
            className="absolute inset-0 z-[40] cursor-pointer"
            onMouseDown={(e) => handleInteractionStart(e.clientY)}
            onMouseUp={handleInteractionEnd}
            onMouseLeave={(e) => {
                handleInteractionEnd();
                handleCursorLeave(e);
            }}
            onMouseMove={handleCursorMove}
            onTouchStart={(e) => handleInteractionStart(e.touches[0].clientY)}
            onTouchEnd={handleInteractionEnd}
            onTouchCancel={handleInteractionEnd}
        >
            <motion.div
                ref={playerRef}
                className="absolute z-30 pointer-events-none"
                style={{
                    left: `${PLAYER_X_POSITION_VW}vw`,
                    top: `${playerY}vh`,
                    width: `${PLAYER_WIDTH_VW}vw`,
                    height: `${PLAYER_HEIGHT_VH}vh`,
                    translateY: "-50%",
                }}
                animate={{ scale: [1, 1.05, 1] }}
                transition={{ duration: 1.5, repeat: Infinity, ease: "easeInOut" }}
            >
                <Image
                    src="/image/stage-4/envelope.svg"
                    alt="Jogador"
                    fill
                    className="object-contain drop-shadow-lg"
                    priority
                />
            </motion.div>

            {entities.map((entity) => (
                <EntityComponent key={entity.id} entity={entity} />
            ))}
        </div>
    );
};