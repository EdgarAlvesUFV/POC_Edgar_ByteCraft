"use client";

import Image from "next/image";
import { motion } from "framer-motion";

export default function CloudDecorationsStages() {
    const cloudScale = "w-[30vw] sm:w-[30vw] md:w-[15vw] h-auto"
    return (
        <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
            {/* NUVENS DA ESQUERDA */}
            <motion.div
                initial={{ x: '-100%' }}
                animate={{
                    x: '0%',
                    rotate: [0, -2, 2, -2, 0],
                }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.5,
                    rotate: { delay: 3, duration: 10, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" },
                }}
                className="absolute top-[-20px] left-[-30px] h-auto w-auto max-w-none"
            >
                <Image 
                    src="/image/clouds/cloudCyan.svg" 
                    alt="Nuvem ciano" 
                    width={232} height={163} 
                    className={cloudScale}
                />
            </motion.div>

            <motion.div
                initial={{ x: '-100%' }}
                animate={{
                    x: '0%',
                    rotate: [0, -2, 2, -2, 0],
                }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.6,
                    rotate: { delay: 3.1, duration: 12, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" },
                }}
                className="absolute top-10 left-[-40px] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudWhite.svg" alt="Nuvem branca" width={328} height={153} 
                    className={cloudScale}/>
            </motion.div>

            <motion.div
                initial={{ x: '-100%' }}
                animate={{
                    x: '0%',
                    rotate: [0, -2, 2, -2, 0],
                }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.55,
                    rotate: { delay: 3.05, duration: 11, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-[00px] left-5 h-auto w-auto max-w-none scale-[1.3]"
            >
                <Image src="/image/clouds/cloudCyanBig.svg" alt="Nuvem ciano Grande" width={280} height={367} 
                    className={cloudScale}/>
            </motion.div>

            <motion.div
                initial={{ x: '-180%' }}
                animate={{ x: '0%', rotate: [0, 4, -2, 4, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.8,
                    rotate: { delay: 3.3, duration: 13, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-[6px] left-0 h-auto w-auto max-w-none scale-[1.3]"
            >
                <Image src="/image/clouds/cloudWhiteBig.svg" alt="Nuvem branca Grande" width={280} height={340} 
                    className={cloudScale}/>
            </motion.div>

            {/* NUVENS DA DIREITA */}
            <motion.div
                initial={{ x: '-100%' }}
                animate={{ x: '0%', rotate: [0, 2, -2, 2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.5,
                    rotate: { delay: 3, duration: 11, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-[-20px] right-[-30px] scale-x-[-1] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudCyan.svg" alt="Nuvem Ciano" width={232} height={163} 
                    className={cloudScale}/>
            </motion.div>

            <motion.div
                initial={{ x: '-120%' }}
                animate={{ x: '0%', rotate: [0, -2, 1, -2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.6,
                    rotate: { delay: 3.1, duration: 12, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-10 right-[-40px] scale-x-[-1] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudWhite.svg" alt="Nuvem branca" width={328} height={153}
                    className={cloudScale} />
            </motion.div>

            <motion.div
                initial={{ x: '-100%' }}
                animate={{ x: '0%', rotate: [0, -2, 1, -2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.55,
                    rotate: { delay: 3.05, duration: 11, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-[00px] right-5 scale-x-[-1.3] scale-y-[1.3] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudCyanBig.svg" alt="Nuvem ciano Grande" width={280} height={367} 
                    className={cloudScale}/>
            </motion.div>

            <motion.div
                initial={{ x: '-180%' }}
                animate={{ x: '0%', rotate: [0, 4, -2, 4, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.8,
                    rotate: { delay: 3.3, duration: 13, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-[6px] right-5 scale-x-[-1.3] scale-y-[1.3] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudWhiteBig.svg" alt="Nuvem branca Grande" width={280} height={340} className={cloudScale}/>
            </motion.div>
        </div>
    );
}
