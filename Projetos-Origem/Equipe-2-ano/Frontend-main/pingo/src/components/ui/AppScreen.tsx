"use client";

import { useState, useEffect } from "react";
import { ButtonAppScreen } from "./ButtonAppScreen";
import { Contact } from "./Contact";
import { ContactSelected } from "./ContactSelected";
import { EmojiOption } from "./EmojiOption";
import { Arrow } from "./Arrow";
import { PingoDialog } from "./PingoDialog";
import { Message } from "./Message";

interface AppScreenProps {
  className?: string;
  isBlocked?: boolean;
  isWifiOn?: boolean;
  onClick?: () => void;
  onClose?: () => void;
  onConnectionNeeded?: () => void;
  onSendMessage?: () => void;
}

export const AppScreen = ({
  className = "",
  isBlocked = false,
  isWifiOn,
  onClick,
  onClose,
  onConnectionNeeded,
  onSendMessage,
}: AppScreenProps) => {
  // Estado para armazenar o contato selecionado
  const [visible, setVisible] = useState(true);
  const [emojiNumbers, setEmojiNumbers] = useState<number[]>([]);
  const [selectedContact, setSelectedContact] = useState<string | null>(null);
  const [selectedEmoji, setSelectedEmoji] = useState<
    [number, "magenta" | "violet"] | null
  >(null);
  const [isMesageSending, setIsMesageSending] = useState<number>(0);

  // Diálogos do Pingo:
  const [showAppMensageDialog, setShowAppMensageDialog] = useState(true);
  const [showSetEmojiDialog, setShowSetEmojiDialog] = useState(false);
  const [showConectionDialog, setConnectionDialog] = useState(false);

  // Efeito para notificar o componente pai quando showConectionDialog mudar
  useEffect(() => {
    if (showConectionDialog && onConnectionNeeded) {
      onConnectionNeeded();
    }
  }, [showConectionDialog, onConnectionNeeded]);

  const generateEmojiNumbers = () => {
    const nums: number[] = [];
    while (nums.length < 4) {
      const n = Math.floor(Math.random() * 12) + 1; // número entre 1 e 12
      if (!nums.includes(n)) nums.push(n);
    }
    setEmojiNumbers(nums);
  };
  // Função chamada quando um contato é clicado
  const handleContactClick = (name: string) => {
    // Se o contato já estiver selecionado, desseleciona
    if (isMesageSending == 0) {
      setSelectedContact((prev) => (prev === name ? null : name));
      setSelectedEmoji(null);
      setShowSetEmojiDialog(true);
      generateEmojiNumbers();
    }
    setShowAppMensageDialog(false);
  };

  const handleEmojiClick = (number: number, color: "magenta" | "violet") => {
    setSelectedEmoji([number, color]);
  };

  const handleSendMessage = () => {
    if (isWifiOn) {
      if (selectedEmoji) {
        localStorage.setItem('userSelectedEmoji', selectedEmoji[0].toString());
      }
      setIsMesageSending(1);
      if (onSendMessage) onSendMessage();
    } else {
      if (isMesageSending === 0) {
        setIsMesageSending(2);
      }
      setShowSetEmojiDialog(false);
      setConnectionDialog(true);
    }
  };

  const handleClose = () => {
    if (!isWifiOn) {
      setVisible(false);
      if (onClose) onClose();
    }
  };

  if (!visible) return null;

  return (
    <div
      className={`
        fixed
        top-[5%] left-[50%]
        -translate-x-[50%]
        w-[70%]
        h-[72%]
        border-2
        border-[var(--brand-midnight)]
        bg-[var(--brand-violet)] 
        rounded-[1%]
        z-20
        noise-effect
        ${className}
      `}
      onClick={onClick}
    >
      <link rel="preload" as="image" href="/image/stage-2/message-red.svg" />

      {/* Barra de título */}
      <div
        className="
        relative
        h-[6%] w-[98%]
        top-[2%] left-[50%]
        -translate-x-[50%]
        bg-[var(--brand-navy)]
        sm:rounded-sm
        md:rounded-md
      "
      >
        <p
          className="
          fixed
          font-bold
          text-[0.5rem]
          sm:text-[0.7rem]
          md:text-[0.8rem]
          lg:text-[1.2rem]
          top-[50%] left-[1%]
          -translate-y-[50%]
          text-white
        "
        >
          APLICATIVO DE MENSAGEM
        </p>

        <div className="absolute flex justify-end h-[100%] w-[15%] right-[2%] gap-[10%] items-center">
          <ButtonAppScreen type="min" />
          <ButtonAppScreen type="max" />
          <ButtonAppScreen type="x" />
        </div>
      </div>

      <div className="absolute top-[9%] w-[100%] h-[0.6%] bg-[var(--brand-midnight)] z-10" />

      {/* Painel de contatos */}
      <div className="absolute flex flex-col bottom-[3%] left-[1%] w-[30%] h-[85%] bg-[var(--brand-navy)] rounded-md md:rounded-xl z-10 items-center gap-[6%]">
        <p
          className="
          flex 
          items-center 
          justify-center 
          w-full 
          h-[3%] 
          text-[0.6rem]
          sm:text-[0.9rem]
          md:text-[1.1rem]
          lg:text-[1.6rem]
          font-bold 
          text-white 
          pt-[15%]"
        >
          CONTATOS
        </p>

        {Array.from({ length: 4 }, (_, i) => (
          <Contact
            key={i}
            name={`COLEGA ${i + 1}`}
            isSelected={selectedContact === `COLEGA ${i + 1}`}
            onClick={handleContactClick}
          />
        ))}
      </div>

      {/* Painel de conversa */}
      <div className="absolute bottom-[3%] right-[1%] w-[65%] h-[85%] bg-[var(--brand-navy)] rounded-md md:rounded-xl z-10 flex items-center justify-center font-bold">
        {selectedContact && (
          <div className="w-full h-full">
            <ContactSelected name={selectedContact} className="z-10" />
            {selectedEmoji && (
              <Message emoji={selectedEmoji[0]} isSent={isMesageSending} />
            )}

            <div className="absolute bottom-[5%] left-1/2 -translate-x-1/2 w-[90%] h-[15%] bg-[var(--brand-violet)] rounded-sm md:rounded-md">
              <div className="flex flex-row h-full w-full gap-[5%] items-center justify-center">
                {selectedEmoji ? (
                  <EmojiOption
                    number={selectedEmoji[0]}
                    color={selectedEmoji[1]}
                    onClick={handleEmojiClick}
                  />
                ) : (
                  <>
                    <EmojiOption
                      number={emojiNumbers[0]}
                      onClick={handleEmojiClick}
                    />
                    <EmojiOption
                      number={emojiNumbers[1]}
                      color="violet"
                      onClick={handleEmojiClick}
                    />
                    <EmojiOption
                      number={emojiNumbers[2]}
                      onClick={handleEmojiClick}
                    />
                    <EmojiOption
                      number={emojiNumbers[3]}
                      color="violet"
                      onClick={handleEmojiClick}
                    />
                  </>
                )}
              </div>
              {selectedEmoji && (
                <Arrow
                  className="top-[50%] right-[3%] -translate-y-[50%]"
                  onClick={handleSendMessage}
                />
              )}
            </div>
          </div>
        )}
      </div>
      {showAppMensageDialog && (
        <PingoDialog
          isBlocked={isBlocked}
          text="COM QUEM VOCÊ QUER CONVERSAR?
          *ESCOLHA UM COLEGA* PARA CONVERSAR"
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-3.m4a"}
          textSpeed={80}
        />
      )}
      {showSetEmojiDialog && (
        <PingoDialog
          isBlocked={isBlocked}
          className="absolute"
          text="AGORA *ESCOLHA UM EMOJI* PARA ENVIAR PARA O SEU AMIGUINHO!"
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-4.m4a"}
          textSpeed={80}
        />
      )}
      {showConectionDialog && !isWifiOn && (
        <PingoDialog
          isBlocked={isBlocked}
          className="absolute"
          text="OPA!
          PARECE QUE ESTAMOS /SEM CONEXÃO/ À *INTERNET*! PRECISAMOS DELA PARA ENVIAR NOSSA MENSAGEM"
          soundPath={"/audios/falas-pingo/falas-doc/fase-2/fase-2-5.m4a"}
          textSpeed={80}
        />
      )}
    </div>
  );
};
