import React, { useState } from 'react';
import fundoTelaLogin from '../assets/fundo-tela-login.png';
import logoDesconecta from "../assets/Titulo nome do jogo.png";
import mascoteImg from "../assets/Dadinho sentado segurando quebra-cabeca.png";
import popupErroCadastro from '../assets/popup-erro-cadastro.png';
import popupCadastroSucesso from '../assets/popup-cadastro-sucesso.png';

// --- Componente Principal: Tela de Cadastro ---
// Tela de cadastro com fundo e dois campos de entrada centralizados
const TelaCadastro = ({ voltarParaInicial, irParaLogin }) => {
  const [apelido, setApelido] = useState('');
  const [senha, setSenha] = useState('');
  const [mostrarPopup, setMostrarPopup] = useState(false);
  const [mostrarPopupSucesso, setMostrarPopupSucesso] = useState(false);

  const handleCadastro = async () => {
    console.log('Dados do cadastro:', { apelido, senha });
    
    // Validação simples
    if (!apelido || !senha) {
      setMostrarPopup(true);
      return;
    }

    try {
      // const response = await fetch('http://localhost:8080/api/aluno/cadastro', {
      const response = await fetch(`${window.location.origin}/desconecta/api/aluno/cadastro`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          apelido: apelido,
          senha: senha
        }),
      });

      if (response.ok) {
        const resultado = await response.json(); // Pega o valor booleano retornado
        if (resultado === true) {
          console.log('Cadastro realizado com sucesso!');
          setMostrarPopupSucesso(true); // Mostra popup de sucesso
          // Limpa os campos
          setApelido('');
          setSenha('');
          // Popup só fecha quando usuário clicar no X
        } else {
          console.error('Erro no cadastro - Apelido já existe ou dados inválidos');
          setMostrarPopup(true); // Mostra popup de erro quando retorna false
        }
      } else {
        console.error('Erro no cadastro');
        setMostrarPopup(true);
      }
    } catch (error) {
      console.error('Erro na requisição:', error);
      setMostrarPopup(true);
    }
  };

  const handleIrParaLogin = () => {
    console.log('Indo para tela de login...');
    irParaLogin();
  };

  return (
    <main 
      className="h-screen w-full bg-cover bg-center bg-no-repeat relative overflow-hidden"
      style={{ backgroundImage: `url(${fundoTelaLogin})` }}
    >
      {/* --- LOGO (Posicionada Independentemente no Topo) --- */}
        <div
          className="absolute"
          style={{
            top: "2%",
            left: "68%",
            transform: "translateX(-50%)",
          }}
        >
          <img
            src={logoDesconecta}
            alt="Logo Desconecta"
            className="w-[1100px] max-w-[80vw]"
          />
        </div>

      
      {/* --- IMAGEM DO MASCOTE (canto inferior esquerdo) --- */}
        <div className="absolute -bottom-20 left-9 p-4">
          <img
            src={mascoteImg}
            alt="Mascote do Jogo"
            className="w-[320px] scale-x-[-1]"
          />
        </div>

      {/* Container específico para as caixas de texto com posicionamento personalizado */}
      <div 
        className="absolute flex flex-col gap-12 max-w-xs items-center"
        style={{ 
          top: '25%',    /* ← MOVER CAIXAS VERTICALMENTE: altere este valor */
          left: '50%',   /* ← MOVER CAIXAS HORIZONTALMENTE: altere este valor */
          transform: 'translate(40%, 50%)' /* ← Mantém caixas centralizadas no ponto */
        }}
      >
        {/* Campo Apelido */}
          <input
            type="text"
            placeholder="Nome"
            value={apelido}
            onChange={(e) => setApelido(e.target.value)}
            className="px-8 py-4 text-gray-800 font-bold text-2xl rounded-2xl shadow-lg border-2 border-purple-800 focus:outline-none focus:border-purple-900 transition-all duration-300"
            style={{ backgroundColor: '#dbedee' }}
          />
          
          {/* Campo Senha */}
          <input
            type="password"
            placeholder="Senha"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className="px-8 py-4 text-gray-800 font-bold text-2xl rounded-2xl shadow-lg border-2 border-purple-800 focus:outline-none focus:border-purple-900 transition-all duration-300"
            style={{ backgroundColor: '#dbedee' }}
          />
        </div>

        {/* POPUP DE SUCESSO - POSICIONAMENTO LIVRE */}
        {mostrarPopupSucesso && (
          <div className="fixed inset-0 z-50 bg-black bg-opacity-50">
            <div 
              className="absolute"
              style={{
                /* ← POSIÇÃO HORIZONTAL: altere este valor */
                left: '50%',     /* Opções: '10%', '30%', '50%', '70%', '90%' */
                /* ← POSIÇÃO VERTICAL: altere este valor */
                top: '50%',      /* Opções: '10%', '30%', '50%', '70%', '90%' */
                /* ← CENTRALIZAÇÃO: ajuste conforme necessário */
                transform: 'translate(-50%, -50%)'  /* -50% = centralizado, 0% = canto */
              }}
            >
              {/* Botão X para fechar - BOLINHA COM FONTE GROSSA */}
              <button
                onClick={() => {
                  setMostrarPopupSucesso(false);
                  irParaLogin(); // Redireciona ao fechar
                }}
                className="absolute rounded-full text-white font-black z-10 hover:opacity-80 hover:scale-105 transition-all duration-300 flex items-center justify-center shadow-lg border-2 border-white"
                style={{ 
                  backgroundColor: '#563066',
                  /* ← POSIÇÃO HORIZONTAL: altere este valor */
                  right: 'min(620px, 40vw)',   /* Movido para a direita (valor menor) */
                  /* ← POSIÇÃO VERTICAL: altere este valor */
                  top: 'min(180px, 22vh)',     /* Movido para baixo (valor maior) */
                  /* ← TAMANHOS RESPONSIVOS */
                  width: 'min(48px, 6vw)',
                  height: 'min(48px, 6vw)',
                  fontSize: 'min(24px, 3vw)'
                }}
              >
                ✕
              </button>
              
              {/* Imagem do popup de sucesso - TAMANHO AUMENTADO */}
              <img 
                src={popupCadastroSucesso} 
                alt="Cadastro realizado com sucesso" 
                className="w-[72rem] h-[28rem] object-contain"
              />
            </div>
          </div>
        )}

        {/* POPUP DE ERRO - POSICIONAMENTO LIVRE */}
        {mostrarPopup && (
          <div className="fixed inset-0 z-50 bg-black bg-opacity-50">
            <div 
              className="absolute"
              style={{
                /* ← POSIÇÃO HORIZONTAL: altere este valor */
                left: '50%',     /* Opções: '10%', '30%', '50%', '70%', '90%' */
                /* ← POSIÇÃO VERTICAL: altere este valor */
                top: '50%',      /* Opções: '10%', '30%', '50%', '70%', '90%' */
                /* ← CENTRALIZAÇÃO: ajuste conforme necessário */
                transform: 'translate(-50%, -50%)'  /* -50% = centralizado, 0% = canto */
              }}
            >
              {/* Botão X para fechar - BOLINHA COM FONTE GROSSA */}
              <button
                onClick={() => setMostrarPopup(false)}
                className="absolute rounded-full text-white font-black z-10 hover:opacity-80 hover:scale-105 transition-all duration-300 flex items-center justify-center shadow-lg border-2 border-white"
                style={{ 
                  backgroundColor: '#563066',
                  /* ← POSIÇÃO HORIZONTAL: altere este valor */
                  right: 'min(600px, 37vw)',   /* Responsivo: mantém posição em telas menores */
                  /* ← POSIÇÃO VERTICAL: altere este valor */
                  top: 'min(96px, 12vh)',      /* Responsivo: mantém posição em telas menores */
                  /* ← TAMANHOS RESPONSIVOS */
                  width: 'min(48px, 6vw)',
                  height: 'min(48px, 6vw)',
                  fontSize: 'min(24px, 3vw)'
                }}
              >
                ✕
              </button>
              
              {/* Imagem do popup */}
              <img 
                src={popupErroCadastro} 
                alt="Erro no cadastro" 
                className="w-[64rem] h-96 object-contain"
              />
            </div>
          </div>
        )}

        {/* Container separado para o botão com posicionamento independente */}
        <div 
          className="absolute flex flex-col gap-6 items-center"
          style={{ 
            top: '60%',    /* ← MOVER BOTÃO VERTICALMENTE: altere este valor */
            left: '50%',   /* ← MOVER BOTÃO HORIZONTALMENTE: altere este valor */
            transform: 'translate(10%, 50%)' /* ← Mantém botão centralizado no ponto */
          }}
        >
          <button
            onClick={handleCadastro}
            className="px-48 py-7 text-white font-bold text-2xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300"
            style={{ backgroundColor: '#563066', ':hover': { backgroundColor: '#4a2857' } }}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
          >
            Cadastrar
          </button>
          
          {/* Texto com parte clicável e não clicável */}
          <div className="text-white font-bold text-xl">
            <span className="text-black">Já tem cadastro? </span>
            <span
              onClick={handleIrParaLogin}
              className="cursor-pointer hover:text-purple-300 underline hover:no-underline transition-all duration-300"
            >
              Entre Agora
            </span>
          </div>
        </div>
    </main>
  );
};

export default TelaCadastro;
