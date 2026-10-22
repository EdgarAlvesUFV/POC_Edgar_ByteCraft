import React from "react";
import mascoteImg from "../assets/Dadinho sentado segurando quebra-cabeca.png";
import bgImg from "../assets/fundo-tela-login.png";
import logoDesconecta from "../assets/Titulo nome do jogo.png";

const TelaInicial = ({ irParaLogin, irParaCadastro }) => {
  const handleEntrar = () => irParaLogin();
  const handleCadastrar = () => irParaCadastro();

  return (
    <main
      className="min-h-screen w-full bg-cover bg-center bg-no-repeat relative overflow-hidden"
      style={{
        backgroundImage: `url(${bgImg})`,
      }}
    >
      {/* --- IMAGEM DO MASCOTE (canto inferior esquerdo) --- */}
      <div className="absolute -bottom-20 left-9 p-4">
        <img
          src={mascoteImg}
          alt="Mascote do Jogo"
          className="w-[320px] scale-x-[-1]"
        />
      </div>

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

      {/* --- BOTÕES (Posicionados Independentemente) --- */}
      <div
        className="absolute flex flex-col items-center gap-16"
        style={{
          top: "60%",
          left: "70%",
          transform: "translate(-50%, -50%)",
        }}
      >
        <button
          onClick={handleEntrar}
          className="w-[600px] py-7 text-white font-bold text-5xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300"
          style={{ backgroundColor: "#563066" }}
        >
          Entrar
        </button>

        <button
          onClick={handleCadastrar}
          className="w-[600px] py-7 text-white font-bold text-5xl rounded-full shadow-lg transform hover:scale-105 transition-all duration-300"
          style={{ backgroundColor: "#563066" }}
        >
          Cadastrar
        </button>
      </div>
    </main>
  );
};

export default TelaInicial;

