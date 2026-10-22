'use client'

import Image from "next/image";
import Lottie from 'react-lottie-player';
import { ButtonFlat } from "./ButtonFlat";
import { useEffect, useState } from "react";

interface TutorialScreenProps {
  className?: string;
}

export const TutorialScreen = ({ className = ""}: TutorialScreenProps) => {
    const [visible, setVisible] = useState(true);
    useEffect(() => {
        if(window.innerWidth < 640) {
            setVisible(false);
        }
    }, []);
    if (!visible) return null; 
    return (
        <div
            className={`
                fixed
                top-[50%] -translate-y-[50%]
                left-[50%] -translate-x-[50%]
                w-[80%]
                h-auto
                md:h-[60%]
                lg:h-[95%]
                max-w-[1400px]
                max-h-[800px]
                aspect-1400/800
                ${className}
            `}
        >
            <div className="
                absolute
                inset-0
                bg-[var(--brand-white-light)]
                opacity-95
                rounded-[50px]
                sm:rounded-[50px]
                md:rounded-[70px]    
                lg:rounded-[80px]
                xl:rounded-[100px]
                -z-10
            " />

            <div className="
                absolute 
                flex
                flex-col
                items-center
                justify-center
                top-[3%] 
                left-[50%] 
                -translate-x-1/2 
                w-[90%] 
                h-[27%] 
                opacity-100
                bg-white
                shadow-xl
                rounded-full
            ">
                <p className="
                    relative
                    text-justify
                    w-[70%]
                    font-bold
                    text-[0.8rem]
                    sm:text-[1rem]
                    md:text-[1.3rem]
                    lg:text-[1.9rem]
                    xl:text-[2.3rem]
                ">
                    O  <span className="text-[var(--brand-magenta)]">MOUSE</span> É COMO A PONTA DO SEU DEDO! MEXA ELE E VEJA UMA SETINHA SE MOVENDO! AGORA, CLIQUE EM <span className="text-[var(--brand-magenta)]">COMEÇAR</span>!
                </p>
            </div>
            
            <div className="
                absolute
                flex
                flex-row
                items-center
                justify-center
                gap-[5%]
                top-[33%] w-full h-[40%]
            ">
                <Image
                    src="image/tutorial/human-computer.svg"
                    alt="human-computer"
                    width={0}
                    height={0}    
                    className="relative h-full w-auto"
                />
                
                <div className="relative h-full w-[37%] bg-white rounded-2xl shadow-2xl">
                    <Image
                        src="image/tutorial/monitor.svg"
                        alt="human-computer"
                        width={0}
                        height={0}    
                        className="absolute h-[70%] w-auto left-[5%] top-[5%]" 
                    />
                    <Lottie
                        path="/animation/tutorial/Cursor.json" 
                        play
                        loop
                        className="absolute h-[28%] w-auto left-[40%] top-[25%]"
                    />
                    <Lottie
                        path="/animation/tutorial/Mouse.json"
                        play
                        loop
                        className="absolute h-[70%] w-auto right-0 top-1/2 -translate-y-1/2"
                    />
                </div>
            </div>

            <ButtonFlat className="
                absolute bottom-[6%] left-1/2 -translate-x-1/2
                py-1 px-1
                text-xl
                sm:py-1 sm:px-6
                md:py-2 md:px-7
                lg:py-3 lg:px-8
                xl:py-4 xl:px-9
                sm:text-3xl
                md:text-4xl 
                xl:text-5xl
            
            " rounded="rounded-full" variant="gradient" size="small" onClick={() => setVisible(false)}>COMEÇAR</ButtonFlat>
        </div>
    );
};