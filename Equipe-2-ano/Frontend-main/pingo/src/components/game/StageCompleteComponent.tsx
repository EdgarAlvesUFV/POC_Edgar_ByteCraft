import React from 'react';
import { createPortal } from 'react-dom';
import Lottie from 'react-lottie-player';
import { ButtonPauseScreen } from '@/components/ui/ButtonPauseScreen';

interface StageCompleteComponentProps {
    onPlayNext: () => void;
    onRestart: () => void;
    onExit: () => void;
    stageText: string;
}

export const StageCompleteComponent = ({ onPlayNext, onRestart, onExit, stageText }: StageCompleteComponentProps) => {
    return createPortal(
        <div
            className="
              fixed inset-0
              bg-[var(--brand-gray-dark)]/40
              z-[9998]
              flex items-center justify-center
            "
        >
            <Lottie
                path='/animation/stage-1/Confetti.json'
                play
                loop={false}
                style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    zIndex: 9999,
                    pointerEvents: 'none',
                }}
            />

            <div
                className="
                 relative
                 z-[10000]
                 bg-white
                 rounded-[30px]
                 shadow-lg
                 font-bold
                 flex flex-col
                 items-center
                 justify-center
                 gap-6
                 w-[90%]
                 max-w-[500px]
                 py-10
                 px-6
                 sm:max-w-[500px]
                 md:max-w-[500px]
                 lg:max-w-[550px]
               "
                onClick={(e) => e.stopPropagation()}
            >
                <p className="
                    text-center
                    text-[1.3rem]
                    sm:text-[1.6rem]
                    md:text-[2rem]
                    lg:text-[2.8rem]
                    text-[var(--brand-midnight)]
                ">
                    PARABÉNS!
                </p>

                <p className="
                    text-[0.9rem]
                    sm:text-[1.1rem]
                    md:text-[1.2rem]
                    lg:text-[1.5rem]
                    text-center
                    font-medium
                    text-[var(--brand-midnight)]
                    px-4
                ">
                    {stageText}
                </p>

                <div className="flex flex-col items-center gap-4 w-full">
                    <ButtonPauseScreen src="/image/common/play.svg" color="cyan" onClick={onPlayNext}>
                        JOGAR
                    </ButtonPauseScreen>
                    <ButtonPauseScreen src="/image/common/restart.svg" color="violet" onClick={onRestart}>
                        REINICIAR
                    </ButtonPauseScreen>
                    <ButtonPauseScreen src="/image/common/arrow_back.svg" color="magenta" onClick={onExit}>
                        VOLTAR
                    </ButtonPauseScreen>
                </div>
            </div>
        </div>,
        document.body
    );
};
