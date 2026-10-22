import React, { useState, useEffect } from 'react';
import MenuNavegacao from './ui/MenuNavegacao';
import TelaPontuacao from './TelaPontuacao';
import JogoGeografia from './JogoGeografia';

// Importar imagem da seta
import arrowLeftCircle from '../assets/Arrow - Left Circle.png';

// Importar todas as imagens estaticamente da pasta HistoriaGeografia
import img1 from '../assets/HistoriaGeografia/Geografia Quadro 1.gif';
import img2 from '../assets/HistoriaGeografia/Geografia Quadro 2.gif';
import img3 from '../assets/HistoriaGeografia/Geografia Quadro 3.gif';
import img4 from '../assets/HistoriaGeografia/Geografia Quadro 4.gif';
import img5 from '../assets/HistoriaGeografia/Geografia Quadro 5.gif';
import img6 from '../assets/HistoriaGeografia/Geografia Quadro 6.gif';
import img7 from '../assets/HistoriaGeografia/Geografia Quadro 7.gif';
import img8 from '../assets/HistoriaGeografia/Geografia Quadro 8.gif';
import img9 from '../assets/HistoriaGeografia/Geografia Quadro 9.gif';
import img10 from '../assets/HistoriaGeografia/Geografia Quadro 10.gif';
import img11 from '../assets/HistoriaGeografia/Geografia Quadro 11.gif';
import img12 from '../assets/HistoriaGeografia/Geografia Quadro 12.gif';
import img13 from '../assets/HistoriaGeografia/Geografia Quadro 13.gif';
import img14 from '../assets/HistoriaGeografia/Geografia Quadro 14.gif';
import img15 from '../assets/HistoriaGeografia/Geografia Quadro 15.gif';
import img16 from '../assets/HistoriaGeografia/Geografia Quadro 16.gif';
import img17 from '../assets/HistoriaGeografia/Geografia Quadro 17.gif';
import img18 from '../assets/HistoriaGeografia/Geografia Quadro 18.gif';
import img19 from '../assets/HistoriaGeografia/Geografia Quadro 19.gif';
import img20 from '../assets/HistoriaGeografia/Geografia Quadro 20.gif';
import img21 from '../assets/HistoriaGeografia/Geografia Quadro 21.gif';
import img22 from '../assets/HistoriaGeografia/Geografia Quadro 22.gif';
import img23 from '../assets/HistoriaGeografia/Geografia Quadro 23.gif';
import img24 from '../assets/HistoriaGeografia/Geografia Quadro 24.gif';
import img25 from '../assets/HistoriaGeografia/Geografia Quadro 25.gif';
import img26 from '../assets/HistoriaGeografia/Geografia Quadro 26.gif';
import img27 from '../assets/HistoriaGeografia/Geografia Quadro 27.gif';
import img28 from '../assets/HistoriaGeografia/Geografia Quadro 28.gif';
import img29 from '../assets/HistoriaGeografia/Geografia Quadro 29.gif';

// Array com todas as imagens em ordem (filtra apenas as que foram importadas com sucesso)
const imagens = [
  img1, img2, img3, img4, img5, img6, img7, img8, img9, img10,
  img11, img12, img13, img14, img15, img16, img17, img18, img19, img20,
  img21, img22, img23, img24, img25, img26, img27, img28, img29
].filter(img => img !== undefined);

