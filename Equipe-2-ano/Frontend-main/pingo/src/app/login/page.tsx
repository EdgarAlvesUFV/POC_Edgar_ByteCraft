"use client";

import React, { useEffect, useRef, useState } from 'react';
import { Input } from '@/components/ui/Input';
import { ButtonFlat } from '@/components/ui/ButtonFlat';
import LoginDecorations from '@/components/decorations/LoginDecorations';
import { motion } from 'framer-motion';
import Image from 'next/image';
import { useAuth } from '@/domain/user/hooks/useAuth';

const LoginPage: React.FC = () => {
    const [showPassword, setShowPassword] = useState(false);
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
    const {
        nickname,
        setNickname,
        password,
        setPassword,
        errorMessage,
        authState,
        handleAuthentication,
    } = useAuth();

    const isLoading = authState === 'Submitting';
    const isSubmitDisabled = authState !== 'ReadyToSubmit';

    return (
        <main className="relative flex min-h-screen flex-col items-center justify-center bg-[var(--brand-indigo-darker)] p-4">
            <audio 
                ref={audioRef}
                src="/audios-stages/Main-Theme.mp3"
                loop
                preload="auto"
            />
            <LoginDecorations />
            <motion.form
                onSubmit={handleAuthentication}
                initial={{ y: -200, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ type: "spring", stiffness: 50, damping: 15, delay: 0.5 }}
                className="relative z-10 flex w-full max-w-lg flex-col items-center justify-center rounded-2xl md:p-12"
            >
                <div className="mb-8 w-full">
                    <label htmlFor="nickname" className="mb-2 block text-6xl font-bold text-white text-center">APELIDO:</label>
                    <Input
                        id="nickname"
                        sizing="large"
                        className="text-4xl"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        required
                    />
                </div>
                <div className="w-full">
                    <label htmlFor="password" className="mb-2 block text-6xl font-bold text-white text-center">SENHA:</label>
                    <div className="relative">
                        <Input
                            id="password"
                            type={showPassword ? "text" : "password"}
                            sizing="large"
                            className="text-4xl pr-20"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute inset-y-0 right-0 flex items-center pr-5 focus:outline-none"
                        >
                            <Image
                                src={showPassword ? "/image/login/eyeOff.svg" : "/image/login/eyeOn.svg"}
                                alt={showPassword ? "Ocultar senha" : "Mostrar senha"}
                                width={60}
                                height={60}
                            />
                        </button>
                    </div>
                </div>

                {authState === 'Error' && <p className="mt-6 text-2xl font-bold text-red-400">{errorMessage}</p>}

                <div className="mt-12">
                    <ButtonFlat
                        type="submit"
                        variant="gradient"
                        size="large"
                        className="px-16 py-5 text-4xl rounded-b-full rounded-t-full"
                        disabled={isSubmitDisabled || isLoading}
                    >
                        {isLoading ? 'ENTRANDO...' : 'COMEÇAR'}
                    </ButtonFlat>
                </div>
            </motion.form>
        </main>
    );
};

export default LoginPage;