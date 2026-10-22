import Image from 'next/image';
import React, { useState, forwardRef } from 'react';
import Lottie from 'react-lottie-player';
import { InfoScreen } from './InfoScreen';

interface NetworkDeviceProps {
    className?: string;
    posX?: number;
    posY?: number;
    hasDeffect?: boolean;
    name?: string;
    onClick?: (e: React.MouseEvent) => void;
}

const NetworkDevice = forwardRef<HTMLDivElement, NetworkDeviceProps>(({ 
    className = "", 
    posX = -1, 
    posY = -1, 
    hasDeffect = false, 
    name = 'modem', 
    onClick
}, ref) => {    
    const [showInfoScreen, setShowInfoScreen] = useState(false);

    const handleClickInfo= () => {
        setShowInfoScreen(!showInfoScreen)
    };
    
    let position = ""
    let positionInfo = ""
    let firePosition = ""
    let size = ""
    
    // ... resto do switch code ...
    switch (posY) {
        case -1:
            break;
        case 0:
            position = "left-[0%] ";
            break;
        case 1:
            position = "left-1/2 -translate-x-1/2 ";
            break;
        case 2:
            position = "right-0 ";
            break;
    }

    switch (posX) {
        case -1:
            break;
        case 0:
            position += "top-0 ";
            break;
        case 1:
            position += "top-1/2 -translate-y-1/2 ";
            break;
        case 2:
            position += "bottom-0 ";
            break;
    }

    switch (name) {
        case 'modem':
            firePosition = "bottom-[-18%] left-[-33%]"
            positionInfo = "bottom-[-8%] right-[-25%]";
            size = "w-[80%] h-full"
            break;
        case 'switch':
            firePosition = "bottom-[10%] left-[-35%]"
            positionInfo = "bottom-[2%] right-[-26%]";
            size = "w-[80%] h-full"
            break;
        case 'server':
            firePosition = "bottom-[-20%] left-[-30%]"
            positionInfo = "bottom-[-15%] right-[-20%]";
            size = "w-[90%] h-[90%]"
            break;
        case 'notebook':
            firePosition = "bottom-[-20%] left-[-35%]"
            positionInfo = "bottom-[-20%] right-[-29%]";
            size = "w-full h-full"
            break;
        case 'computer':
            firePosition = "bottom-[-23%] left-[-40%]"
            positionInfo = "bottom-[-20%] right-[-29%]";
            size = "w-full h-full"
            break;
        case 'tablet':
            firePosition = "bottom-[-10%] left-[-38%]"
            positionInfo = "bottom-[-16%] right-[-20%]";
            size = "w-[80%] h-full"
            break;
        case 'smartphone':
            firePosition = "bottom-[-5%] left-[-24%]"
            positionInfo = "bottom-[-12%] right-[-3%]";
            size = "w-full h-[80%]"
            break;
        case 'antenna':
            firePosition = "bottom-[-20%] left-[-25%]"
            positionInfo = "bottom-[-9%] right-[-9%]";
            size = "w-full h-[95%]"
            break;
    }
    
    return (
        <div
            ref={ref} 
            className={`
            absolute
            ${position}
            w-[15%]
            h-[15%]
            aspect-[1/1]
            flex
            justify-center
            items-center
            cursor-pointer
            ${className}
            transition-transform
            active:scale-90
        `}
        onClick={onClick}>
            <Image
                src={`/image/stage-3/network-device/${name}.svg`}
                alt="Network Device"
                width={0}
                height={0}
                className={`
                    object-contain 
                    ${size}
                `}
            />  
            <Image
                src={`/image/stage-3/info.svg`}
                alt="info"
                width={0}
                height={0}
                className={`absolute w-[45%] h-[45%] ${positionInfo}`}
                onClick={handleClickInfo}
            />
            {hasDeffect && (
                <Lottie
                    path='/animation/stage-3/FireAnimation.json'
                    play
                    loop
                    className={`absolute w-[100%] h-[100%] ${firePosition}`}
                />
            )}
    
            {showInfoScreen && (<InfoScreen type={name} onClick={handleClickInfo}/>)}
        </div>
    );
});

NetworkDevice.displayName = 'NetworkDevice';

export default NetworkDevice;