import React, { useState } from 'react';
import PopupTutorial from '../PopupTutorial';

// Importar imagem de fundo do menu
import menuBarraFundo from '../../assets/Menu-barra.png';

// Importar ícone do menu
import navBarIcon from '../../assets/MenuLateral/NavBar.png';

// Importar imagens dos botões do menu lateral
import menuVoltarTrilha from '../../assets/MenuLateral/MenuVoltarTrilha.png';
import menuPontuacao from '../../assets/MenuLateral/MenuPontuacao.png';
import menuSair from '../../assets/MenuLateral/MenuSair.png';
import menuTutorial from '../../assets/MenuLateral/MenuTutorial.png';

// --- Componente: Menu de Navegação Reutilizável ---
const MenuNavegacao = ({ 
  onVoltarTrilha, 
  onVoltarMenu,
  onSairConta,
  onAbrirRanking,
  posicao = "top-right", // "top-right", "top-left", "bottom-right", "bottom-left"
  distanciaDoTopo = 180, // Distância regulável do topo do background do menu
  tipoTutorial = "todos" // "conecta", "caca-palavras", "todos"
}) => {
  const [menuAberto, setMenuAberto] = useState(false);
  const [tutorialPopupAberto, setTutorialPopupAberto] = useState(false);

  const toggleMenu = () => {
    setMenuAberto(!menuAberto);
  };

  // Definir posicionamento baseado na prop
  const getPosicaoClasses = () => {
    switch (posicao) {
      case "top-left":
        return "fixed top-4 left-4 z-30";
      case "bottom-right":
        return "fixed bottom-4 right-4 z-30";
      case "bottom-left":
        return "fixed bottom-4 left-4 z-30";
      default: // "top-right"
        return "fixed top-4 right-4 z-30";
    }
  };

  // Definir posição do dropdown baseado na posição do botão
  const getDropdownClasses = () => {
    switch (posicao) {
      case "top-left":
        return "absolute top-16 left-0";
      case "bottom-right":
        return "absolute bottom-16 right-0";
      case "bottom-left":
        return "absolute bottom-16 left-0";
      default: // "top-right"
        return "absolute top-16 right-0";
    }
  };

  return (
    <>
      {/* Overlay para fechar menu quando clicar fora */}
      {menuAberto && (
        <div 
          className="fixed inset-0 z-20" 
          onClick={() => setMenuAberto(false)}
        ></div>
      )}
      
      <div className={getPosicaoClasses()}>
        <button
          onClick={(e) => {
            e.stopPropagation();
            toggleMenu();
          }}
          className="rounded-lg transition-all duration-300 relative z-50 hover:scale-110"
          style={{ 
            backgroundColor: 'transparent',
            padding: 0,
            border: 'none',
            outline: 'none'
          }}
        >
          <img 
            src={navBarIcon} 
            alt="Menu" 
            className="w-16 h-16"
            style={{ display: 'block' }}
          />
        </button>

        {/* Menu com Imagem de Fundo */}
        {menuAberto && (
          <div 
            className="fixed top-0 right-0 h-full w-1/3 z-50 transform transition-transform duration-300 ease-in-out"
            style={{
              backgroundImage: `url(${menuBarraFundo})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat'
            }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* Container dos botões com distância regulável do topo */}
            <div 
              className="flex flex-col space-y-4 px-6"
              style={{ marginTop: `${distanciaDoTopo}px` }}
            >
              {/* Botão 1: Voltar à Trilha */}
              <button
                onClick={() => {
                  if (onVoltarTrilha) onVoltarTrilha();
                  setMenuAberto(false);
                }}
                className="w-full py-4 px-6 text-white text-3xl font-semibold transition-all duration-200 rounded-lg flex items-center justify-start"
                style={{ 
                  backgroundColor: 'transparent',
                  border: '2px solid transparent'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                  e.target.style.borderColor = 'rgba(255, 255, 255, 0.3)';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                  e.target.style.borderColor = 'transparent';
                }}
              >
                <img 
                  src={menuVoltarTrilha} 
                  alt="Voltar à Trilha" 
                  className="w-16 h-17 mr-4"
                />
                Voltar à Trilha
              </button>

              {/* Botão 2: Tela de Pontuação (Ranking) */}
              <button
                onClick={() => {
                  setMenuAberto(false);
                  // Abre o ranking diretamente
                  if (onAbrirRanking) {
                    onAbrirRanking();
                  }
                }}
                className="w-full py-4 px-6 text-white text-3xl font-semibold transition-all duration-200 rounded-lg flex items-center justify-start"
                style={{ 
                  backgroundColor: 'transparent',
                  border: '2px solid transparent'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                  e.target.style.borderColor = 'rgba(255, 255, 255, 0.3)';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                  e.target.style.borderColor = 'transparent';
                }}
              >
                <img 
                  src={menuPontuacao} 
                  alt="Pontuação" 
                  className="w-16 h-16 mr-4"
                />
                Pontuação
              </button>

              {/* Botão 3: Sair da Conta */}
              <button
                onClick={() => {
                  // Fazer logout do jogador
                  // Limpar dados de autenticação do localStorage/sessionStorage
                  localStorage.removeItem('authToken');
                  localStorage.removeItem('userSession');
                  localStorage.removeItem('playerData');
                  sessionStorage.clear();
                  
                  // Chamar função de callback se fornecida
                  if (onSairConta) onSairConta();
                  
                  // Fechar menu
                  setMenuAberto(false);
                  
                  // Redirecionar para tela inicial (recarregar página para resetar estado)
                  window.location.href = `${window.location.origin}/desconecta/`;
                }}
                className="w-full py-4 px-6 text-white text-3xl font-semibold transition-all duration-200 rounded-lg flex items-center justify-start"
                style={{ 
                  backgroundColor: 'transparent',
                  border: '2px solid transparent'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                  e.target.style.borderColor = 'rgba(255, 255, 255, 0.3)';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                  e.target.style.borderColor = 'transparent';
                }}
              >
                <img 
                  src={menuSair} 
                  alt="Sair da Conta" 
                  className="w-16 h-16 mr-4"
                />
                Sair da Conta
              </button>

              {/* Botão 4: Ver Tutorial (não implementado) */}
              <button
                onClick={() => {
                  setTutorialPopupAberto(true);
                  setMenuAberto(false);
                }}
                className="w-full py-4 px-6 text-white text-3xl font-semibold transition-all duration-200 rounded-lg flex items-center justify-start"
                style={{ 
                  backgroundColor: 'transparent',
                  border: '2px solid transparent'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = 'rgba(255, 255, 255, 0.1)';
                  e.target.style.borderColor = 'rgba(255, 255, 255, 0.3)';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                  e.target.style.borderColor = 'transparent';
                }}
              >
                <img 
                  src={menuTutorial} 
                  alt="Ver Tutorial" 
                  className="w-16 h-16 mr-4"
                />
                Ver Tutorial
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Popup de Tutorial */}
      <PopupTutorial 
        isOpen={tutorialPopupAberto}
        onClose={() => setTutorialPopupAberto(false)}
        tipoTutorial={tipoTutorial}
      />
    </>
  );
};

export default MenuNavegacao;
