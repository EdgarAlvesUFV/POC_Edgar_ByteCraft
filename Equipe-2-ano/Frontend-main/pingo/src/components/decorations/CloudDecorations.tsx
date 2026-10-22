"use client";

import Image from "next/image";
import { motion } from "framer-motion";

export default function CloudDecorations() {
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
                className="absolute top-2 h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudPurple.svg" alt="Nuvem roxa" width={232} height={163} />
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
                className="absolute top-20 h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudWhite.svg" alt="Nuvem branca" width={328} height={153} />
            </motion.div>

            <motion.div
                initial={{ x: '-100%' }}
                animate={{
                    x: '0%',
                    rotate: [0, -2, 2, -2, 0],
                }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.7,
                    rotate: { delay: 3.2, duration: 9, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-1/3 h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudBlue.svg" alt="Nuvem azul" width={254} height={153} />
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
                className="absolute bottom-5 h-auto w-auto max-w-none scale-[1.3]"
            >
                <Image src="/image/clouds/cloudPurpleBig.svg" alt="Nuvem roxa Grande" width={340} height={367} />
            </motion.div>

            <motion.div
                initial={{ x: '-180%' }}
                animate={{ x: '0%', rotate: [0, 4, -2, 4, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.8,
                    rotate: { delay: 3.3, duration: 13, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-10 h-auto w-auto max-w-none scale-[1.3]"
            >
                <Image src="/image/clouds/cloudYellow.svg" alt="Nuvem amarela" width={252} height={267} />
            </motion.div>

            <motion.div
                initial={{ x: '-200%' }}
                animate={{ x: '0%', rotate: [0, -2, 2, -2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.9,
                    rotate: { delay: 3.4, duration: 10, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute -bottom-4 h-auto w-auto max-w-none scale-[1.3]"
            >
                <Image src="/image/clouds/cloudSalmon.svg" alt="Nuvem Salmon" width={167} height={171} />
            </motion.div>



            {/* NUVENS DA DIREITA */}
            <motion.div
                initial={{ x: '-100%' }}
                animate={{ x: '0%', rotate: [0, 2, -2, 2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.5,
                    rotate: { delay: 3, duration: 11, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-2 right-0 scale-x-[-1] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudPurple.svg" alt="Nuvem roxa" width={232} height={163} />
            </motion.div>


            <motion.div
                initial={{ x: '-120%' }}
                animate={{ x: '0%', rotate: [0, -2, 1, -2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.6,
                    rotate: { delay: 3.1, duration: 12, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-20 right-0 scale-x-[-1] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudWhite.svg" alt="Nuvem branca" width={328} height={153} />
            </motion.div>

            <motion.div
                initial={{ x: '-150%' }}
                animate={{ x: '0%', rotate: [0, 1, -2, 1, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.7,
                    rotate: { delay: 3.2, duration: 9, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute top-1/3 right-0 scale-x-[-1] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudBlue.svg" alt="Nuvem azul" width={254} height={153} />
            </motion.div>

            <motion.div
                initial={{ x: '-100%' }}
                animate={{ x: '0%', rotate: [0, -2, 1, -2, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.55,
                    rotate: { delay: 3.05, duration: 11, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-5 right-0 scale-x-[-1.3] scale-y-[1.3] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudPurpleBig.svg" alt="Nuvem roxa Grande" width={340} height={367} />
            </motion.div>

            <motion.div
                initial={{ x: '-180%' }}
                animate={{ x: '0%', rotate: [0, 4, -2, 4, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.8,
                    rotate: { delay: 3.3, duration: 13, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute bottom-10 right-0 scale-x-[-1.3] scale-y-[1.3] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudYellow.svg" alt="Nuvem amarela" width={252} height={267} />
            </motion.div>

            <motion.div
                initial={{ x: '-200%' }}
                animate={{ x: '0%', rotate: [0, -3, 2, -3, 0] }}
                transition={{
                    duration: 2.5, ease: "easeOut", delay: 0.9,
                    rotate: { delay: 3.4, duration: 10, repeat: Infinity, repeatType: "mirror", ease: "easeInOut" }
                }}
                className="absolute -bottom-4 right-0 scale-x-[-1.3] scale-y-[1.3] h-auto w-auto max-w-none"
            >
                <Image src="/image/clouds/cloudSalmon.svg" alt="Nuvem Salmon" width={167} height={171} />
            </motion.div>
        </div>
    );
}