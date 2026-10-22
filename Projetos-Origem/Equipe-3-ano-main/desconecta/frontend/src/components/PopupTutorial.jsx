import React, { useState, useRef, useEffect } from 'react';
import arrowLeftCircle from '../assets/Arrow - Left Circle.png';

// Importar vídeos dos tutoriais
import tutorialConecta from '../assets/Dadolandia/Tutorial Conecta Dadolandia  .mp4';
import tutorialCacaPalavras from '../assets/Dadolandia/Tutorial Caça-palavras.mp4';

// --- Componente: Popup de Tutorial ---
const PopupTutorial = ({ isOpen, onClose, tipoTutorial = "todos" }) => {
  const [tutorialAtual, setTutorialAtual] = useState(0);
  const videoRef = useRef(null);

  // Array com TODOS os tutoriais disponíveis
  const todosTutoriais = [
    { nome: 'Conecta', src: tutorialConecta, tipo: 'conecta' },
    { nome: 'Caça-palavras', src: tutorialCacaPalavras, tipo: 'caca-palavras' }
  ];

  // Filtrar tutoriais baseado no tipo
  const tutoriais = tipoTutorial === "todos" 
    ? todosTutoriais 
    : todosTutoriais.filter(t => t.tipo === tipoTutorial);

  // Resetar índice ao mudar o tipo de tutorial
  useEffect(() => {
    setTutorialAtual(0);
  }, [tipoTutorial]);

  // Função para ir para o próximo tutorial
  const proximoTutorial = () => {
    setTutorialAtual((prev) => (prev + 1) % tutoriais.length);
    if (videoRef.current) {
      videoRef.current.currentTime = 0;
    }
  };

  // Função para ir para o tutorial anterior
  const tutorialAnterior = () => {
    setTutorialAtual((prev) => (prev - 1 + tutoriais.length) % tutoriais.length);
    if (videoRef.current) {
      videoRef.current.currentTime = 0;
    }
  };

  // Fechar popup ao clicar no overlay
  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  // Efeito para resetar o vídeo ao mudar de tutorial
  useEffect(() => {
    if (videoRef.current) {
      videoRef.current.currentTime = 0;
      videoRef.current.play();
    }
  }, [tutorialAtual]);

  if (!isOpen) {
    return null;
  }

  // Se não houver tutoriais disponíveis para o tipo selecionado
  if (tutoriais.length === 0) {
    return (
      <div
        className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50"
        onClick={handleOverlayClick}
      >
        <div className="bg-white p-8 rounded-xl shadow-2xl text-center">
          <h2 className="text-2xl font-bold mb-4">Tutorial não disponível</h2>
          <p className="mb-4">Este tutorial ainda não foi adicionado.</p>
          <button
            onClick={onClose}
            className="px-6 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            Fechar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50"
      onClick={handleOverlayClick}
    >
      {/* Container do Popup */}
      <div className="relative w-11/12 h-4/5 md:w-3/4 md:h-3/4 lg:w-2/3 lg:h-3/4 bg-black rounded-xl shadow-2xl overflow-hidden">
        
        {/* Vídeo */}
        <div className="w-full h-full flex items-center justify-center bg-black">
          <video
            ref={videoRef}
            src={tutoriais[tutorialAtual].src}
            controls
            autoPlay
            loop
            className="max-w-full max-h-full object-contain"
            onError={(e) => {
              console.error('Erro ao carregar vídeo:', e.target.src);
            }}
          />
        </div>

        {/* Botão Fechar (X) */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-white font-bold w-10 h-10 md:w-12 md:h-12 rounded-full flex items-center justify-center shadow-lg transition-all duration-200"
          style={{ 
            fontSize: 'min(20px, 2vw)',
            backgroundColor: '#563066'
          }}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
          onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
        >
          ✕
        </button>

        {/* Botão Anterior - Extremo esquerdo - Só mostra se houver mais de um tutorial */}
        {tutoriais.length > 1 && (
          <button
            onClick={tutorialAnterior}
            className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 z-20 flex items-center justify-center"
            style={{
              width: 'min(80px, 10vw)',
              height: 'min(80px, 10vw)'
            }}
            title="Tutorial anterior"
          >
            <img
              src={arrowLeftCircle}
              alt="Seta anterior"
              className="w-full h-full object-contain transform scale-x-[-1] drop-shadow-lg"
            />
          </button>
        )}

        {/* Botão Próximo - Extremo direito - Só mostra se houver mais de um tutorial */}
        {tutoriais.length > 1 && (
          <button
            onClick={proximoTutorial}
            className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 z-20 flex items-center justify-center"
            style={{
              width: 'min(80px, 10vw)',
              height: 'min(80px, 10vw)'
            }}
            title="Próximo tutorial"
          >
            <img
              src={arrowLeftCircle}
              alt="Seta próxima"
              className="w-full h-full object-contain drop-shadow-lg"
            />
          </button>
        )}
      </div>
    </div>
  );
};

export default PopupTutorial;
