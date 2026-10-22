import React, { useEffect } from 'react';
import MenuNavegacao from './ui/MenuNavegacao';
import useAlunoLogado from '../hooks/useAlunoLogado';

// Assets para o fundo
import fundoPontuacao from '../assets/fundo-pontuacao.png';
import estrelas from '../assets/Estrela.png'
import parabens from '../assets/parabens.png'
import dadinhoConfete from '../assets/dadinhoConfete.png'

// --- Componente: Tela Pontuação ---
const TelaPontuacao = ({ onVoltarTrilha, onVoltarMenu, onAbrirRanking, ilhaCompletada = 1, nomeIlhaJogada = null }) => {
  const { alunoId, isLogado } = useAlunoLogado();
  
  // Buscar dados de pontuação do sessionStorage
  const dadosPontuacaoString = sessionStorage.getItem('dadosPontuacao');
  const dadosPontuacao = dadosPontuacaoString ? JSON.parse(dadosPontuacaoString) : null;
  
  // Valores da pontuação - usa dados do jogo se disponíveis, caso contrário usa valores padrão
  const tempoSegundos = dadosPontuacao?.tempo ?? 120;
  const tentativas = dadosPontuacao?.tentativas ?? 3;
  const pontos = dadosPontuacao?.pontos ?? 850;
  
  // Flag para saber se já foi concluído (vem do sessionStorage)
  const jaFoiConcluido = dadosPontuacao?.jaFoiConcluido ?? false;

  // Função para avançar a ilha do aluno APENAS se for a primeira conclusão
  const avancarIlhaAluno = async () => {
    if (!alunoId || !isLogado) {
      console.log('⚠️ Aluno não logado, não avançará ilha');
      return false;
    }

    // Se não temos o nome da ilha, não podemos verificar
    if (!nomeIlhaJogada) {
      console.log('⚠️ Nome da ilha não fornecido. Não avançará ilha.');
      return false;
    }

    try {
      console.log('🔍 Verificando se deve avançar ilha...');
      console.log('Ilha jogada:', nomeIlhaJogada);
      
      // Buscar o ID do progresso do aluno primeiro
      // const responseIdProgresso = await fetch(`http://localhost:8080/api/progresso-aluno/id/${alunoId}`);
      const responseIdProgresso = await fetch(`${window.location.origin}/desconecta/api/progresso-aluno/id/${alunoId}`);
      
      if (!responseIdProgresso.ok) {
        console.error('❌ Erro ao buscar ID do progresso');
        return false;
      }
      
      const idProgressoAluno = await responseIdProgresso.json();
      console.log('ID do progresso do aluno:', idProgressoAluno);
      
      if (!idProgressoAluno) {
        console.error('❌ ID do progresso não encontrado');
        return false;
      }
      
      // VERIFICAÇÃO UNIFICADA: Verificar se a ilha já foi jogada (para TODAS as ilhas)
      console.log(`ℹ️ Verificando se ${nomeIlhaJogada} já foi jogada...`);
      
      const responseFoiJogada = await fetch(
        // `http://localhost:8080/api/ilhas/verificar-foi-jogada?idProgressoAluno=${idProgressoAluno}&nomeIlha=${nomeIlhaJogada}`
        `${window.location.origin}/desconecta/api/ilhas/verificar-foi-jogada?idProgressoAluno=${idProgressoAluno}&nomeIlha=${nomeIlhaJogada}`
      );
      
      if (!responseFoiJogada.ok) {
        console.error('❌ Erro ao verificar se ilha foi jogada');
        return false;
      }
      
      const foiJogada = await responseFoiJogada.json();
      console.log(`${nomeIlhaJogada} já foi jogada?`, foiJogada);
      
      // Só avança se a ilha NÃO foi jogada (foiJogada === false)
      if (foiJogada) {
        console.log(`⚠️ ${nomeIlhaJogada} já foi jogada anteriormente. Não avançará para próxima ilha.`);
        return false;
      }
      
      console.log(`✅ Primeira vez jogando ${nomeIlhaJogada}! Avançando ilha...`);
      
      // Chamar diretamente avancarIlha - ele já faz todas as validações necessárias
      console.log('Chamando avancar-ilha com PUT...');
      // const responseAvancar = await fetch(`http://localhost:8080/api/ilhas/avancar-ilha/${idProgressoAluno}`, {
        const responseAvancar = await fetch(`${window.location.origin}/desconecta/api/ilhas/avancar-ilha/${idProgressoAluno}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Status da resposta:', responseAvancar.status);
      
      if (responseAvancar.ok) {
        const novaPosicao = await responseAvancar.json();
        
        if (novaPosicao !== -1) {
          console.log('✅ Ilha avançada para posição:', novaPosicao);
          console.log('✅ Próxima ilha foi criada e desbloqueada automaticamente');
          return true;
        } else {
          console.log('⚠️ Não foi possível avançar (última ilha ou ilha já avançada)');
          return false;
        }
      } else {
        const errorText = await responseAvancar.text();
        console.error('❌ Erro ao avançar ilha:', errorText);
        return false;
      }
      
    } catch (error) {
      console.error('❌ Erro ao avançar ilha:', error);
      return false;
    }
  };

  // Função para voltar à trilha e forçar recarregamento
  const handleVoltarTrilha = async () => {
    // Limpar APENAS dados de pontuação do sessionStorage
    // NÃO remove dados do localStorage (alunoId, apelidoAluno, etc.)
    sessionStorage.removeItem('dadosPontuacao');
    
    // Sinaliza que o progresso foi atualizado para forçar recarregamento
    localStorage.setItem('progressoAtualizado', 'true');
    onVoltarTrilha();
  };

  // Avançar a ilha quando a tela de pontuação for carregada (apenas uma vez)
  useEffect(() => {
    let executado = false;
    
    const executarAvanco = async () => {
      if (!executado && alunoId && isLogado) {
        executado = true;
        await avancarIlhaAluno();
      }
    };
    
    executarAvanco();
  }, [alunoId, isLogado]);
  return (
    <main 
      // className="min-h-screen relative overflow-hidden w-full"
      className="min-h-screen w-full relative flex flex-col items-center justify-center overflow-hidden"
      style={{
        backgroundImage: `url(${fundoPontuacao})`,
        backgroundSize:'cover',
        // backgroundSize: '100%',
        backgroundPosition:'center',
        // backgroundPosition: 'center center',
        backgroundRepeat: 'no-repeat'
      }}
    >
      <div className="relative flex flex-col items-center justify-between" style={{ height: '600px' }}>
      <img
        src={estrelas}
        // className="absolute left-1/2 top-[5vh] transform -translate-x-1/2 w-96 scale-y-105"
        className="w-96 scale-y-105"
      />

      <img
        src={parabens}
        // className="absolute center left-1/2 transform -translate-x-1/2 translate-y-44 w-96 scale-y-105"
        // className="absolute left-1/2 top-[32vh] transform -translate-x-1/2 w-96 scale-y-105"
        className="w-96 scale-y-105"
      />



      {/* Menu de Navegação Reutilizável */}
      <MenuNavegacao 
        onVoltarTrilha={handleVoltarTrilha}
        onVoltarMenu={handleVoltarTrilha}
        onAbrirRanking={onAbrirRanking}
        posicao="top-right"
      />

      {/* Conteúdo da tela de pontuação */}
      <div className="w-full h-screen flex items-end justify-center pb-3">
        <div className="text-white">
          <div className="text-5xl font-sans font-bold leading-relaxed mb-2">
            <div className="mb-2 text-left">Tempo ...................... {tempoSegundos}</div>
            <div className="mb-4 text-left">Tentativas ................. {tentativas}</div>
            <div className="text-5xl font-bold text-center mb-5" style={{ color: '#feeb6c' }}>+{pontos} pontos</div>
          </div>
          
          {/* Botão Concluir */}
          <div className="text-center">
            <button
              onClick={handleVoltarTrilha}
              className="text-white font-bold px-16 pb-1 pt-0.5 rounded-full text-3xl transition-all duration-300 transform hover:scale-105 shadow-2xl"
              style={{ 
                backgroundColor: '#f93d6e',
                ':hover': { backgroundColor: '#e02a5b' }
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#e02a5b'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#f93d6e'}
            >
              CONCLUIR
            </button>
          </div>
        </div>
      </div>
    </div>
    <img
        src={dadinhoConfete}
        className="absolute bottom-0 left-6 w-96"
    />
  </main>
  );
};

export default TelaPontuacao;
