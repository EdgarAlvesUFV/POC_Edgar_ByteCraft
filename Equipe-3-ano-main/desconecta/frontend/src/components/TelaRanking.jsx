import React, { useState, useEffect } from 'react';
import fundoRanking from '../assets/imgTelaRanking/FundoCruClassificação.svg';
import letreiroClassificacao from '../assets/imgTelaRanking/Letreiro classificacao.svg';
import MenuNavegacao from './ui/MenuNavegacao';

// --- Componente Principal: Tela de Ranking ---
const TelaRanking = ({ onVoltar }) => {
  const [rankings, setRankings] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [classificacaoAluno, setClassificacaoAluno] = useState(null);

  // Função para limpar métricas e voltar para a trilha
  const handleVoltar = () => {
    // Limpar APENAS dados de pontuação do sessionStorage
    // NÃO remove dados do localStorage (alunoId, apelidoAluno, etc.)
    sessionStorage.removeItem('dadosPontuacao');
    console.log('🧹 Métricas do jogo limpas do sessionStorage (dados de login preservados)');
    
    // Chamar a função de voltar
    if (onVoltar) {
      onVoltar();
    }
  };

  // Função para buscar os rankings do backend
  const buscarRankings = async () => {
    try {
      setCarregando(true);
      
      // Buscar ranking geral
      // const response = await fetch('http://localhost:8080/api/classificacao/classificacaoGeral');
      const response = await fetch(`${window.location.origin}/desconecta/api/classificacao/classificacaoGeral`);
      
      if (!response.ok) {
        throw new Error('Erro ao carregar rankings');
      }
      
      const data = await response.json();
      
      // Transformar os dados da API para o formato esperado pelo componente
      const rankingsFormatados = data.map((item, index) => ({
        posicao: index + 1,
        nome: item.nomeAluno,
        pontos: item.pontuacaoTotalAluno
      }));
      
      setRankings(rankingsFormatados);
      setErro(null);
    } catch (error) {
      console.error('Erro ao buscar rankings:', error);
      setErro('Não foi possível carregar os rankings.');
    } finally {
      setCarregando(false);
    }
  };

  // Função para buscar a classificação do aluno logado
  const buscarClassificacaoAluno = async () => {
    const apelidoAluno = localStorage.getItem('alunoApelido');
    
    if (!apelidoAluno) {
      return;
    }

    try {
      const response = await fetch(
        // `http://localhost:8080/api/classificacao/classificacaoAluno?apelidoAluno=${encodeURIComponent(apelidoAluno)}`
        `${window.location.origin}/desconecta/api/classificacao/classificacaoAluno?apelidoAluno=${encodeURIComponent(apelidoAluno)}`
      );
      
      if (response.ok) {
        const dadosAluno = await response.json();
        
        if (!dadosAluno.erro) {
          setClassificacaoAluno({
            posicao: dadosAluno.posicao,
            nome: dadosAluno.nomeAluno,
            pontos: dadosAluno.pontuacaoTotalAluno
          });
        }
      }
    } catch (error) {
      console.error('Erro ao buscar classificação do aluno:', error);
    }
  };

  useEffect(() => {
    buscarRankings();
    buscarClassificacaoAluno();
  }, []);

  // Função para obter a cor do círculo da posição
  const getCorCirculoPosicao = (posicao) => {
    return posicao === 1 ? '#9970a3' : '#563066';
  };

  // Função para obter a cor do retângulo baseado na posição
  const getCorRetangulo = (posicao) => {
    switch (posicao) {
      case 1:
        return '#563066';
      case 2:
        return '#9970a3';
      case 3:
        return '#B888C5';
      default:
        return '#dbc0e3';
    }
  };

  return (
    <>
      <style>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 14px;
        }

        .custom-scrollbar::-webkit-scrollbar-track {
          background: transparent;
          margin: 8px 0;
        }

        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: rgba(255, 255, 255, 0.8);
          border-radius: 50px;
          border: 3px solid #257894;
          min-height: 60px;
        }

        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: rgba(255, 255, 255, 1);
        }

        /* Remove as setas (botões) da scrollbar */
        .custom-scrollbar::-webkit-scrollbar-button {
          display: none;
        }

        /* Firefox */
        .custom-scrollbar {
          scrollbar-width: thin;
          scrollbar-color: rgba(255, 255, 255, 0.8) transparent;
        }
      `}</style>
      
      <main
        className="h-screen w-full bg-cover bg-center bg-no-repeat relative overflow-hidden flex flex-col"
        style={{ 
          backgroundImage: `url(${fundoRanking})`,
          backgroundSize: 'cover',
          backgroundColor: '#4a919e'
        }}
      >
      {/* Menu de Navegação */}
      <MenuNavegacao 
        onVoltarTrilha={handleVoltar}
        onVoltarMenu={handleVoltar}
        onAbrirRanking={() => {}} // Já está no ranking
        posicao="top-right"
      />

      {/* Botão de Voltar */}
      <div className="absolute top-8 left-8 z-10">
        <button
          onClick={handleVoltar}
          className="flex items-center gap-2 px-6 py-3 text-white font-bold text-xl rounded-full shadow-lg transition-all duration-300"
          style={{ backgroundColor: '#563066' }}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
          onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
        >
          ← Voltar
        </button>
      </div>

      {/* Letreiro Classificação - Centralizado no topo */}
      <div className="w-full flex justify-center pt-4 pb-4">
        <img
          src={letreiroClassificacao}
          alt="Classificação"
          className="h-16 md:h-20 object-contain"
        />
      </div>

      {/* Container Principal - Retângulo Azul Claro */}
      <div className="flex-1 flex items-center justify-center px-4 pb-8">
        <div
          className="w-full max-w-6xl rounded-[50px] shadow-2xl p-10 flex flex-col"
          style={{
            backgroundColor: '#78b9be',
            minHeight: '570px',
            maxHeight: '72vh',
          }}
        >
          {/* Classificação do Aluno Logado - Fixa no topo */}
          {classificacaoAluno && (
            <div className="mb-3">
              <div
                className="flex items-center justify-between px-5 py-2.5 rounded-full shadow-lg max-w-5xl mx-auto"
                style={{
                  backgroundColor: '#257894',
                }}
              >
                {/* Círculo com Posição */}
                <div 
                  className="flex items-center justify-center rounded-full shadow-md"
                  style={{
                    backgroundColor: '#78b9be',
                    width: '50px',
                    height: '50px',
                    minWidth: '50px',
                  }}
                >
                  <span className="text-xl font-bold text-white">
                    {classificacaoAluno.posicao}
                  </span>
                </div>

                {/* Nome */}
                <div className="flex-1 text-left pl-4">
                  <span className="text-xl font-bold text-white">
                    {classificacaoAluno.nome}
                  </span>
                </div>

                {/* Pontos */}
                <div className="flex items-center gap-2">
                  <span className="text-xl font-bold text-white">
                    {classificacaoAluno.pontos} pontos
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Container Interno - Retângulo Azul Escuro com Scroll */}
          <div
            className="w-full mx-auto rounded-[30px] shadow-inner overflow-y-auto custom-scrollbar flex-1"
            style={{
              backgroundColor: '#257894',
              padding: '20px',
            }}
          >
            {carregando ? (
              <div className="text-center text-white text-2xl py-10">
                Carregando rankings...
              </div>
            ) : erro && rankings.length === 0 ? (
              <div className="text-center text-white text-2xl py-10">
                {erro}
              </div>
            ) : (
              <div className="space-y-3">
                {rankings.map((rank, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between px-5 py-2.5 rounded-full shadow-lg transition-all duration-300"
                    style={{
                      backgroundColor: getCorRetangulo(rank.posicao),
                    }}
                  >
                    {/* Círculo com Posição */}
                    <div 
                      className="flex items-center justify-center rounded-full shadow-md"
                      style={{
                        backgroundColor: getCorCirculoPosicao(rank.posicao),
                        width: '50px',
                        height: '50px',
                        minWidth: '50px',
                      }}
                    >
                      <span className="text-2xl font-bold text-white">
                        {rank.posicao}
                      </span>
                    </div>

                    {/* Nome */}
                    <div className="flex-1 text-left pl-4">
                      <span className="text-xl font-bold text-white">
                        {rank.nome}
                      </span>
                    </div>

                    {/* Pontos */}
                    <div className="flex items-center gap-2">
                      <span className="text-xl font-bold text-white">
                        {rank.pontos} pontos
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
      </main>
    </>
  );
};

export default TelaRanking;
