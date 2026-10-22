'use client'
import { useState, useEffect } from "react";

interface ClockProps {
  className?: string;
}

export const Clock = ({ className = "" }: ClockProps) => {
  const [time, setTime] = useState<string>(() => {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes}`;
  });

  useEffect(() => {
    const interval = setInterval(() => {
      const now = new Date();
      const hours = String(now.getHours()).padStart(2, "0");
      const minutes = String(now.getMinutes()).padStart(2, "0");
      setTime(`${hours}:${minutes}`);
    }, 1000);

    return () => clearInterval(interval); 
  }, []);

  return <p className={`
    fixed
    flex
    items-center
    justify-center
    font-var(--font-sans) 
    font-bold 
    h-[2.5%]
    text-[0.5rem]
    sm:text-[0.6rem]
    md:text-[0.8rem]
    lg:text-[1.2rem]
    ${className}
  `}>{time}</p>;
};
