import React, { useState } from 'react';
import fundoTelaLogin from '../assets/Fundo-da-tela-inicial.png';
import tiuloSaducao from '../assets/Titulo saudações.png';
import mascoteImg from "../assets/Dadinho Pulando.png";
import { GamepadIcon, StarIcon } from './ui/botoes';
import TelaTrilha from './TelaTrilha';
import TelaRanking from './TelaRanking';

// --- Componente Principal: Tela de Jogo ---
const TelaJogo = () => {
  const [telaAtiva, setTelaAtiva] = useState('menu'); // 'menu', 'jogo' ou 'ranking'
  const [telaAnterior, setTelaAnterior] = useState('menu'); // NOVO: guarda de onde o usuário veio
  const [carregandoJogo, setCarregandoJogo] = useState(false);

  const handleJogar = () => {
    setCarregandoJogo(true);
    // Pequeno delay para garantir transição suave
    setTimeout(() => {
      setTelaAnterior('menu'); // guarda a tela anterior
      setTelaAtiva('jogo');
      setCarregandoJogo(false);
    }, 100);
  };

  const handleVoltarMenu = () => {
    setTelaAtiva('menu');
  };

  // NOVO: volta para a tela anterior (pode ser menu ou trilha)
  const handleVoltarGenerico = () => {
    setTelaAtiva(telaAnterior);
  };

  const handleVerPontuacao = () => {
    setTelaAnterior(telaAtiva); // guarda de onde veio antes de abrir o ranking
    setTelaAtiva('ranking');
  };

  // Se estiver na tela de jogo, renderizar TelaTrilha
  if (telaAtiva === 'jogo') {
    return (
      <TelaTrilha
        onVoltar={handleVoltarMenu}
        onAbrirRanking={handleVerPontuacao}
      />
    );
  }

  // Se estiver na tela de ranking, renderizar TelaRanking
  if (telaAtiva === 'ranking') {
    return <TelaRanking onVoltar={handleVoltarGenerico} />;
  }

  // Renderizar tela de menu (original)
  return (
    <main
      className="min-h-screen w-full bg-cover bg-center bg-no-repeat relative overflow-hidden"
      style={{
        backgroundColor: '#4a919e',
        backgroundImage: `url(${fundoTelaLogin})`,
        backgroundSize: 'cover',
      }}
    >
      {/* --- IMAGEM DO DADINHO*/}
      <div
        className="absolute -center-20 left-9 p-4"
        style={{
          top: '50%',
          left: '20%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        <img
          src={mascoteImg}
          alt="Mascote do Jogo"
          className="w-[620px] scale-x-[-1]"
        />
      </div>

      {/* Container titulo saudacao */}
      <div
        className="absolute"
        style={{
          top: '30%',
          left: '72%',
          transform: 'translate(-50%, -50%)',
          width: '750px',
          maxWidth: '90vw',
        }}
      >
        <img
          src={tiuloSaducao}
          alt="Que bom te ver!"
          className="w-full max-w-4xl"
        />
      </div>

      {/* Container dos botões */}
      <div
        className="absolute flex flex-col gap-8 items-center"
        style={{
          top: '80%',
          left: '75%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        {/* Botão Jogar */}
        <button
          onClick={handleJogar}
          disabled={carregandoJogo}
          className="flex items-center w-[500px] gap-4 py-6 text-white font-bold text-4xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300 whitespace-nowrap disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
          style={{ backgroundColor: carregandoJogo ? '#4a2857' : '#563066' }}
          onMouseEnter={(e) =>
            !carregandoJogo && (e.target.style.backgroundColor = '#4a2857')
          }
          onMouseLeave={(e) =>
            !carregandoJogo && (e.target.style.backgroundColor = '#563066')
          }
        >
          <GamepadIcon />
          <span>{carregandoJogo ? 'Carregando...' : 'Jogar'}</span>
        </button>

        {/* Botão Ver Pontuação */}
        <button
          onClick={handleVerPontuacao}
          className="flex items-center w-[500px] gap-4 py-6 text-white font-bold text-4xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300 whitespace-nowrap"
          style={{ backgroundColor: '#563066' }}
          onMouseEnter={(e) => (e.target.style.backgroundColor = '#4a2857')}
          onMouseLeave={(e) => (e.target.style.backgroundColor = '#563066')}
        >
          <StarIcon />
          <span>Ver Pontuação</span>
        </button>
      </div>
    </main>
  );
};

export default TelaJogo;

