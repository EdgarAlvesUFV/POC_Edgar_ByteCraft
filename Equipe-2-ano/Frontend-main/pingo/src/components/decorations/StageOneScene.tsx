'use client';
import React from 'react';
import Image from 'next/image';
import Lottie from 'react-lottie-player';
import { InfoScreen } from '../ui/InfoScreen';

// Tipos dos Estados
type HintState =
    | 'TASK_1_STEP_1_GRAB_CABLE'
    | 'TASK_1_STEP_2_PLUG_CABLE'
    | 'TASK_1_STEP_3_PRESS_TOWER'
    | 'TASK_2_STEP_1_GRAB_CABLE'
    | 'TASK_2_STEP_2_PLUG_CABLE'
    | 'TASK_3_STEP_1_PRESS_ROUTER'
    | null;

// Interface de Props
interface StageOneSceneProps {
    onClickPC_Cable: () => void;
    onClickRightOutlet: () => void;
    onClickTower: () => void;
    onClickRouterCable: () => void;
    onClickLeftOutlet: () => void;
    onClickRouterDevice: () => void;
    onWrongClick: () => void;

    isTowerPlugged: boolean;
    isTowerOn: boolean;
    isComputerOn: boolean;
    isRouterPlugged: boolean;
    isRouterOn: boolean;
    isHoldingPCCable: boolean;
    isHoldingRouterCable: boolean;

    activeHint: HintState;

    infoScreen: 'computer' | 'modem' | null;
    setInfoScreen: (type: 'computer' | 'modem' | null) => void;
}

// --- Imagens ---
const imagePaths = {
    window: '/image/stage-1/window.svg',
    table: '/image/stage-1/table.svg',
    cabinet: '/image/stage-1/cabinetOff.svg',
    cabinetOn: '/image/stage-1/cabinetOn.svg',
    chair: '/image/stage-1/chair.svg',
    monitorOff: '/image/stage-1/monitorOff.svg',
    monitorOn: '/image/stage-1/monitorOn.svg',
    router: '/image/stage-1/router.svg',
    outlet: '/image/stage-1/outlet.svg',
    floor: '/image/stage-1/floor.svg',
    routerSocket: '/image/stage-1/routerSocket.svg',
    routerSocketOn: '/image/stage-1/routerSocketOn.svg',
    cabinetSocket: '/image/stage-1/cabinetSocket.svg',
    cabinetSocketOn: '/image/stage-1/cabinetSocketOn.svg',
    arrowHint: '/image/stage-1/arrow.svg',
    infoIcon: '/image/stage-3/info.svg'
};


const ArrowHint: React.FC<{ className?: string }> = ({ className = "" }) => (
    <div className={`absolute z-50 animate-bounce ${className}`}>
        <Image
            src={imagePaths.arrowHint}
            alt="Seta de Dica"
            fill
            className="object-contain"
        />
    </div>
);

