"use client";

import React, { useEffect, useRef, useState } from 'react';
import Image from "next/image";
import { motion } from "framer-motion";
import Lottie from 'react-lottie-player';
import { PingoDialog } from '@/components/ui/PingoDialog';

const jobTitles = [
    "COMANDANDO A MISSÃO",
    "ARTE PRODUZIDA POR",
    "TRILHA SONORA",
    "DESIGN DE SISTEMAS",
    "ARQUITETURA",
    "GERÊNCIA DE PROCESSOS",
    "GERÊNCIA DE CONFIGURAÇÃO",
    "DESENVOLVIMENTO",
    "QUALIDADE E TESTES",
    "SCRUM MASTERS",
    "LÍDERES DOS JUNIORES",
    "DESENVOLVEDORES JUNIORES",
    '|',
    "MONITORIA",
    "PROFESSORES",
    "APOIO ESPECIAL",
];

const creditsData = [
    "EDMARCOS, NOSSO LÍDER",
    "ALEXIA E ANA LUISA",
    "GUTO, MOODMODE E BLIU MUSIC",
    "ALTAMIRO",
    "ANA CAROLINA",
    "ANA LUISA",
    "CÉSAR",
    "DANIEL, CÉSAR E VINICIUS",
    "KAYO E LUCAS",
    "LUCAS E VINICIUS",
    "ANA CLARA E GABRIEL",
    "ANA CLARA, GABRIEL, JOÃO, DUDINHA, MATHEUS, PEDRO, RAFAEL E VÍTOR",
    "GABRIEL E EMILY",
    "GLAÚCIA, PHILIPE E DANIEL",
    "ESCOLA ESTADUAL SERAFIM, NOSSA GRANDE PARCEIRA NA MISSÃO DE ENSINAR JOGANDO",
];
const Credits: React.FC = () => {
    const [emoji, setEmoji] = useState(false);
    const [congratulations, setCongratulations] = useState(false);
    const [credits, setCredits] = useState(false);
    const [light, setLight] = useState(true);
    const audioRef = useRef<HTMLAudioElement>(null);
    const [userEmoji, setUserEmoji] = useState<number>(1);

    useEffect(() => {
        const savedEmoji = localStorage.getItem('userSelectedEmoji');
        if (savedEmoji) {
            setUserEmoji(parseInt(savedEmoji));
        }
    }, []);
    const paragraphClasses = `
        text-[0.6rem] 
        sm:text-[0.8rem] 
        md:text-[0.9rem] 
        lg:text-[1.2rem] 
        xl:text-[1.5rem]
        font-bold
    `
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
                    document.removeEventListener('click', playOnInteraction);
                };
                document.addEventListener('click', playOnInteraction);
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
        const timer = setTimeout(() => {
            setEmoji(true);
        }, 4000);
        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        const timer = setTimeout(() => {
            setCongratulations(true);
        }, 5000);
        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        const timer = setTimeout(() => {
            setCongratulations(false);
            setCredits(true);
        }, 20000);
        return () => clearTimeout(timer);
    }, []);


    
    return (
        <main className="relative flex w-screen h-screen bg-[var(--brand-back-dark)]">
            <audio 
                ref={audioRef}
                src="/audios-stages/Main-Theme.mp3"
                loop
            />
            {credits && (
                <>
                    <motion.div
                        className="absolute w-[100%] h-[100%] top-0 left-0 z-10 bg-black"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        transition={{ 
                            type: "spring", 
                            stiffness: 260, 
                            damping: 20,
                            duration: 1 
                        }}
                    />

                    <div className='
                        absolute 
                        flex flex-col
                        w-[90%] h-[100%] 
                        max-w-[1500px]
                        left-1/2 -translate-x-1/2 
                        z-11
                    '>
                        

                        <Lottie
                            path="/animation/Pingo.json"
                            play loop
                            className="
                            absolute 
                            left-[-5%] bottom-[-2%]
                            sm:bottom-[-3%]
                            md:bottom-[-5%]
                            lg:bottom-[-7%]
                            w-[28%] h-[auto]"
                        />

                        <div className='absolute w-[75%] h-[100%] z-12 left-[50%] translate-x-[-48%] text-white'>
                            <Image
                                src="/image/credits/shine.svg"
                                alt="shine"
                                width={0}
                                height={0}
                                className="absolute top-0 right-0 w-[15%] h-auto z-14"
                            />
                            <Image
                                src="/image/stage-3/message.svg"
                                alt="message"
                                width={0}
                                height={0}
                                className="absolute top-[4%] right-[10%] w-[4%] h-auto rotate-340 z-14"
                            />
                            <p className='
                                absolute
                                top-6 left-[-5%]
                                text-[1rem] 
                                sm:text-[1.2rem] 
                                md:text-[1.3rem] 
                                lg:text-[1.6rem] 
                                xl:text-[2rem]
                                font-bold
                            '>CRÉDITOS</p>
                            <motion.div
                                // Estado inicial: Começa totalmente acima da tela (-100% da altura)
                                initial={{ y: "-75%" }}
                                // Estado final: Vai para baixo da tela (100% da altura da viewport)
                                animate={{ y: "100vh" }}
                                // Configuração: Duração de 10s, velocidade linear, repetição infinita
                                transition={{ 
                                    duration: 20, 
                                    ease: "linear", 
                                    repeat: Infinity 
                                }}
                                className='absolute flex flex-row items-center justify-center gap-[4%] w-[100%] h-[100%] z-13 text-white'
                                >
                                    {/* O conteúdo interno permanece igual */}
                                    <div className='w-[42%] h-full z-13 text-end'>
                                        {jobTitles.map((title, index) => (
                                            <p key={`title-${index}`} className={paragraphClasses + ` ${title.includes('|') ? 'invisible' : ''}`}>
                                                {title}
                                            </p>
                                        ))}
                                    </div>
                                    <div className='w-[58%] h-full z-13 text-start'>
                                        {creditsData.map((title, index) => (
                                            <p key={`title-${index}`} className={paragraphClasses}>
                                                {title}
                                            </p>
                                        ))}
                                    </div>
                            </motion.div>
                        </div>
                    </div>
                </>
            )}
            <Image
                src="/image/credits/floor.svg"
                alt="floor"
                width={0}
                height={0}
                className="absolute object-contain w-[100%] h-auto bottom-0 z-0"
            />

            
            <div className="absolute w-[90%] h-auto bottom-[10%] left-[50%] max-w-[1200px] -translate-x-1/2 z-1">
                
                <Image
                    src="/image/credits/table.svg"
                    alt="table"
                    width={0}
                    height={0}
                    className="object-contain w-[100%] h-auto"
                />

                <Image
                    src="/image/credits/chair.svg"
                    alt="chair"
                    width={0}
                    height={0}
                    className="absolute object-contain left-1/2 -translate-x-1/2 w-[21%] h-auto bottom-0 z-4"
                />

                <Image
                    src={`/image/credits/notebook.svg`}
                    alt="notebook"
                    width={0}
                    height={0}
                    className="absolute w-[25%] h-auto bottom-[85%] left-[50%] -translate-x-[51%] "
                />
                {emoji && (
                    <motion.div
                        // 1. O Posicionamento (absolute, left, bottom, width) vai NO MOTION.DIV
                        className="absolute w-[3%] bottom-[115%] left-[51%] z-15 origin-bottom" 
                        
                        initial={{ scale: 0, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        transition={{ 
                            type: "spring", 
                            stiffness: 260, 
                            damping: 20,
                            duration: 0.5 
                        }}
                    >
                        <Image
                            src={`/image/stage-2/emoji/emoji-${userEmoji}.svg`}
                            alt="emoji"
                            width={983}
                            height={639}
                            className="w-full h-auto object-contain"
                            priority
                        />
                    </motion.div>
                )}
        
                <Image
                    src="/image/credits/outlet.svg"
                    alt="outlet"
                    width={0}
                    height={0}
                    className="absolute object-contain left-1/2 -translate-x-[-57%] w-[19%] h-auto top-[-40%]"
                />

                <Image
                    src="/image/credits/modem.svg"
                    alt="modem"
                    width={0}
                    height={0}
                    className="absolute w-[20%] h-auto top-[-40%] left-[6%] max-w-[300px]"
                />

                <Image
                    src="/image/credits/lamp-off.svg"
                    alt="lamp"
                    width={0}
                    height={0}
                    className="absolute w-[10.3%] h-auto bottom-[95%] right-[6%] z-10"
                    onClick={() => setLight(!light)}
                />
                { light && (<Image
                    src="/image/credits/light.svg"
                    alt="light"
                    width={0}
                    height={0}
                    className="absolute w-[15%] h-auto bottom-[82%] right-[10%] z-9"
                />)}

                <Image
                    src="/image/credits/paper.svg"
                    alt="paper"
                    width={0}
                    height={0}
                    className="absolute w-[10%] h-auto bottom-[85%] right-[14%] z-11"
                />
                <Image
                    src="/image/credits/pencil.svg"
                    alt="pencil"
                    width={0}
                    height={0}
                    className="absolute w-[2%] h-auto bottom-[90%] right-[15%] z-11"
                />
            </div>

            { congratulations && (
            <>
                <PingoDialog
                    isBlocked={false}
                    className="absolute"
                    text="PARABÉNS! VOCÊ MANDOU MUITO BEM! GRAÇAS A VOCÊ (E A *INTERNET*) A MENSAGEM FOI ENVIADA. OBRIGADO POR JOGAR!"
                    soundPath={"/audios/falas-pingo/falas-doc/tela-final/tela-final.m4a"}
                    textSpeed={100}
                    onClose={() => setCredits(true)}
                />
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
            </>
            )}
        </main>
    );
};

export default Credits;