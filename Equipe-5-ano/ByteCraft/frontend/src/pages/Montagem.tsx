import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import DraggableItem from "../components/DraggableItem";
import DropZone from "../components/DropZone";
import SucessoModal from "../components/SucessoModal";
import ErroModal from "../components/ErroModal";
import HistoriaModal from "../components/HistoriaModal";
import BoasVindasModal from "../components/BoasVindasModal";
import PainelPontuacao from "../components/PainelPontuacao";
import Cronometro from "../components/Cronometro";
import { useCronometro } from "../hooks/useCronometro";
import { usePontuacao } from "../hooks/usePontuacao";
import api from "../api/api";
import type { 
  PecaItem, 
  NivelDificuldade,
  Aluno as AlunoType,
  DialogoHistoria
} from "../types";
import {useSound} from "../hooks/useSounds";
import "./styles/Montagem.css";

import monitorCinza from "../assets/peças/monitor_cinza.png";
import monitorAzul from "../assets/peças/monitor_azul.png";
import monitorRosa from "../assets/peças/monitor_rosa.png";
import mouseCinza from "../assets/peças/mouse_cinza.png";
import mouseAzul from "../assets/peças/mouse_azul.png";
import mouseRosa from "../assets/peças/mouse_rosa.png";
import tecladoCinza from "../assets/peças/teclado_cinza.png";
import tecladoAzul from "../assets/peças/teclado_azul.png";
import tecladoRosa from "../assets/peças/teclado_rosa.png";
import caixaSomD from "../assets/peças/caixa_som_d.png";

const SEQUENCIA_PECAS = ["monitor_1", "teclado_1", "mouse_1", "caixa_som_1"];

const HISTORIAS_FIXAS: DialogoHistoria[] = [
  {
    id: "1",
    titulo: "A Tela do Computador",
    texto: "A tela é o dispositivo onde as imagens são exibidas. Ela permite que você veja tudo o que o computador faz.",
    pecaId: "monitor_1"
  },
  {
    id: "2",
    titulo: "O Teclado",
    texto: "O teclado serve para digitar comandos e textos, enviando informações ao computador.",
    pecaId: "teclado_1"
  },
  {
    id: "3",
    titulo: "O Mouse",
    texto: "O mouse é usado para mover o cursor e interagir com os elementos da tela.",
    pecaId: "mouse_1"
  },
  {
    id: "4",
    titulo: "A Caixa de Som",
    texto: "A caixa de som emite os sons e músicas que o computador produz.",
    pecaId: "caixa_som_1"
  }
];

const imagensMap: Record<string, Record<string, string>> = {
  monitor: {
    cinza: monitorCinza,
    azul: monitorAzul,
    rosa: monitorRosa,
  },
  mouse: {
    cinza: mouseCinza,
    azul: mouseAzul,
    rosa: mouseRosa,
  },
  teclado: {
    cinza: tecladoCinza,
    azul: tecladoAzul,
    rosa: tecladoRosa,
  },
  caixa_som: {
    padrão: caixaSomD,
  },
};

const MENSAGENS_MODAIS = {
  facil: {
    sucesso: [
      "Parabéns! Você acertou!",
      "Muito bem! Continue assim!",
      "Excelente! Peça encaixada corretamente!",
      "Você é um expert em montagem!",
      "Que legal! Você conseguiu!",
      "Perfeito! Mais uma peça no lugar certo!"
    ],
    erro: [
      "Quase lá! Tente novamente.",
      "Dica: Observe com atenção o formato da peça.",
      "Não desista! Você consegue!",
      "Lembre-se: cada peça tem seu lugar específico.",
      "Opa! Vamos tentar de novo?",
      "Preste atenção onde as outras peças se encaixam."
    ]
  },
  medio: {
    sucesso: [
      "Ótimo trabalho! Conexão estabelecida.",
      "Perfeito! Componente instalado corretamente.",
      "Funcionamento ideal confirmado!",
      "Sistema reconheceu o hardware!",
      "Montagem precisa! Continue assim.",
      "Interface conectada com sucesso!"
    ],
    erro: [
      "Conexão inadequada. Verifique os encaixes.",
      "Ajuste necessário - tente outra posição.",
      "Compatibilidade não detectada.",
      "Refaça a conexão cuidadosamente.",
      "Verifique a orientação do componente.",
      "Reposicione e tente novamente."
    ]
  },
  dificil: {
    sucesso: [
      "Precisão técnica exemplar!",
      "Configuração otimizada alcançada!",
      "Interface periférica sincronizada!",
      "Performance máxima estabelecida!",
      "Subsistema integrado com eficiência!",
      "Fluxo de dados estabilizado!"
    ],
    erro: [
      "Falha na interface de comunicação.",
      "Análise: Verifique protocolos de conexão.",
      "Erro de compatibilidade de barramento.",
      "Requer recalibração do subsistema.",
      "Interferência no sinal de dados detectada.",
      "Configuração não atende aos parâmetros técnicos."
    ]
  }
};

