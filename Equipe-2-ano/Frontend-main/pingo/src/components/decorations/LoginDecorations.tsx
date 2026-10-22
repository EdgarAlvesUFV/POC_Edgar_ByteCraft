"use client";

import Image from "next/image";
import { motion } from "framer-motion";
import CloudDecorations from "@/components/decorations/CloudDecorations";

const baseDelay = 0.8;

export default function LoginDecorations() {
    return (
        <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
            {/* Elipses */}
            <motion.div
                initial={{ x: '-100%', y: '-100%', opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, -2, 2, -2, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 40, damping: 15, delay: baseDelay + 0.2 },
                    rotate: {
                        duration: 12,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute top-0 left-0"
            >
                <Image src="/image/login/ellipseLPink.svg" alt="Ellipse Left Pink" width={349} height={315} />
            </motion.div>
            <motion.div
                initial={{ x: '100%', y: '-100%', opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, 2, -2, 2, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 40, damping: 15, delay: baseDelay + 0.2 },
                    rotate: {
                        duration: 13,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute top-0 right-0"
            >
                <Image src="/image/login/ellipseRPink.svg" alt="Ellipse Right Pink" width={349} height={300} />
            </motion.div>
            <motion.div
                initial={{ x: '-100%', y: '100%', opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, 2, -2, 2, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 40, damping: 15, delay: baseDelay + 0.3 },
                    rotate: {
                        duration: 14,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute bottom-0 left-0"
            >
                <Image src="/image/login/ellipseLGreen.svg" alt="Ellipse Left Green" width={338} height={305} />
            </motion.div>
            <motion.div
                initial={{ x: '100%', y: '100%', opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, -2, 2, -2, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 40, damping: 15, delay: baseDelay + 0.3 },
                    rotate: {
                        duration: 15,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute bottom-0 right-0"
            >
                <Image src="/image/login/ellipseRGreen.svg" alt="Ellipse Right Green" width={400} height={223} />
            </motion.div>

            {/* Personagens e Ícones */}
            <motion.div
                initial={{ x: -100, y: -100, opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, -5, 5, -5, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 50, damping: 10, delay: baseDelay + 0.5 },
                    rotate: {
                        duration: 12,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute top-10 left-10 z-10"
            >
                <Image src="/image/login/screenIcon.svg" alt="Screen Icon" width={248} height={176} />
            </motion.div>
            <motion.div
                initial={{ x: 100, y: -100, opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, 5, -5, 5, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 50, damping: 10, delay: baseDelay + 0.5 },
                    rotate: {
                        duration: 11,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute top-10 right-10 z-10"
            >
                <Image src="/image/login/handPhone.svg" alt="Hand Phone" width={218} height={182} />
            </motion.div>
            <motion.div
                initial={{ x: -100, y: 100, opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, 5, -5, 5, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 50, damping: 10, delay: baseDelay + 0.6 },
                    rotate: {
                        duration: 10,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute bottom-10 left-10 z-10"
            >
                <Image src="/image/login/internet.svg" alt="Internet" width={257} height={257} />
            </motion.div>
            <motion.div
                initial={{ x: 100, y: 100, opacity: 0 }}
                animate={{ x: 0, y: 0, opacity: 1, rotate: [0, -5, 5, -5, 0] }}
                transition={{
                    default: { type: "spring", stiffness: 50, damping: 10, delay: baseDelay + 0.6 },
                    rotate: {
                        duration: 12,
                        repeat: Infinity,
                        repeatType: "mirror",
                        ease: "easeInOut",
                    }
                }}
                className="absolute bottom-10 right-10 z-10"
            >
                <Image src="/image/login/phone.svg" alt="Phone" width={198} height={191} />
            </motion.div>

            <CloudDecorations />
        </div>
    );
}