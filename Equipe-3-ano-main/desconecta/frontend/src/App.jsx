import React, { useState } from "react";
import TelaInicial from "./components/TelaInicial";
import TelaCadastro from "./components/TelaCadastro";
import TelaLogin from "./components/TelaLogin";
import TelaJogo from "./components/TelaJogo"; // Importa a nova tela do jogo

function App() {
  const [tela, setTela] = useState("inicial");

  return (
    <>
      {tela === "inicial" && (
        <TelaInicial
          irParaLogin={() => setTela("login")}
          irParaCadastro={() => setTela("cadastro")}
        />
      )}
      {tela === "login" && (
        <TelaLogin
          voltarParaInicial={() => setTela("inicial")}
          irParaCadastro={() => setTela("cadastro")}
          // A função para ir para o jogo é passada aqui!
          irParaJogo={() => setTela("jogo")} 
        />
      )}
      {tela === "cadastro" && (
        <TelaCadastro
          voltarParaInicial={() => setTela("inicial")}
          irParaLogin={() => setTela("login")}
        />
      )}
      {tela === "jogo" && (
        <TelaJogo 
          voltarParaInicial={() => setTela("inicial")}
        />
      )}
    </>
  );
}

export default App;
