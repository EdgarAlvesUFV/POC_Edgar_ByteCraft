'use client';

import Image from 'next/image';
import { AnimatePresence, motion } from 'framer-motion';
import React from 'react';

type TutorialStage =
    | 'DIALOG_1_PATH'
    | 'DIALOG_2_NOISES'
    | 'DIALOG_3_HIT_TUTORIAL'
    | 'DIALOG_4_SHINE_TUTORIAL'
    | 'READY_FOR_DIFFICULTY_SELECT';

// --- Componentes de Entidade (Helpers) ---
const Player: React.FC<{ x: number; y: number }> = ({ x, y }) => (
    <motion.div
        className="absolute z-30 h-24 w-24"
        style={{
            left: `${x}%`,
            top: `${y}%`,
            translateX: '-50%',
            translateY: '-50%',
        }}
    >
        <Image
            src="/image/stage-4/envelope.svg"
            alt="Jogador"
            fill
            className="object-contain drop-shadow-lg"
        />
    </motion.div>
);

const Destiny: React.FC = () => (
    <motion.div
        className="absolute z-20 flex items-center justify-center"
        style={{
            left: '35%',
            top: '20%',
            transform: 'translate(-50%, -50%)',
        }}
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        transition={{ delay: 0.5, type: 'spring' }}
    >
        <div
            className="relative"
            style={{
                width: 'clamp(150px, 25vw, 600px)',
                height: 'clamp(150px, 25vw, 600px)',
            }}
        >
            <Image
                src="/image/stage-4/destiny.svg"
                alt="Destino"
                fill
                className="object-contain drop-shadow-lg"
                priority
            />
        </div>
    </motion.div>
);

const ENTITY_ASSETS = {
    'noise-red': '/image/stage-4/noiseRed.svg',
    'noise-blue': '/image/stage-4/noiseBlue.svg',
    'noise-purple': '/image/stage-4/noisePurple.svg',
    'noise-yellow': '/image/stage-4/noiseYellow.svg',
};

const Entity: React.FC<{
    x: number;
    y: number;
    type: keyof typeof ENTITY_ASSETS;
}> = ({ x, y, type }) => {
    const imgSrc = ENTITY_ASSETS[type];
    return (
        <div
            className="absolute h-20 w-20"
            style={{
                left: `${x}%`,
                top: `${y}%`,
                transform: 'translate(-50%, -50%)',
            }}
        >
            <Image src={imgSrc} alt={type} fill />
        </div>
    );
};

// --- Props do Componente ---
interface StageFourSceneProps {
    currentStage: TutorialStage;
}

// --- Componente Principal da Cena ---
export default function StageFourScene({ currentStage }: StageFourSceneProps) {
    const playerX = 25;

    return (
        <div className="pointer-events-none absolute inset-0 z-10 overflow-hidden">
            <AnimatePresence mode="wait">
                <motion.div
                    key={currentStage}
                    className="absolute inset-0"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.5 }}
                >
                    {currentStage === 'DIALOG_1_PATH' && (
                        <>
                            <Destiny />
                        </>
                    )}

                    {currentStage === 'DIALOG_2_NOISES' && (
                        <>
                            <Player x={playerX} y={50} />
                            <Entity x={45} y={30} type="noise-red" />
                            <Entity x={55} y={70} type="noise-blue" />
                            <Entity x={70} y={40} type="noise-purple" />
                            <Entity x={80} y={60} type="noise-yellow" />
                        </>
                    )}

                    {currentStage === 'DIALOG_3_HIT_TUTORIAL' && (
                        <>
                            <Player x={playerX} y={50} />
                            <motion.div
                                className="absolute z-20 h-20 w-20"
                                style={{ top: '50%', transform: 'translateY(-50%)' }}
                                initial={{ left: '110%' }}
                                animate={{ left: `${playerX}%` }}
                                transition={{ duration: 3, ease: 'easeInOut', delay: 0.5 }}
                            >
                                <Image src="/image/stage-4/noiseRed.svg" alt="Ruído" fill />
                            </motion.div>
                        </>
                    )}

                    {currentStage === 'DIALOG_4_SHINE_TUTORIAL' && (
                        <>
                            <Player x={playerX} y={50} />
                            <motion.div
                                className="absolute z-20 h-16 w-16"
                                style={{ top: '50%', transform: 'translateY(-50%)' }}
                                initial={{ left: '110%' }}
                                animate={{ left: `${playerX}%` }}
                                transition={{ duration: 3, ease: 'easeInOut', delay: 0.5 }}
                            >
                                <Image
                                    src="/image/stage-4/shine.svg"
                                    alt="Pontos"
                                    width={120}
                                    height={120}
                                    className="h-12 w-12 md:h-24 md:w-24 scale-110 md:scale-125"
                                />
                            </motion.div>
                        </>
                    )}

                    {currentStage === 'READY_FOR_DIFFICULTY_SELECT' && (
                        <Player x={playerX} y={50} />
                    )}
                </motion.div>
            </AnimatePresence>
        </div>
    );
};