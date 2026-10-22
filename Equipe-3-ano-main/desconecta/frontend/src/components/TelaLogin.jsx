import React, { useState } from 'react';
import fundoTelaLogin from '../assets/fundo-tela-login.png';
import logoDesconecta from "../assets/Titulo nome do jogo.png";
import mascoteImg from "../assets/Dadinho sentado segurando quebra-cabeca.png";
import popupErroEntrar from '../assets/popup-erro-entrar.png';

// --- Componente Principal: Tela de Login ---
// Tela de login com fundo e dois campos de entrada centralizados
const TelaLogin = ({ voltarParaInicial, irParaCadastro, irParaJogo }) => {
  const [apelido, setApelido] = useState('');
  const [senha, setSenha] = useState('');
  const [mostrarPopupErro, setMostrarPopupErro] = useState(false);

  const handleLogin = async () => {
    console.log('Dados do login:', { apelido, senha });
    
    // Validação básica
    if (!apelido || !senha) {
      setMostrarPopupErro(true);
      return;
    }

    // --- MODO DE TESTE ---
    // Se o apelido for "teste" e a senha "123", o login é bem-sucedido
    // e a chamada para a API é ignorada.
    if (apelido === 'teste' && senha === '123') {
      console.log('Login de teste realizado com sucesso!');
      setApelido('');
      setSenha('');
      irParaJogo(); // Navega para a próxima tela
      return; // Para a execução da função aqui
    } 
    

    try {
      // Dados que serão enviados para o backend
      const dadosLogin = {
        apelido: apelido,
        senha: senha
      };

      // Requisição para o endpoint de login
      // const response = await fetch('http://localhost:8080/api/aluno/login', {
      const response = await fetch(`${window.location.origin}/desconecta/api/aluno/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(dadosLogin)
      });

      // Verificar se a requisição foi bem-sucedida
      if (response.ok) {
        const alunoId = await response.json();
        if (alunoId !== 0) {
          console.log('Login realizado com sucesso! ID do aluno:', alunoId);
          
          // Armazenar o ID do aluno no localStorage para uso em outras telas
          localStorage.setItem('alunoId', alunoId.toString());
          localStorage.setItem('alunoApelido', apelido); // Também guardar o apelido para referência
          
          // Limpar os campos após sucesso
          setApelido('');
          setSenha('');
          
          // Redireciona diretamente para a tela do jogo
          irParaJogo();
        } else {
          console.log('Credenciais inválidas');
          setMostrarPopupErro(true);
        }
      } else {
        setMostrarPopupErro(true);
      }
    } catch (error) {
      console.error('Erro ao realizar login:', error);
      setMostrarPopupErro(true);
    }
  };

  const handleIrParaCadastro = () => {
    console.log('Indo para tela de cadastro...');
    irParaCadastro();
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
          onClick={handleLogin}
          className="px-52 py-7 text-white font-bold text-2xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300"
          style={{ backgroundColor: '#563066', ':hover': { backgroundColor: '#4a2857' } }}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#4a2857'}
          onMouseLeave={(e) => e.target.style.backgroundColor = '#563066'}
        >
          Entrar
        </button>
        
        {/* Texto com parte clicável e não clicável */}
        <div className="text-white font-bold text-xl">
          <span className="text-black">NÃO tem cadastro? </span>
          <span
            onClick={handleIrParaCadastro}
            className="cursor-pointer hover:text-purple-300 underline hover:no-underline transition-all duration-300"
          >
            Cadastre-se
          </span>
        </div>
      </div>

      {/* POPUP DE ERRO - Com botão X para fechar */}
      {mostrarPopupErro && (
        <div className="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
          <div className="relative">
            {/* Botão X para fechar - BOLINHA COM FONTE GROSSA */}
            <button
              onClick={() => setMostrarPopupErro(false)}
              className="absolute w-12 h-12 rounded-full text-white font-black text-2xl z-10 hover:opacity-80 hover:scale-105 transition-all duration-300 flex items-center justify-center shadow-lg border-2 border-white"
              style={{ 
                backgroundColor: '#563066',
                /* ← POSIÇÃO HORIZONTAL: altere este valor */
                right: '716px',   /* Valores: -16px, -8px, 0px, 8px, 16px, etc. */
                /* ← POSIÇÃO VERTICAL: altere este valor */
                top: '96px'      /* Valores: -16px, -8px, 0px, 8px, 16px, etc. */
              }}
            >
              ✕
            </button>
            
            {/* Imagem do popup */}
            <img 
              src={popupErroEntrar} 
              alt="Erro ao entrar" 
              className="w-[64rem] h-96 object-contain"
            />
          </div>
        </div>
      )}
    </main>
  );
};

export default TelaLogin;