// --- Componente: Tela Jogo Geografia ---
const TelaJogoGeografia = ({ onVoltarTrilha, onVoltarMenu, onAbrirRanking }) => {
  const [imagemAtual, setImagemAtual] = useState(0);
  const [podeNavegar, setPodeNavegar] = useState(true);
  const [tempoRestante, setTempoRestante] = useState(0);
  const [mostrarPontuacao, setMostrarPontuacao] = useState(false);
  const [mostrarJogoGeografia, setMostrarJogoGeografia] = useState(false);
  const [jogoGeografiaConcluido, setJogoGeografiaConcluido] = useState(false);

  // Debug: verificar se as imagens foram carregadas
  console.log('Total de imagens carregadas:', imagens.length);
  console.log('Imagem atual:', imagens[imagemAtual]);

  // Cleanup dos timers quando o componente for desmontado
  useEffect(() => {
    return () => {
      // Limpar qualquer timer ativo
      setPodeNavegar(true);
      setTempoRestante(0);
    };
  }, []);

  const iniciarTimer = () => {
    setPodeNavegar(false);
    setTempoRestante(0);
    
    const timer = setInterval(() => {
      setTempoRestante(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          setPodeNavegar(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const proximaImagem = () => {
    if (podeNavegar && imagemAtual < imagens.length - 1) {
      // Se estamos na imagem 22 (índice 21), ir para JogoGeografia
      if (imagemAtual === 21) {
        setMostrarJogoGeografia(true);
      } else {
        setImagemAtual(prev => prev + 1);
        iniciarTimer();
      }
    } else if (podeNavegar && imagemAtual === imagens.length - 1) {
      // Última imagem - redirecionar para pontuação
      setMostrarPontuacao(true);
    }
  };

  const imagemAnterior = () => {
    // Permite voltar em todas as imagens, EXCETO na imagem 23 (índice 22)
    // que é a imagem imediatamente após o jogo JogoGeografia
    if (imagemAtual > 0 && imagemAtual !== 22) {
      setImagemAtual(prev => prev - 1);
      // Não inicia timer para voltar - deixa livre
    }
  };

  // Se deve mostrar a tela JogoGeografia, renderizar JogoGeografia
  if (mostrarJogoGeografia) {
    return (
      <JogoGeografia 
        onVoltarTrilha={onVoltarTrilha} 
        onVoltarMenu={onVoltarMenu}
        onAbrirRanking={onAbrirRanking}
        onConcluido={() => {
          // Após concluir o jogo, voltar para img23 (índice 22)
          setImagemAtual(22);
          setMostrarJogoGeografia(false);
          setJogoGeografiaConcluido(true);
        }}
      />
    );
  }

  // Se deve mostrar a tela de pontuação, renderizar TelaPontuacao
  if (mostrarPontuacao) {
    // Geografia corresponde ao enum posição 3 (0=DADOLANDIA, 1=CIENCIAS, 2=MATEMATICA, 3=GEOGRAFIA, 4=HISTORIA)
    return <TelaPontuacao 
      onVoltarTrilha={onVoltarTrilha} 
      onVoltarMenu={onVoltarMenu}
      onAbrirRanking={onAbrirRanking} 
      ilhaCompletada={3} 
      nomeIlhaJogada="GEOGRAFIA" 
    />;
  }

  return (
    <main className="min-h-screen bg-black relative overflow-hidden">
      {/* Menu de Navegação Reutilizável */}
      <MenuNavegacao 
        onVoltarTrilha={onVoltarTrilha}
        onVoltarMenu={onVoltarMenu}
        onAbrirRanking={onAbrirRanking}
        posicao="top-right"
      />

      {/* Imagem em tela cheia */}
      <div className="w-full h-screen flex items-center justify-center relative">
        {imagens.length > 0 ? (
          <img
            src={imagens[imagemAtual]}
            alt={`História da Geografia - Slide ${imagemAtual + 1}`}
            className="max-w-full max-h-full object-contain"
            style={{
              width: '100vw',
              height: '100vh',
              objectFit: 'contain'
            }}
            onError={(e) => {
              console.error('Erro ao carregar imagem:', e.target.src);
            }}
            onLoad={() => {
              console.log('Imagem carregada com sucesso:', imagens[imagemAtual]);
            }}
          />
        ) : (
          <div className="text-white text-center">
            <h2 className="text-4xl font-bold mb-4">Carregando imagens...</h2>
            <p className="text-xl">Nenhuma imagem foi encontrada na pasta HistoriaGeografia</p>
          </div>
        )}
      </div>

      {/* Botão Anterior - Extremo esquerdo */}
      {/* Desabilitado apenas na imagem 23 (índice 22) - imediatamente após o jogo */}
      <button
        onClick={imagemAnterior}
        disabled={imagens.length === 0 || imagemAtual === 0 || imagemAtual === 22}
        className="fixed left-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed z-20 flex items-center justify-center"
        style={{
          width: 'min(100px, 12vw)',
          height: 'min(100px, 12vw)'
        }}
      >
        <img 
          src={arrowLeftCircle} 
          alt="Seta anterior" 
          className="w-full h-full object-contain transform scale-x-[-1]"
        />
      </button>

      {/* Botão Próximo - Extremo direito */}
      <button
        onClick={proximaImagem}
        disabled={imagens.length === 0 || !podeNavegar}
        className="fixed right-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed z-20 flex items-center justify-center"
        style={{
          width: 'min(100px, 12vw)',
          height: 'min(100px, 12vw)'
        }}
      >
        <img 
          src={arrowLeftCircle} 
          alt="Seta próxima" 
          className="w-full h-full object-contain"
        />
      </button>
    </main>
  );
};

export default TelaJogoGeografia;