const obterMensagemAleatoria = (nivel: NivelDificuldade, tipo: 'sucesso' | 'erro'): string => {
  const mensagens = MENSAGENS_MODAIS[nivel]?.[tipo] || MENSAGENS_MODAIS.medio[tipo];
  return mensagens[Math.floor(Math.random() * mensagens.length)];
};

const MAPEAMENTO_CORRETO: Record<string, string> = {
  "monitor_1": "dropzone_monitor",
  "teclado_1": "dropzone_teclado",
  "mouse_1": "dropzone_mouse",
  "caixa_som_1": "dropzone_som"
};

const Montagem: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const aluno = location.state?.aluno as AlunoType | undefined;
  const nivelDificuldade = (location.state?.nivel || 'medio') as NivelDificuldade;
  
  const apelido = aluno?.apelido || localStorage.getItem("apelido") || "teste";
  const codigoSala = aluno?.codigoSala || Number(localStorage.getItem("codigoSala")) || 999;
  const nivel = (localStorage.getItem("nivelSelecionado") as NivelDificuldade) || "medio";

  const { playClick } = useSound();
  const { playSuccess } = useSound();
  const { playError } = useSound();

  const [mensagemSucesso, setMensagemSucesso] = useState("");
  const [mostrarBoasVindas, setMostrarBoasVindas] = useState(true);

  const {
    pontuacaoTotal,
    registrarTentativa,
    resetar: resetarPontuacao,
    obterResumo
  } = usePontuacao();

  const {
    tempo,
    iniciar: iniciarCronometro,
    pausar: pausarCronometro,
    resetar: resetarCronometro
  } = useCronometro();

  useEffect(() => {
    if (!mostrarBoasVindas) {
      iniciarCronometro();
    }
    return () => pausarCronometro();
  }, [mostrarBoasVindas]);

  const [pecas, setPecas] = useState<PecaItem[]>([
    { 
      id: "monitor_1", 
      label: "Monitor", 
      imagem: monitorCinza, 
      color: "cinza",
      descricao: "Tela para visualizar o computador"
    },
    { 
      id: "teclado_1", 
      label: "Teclado", 
      imagem: tecladoCinza, 
      color: "cinza",
      descricao: "Dispositivo para digitação"
    },
    { 
      id: "mouse_1", 
      label: "Mouse", 
      imagem: mouseCinza, 
      color: "cinza",
      descricao: "Dispositivo de controle"
    },
    { 
      id: "caixa_som_1", 
      label: "Caixa de Som", 
      imagem: caixaSomD, 
      color: "padrão",
      descricao: "Dispositivo de áudio"
    },
  ]);

  const [pecasColocadas, setPecasColocadas] = useState<Set<string>>(new Set());
  const [pecaSelecionada, setPecaSelecionada] = useState<string | null>(null);
  
  const [dropZoneDestacada, setDropZoneDestacada] = useState<string | null>(null);
  const [mostrarDica, setMostrarDica] = useState(false);
  const [dicaAtual, setDicaAtual] = useState("");
  
  const [showSucesso, setShowSucesso] = useState(false);
  const [pontosGanhos, setPontosGanhos] = useState(0);
  const [showErro, setShowErro] = useState(false);
  const [mensagemErro, setMensagemErro] = useState("");
  
  const [indicePecaAtual, setIndicePecaAtual] = useState(0);
  const [tentativasPeca, setTentativasPeca] = useState(0);
  const [historiaAtual, setHistoriaAtual] = useState<DialogoHistoria | null>(HISTORIAS_FIXAS[0]);

  const pecaAtivaId = SEQUENCIA_PECAS[indicePecaAtual];

  const handleContinuarBoasVindas = () => {
    setMostrarBoasVindas(false);
  };

  const avancarParaProximaPeca = () => {
    const proximoIndex = indicePecaAtual + 1;

    if (proximoIndex < SEQUENCIA_PECAS.length) {
      setIndicePecaAtual(proximoIndex);
      setHistoriaAtual(HISTORIAS_FIXAS[proximoIndex]);
      setTentativasPeca(0);
    } else {
      finalizarMontagemExterna(pontuacaoTotal);
    }
  };

  const handleColorChange = (itemId: string, color: string) => {
    const pecaType = itemId.split("_")[0];
    const novaImagem = imagensMap[pecaType]?.[color];

    if (novaImagem) {
      setPecas(pecas.map(p => 
        p.id === itemId ? { ...p, imagem: novaImagem, color } : p
      ));
    }
  };

  const handleSelectPeca = (itemId: string) => {
    if (!pecasColocadas.has(itemId)) {
      const novaSelecao = pecaSelecionada === itemId ? null : itemId;
      setPecaSelecionada(novaSelecao);
      setDropZoneDestacada(null);
      setMostrarDica(false);
    }
  };

  const aplicarAjudaVisual = (tentativasAtual: number) => {
    const dropZoneCorreta = MAPEAMENTO_CORRETO[pecaAtivaId];
    
    if (nivelDificuldade === 'facil') {
      if (tentativasAtual === 2) {
        setMostrarDica(false);
        setDropZoneDestacada(null);
      } else if (tentativasAtual === 3) {
        setDicaAtual("Observe a área destacada!");
        setMostrarDica(true);
        setDropZoneDestacada(dropZoneCorreta);
      } else if (tentativasAtual >= 4) {
        setDicaAtual("O local correto está brilhando!");
        setMostrarDica(true);
        setDropZoneDestacada(dropZoneCorreta);
      }
    } else if (nivelDificuldade === 'medio') {
      if (tentativasAtual <= 3) {
        setMostrarDica(false);
        setDropZoneDestacada(null);
      } else if (tentativasAtual === 4) {
        setMostrarDica(false);
        setDropZoneDestacada(null);
      } else if (tentativasAtual >= 5) {
        setDicaAtual("Lembre-se da história sobre esta peça!");
        setMostrarDica(true);
        setDropZoneDestacada(dropZoneCorreta);
      }
    }
  };

  const handleDrop = (itemId: string, targetId: string) => {
    const ehPecaAtiva = itemId === pecaAtivaId;
    if (!ehPecaAtiva) {
      registrarTentativa(itemId, false, nivelDificuldade || "medio");
      setMensagemErro(obterMensagemAleatoria(nivel, 'erro'));
      setShowErro(true);
      return;
    }

    const dropZoneCorreta = MAPEAMENTO_CORRETO[pecaAtivaId];
    const acertouLocal = targetId === dropZoneCorreta;
    
    if (!acertouLocal) {
      playError();
      const novasTentativas = tentativasPeca + 1;
      setTentativasPeca(novasTentativas);
      registrarTentativa(itemId, false, nivelDificuldade || "medio");
      setMensagemErro(obterMensagemAleatoria(nivel, 'erro'));
      setShowErro(true);
      aplicarAjudaVisual(novasTentativas);
      return;
    }

    playSuccess();
    const pontosObtidos = registrarTentativa(itemId, true, nivelDificuldade || "medio");
    const novaPontuacaoTotal = pontuacaoTotal + pontosObtidos;
    const novasPecasColocadas = new Set([...pecasColocadas, itemId]);
    setPecasColocadas(novasPecasColocadas);
    
    setPontosGanhos(pontosObtidos);
    setPecaSelecionada(null);
    setDropZoneDestacada(null);
    setMostrarDica(false);
    setMensagemSucesso(obterMensagemAleatoria(nivel, 'sucesso'));
    
    try {
      api.salvarProgresso(apelido, codigoSala, false);
    } catch (err) {}

    const ehUltimaPeca = novasPecasColocadas.size === pecas.length;
    
    if (ehUltimaPeca) {
      setShowSucesso(true);
      setTimeout(() => {
        setShowSucesso(false);
        finalizarMontagemExterna(novaPontuacaoTotal);
      }, 2000);
    } else {
      setShowSucesso(true);
      setTimeout(() => {
        setShowSucesso(false);
        avancarParaProximaPeca();
      }, 2000);
    }
  };

  function finalizarMontagemExterna(pontuacaoAtualizada: number) {
    pausarCronometro();
    
    const pontuacaoExterna = pontuacaoAtualizada;
    
    localStorage.setItem("pontuacaoMontagem", pontuacaoExterna.toString());
    localStorage.setItem("tempoMontagem", tempo.toString());
    
    navigate("/montagem-interna", {
      state: {
        aluno: aluno,
        nivel: nivelDificuldade
      }
    });
  }

  const handleErroClose = () => {
    setShowErro(false);
  };

  const handleVoltarFases = () => {
    playClick();
    resetarPontuacao();
    resetarCronometro();
    
    navigate('/fases', {
      state: {
        aluno: aluno,
        nivel: nivelDificuldade
      },
      replace: true
    });
  };

  const obterImagemPeca = (pecaId: string): string | undefined => {
    return pecas.find(p => p.id === pecaId)?.imagem;
  };

  const obterLabelPecaAtiva = (): string => {
    const peca = pecas.find(p => p.id === pecaAtivaId);
    return peca?.label || "Monitor";
  };

  if (!aluno?.apelido) {
    return (
      <div className="montagem-container" style={{ textAlign: 'center', padding: '20px' }}>
        <h2>Erro ao carregar dados do aluno</h2>
        <p>Faça login novamente</p>
        <button onClick={() => window.location.href = '/aluno'}>Voltar ao Login</button>
      </div>
    );
  }

  return (
    <div className="montagem-container">
      <BoasVindasModal 
        isOpen={mostrarBoasVindas}
        onContinuar={handleContinuarBoasVindas}
      />

      <div className="montagem-fantasma-esquerda" />
      
      <div className="montagem-header">
        <PainelPontuacao 
          pontuacao={pontuacaoTotal}
          pecasRestantes={pecas.length - pecasColocadas.size}
          totalPecas={pecas.length}
        />
        <Cronometro tempo={tempo} />
      </div>

      {mostrarDica && (
        <div className="montagem-dica-banner">
          {dicaAtual}
        </div>
      )}

      {!mostrarBoasVindas && (
        <div className="montagem-alerta-selecao">
          <span className="montagem-alerta-icone">👆</span>
          <span>Arraste a peça <strong>{obterLabelPecaAtiva()}</strong> para o local correto!</span>
        </div>
      )}

      <div className="montagem-content">
        <div className="montagem-workspace">
          <div className="montagem-central-wrapper">
            <div className="montagem-computador">
              <div className="montagem-computador-monitor">
                <DropZone
                  id="dropzone_monitor"
                  onDrop={handleDrop}
                  placed={pecasColocadas.has("monitor_1")}
                  image={obterImagemPeca("monitor_1")}
                  destacar={dropZoneDestacada === "dropzone_monitor"}
                  nivel={nivelDificuldade}
                />
              </div>

              <div className="montagem-computador-perifericos">
                <div className="montagem-computador-som">
                  <DropZone
                    id="dropzone_som"
                    onDrop={handleDrop}
                    placed={pecasColocadas.has("caixa_som_1")}
                    image={obterImagemPeca("caixa_som_1")}
                    destacar={dropZoneDestacada === "dropzone_som"}
                    nivel={nivelDificuldade}
                  />
                </div>

                <div className="montagem-computador-teclado">
                  <DropZone
                    id="dropzone_teclado"
                    onDrop={handleDrop}
                    placed={pecasColocadas.has("teclado_1")}
                    image={obterImagemPeca("teclado_1")}
                    destacar={dropZoneDestacada === "dropzone_teclado"}
                    nivel={nivelDificuldade}
                  />
                </div>

                <div className="montagem-computador-mouse">
                  <DropZone
                    id="dropzone_mouse"
                    onDrop={handleDrop}
                    placed={pecasColocadas.has("mouse_1")}
                    image={obterImagemPeca("mouse_1")}
                    destacar={dropZoneDestacada === "dropzone_mouse"}
                    nivel={nivelDificuldade}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="montagem-pecas-horizontal">
          <h3>Peças Disponíveis</h3>
          <div className="montagem-pecas-lista-horizontal">
            {pecas.map((peca) => (
              <DraggableItem
                key={peca.id}
                item={peca}
                onColorChange={handleColorChange}
                onSelect={handleSelectPeca}
                placed={pecasColocadas.has(peca.id)}
                isSelected={pecaSelecionada === peca.id}
                disabled={pecasColocadas.has(peca.id)}
              />
            ))}
          </div>
        </div>
      </div>

      <HistoriaModal
        isOpen={!mostrarBoasVindas && historiaAtual !== null}
        historia={historiaAtual}
      />

      <SucessoModal
        isOpen={showSucesso}
        onClose={() => setShowSucesso(false)}
        pontosGanhos={pontosGanhos}
        mensagem={mensagemSucesso}
      />

      <ErroModal
        isOpen={showErro}
        onClose={handleErroClose}
        mensagem={mensagemErro}
      />
    </div>
  );
};

export default Montagem;