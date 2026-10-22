import React, { useState, useEffect } from 'react';
import fundoTrilha from '../assets/imgTelaTrilha/fundoTrilha.png';
import ilhaDadolandia from '../assets/imgTelaTrilha/Ilha-Dadolandia.png';
import ilhaHistoria from '../assets/imgTelaTrilha/Ilha-história.png';
import ilhaCiencias from '../assets/imgTelaTrilha/Ilha-Ciencias.png';
import ilhaGeografia from '../assets/imgTelaTrilha/Ilha-geografia.png';
import ilhaMatematica from '../assets/imgTelaTrilha/Ilha-Matematica.png';
import popupJogarIlha from '../assets/imgTelaTrilha/popup-jogar-ilha.png';
import TelaJogoCiencia from './TelaJogoCiencia';
import TelaDadolandia from './TelaDadolandia';
import TelaJogoGeografia from './TelaJogoGeografia';
import TelaJogoMatematica from './TelaJogoMatematica';
import TelaPontuacao from './TelaPontuacao';
import useAlunoLogado from '../hooks/useAlunoLogado';
import MenuNavegacao from './ui/MenuNavegacao';


// --- Componente: Tela Trilha ---
const TelaTrilha = ({ onVoltar, onAbrirRanking }) => {
  const [posicaoIlhaAtual, setPosicaoIlhaAtual] = useState(null);
  const [telaAtiva, setTelaAtiva] = useState('trilha'); // 'trilha', 'dadolandia', 'ciencia', 'geografia', 'matematica', ou 'pontuacao'
  const [nomeIlhaJogada, setNomeIlhaJogada] = useState(null); // Rastreia qual ilha foi jogada
  const [popupDadolandiaAberto, setPopupDadolandiaAberto] = useState(false);
  const [popupCienciaAberto, setPopupCienciaAberto] = useState(false);
  const [popupMatematicaAberto, setPopupMatematicaAberto] = useState(false);
  const [popupGeografiaAberto, setPopupGeografiaAberto] = useState(false);
  const [popupHistoriaAberto, setPopupHistoriaAberto] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [startX, setStartX] = useState(0);
  const [scrollLeft, setScrollLeft] = useState(0);
  const [carregandoProgresso, setCarregandoProgresso] = useState(true);

  const { alunoId, isLogado } = useAlunoLogado();

  // Função para buscar a posição da ilha do aluno
  const buscarPosicaoIlha = async () => {
    if (!alunoId || !isLogado) {
      setCarregandoProgresso(false);
      return;
    }

    try {
      setCarregandoProgresso(true);
      
      // Primeiro, buscar o ID do progresso do aluno usando o ID do aluno
      // const responseIdProgresso = await fetch(`http://localhost:8080/api/progresso-aluno/id/${alunoId}`);
      const responseIdProgresso = await fetch(`${window.location.origin}/desconecta/api/progresso-aluno/id/${alunoId}`);
      
      if (responseIdProgresso.ok) {
        const idProgressoAluno = await responseIdProgresso.json();
        
        if (idProgressoAluno) {
          // Agora buscar a lista de posições das ilhas usando o ID do progresso
          // const responsePosicoes = await fetch(`http://localhost:8080/api/ilhas/posicoes-ilhas/${idProgressoAluno}`);
          const responsePosicoes = await fetch(`${window.location.origin}/desconecta/api/ilhas/posicoes-ilhas/${idProgressoAluno}`);
          
          if (responsePosicoes.ok) {
            const posicoesIlhas = await responsePosicoes.json();
            
            // A posição mais avançada é o último elemento da lista ordenada
            if (posicoesIlhas && posicoesIlhas.length > 0) {
              const posicaoMaisAvancada = posicoesIlhas[posicoesIlhas.length - 1];
              setPosicaoIlhaAtual(posicaoMaisAvancada);
              console.log('Posição da ilha mais avançada:', posicaoMaisAvancada);
            }
          }
        }
      }
    } catch (error) {
      console.error('Erro ao carregar progresso:', error);
    } finally {
      setCarregandoProgresso(false);
    }
  };

  // useEffect para carregar o progresso quando o componente for montado
  useEffect(() => {
    if (alunoId && isLogado) {
      buscarPosicaoIlha();
    }
  }, [alunoId, isLogado]);

  // useEffect para verificar se houve atualização do progresso e recarregar
  useEffect(() => {
    const progressoAtualizado = localStorage.getItem('progressoAtualizado');
    if (progressoAtualizado === 'true') {
      // Remove a flag e recarrega o progresso
      localStorage.removeItem('progressoAtualizado');
      if (alunoId && isLogado) {
        buscarPosicaoIlha();
      }
    }
  }, [telaAtiva, alunoId, isLogado]); // Executa quando volta para a tela da trilha e quando o aluno muda

  // Funções para abrir popups
  const handleAbrirPopupDadolandia = () => {
    setPopupDadolandiaAberto(true);
  };

  const handleAbrirPopupCiencia = () => {
    setPopupCienciaAberto(true);
  };

  const handleAbrirPopupMatematica = () => {
    setPopupMatematicaAberto(true);
  };

  const handleAbrirPopupGeografia = () => {
    setPopupGeografiaAberto(true);
  };

  const handleAbrirPopupHistoria = () => {
    setPopupHistoriaAberto(true);
  };

  // Funções para fechar popups
  const handleFecharPopupDadolandia = () => {
    setPopupDadolandiaAberto(false);
  };

  const handleFecharPopupCiencia = () => {
    setPopupCienciaAberto(false);
  };

  const handleFecharPopupMatematica = () => {
    setPopupMatematicaAberto(false);
  };

  const handleFecharPopupGeografia = () => {
    setPopupGeografiaAberto(false);
  };

  const handleFecharPopupHistoria = () => {
    setPopupHistoriaAberto(false);
  };

  // Funções para ir para cada ilha
  const handleIrParaDadolandia = () => {
    setPopupDadolandiaAberto(false);
    setNomeIlhaJogada('DADOLANDIA');
    setTelaAtiva('dadolandia');
  };

  const handleIrParaCiencia = () => {
    setPopupCienciaAberto(false);
    setNomeIlhaJogada('CIENCIAS');
    setTelaAtiva('ciencia');
  };

  const handleIrParaMatematica = () => {
    setPopupMatematicaAberto(false);
    setNomeIlhaJogada('MATEMATICA');
    setTelaAtiva('matematica');
  };

  const handleIrParaGeografia = () => {
    setPopupGeografiaAberto(false);
    setNomeIlhaJogada('GEOGRAFIA');
    setTelaAtiva('geografia');
  };

  const handleIrParaHistoria = () => {
    setPopupHistoriaAberto(false);
    setNomeIlhaJogada('HISTORIA');
    setTelaAtiva('historia');
  };

  const handleVoltarTrilha = () => {
    // Limpa o nome da ilha jogada ao voltar para a trilha
    setNomeIlhaJogada(null);
    // Sempre recarrega o progresso ao voltar da tela de jogo
    if (alunoId && isLogado) {
      buscarPosicaoIlha();
    }
    setTelaAtiva('trilha');
  };

  const handleIrParaPontuacao = () => {
    setTelaAtiva('pontuacao');
  };

  // Funções para arrastar
  const handleMouseDown = (e) => {
    setIsDragging(true);
    setStartX(e.pageX - e.currentTarget.offsetLeft);
    setScrollLeft(e.currentTarget.scrollLeft);
    e.currentTarget.style.cursor = 'grabbing';
  };

  const handleMouseLeave = (e) => {
    setIsDragging(false);
    e.currentTarget.style.cursor = 'grab';
  };

  const handleMouseUp = (e) => {
    setIsDragging(false);
    e.currentTarget.style.cursor = 'grab';
  };

  const handleMouseMove = (e) => {
    if (!isDragging) return;
    e.preventDefault();
    const x = e.pageX - e.currentTarget.offsetLeft;
    const walk = (x - startX) * 2; // Multiplicador para sensibilidade
    e.currentTarget.scrollLeft = scrollLeft - walk;
  };

  // CSS inline para esconder barra de scroll
  const scrollStyle = {
    backgroundColor: '#4a919e',
    scrollbarWidth: 'none', // Firefox
    msOverflowStyle: 'none', // Internet Explorer 10+
    cursor: 'grab',
    userSelect: 'none', // Previne seleção de texto durante arraste
  };

  // Adicionar regra CSS para WebKit browsers
  const styleSheet = document.styleSheets[0] || document.head.appendChild(document.createElement('style')).sheet;
  const webkitScrollbarRule = `
    .min-h-screen::-webkit-scrollbar {
      display: none;
    }
  `;
  
  // Verificar se a regra já foi adicionada
  if (!document.querySelector('#webkit-scrollbar-style')) {
    const style = document.createElement('style');
    style.id = 'webkit-scrollbar-style';
    style.textContent = webkitScrollbarRule;
    document.head.appendChild(style);
  }

  // Se estiver na tela de ciência, renderizar TelaJogoCiencia
  if (telaAtiva === 'ciencia') {
    return <TelaJogoCiencia onVoltarTrilha={handleVoltarTrilha} onVoltarMenu={onVoltar} onAbrirRanking={onAbrirRanking} />;
  }

  // Se estiver na tela de Dadolandia, renderizar TelaDadolandia
  if (telaAtiva === 'dadolandia') {
    return <TelaDadolandia onVoltarTrilha={handleVoltarTrilha} onVoltarMenu={onVoltar} onAbrirRanking={onAbrirRanking} />;
  }

  // Se estiver na tela de geografia, renderizar TelaJogoGeografia
  if (telaAtiva === 'geografia') {
    return <TelaJogoGeografia onVoltarTrilha={handleVoltarTrilha} onVoltarMenu={onVoltar} onAbrirRanking={onAbrirRanking} />;
  }

  // Se estiver na tela de matemática, renderizar TelaJogoMatematica
  if (telaAtiva === 'matematica') {
    return <TelaJogoMatematica onVoltarTrilha={handleVoltarTrilha} onVoltarMenu={onVoltar} onAbrirRanking={onAbrirRanking} />;
  }

  // Se estiver na tela de pontuação, renderizar TelaPontuacao
  if (telaAtiva === 'pontuacao') {
    // Dadolandia corresponde ao enum posição 0 (0=DADOLANDIA, 1=CIENCIAS, 2=GEOGRAFIA, 3=MATEMATICA, 4=HISTORIA)
    return <TelaPontuacao 
      onVoltarTrilha={handleVoltarTrilha} 
      onVoltarMenu={onVoltar} 
      onAbrirRanking={onAbrirRanking}
      ilhaCompletada={0} 
      nomeIlhaJogada={nomeIlhaJogada} 
    />;
  }

  // Se o aluno não estiver logado, mostrar mensagem
  if (!isLogado) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">
            Você precisa estar logado para acessar a trilha
          </h2>
          <button
            onClick={onVoltar}
            className="px-6 py-3 bg-purple-600 text-white font-bold rounded-lg hover:bg-purple-700 transition-colors"
          >
            Voltar ao Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <main
      className="min-h-screen overflow-x-auto overflow-y-hidden relative"
      style={scrollStyle}
      onMouseDown={handleMouseDown}
      onMouseLeave={handleMouseLeave}
      onMouseUp={handleMouseUp}
      onMouseMove={handleMouseMove}
    >
      {/* Menu de Navegação */}
      <MenuNavegacao 
        onVoltarTrilha={() => {}} // Já está na trilha
        onVoltarMenu={onVoltar}
        onAbrirRanking={onAbrirRanking}
        posicao="top-right"
      />

      {/* Container estendido horizontalmente */}
      <div 
        className="flex relative w-screen h-screen"
        style={{ 
          width: '134.33vw', // Volta ao tamanho original para preencher completamente
          height: '100vh', // Altura fixa da viewport
          backgroundImage: `url(${fundoTrilha})`, 
          backgroundSize: 'cover', // Cobre toda a área
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center center',
          backgroundAttachment: 'local' // Melhor performance em mobile
        }}
      >
        {/* Botões fixos no topo (sempre visíveis) */}
        <div className="fixed flex gap-2 sm:gap-4 z-10" style={{
          top: 'min(32px, 4vh)',
          left: 'min(32px, 4vw)'
        }}>
          {/* Botão de Voltar */}
          <button
            onClick={onVoltar}
            className="flex items-center gap-1 sm:gap-2 text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-300"
            style={{ 
              backgroundColor: '#563066',
              padding: 'min(12px, 2vh) min(24px, 3vw)',
              fontSize: 'min(20px, 2.5vw)'
            }}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
          >
            ← Voltar
          </button>
        </div>

        {/* PRIMEIRA SEÇÃO (0-66.67vw) - Dadolandia, Ciências e Matemática */}
        <div className="h-full relative flex-shrink-0" style={{ width: '66.67vw' }}>
          {/* Botão Redondo com Ilha Ciências */}
          <div className="absolute flex flex-col items-center justify-center" 
            style={{ 
              top: '30%', 
              left: '50%', 
              transform: 'translate(-45%, -40%)' 
            }}>
            <button
              onClick={(posicaoIlhaAtual !== null && posicaoIlhaAtual >= 1) ? handleAbrirPopupCiencia : undefined}
              disabled={posicaoIlhaAtual === null || posicaoIlhaAtual < 1}
              className={`rounded-full shadow-lg transform transition-all duration-300 border-8 ${
                (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 1)
                  ? 'hover:scale-110 cursor-pointer' 
                  : 'opacity-50 cursor-not-allowed'
              }`}
              style={{ 
                width: 'min(240px, 30vw)',
                height: 'min(240px, 30vw)',
                backgroundImage: `url(${ilhaCiencias})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundColor: '#563066',
                borderColor: '#dbedee'
              }}
              onMouseEnter={(e) => {
                if (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 1) {
                  e.target.style.backgroundColor = '#4a2857';
                  e.target.style.borderColor = '#dbedee';
                }
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = '#563066';
                e.target.style.borderColor = '#dbedee';
              }}
            />
            <div className={`bg-white rounded-full shadow-lg ${
              (posicaoIlhaAtual === null || posicaoIlhaAtual < 1) ? 'opacity-50' : ''
            }`} style={{
              marginTop: 'min(16px, 2vh)',
              padding: 'min(12px, 1.5vh) min(24px, 3vw)'
            }}>
              <span style={{
                fontSize: 'min(18px, 2.3vw)',
                fontWeight: 'bold',
                color: '#1f2937'
              }}>Ciências</span>
            </div>
          </div>

          {/* Botão Redondo com Ilha Dadolandia */}
          <div
  className="absolute flex flex-col items-center justify-center"
  style={{
    top: '65%',
    left: '25%',
    transform: 'translate(-70%, -30%)'
  }}
>
  {/* Botão redondo */}
  <button
    onClick={handleAbrirPopupDadolandia}
    className="rounded-full shadow-lg transform hover:scale-110 transition-all duration-300 border-8"
    style={{
      width: 'min(240px, 30vw)',
      height: 'min(240px, 30vw)',
      backgroundImage: `url(${ilhaDadolandia})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundColor: '#563066',
      borderColor: '#dbedee'
    }}
    onMouseEnter={(e) => {
      e.currentTarget.style.backgroundColor = '#4a2857';
      e.currentTarget.style.borderColor = '#dbedee';
    }}
    onMouseLeave={(e) => {
      e.currentTarget.style.backgroundColor = '#563066';
      e.currentTarget.style.borderColor = '#dbedee';
    }}
  />

  {/* Texto ao lado ou abaixo */}
  <div
    className="bg-white rounded-full shadow-lg"
    style={{
      marginTop: 'min(3px, 3vh)', // empurra para baixo do botão
      marginLeft: '12px',          // empurra um pouco para a direita
      padding: 'min(5px, 1.5vh) min(24px, 3vw)'
    }}
  >
    <span
      style={{
        fontSize: 'min(18px, 2.3vw)',
        fontWeight: 'bold',
        color: '#1f2937'
      }}
    >
      Dadolandia
    </span>
  </div>
</div>

          {/* Botão Redondo com Ilha Matemática */}
          <div className="absolute flex flex-col items-center justify-center" 
            style={{ 
              top: '70%', 
              left: '75%', 
              transform: 'translate(30%, -110%)' 
            }}>
            <button
              onClick={(posicaoIlhaAtual !== null && posicaoIlhaAtual >= 2) ? handleAbrirPopupMatematica : undefined}
              disabled={posicaoIlhaAtual === null || posicaoIlhaAtual < 2}
              className={`rounded-full shadow-lg transform transition-all duration-300 border-8 ${
                (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 2)
                  ? 'hover:scale-110 cursor-pointer' 
                  : 'opacity-50 cursor-not-allowed'
              }`}
              style={{ 
                width: 'min(240px, 30vw)',
                height: 'min(240px, 30vw)',
                backgroundImage: `url(${ilhaMatematica})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundColor: '#563066',
                borderColor: '#dbedee'
              }}
              onMouseEnter={(e) => {
                if (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 2) {
                  e.target.style.backgroundColor = '#4a2857';
                  e.target.style.borderColor = '#dbedee';
                }
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = '#563066';
                e.target.style.borderColor = '#dbedee';
              }}
            />
            <div className={`bg-white rounded-full shadow-lg ${
              (posicaoIlhaAtual === null || posicaoIlhaAtual < 2) ? 'opacity-50' : ''
            }`} style={{
              marginTop: 'min(16px, 2vh)',
              padding: 'min(12px, 1.5vh) min(24px, 3vw)'
            }}>
              <span style={{
                fontSize: 'min(18px, 2.3vw)',
                fontWeight: 'bold',
                color: '#1f2937'
              }}>Matemática</span>
            </div>
          </div>
        </div>

        {/* SEGUNDA SEÇÃO (66.67vw-133.33vw) - Geografia e História */}
        <div className="h-full relative flex-shrink-0" style={{ width: '66.67vw' }}>
          {/* Botão Redondo com Ilha Geografia */}
          <div className="absolute flex flex-col items-center justify-center" 
            style={{ 
              top: '50%', 
              left: '30%', 
              transform: 'translate(-30%, -15%)' 
            }}>
            <button
              onClick={(posicaoIlhaAtual !== null && posicaoIlhaAtual >= 3) ? handleAbrirPopupGeografia : undefined}
              disabled={posicaoIlhaAtual === null || posicaoIlhaAtual < 3}
              className={`rounded-full shadow-lg transform transition-all duration-300 border-8 ${
                (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 3)
                  ? 'hover:scale-110 cursor-pointer' 
                  : 'opacity-50 cursor-not-allowed'
              }`}
              style={{ 
                width: 'min(240px, 30vw)',
                height: 'min(240px, 30vw)',
                backgroundImage: `url(${ilhaGeografia})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundColor: '#563066',
                borderColor: '#dbedee'
              }}
              onMouseEnter={(e) => {
                if (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 3) {
                  e.target.style.backgroundColor = '#4a2857';
                  e.target.style.borderColor = '#dbedee';
                }
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = '#563066';
                e.target.style.borderColor = '#dbedee';
              }}
            />
            <div className={`bg-white rounded-full shadow-lg ${
              (posicaoIlhaAtual === null || posicaoIlhaAtual < 3) ? 'opacity-50' : ''
            }`} style={{
              marginTop: 'min(16px, 2vh)',
              padding: 'min(12px, 1.5vh) min(24px, 3vw)'
            }}>
              <span style={{
                fontSize: 'min(18px, 2.3vw)',
                fontWeight: 'bold',
                color: '#1f2937'
              }}>Geografia</span>
            </div>
          </div>

          {/* Botão Redondo com Ilha História */}
          <div className="absolute flex flex-col items-center justify-center" 
            style={{ 
              top: '40%', 
              left: '65%', 
              transform: 'translate(20%, 20%)' 
            }}>
            <button
              onClick={(posicaoIlhaAtual !== null && posicaoIlhaAtual >= 4) ? handleAbrirPopupHistoria : undefined}
              disabled={posicaoIlhaAtual === null || posicaoIlhaAtual < 4}
              className={`rounded-full shadow-lg transform transition-all duration-300 border-8 ${
                (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 4)
                  ? 'hover:scale-110 cursor-pointer' 
                  : 'opacity-50 cursor-not-allowed'
              }`}
              style={{ 
                width: 'min(240px, 30vw)',
                height: 'min(240px, 30vw)',
                backgroundImage: `url(${ilhaHistoria})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundColor: '#563066',
                borderColor: '#dbedee'
              }}
              onMouseEnter={(e) => {
                if (posicaoIlhaAtual !== null && posicaoIlhaAtual >= 4) {
                  e.target.style.backgroundColor = '#4a2857';
                  e.target.style.borderColor = '#dbedee';
                }
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = '#563066';
                e.target.style.borderColor = '#dbedee';
              }}
            />
            <div className={`bg-white rounded-full shadow-lg ${
              (posicaoIlhaAtual === null || posicaoIlhaAtual < 4) ? 'opacity-50' : ''
            }`} style={{
              marginTop: 'min(16px, 2vh)',
              padding: 'min(12px, 1.5vh) min(24px, 3vw)'
            }}>
              <span style={{
                fontSize: 'min(18px, 2.3vw)',
                fontWeight: 'bold',
                color: '#1f2937'
              }}>História</span>
            </div>
          </div>
        </div>

      </div>

      {/* Popup de Confirmação para Ilha Dadolandia */}
      {popupDadolandiaAberto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div 
            className="relative"
            style={{
              backgroundImage: `url(${popupJogarIlha})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              width: 'min(600px, 90vw)',
              height: 'min(500px, 90vh)',
              aspectRatio: '6/5'
            }}
          >
            {/* Botão X para fechar */}
            <button
              onClick={handleFecharPopupDadolandia}
              className="absolute text-white rounded-full flex items-center justify-center font-bold transition-colors duration-200 shadow-lg"
              style={{ 
                backgroundColor: '#563066',
                top: 'min(160px, 32%)',
                left: 'min(24px, 4%)',
                width: 'min(64px, 10.7vw)',
                height: 'min(64px, 10.7vw)',
                fontSize: 'min(24px, 4vw)'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
            >
              X
            </button>

            {/* Botões Sim e Não */}
            <div 
              className="absolute flex"
              style={{
                bottom: 'min(64px, 12.8%)',
                left: 'min(180px, 40%)',
                transform: 'translateX(-50%)',
                gap: 'min(120px, 25%)'
              }}
            >
              <button
                onClick={handleIrParaDadolandia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Sim
              </button>

              <button
                onClick={handleFecharPopupDadolandia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup de Confirmação para Ilha da Ciência */}
      {popupCienciaAberto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div 
            className="relative"
            style={{
              backgroundImage: `url(${popupJogarIlha})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              width: 'min(600px, 90vw)',
              height: 'min(500px, 90vh)',
              aspectRatio: '6/5'
            }}
          >
            {/* Botão X para fechar - Posição original responsiva */}
            <button
              onClick={handleFecharPopupCiencia}
              className="absolute text-white rounded-full flex items-center justify-center font-bold transition-colors duration-200 shadow-lg"
              style={{ 
                backgroundColor: '#563066',
                top: 'min(160px, 32%)',
                left: 'min(24px, 4%)',
                width: 'min(64px, 10.7vw)',
                height: 'min(64px, 10.7vw)',
                fontSize: 'min(24px, 4vw)'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
            >
              X
            </button>

            {/* Botões Sim e Não - Posição original responsiva */}
            <div 
              className="absolute flex"
              style={{
                bottom: 'min(64px, 12.8%)',
                left: 'min(180px, 40%)',
                transform: 'translateX(-50%)',
                gap: 'min(120px, 25%)'
              }}
            >
              {/* Botão Sim */}
              <button
                onClick={handleIrParaCiencia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Sim
              </button>

              {/* Botão Não */}
              <button
                onClick={handleFecharPopupCiencia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup de Confirmação para Ilha da Matemática */}
      {popupMatematicaAberto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div 
            className="relative"
            style={{
              backgroundImage: `url(${popupJogarIlha})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              width: 'min(600px, 90vw)',
              height: 'min(500px, 90vh)',
              aspectRatio: '6/5'
            }}
          >
            <button
              onClick={handleFecharPopupMatematica}
              className="absolute text-white rounded-full flex items-center justify-center font-bold transition-colors duration-200 shadow-lg"
              style={{ 
                backgroundColor: '#563066',
                top: 'min(160px, 32%)',
                left: 'min(24px, 4%)',
                width: 'min(64px, 10.7vw)',
                height: 'min(64px, 10.7vw)',
                fontSize: 'min(24px, 4vw)'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
            >
              X
            </button>

            <div 
              className="absolute flex"
              style={{
                bottom: 'min(64px, 12.8%)',
                left: 'min(180px, 40%)',
                transform: 'translateX(-50%)',
                gap: 'min(120px, 25%)'
              }}
            >
              <button
                onClick={handleIrParaMatematica}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Sim
              </button>

              <button
                onClick={handleFecharPopupMatematica}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup de Confirmação para Ilha da Geografia */}
      {popupGeografiaAberto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div 
            className="relative"
            style={{
              backgroundImage: `url(${popupJogarIlha})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              width: 'min(600px, 90vw)',
              height: 'min(500px, 90vh)',
              aspectRatio: '6/5'
            }}
          >
            <button
              onClick={handleFecharPopupGeografia}
              className="absolute text-white rounded-full flex items-center justify-center font-bold transition-colors duration-200 shadow-lg"
              style={{ 
                backgroundColor: '#563066',
                top: 'min(160px, 32%)',
                left: 'min(24px, 4%)',
                width: 'min(64px, 10.7vw)',
                height: 'min(64px, 10.7vw)',
                fontSize: 'min(24px, 4vw)'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
            >
              X
            </button>

            <div 
              className="absolute flex"
              style={{
                bottom: 'min(64px, 12.8%)',
                left: 'min(180px, 40%)',
                transform: 'translateX(-50%)',
                gap: 'min(120px, 25%)'
              }}
            >
              <button
                onClick={handleIrParaGeografia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Sim
              </button>

              <button
                onClick={handleFecharPopupGeografia}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Popup de Confirmação para Ilha da História */}
      {popupHistoriaAberto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div 
            className="relative"
            style={{
              backgroundImage: `url(${popupJogarIlha})`,
              backgroundSize: 'contain',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              width: 'min(600px, 90vw)',
              height: 'min(500px, 90vh)',
              aspectRatio: '6/5'
            }}
          >
            <button
              onClick={handleFecharPopupHistoria}
              className="absolute text-white rounded-full flex items-center justify-center font-bold transition-colors duration-200 shadow-lg"
              style={{ 
                backgroundColor: '#563066',
                top: 'min(160px, 32%)',
                left: 'min(24px, 4%)',
                width: 'min(64px, 10.7vw)',
                height: 'min(64px, 10.7vw)',
                fontSize: 'min(24px, 4vw)'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
            >
              X
            </button>

            <div 
              className="absolute flex"
              style={{
                bottom: 'min(64px, 12.8%)',
                left: 'min(180px, 40%)',
                transform: 'translateX(-50%)',
                gap: 'min(120px, 25%)'
              }}
            >
              <button
                onClick={handleIrParaHistoria}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Sim
              </button>

              <button
                onClick={handleFecharPopupHistoria}
                className="text-white font-bold rounded-full shadow-lg transform hover:scale-105 transition-all duration-200"
                style={{ 
                  backgroundColor: '#563066',
                  paddingTop: 'min(20px, 4%)',
                  paddingBottom: 'min(20px, 4%)',
                  paddingLeft: 'min(150px, 28%)',
                  paddingRight: 'min(150px, 28%)',
                  fontSize: 'min(30px, 5vw)'
                }}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
};

export default TelaTrilha;
