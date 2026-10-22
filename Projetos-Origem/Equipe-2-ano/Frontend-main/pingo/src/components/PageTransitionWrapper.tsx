"use client";

import { motion, AnimatePresence } from 'framer-motion';
import { usePathname } from 'next/navigation';
import React from 'react';

const PageTransitionWrapper = ({ children }: { children: React.ReactNode }) => {
    const pathname = usePathname();

    const numColumns = 4;
    const columnVariants = {
        initial: { y: '100%' },
        animate: { y: '0%' },
        exit: { y: '-100%' }
    };

    return (
        <AnimatePresence mode="wait">
            <motion.div key={pathname}>
                {children}

                {/* CAMADA QUE COBRE A PÁGINA ANTIGA (ON EXIT) */}
                <motion.div
                    className="pointer-events-none fixed top-0 left-0 flex h-screen w-full z-50"
                    initial="initial"
                    animate="initial"
                    exit="animate"
                    transition={{
                        staggerChildren: 0.06
                    }}
                >
                    {[...Array(numColumns)].map((_, i) => (
                        <motion.div
                            key={i}
                            className="relative h-full w-full bg-[var(--brand-indigo-deep)]"
                            variants={columnVariants}
                            transition={{ duration: 0.7, ease: [0.76, 0, 0.24, 1] }}
                        />
                    ))}
                </motion.div>

                {/* CAMADA QUE REVELA A PÁGINA NOVA (ON ENTER) */}
                <motion.div
                    className="pointer-events-none fixed top-0 left-0 flex h-screen w-full z-50"
                    initial="animate"
                    animate="exit"
                    transition={{
                        staggerChildren: 0.06,
                        delay: 0.1
                    }}
                >
                    {[...Array(numColumns)].map((_, i) => (
                        <motion.div
                            key={i}
                            className="relative h-full w-full bg-[var(--brand-indigo-deep)]"
                            variants={columnVariants}
                            transition={{ duration: 0.7, ease: [0.76, 0, 0.24, 1] }}
                        />
                    ))}
                </motion.div>

            </motion.div>
        </AnimatePresence>
    );
};

export default PageTransitionWrapper;