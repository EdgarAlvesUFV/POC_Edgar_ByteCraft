import React, { useRef, useEffect } from "react";
import { useDrop } from "react-dnd";
import type { NivelDificuldade } from "../types";
import "./DropZone.css";

interface DropZoneProps {
  id: string;
  onDrop: (itemId: string, targetId: string) => void;
  placed?: boolean;
  image?: string;
  destacar?: boolean;
  nivel?: NivelDificuldade;
}

const DropZone: React.FC<DropZoneProps> = ({
  id,
  image,
  onDrop,
  placed,
  destacar = false,
  nivel = "medio",
}) => {
  const ref = useRef<HTMLDivElement>(null);

  const [{ isOver, canDrop }, drop] = useDrop(
    () => ({
      accept: "COMPONENT",
      drop: (item: { id: string; image: string }) => {
        onDrop(item.id, id);
        return { success: true };
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
        canDrop: monitor.canDrop(),
      }),
    }),
    [id, onDrop]
  );

  useEffect(() => {
    if (ref.current) drop(ref.current);
  }, [drop, id]);

  const getAreaInfo = () => {
    switch (id) {
      case "dropzone_placa_mae":
        return { tipo: "placa_mae", label: "Placa-Mãe", icone: "🔌" };
      case "dropzone_monitor":
        return { tipo: "monitor", label: "Monitor", icone: "🖥️" };
      case "dropzone_teclado":
        return { tipo: "teclado", label: "Teclado", icone: "⌨️" };
      case "dropzone_mouse":
        return { tipo: "mouse", label: "Mouse", icone: "🖱️" };
      case "dropzone_som":
        return { tipo: "som", label: "Caixa de Som", icone: "🔊" };
      case "dropzone_processador":
        return { tipo: "processador", label: "Processador", icone: "🧠" };
      case "dropzone_ram":
        return { tipo: "ram", label: "Memória RAM", icone: "💾" };
      case "dropzone_ssd":
        return { tipo: "ssd", label: "SSD", icone: "💿" };
      case "dropzone_placa_video":
        return { tipo: "placa_video", label: "Placa de Vídeo", icone: "🎮" };
      case "dropzone_fan":
        return { tipo: "fan", label: "Cooler", icone: "❄️" };
      default:
        return { tipo: "", label: "", icone: "" };
    }
  };

  const { tipo, label, icone } = getAreaInfo();

  const getClassName = () => {
    const classes = ["dropzone-area", tipo];
    if (placed) classes.push("dropzone-placed");
    else if (destacar) classes.push("dropzone-iluminado");
    else if (isOver && canDrop) classes.push("dropzone-hover");
    
    classes.push(`nivel-${nivel}`);
    
    return classes.join(" ");
  };

  const mostrarIcone = !placed;

  return (
    <div ref={ref} className={getClassName()} data-label={label}>
      {mostrarIcone && <span className="dropzone-icone">{icone}</span>}

      {placed && image && (
        <img src={image} alt={label} className="dropzone-imagem" />
      )}

      {!placed && destacar && (
        <div className="dropzone-placeholder">
          <div className="dropzone-brilho-container">
            <span className="dropzone-brilho-texto">✨ Aqui! ✨</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default DropZone;