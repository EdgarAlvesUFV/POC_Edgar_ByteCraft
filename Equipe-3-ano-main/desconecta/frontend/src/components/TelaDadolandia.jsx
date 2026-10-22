import React, { useState, useEffect, useRef } from 'react';
import MenuNavegacao from './ui/MenuNavegacao';
import TelaPontuacao from './TelaPontuacao';

// Importar imagem da seta
import arrowLeftCircle from '../assets/Arrow - Left Circle.png';

// Importar todas as imagens estaticamente da pasta Dadolandia
import img1 from '../assets/Dadolandia/Dadolandia Quadro 1.gif';
import img2 from '../assets/Dadolandia/Dadolandia Quadro 2.gif';
import img3 from '../assets/Dadolandia/Dadolandia Quadro 3.gif';
import img4 from '../assets/Dadolandia/Dadolandia Quadro 4.gif';
import img5 from '../assets/Dadolandia/Dadolandia Quadro 5.gif';
import img6 from '../assets/Dadolandia/Dadolandia Quadro 6.gif';
import img7 from '../assets/Dadolandia/Dadolandia Quadro 7.gif';
import img8 from '../assets/Dadolandia/Dadolandia Quadro 8.gif';
import img9 from '../assets/Dadolandia/Dadolandia Quadro 9.gif';
import img10 from '../assets/Dadolandia/Dadolandia Quadro 10.gif';
import img11 from '../assets/Dadolandia/Dadolandia Quadro 11.gif';
import img12 from '../assets/Dadolandia/Dadolandia Quadro 12.gif';
import img13 from '../assets/Dadolandia/Dadolandia Quadro 13.gif';
import img14 from '../assets/Dadolandia/Dadolandia Quadro 14.gif';
import img15 from '../assets/Dadolandia/Dadolandia Quadro 15.gif';
import img16 from '../assets/Dadolandia/Dadolandia Quadro 16.gif';
import img17 from '../assets/Dadolandia/Dadolandia Quadro 17.gif';
import img18 from '../assets/Dadolandia/Dadolandia Quadro 18.gif';
import img19 from '../assets/Dadolandia/Dadolandia Quadro 19.gif';
import img20 from '../assets/Dadolandia/Dadolandia Quadro 20.gif';
import img21 from '../assets/Dadolandia/Dadolandia Quadro 21.gif';
import img22 from '../assets/Dadolandia/Dadolandia Quadro 22.gif';

// Importar vídeos dos tutoriais
import tutorialConecta from '../assets/Dadolandia/Tutorial Conecta Dadolandia  .mp4';
import tutorialCacaPalavras from '../assets/Dadolandia/Tutorial Caça-palavras.mp4';

// Array com todas as imagens em ordem (filtra apenas as que foram importadas com sucesso)
const imagens = [
  img1, img2, img3, img4, img5, img6, img7, img8, img9, img10,
  img11, img12, img13, img14, img15, img16, img17, img18, img19, img20,
  img21, img22
].filter(img => img !== undefined);

// Estados para as fases
const FASES = {
  IMAGENS: 'imagens',
  TUTORIAL_CONECTA: 'tutorialConecta',
  TUTORIAL_CACAPALAVRAS: 'tutorialCacaPalavras',
  CONCLUSAO: 'conclusao'
};