export default function StageOneScene({
                                          onClickPC_Cable,
                                          onClickRightOutlet,
                                          onClickTower,
                                          onClickRouterCable,
                                          onClickLeftOutlet,
                                          onClickRouterDevice,
                                          onWrongClick,
                                          isTowerPlugged,
                                          isTowerOn,
                                          isComputerOn,
                                          isRouterPlugged,
                                          isRouterOn,
                                          isHoldingPCCable,
                                          isHoldingRouterCable,
                                          activeHint,
                                          infoScreen,
                                          setInfoScreen
                                      }: StageOneSceneProps) {

    const monitorImage = isComputerOn ? imagePaths.monitorOn : imagePaths.monitorOff;
    const cabinetImage = isTowerOn ? imagePaths.cabinetOn : imagePaths.cabinet;
    const cabinetSocketImage = isTowerPlugged ? imagePaths.cabinetSocketOn : imagePaths.cabinetSocket;
    const routerSocketImage = isRouterPlugged ? imagePaths.routerSocketOn : imagePaths.routerSocket;

    return (
        <div
            className="relative w-full min-h-screen overflow-hidden"
            onClick={(e) => {
                if (e.target === e.currentTarget) onWrongClick();
            }}
        >
            {/* Chão */}
            <div className="absolute left-0 right-0 bottom-0 h-[15vh] md:h-[20vh] min-h-[100px] z-10">
                <img src={imagePaths.floor} alt="Floor" className="w-full h-full object-cover" />
            </div>

            {/* Janela */}
            <div className="absolute top-0 left-0 w-[30vw] md:w-[25vw] lg:w-[20vw] min-w-[100px] max-w-[250px] z-20">
                <img src={imagePaths.window} alt="Window" className="w-full h-auto" />
            </div>

            {/* Cena principal */}
            <div className="absolute left-1/2 -translate-x-1/2 bottom-[5vh] w-[95vw] md:w-[75vw] lg:w-[65vw] max-w-[1100px] z-20">
                <div className="relative max-w-full z-20">
                    <img src={imagePaths.table} alt="Desk" className="w-full h-auto" />

                    {/* --- ROTEADOR --- */}
                    {!isRouterPlugged && (
                        <div className="absolute left-[10%] bottom-[120%] w-[10%] min-w-[40px] max-w-[70px] z-30">
                            <button
                                onClick={(e) => { e.stopPropagation(); onClickLeftOutlet(); }}
                                className="relative w-full h-full transform transition-all duration-200 hover:scale-115"
                            >
                                <img src={imagePaths.outlet} alt="Left Outlet" className="w-full h-auto" />
                            </button>

                            {activeHint === 'TASK_2_STEP_2_PLUG_CABLE' && (
                                <ArrowHint className="bottom-[110%] left-1/2 -translate-x-1/2 w-[10vw] sm:w-[7vw] md:w-[5vw] lg:w-[4vw] aspect-square" />
                            )}
                        </div>
                    )}

                    {!isHoldingRouterCable && !isRouterPlugged && (
                        <div className="absolute left-[11%] bottom-[80%] w-[12%] min-w-[20px] z-10">
                            <button
                                onClick={(e) => { e.stopPropagation(); onClickRouterCable(); }}
                                className="relative w-full h-full transform transition-all duration-200 hover:scale-115"
                            >
                                <img src={routerSocketImage} alt="Router Socket" className="w-full h-auto" />
                            </button>

                            {activeHint === 'TASK_2_STEP_1_GRAB_CABLE' && (
                                <ArrowHint className="bottom-[60%] left-[30%] -translate-x-1/2 w-[9vw] sm:w-[7vw] md:w-[5vw] lg:w-[4vw] aspect-square" />
                            )}
                        </div>
                    )}

                    {isRouterPlugged && (
                        <div className="absolute left-[10%] bottom-[100%] w-[12%] min-w-[20px] z-10">
                            <img src={routerSocketImage} alt="Router Socket On" className="w-full h-auto" />
                        </div>
                    )}

                    {/* DISPOSITIVO ROTEADOR */}
                    <div className="absolute left-[20%] bottom-[95%] w-[15%] min-w-[55px] z-20 hover:scale-110 transition-transform">
                        <button
                            onClick={(e) => { e.stopPropagation(); onClickRouterDevice(); }}
                            className="relative w-full h-full"
                        >
                            {(isRouterPlugged && isRouterOn) ? (
                                <Lottie path='/animation/stage-1/RouterOn.json'  speed={3} play loop className="absolute w-[130%] h-auto top-[90%] left-1/2 -translate-x-1/2 -translate-y-[90%]" />
                            ) : (
                                <img src={imagePaths.router} alt="Router" className="w-full h-auto" />
                            )}
                        </button>

                        {/* ÍCONE DE INFO (ROTEADOR) */}
                        <Image
                            src={imagePaths.infoIcon}
                            alt="info modem"
                            width={30}
                            height={30}
                            className="absolute top-[-20px] right-[-20px] w-[50px] h-[50px] cursor-pointer hover:scale-110 z-30"
                            onClick={(e) => { e.stopPropagation(); setInfoScreen('modem'); }}
                        />

                        {activeHint === 'TASK_3_STEP_1_PRESS_ROUTER' && (
                            <ArrowHint className="bottom-[50%] left-1/2 -translate-x-1/2 w-[12vw] sm:w-[8vw] md:w-[6vw] lg:w-[4vw] aspect-square" />
                        )}
                    </div>

                    {/* --- PC --- */}
                    <div className="absolute left-1/2 -translate-x-1/2 bottom-[95%] w-[26%] min-w-[130px] z-20">
                        <img src={monitorImage} alt="Monitor" className="w-full h-auto" />
                    </div>

                    {!isTowerPlugged && (
                        <div className="absolute right-[10%] bottom-[120%] w-[10%] min-w-[40px] max-w-[70px] z-30">
                            <button
                                onClick={(e) => { e.stopPropagation(); onClickRightOutlet(); }}
                                className="relative w-full h-full transform transition-all duration-200 hover:scale-115"
                            >
                                <img src={imagePaths.outlet} alt="Right Outlet" className="w-full h-auto" />
                            </button>

                            {activeHint === 'TASK_1_STEP_2_PLUG_CABLE' && (
                                <ArrowHint className="bottom-[100%] left-1/2 -translate-x-1/2 w-[10vw] sm:w-[8vw] md:w-[6vw] lg:w-[4vw] aspect-square" />
                            )}
                        </div>
                    )}

                    {!isHoldingPCCable && !isTowerPlugged && (
                        <div className="absolute right-[7%] bottom-[85%] w-[20%] min-w-[40px] z-10">
                            <button
                                onClick={(e) => { e.stopPropagation(); onClickPC_Cable(); }}
                                className="relative w-full h-full transform transition-all duration-200 hover:scale-125"
                            >
                                <img src={cabinetSocketImage} alt="Cabinet Socket" className="w-full h-auto" />
                            </button>

                            {activeHint === 'TASK_1_STEP_1_GRAB_CABLE' && (
                                <ArrowHint className="bottom-[40%] left-[60%] -translate-x-1/2 w-[9vw] sm:w-[7vw] md:w-[5vw] lg:w-[4vw] aspect-square" />
                            )}
                        </div>
                    )}

                    {isTowerPlugged && (
                        <div className="absolute right-[10%] bottom-[110%] w-[12%] min-w-[20px] z-10">
                            <img src={cabinetSocketImage} alt="Cabinet Socket On" className="w-full h-auto" />
                        </div>
                    )}

                    {/* DISPOSITIVO GABINETE (CPU) */}
                    <div className="absolute right-[20%] bottom-[93%] w-[12%] min-w-[45px] z-20">
                        <button
                            onClick={(e) => { e.stopPropagation(); onClickTower(); }}
                            className="relative w-full h-full hover:scale-110 transition-transform"
                            disabled={!isTowerPlugged}
                        >
                            <img src={cabinetImage} alt="CPU Cabinet" className="w-full h-auto" />
                        </button>

                        <Image
                            src={imagePaths.infoIcon}
                            alt="info computer"
                            width={30}
                            height={30}
                            className="absolute top-[-20px] left-[-20px] w-[50px] h-[50px] cursor-pointer hover:scale-110 z-30"
                            onClick={(e) => { e.stopPropagation(); setInfoScreen('computer'); }}
                        />

                        {activeHint === 'TASK_1_STEP_3_PRESS_TOWER' && (
                            <ArrowHint className="bottom-[35%] left-[45%] -translate-x-1/2 w-[12vw] sm:w-[8vw] md:w-[6vw] lg:w-[4vw] aspect-square" />
                        )}
                    </div>
                </div>

                {/* Cadeira */}
                <div className="absolute left-[60%] -translate-x-1/2 bottom-0 w-[28%] min-w-[140px] max-w-[280px] z-40">
                    <img src={imagePaths.chair} alt="Chair" className="w-full h-auto" />
                </div>
            </div>

            {/* O InfoScreen  */}
            {infoScreen && (
                <InfoScreen
                    type={infoScreen}
                    onClick={() => setInfoScreen(null)}
                />
            )}
        </div>
    );
}