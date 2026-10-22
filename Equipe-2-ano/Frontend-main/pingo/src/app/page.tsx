"use client";

import React, { useEffect, useRef } from 'react';
import Image from "next/image";
import Link from "next/link";
import { Button3D } from "@/components/ui/Button3D";
import { motion } from "framer-motion";
import CloudDecorations from "@/components/decorations/CloudDecorations";
import PlanetDecoration from "@/components/decorations/PlanetDecorations";
import { TutorialScreen } from '@/components/ui/TutorialScreen';

const HomePage: React.FC = () => {
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
                    document.removeEventListener('click', playOnInteraction);
                };
                document.addEventListener('click', playOnInteraction);
            });
        }
        return () => {
            audio.pause();
            audio.currentTime = 0;
        };
    }, []);
    return (
        <main className="relative flex min-h-screen flex-col items-center justify-start pt-5 pb-32 md:pb-40 bg-[var(--brand-back-dark)]">
            <audio 
                ref={audioRef}
                src="/audios-stages/Main-Theme.mp3"
                loop
                preload="auto"
            />
            <CloudDecorations />
            
            <div className="relative z-10 flex flex-col items-center justify-start text-center w-full px-4">
                <motion.div
                    className="w-full max-w-lg md:max-w-3xl"
                    initial={{ opacity: 0, scale: 0.5 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.7, ease: "backOut", delay: 1 }}
                >
                    <Image
                        src="/image/homeScreen/logoPingo.svg"
                        alt="Logo do Pingo: Uma Aventura na Rede"
                        width={983}
                        height={639}
                        className="h-auto w-full"
                        priority
                    />
                </motion.div>
                <motion.div
                    className="-mt-16 md:-mt-24"
                    initial={{ opacity: 0, scale: 0.5 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.7, delay: 1.3, ease: "backOut" }}
                >
                    <Link href={'/login'}>
                    <Button3D className="text-6xl font-bold md:text-7xl">
                        Jogar
                    </Button3D>
                    </Link>
                </motion.div>
                <TutorialScreen/>
            </div>
            <PlanetDecoration />
        </main>
    );
};

export default HomePage;