// --- Componente: Tela Dadolandia ---
const TelaDadolandia = ({ onVoltarTrilha, onVoltarMenu, onAbrirRanking }) => {
  const [imagemAtual, setImagemAtual] = useState(0);
  const [podeNavegar, setPodeNavegar] = useState(true);
  const [tempoRestante, setTempoRestante] = useState(0);
  const [faseAtual, setFaseAtual] = useState(FASES.IMAGENS);
  const [mostrarPontuacao, setMostrarPontuacao] = useState(false);
  const [tutorialConectaConcluido, setTutorialConectaConcluido] = useState(false);
  const [tutorialCacaPalavrasConcluido, setTutorialCacaPalavrasConcluido] = useState(false);

  // Refs para os vídeos
  const videoConectaRef = useRef(null);
  const videoCacaPalavrasRef = useRef(null);

  // Debug: verificar se as imagens foram carregadas
  console.log('Total de imagens carregadas:', imagens.length);
  console.log('Imagem atual:', imagens[imagemAtual]);
  console.log('Fase atual:', faseAtual);

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

  const handleVideoConcluido = (tipoVideo) => {
    if (tipoVideo === 'conecta') {
      setTutorialConectaConcluido(true);
      console.log('Tutorial Conecta concluído!');
    } else if (tipoVideo === 'cacaPalavras') {
      setTutorialCacaPalavrasConcluido(true);
      console.log('Tutorial Caça-palavras concluído!');
    }
  };

  const proximaImagem = () => {
    if (faseAtual === FASES.IMAGENS) {
      // Se estamos na imagem 20 (índice 19), ir para Tutorial Conecta
      if (podeNavegar && imagemAtual === 19) {
        setFaseAtual(FASES.TUTORIAL_CONECTA);
        setTutorialConectaConcluido(false); // Resetar o estado do vídeo
        setPodeNavegar(true);
      } else if (podeNavegar && imagemAtual < imagens.length - 1) {
        // Navegação normal entre imagens
        setImagemAtual(prev => prev + 1);
        iniciarTimer();
      } else if (podeNavegar && imagemAtual === imagens.length - 1) {
        // Última imagem (22) - redirecionar para TelaPontuacao
        setMostrarPontuacao(true);
      }
    } else if (faseAtual === FASES.TUTORIAL_CONECTA) {
      // Só avançar se o vídeo foi concluído
      if (tutorialConectaConcluido) {
        setFaseAtual(FASES.TUTORIAL_CACAPALAVRAS);
        setTutorialCacaPalavrasConcluido(false); // Resetar o estado do próximo vídeo
      }
    } else if (faseAtual === FASES.TUTORIAL_CACAPALAVRAS) {
      // Só avançar se o vídeo foi concluído
      if (tutorialCacaPalavrasConcluido) {
        setFaseAtual(FASES.IMAGENS);
        setImagemAtual(20);
        setPodeNavegar(true);
      }
    }
  };

  const imagemAnterior = () => {
    if (faseAtual === FASES.IMAGENS) {
      // Se estamos na imagem 21 (índice 20), voltar para Tutorial Caça-palavras
      if (imagemAtual === 20) {
        setFaseAtual(FASES.TUTORIAL_CACAPALAVRAS);
      } else if (imagemAtual > 0) {
        setImagemAtual(prev => prev - 1);
        // Não inicia timer para voltar - deixa livre
      }
    } else if (faseAtual === FASES.TUTORIAL_CACAPALAVRAS) {
      // Voltar do Tutorial Caça-palavras para Tutorial Conecta
      setFaseAtual(FASES.TUTORIAL_CONECTA);
    } else if (faseAtual === FASES.TUTORIAL_CONECTA) {
      // Voltar do Tutorial Conecta para a imagem 20 (índice 19)
      setFaseAtual(FASES.IMAGENS);
      setImagemAtual(19);
    }
  };

  // Se deve mostrar a tela de pontuação, renderizar TelaPontuacao
  if (mostrarPontuacao) {
    // Dadolandia corresponde ao enum posição 0 (0=DADOLANDIA, 1=CIENCIAS, 2=MATEMATICA, 3=GEOGRAFIA, 4=HISTORIA)
    return <TelaPontuacao 
      onVoltarTrilha={onVoltarTrilha} 
      onVoltarMenu={onVoltarMenu}
      onAbrirRanking={onAbrirRanking}
      ilhaCompletada={0} 
      nomeIlhaJogada="DADOLANDIA" 
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

      {/* Renderização condicional baseada na fase atual */}
      {faseAtual === FASES.IMAGENS && (
        <>
          {/* Imagem em tela cheia */}
          <div className="w-full h-screen flex items-center justify-center relative">
            {imagens.length > 0 ? (
              <img
                src={imagens[imagemAtual]}
                alt={`Dadolandia - Slide ${imagemAtual + 1}`}
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
                <p className="text-xl">Nenhuma imagem foi encontrada na pasta Dadolandia</p>
              </div>
            )}
          </div>

          {/* Botão Anterior - Extremo esquerdo */}
          <button
            onClick={imagemAnterior}
            disabled={imagens.length === 0 || imagemAtual === 0}
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
        </>
      )}

      {faseAtual === FASES.TUTORIAL_CONECTA && (
        <>
          {/* Vídeo do Tutorial Conecta em tela cheia */}
          <div className="w-full h-screen flex items-center justify-center relative">
            <video
              ref={videoConectaRef}
              src={tutorialConecta}
              controls
              autoPlay
              className="max-w-full max-h-full object-contain"
              style={{
                width: '100vw',
                height: '100vh',
                objectFit: 'contain'
              }}
              onEnded={() => handleVideoConcluido('conecta')}
              onError={(e) => {
                console.error('Erro ao carregar vídeo:', e.target.src);
              }}
            />
          </div>

          {/* Botão Anterior - Extremo esquerdo */}
          <button
            onClick={imagemAnterior}
            className="fixed left-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 z-20 flex items-center justify-center"
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
            disabled={!tutorialConectaConcluido}
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
        </>
      )}

      {faseAtual === FASES.TUTORIAL_CACAPALAVRAS && (
        <>
          {/* Vídeo do Tutorial Caça-palavras em tela cheia */}
          <div className="w-full h-screen flex items-center justify-center relative">
            <video
              ref={videoCacaPalavrasRef}
              src={tutorialCacaPalavras}
              controls
              autoPlay
              className="max-w-full max-h-full object-contain"
              style={{
                width: '100vw',
                height: '100vh',
                objectFit: 'contain'
              }}
              onEnded={() => handleVideoConcluido('cacaPalavras')}
              onError={(e) => {
                console.error('Erro ao carregar vídeo:', e.target.src);
              }}
            />
          </div>

          {/* Botão Anterior - Extremo esquerdo */}
          <button
            onClick={imagemAnterior}
            className="fixed left-4 top-1/2 transform -translate-y-1/2 bg-transparent hover:scale-110 transition-all duration-300 z-20 flex items-center justify-center"
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
            disabled={!tutorialCacaPalavrasConcluido}
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
        </>
      )}
    </main>
  );
};

export default TelaDadolandia;
