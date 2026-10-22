import { motion } from "framer-motion";
import Image from 'next/image';
import React from 'react';

interface PlanetDecorationProps {
    containerClassName?: string;
}

const PlanetDecoration: React.FC<PlanetDecorationProps> = ({ containerClassName = "" }) => {
    const imagePaths = {
        planet: "/image/planet/planet.svg",
        continentUp: "/image/planet/continentUp.svg",
        continentDown: "/image/planet/continentDown.svg",
        message: "/image/planet/message.svg",
    };

    return (
        <>
            {/* Planeta */}
            <motion.div
                className={`absolute w-auto max-w-none h-auto left-1/2 -translate-x-1/2 bottom-0 pointer-events-none ${containerClassName}`}
                initial={{ opacity: 0, scale: 0.5 }}
                animate={{ opacity: 1, scale: 1}}
                transition={{ duration: 1, ease: "backOut", delay: 1 }}
            >
                <Image
                    src={imagePaths.planet}
                    alt="Planeta estilizado"
                    width={1440}
                    height={300}
                    className={`absolute w-auto max-w-none h-auto left-1/2 -translate-x-1/2 bottom-0 pointer-events-none ${containerClassName}`}
                    priority
                />
            </motion.div>


            {/* Sombra do Continente */}
            <motion.div
                initial={{ opacity: 0, y: 240, scale: 0.9 }}
                animate={{
                    opacity: [0, 1, 1],
                    y: [240, 240, 0],
                    scale: [0, 0, 1],
                    rotate: [0, -0.1, 0.1, -0.1, 0]

                }}
                transition={{
                    duration: 2,
                    delay: 1.7,
                    ease: "backOut",
                    times: [0, 0.3, 1],
                    rotate: { delay: 3.4, duration: 10, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-0 w-auto max-w-none left-1/2 -translate-x-1/2 h-auto pointer-events-none"
            >
                <Image
                    src={imagePaths.continentDown}
                    alt="Sombra do continente"
                    width={1440}
                    height={273}
                    className="absolute bottom-0 w-auto max-w-none left-1/2 -translate-x-1/2 h-auto pointer-events-none"
                />
            </motion.div>

            {/* Continente */}
            <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{
                    opacity: [0, 1, 1],
                    scale: [0, 0, 1],
                    rotate: [0, -0.4, 0.4, -0.4, 0]
                }}
                transition={{
                    duration: 2,
                    delay: 1.5,
                    ease: "backOut",
                    times: [0, 0.3, 1],
                    rotate: { delay: 3.4, duration: 10, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }

                }}
                className="absolute bottom-0 w-auto max-w-none left-1/2 -translate-x-1/2 h-auto pointer-events-none"
            >
                <Image
                    src={imagePaths.continentUp}
                    alt="Continente"
                    width={1440}
                    height={305}
                    className="absolute bottom-0 w-auto max-w-none left-1/2 -translate-x-1/2 h-auto pointer-events-none"
                />
            </motion.div>



            {/* Mensagem */}
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 1, ease: "easeOut", delay: 2.7 }}
            >
                <Image
                    src={imagePaths.message}
                    alt="Mensagem no planeta"
                    width={554}
                    height={202}
                    className="absolute bottom-[11%] md:bottom-[11%] w-auto left-1/2 -translate-x-1/2 h-auto pointer-events-none"
                />
            </motion.div>
        </>
    );
};

export default PlanetDecoration;
