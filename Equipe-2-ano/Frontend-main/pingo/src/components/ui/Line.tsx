import { useEffect, useRef } from 'react';

interface LineProps extends React.InputHTMLAttributes<HTMLInputElement> {
    initX?: number;
    initY?: number;
    toX?: number;
    toY?: number;
    className?: string;

}

export const Line = ({initX = 0, initY = 0, toX = 10, toY = 500, className = ''}: LineProps) => {
    const canvasRef = useRef<HTMLCanvasElement>(null);

    useEffect(() => {
    if (canvasRef.current) {
        const ctx = canvasRef.current.getContext('2d');
        if (ctx) {
            ctx.fillStyle = 'blue';
            ctx.beginPath();
            ctx.moveTo(initX, initY);
            ctx.lineTo(10, 500);
            ctx.lineWidth = 5;
            ctx.stroke();
        }
    }
    }, []);

  return <canvas ref={canvasRef} className='absolute top-0 left-0 w-full h-full' />;
}