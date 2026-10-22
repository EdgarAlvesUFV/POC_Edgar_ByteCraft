import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { PillButton } from "@/components/PillButton";
import { useTentativas } from "@/hooks/useTentativas";
import { usePistaUso } from "@/hooks/usePistaUso";
import { useProgresso } from "@/hooks/useProgresso";
import { API_BASE } from "@/api/client";

interface Pista {
  id: number;
  conteudo: string;
  tipo: string;
}

interface Resposta {
  id: number;
  descricao: string;
  ordem: number;
  tipo: string;
  status: boolean;
}

interface Misterio {
  idMisterio: number;
  titulo: string;
  sessaoMisterioId: number;
  pistas: Pista[];
  respostas: Resposta[];
}

interface Sessao {
  idSessao: number;
  turmaId: number;
  misterios: Misterio[];
}

const Mystery = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const sessao = (location.state as { sessao: Sessao })?.sessao;

  const [currentAudio, setCurrentAudio] = useState<HTMLAudioElement | null>(null);
  const [misterioIndex, setMisterioIndex] = useState(0);
  const [currentClueIndex, setCurrentClueIndex] = useState(0);
  const [removedAnswers, setRemovedAnswers] = useState<number[]>([]);
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [inicioTentativa, setInicioTentativa] = useState<number>(Date.now());

  const [mistériosResolvidos, setMistériosResolvidos] = useState<boolean[]>(
    () => (sessao?.misterios ? new Array(sessao.misterios.length).fill(false) : [])
  );

  const [pontuacoesPorMisterio, setPontuacoesPorMisterio] = useState<number[]>(() =>
    sessao?.misterios ? new Array(sessao.misterios.length).fill(5) : []
  );

  const tentativaMutation = useTentativas();
  const pistaMutation = usePistaUso();
  const progressoMutation = useProgresso();

  if (!sessao) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4">
        <p className="text-red-600 font-bold">Nenhuma sessão recebida.</p>
        <PillButton onClick={() => navigate("/selecionar-turma")}>Voltar</PillButton>
      </div>
    );
  }

  const misterio = sessao.misterios[misterioIndex];
  const pistas = misterio.pistas;
  const respostas = misterio.respostas.filter((r) => !removedAnswers.includes(r.id));

  const getFullUrl = (path: string) =>
    path.startsWith("http") ? path : `${API_BASE}${path}`;

  useEffect(() => {
    setInicioTentativa(Date.now());
    setRemovedAnswers([]);
    setCurrentClueIndex(0);

    progressoMutation.mutate(
      {
        sessaoMisterioId: misterio.sessaoMisterioId,
        pontuacaoObtida: 0,
        tempoResolucaoMs: 0,
        resolvido: false,
      },
      {
        onSuccess: () => {
          const primeiraPista = misterio.pistas[0];
          if (primeiraPista) {
            pistaMutation.mutate({
              sessaoMisterioId: misterio.sessaoMisterioId,
              pistaId: primeiraPista.id,
              tipoPista: primeiraPista.tipo,
              ordem: 1,
            });
          }
        },
      }
    );
  }, [misterioIndex]);

  const handlePlayAudio = (url: string, pistaId?: number) => {
    if (currentAudio) {
      currentAudio.pause();
      setCurrentAudio(null);
    }
    const audio = new Audio(getFullUrl(url));
    audio.play().catch(() => console.log("Erro ao reproduzir áudio"));
    setCurrentAudio(audio);
    if (pistaId) {
      pistaMutation.mutate({
        sessaoMisterioId: misterio.sessaoMisterioId,
        pistaId,
        tipoPista: "AUDIO",
        ordem: currentClueIndex + 1,
      });
    }
  };

  const handleAnswerClick = (resposta: Resposta) => {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
      setCurrentAudio(null);
    }

    const tempoMs = Date.now() - inicioTentativa;
    const tempoSegundos = Math.round(tempoMs / 1000);

    tentativaMutation.mutate({
      sessaoId: sessao.idSessao,
      misterioId: misterio.idMisterio,
      respostaId: resposta.id,
      correta: resposta.status,
      tempoSegundos,
    });

    if (resposta.status) {
      setShowSuccessPopup(true);
      setMistériosResolvidos((prev) =>
        prev.map((v, i) => (i === misterioIndex ? true : v))
      );

      const pontuacaoFinal = pontuacoesPorMisterio[misterioIndex];
      progressoMutation.mutate({
        sessaoMisterioId: misterio.sessaoMisterioId,
        pontuacaoObtida: pontuacaoFinal,
        tempoResolucaoMs: tempoMs,
        resolvido: true,
      });
    } else {
      setPontuacoesPorMisterio((prev) =>
        prev.map((p, i) => (i === misterioIndex ? Math.max(p - 1, 0) : p))
      );

      if (currentClueIndex < pistas.length - 1) {
        const novaPista = pistas[currentClueIndex + 1];
        pistaMutation.mutate({
          sessaoMisterioId: misterio.sessaoMisterioId,
          pistaId: novaPista.id,
          tipoPista: novaPista.tipo,
          ordem: currentClueIndex + 2,
        });
        setCurrentClueIndex((prev) => prev + 1);
      }

      setRemovedAnswers((prev) => [...prev, resposta.id]);
    }
  };

  const handleNextMystery = () => {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
      setCurrentAudio(null);
    }

    setShowSuccessPopup(false);
    setRemovedAnswers([]);
    setCurrentClueIndex(0);
    setInicioTentativa(Date.now());

    if (misterioIndex < sessao.misterios.length - 1) {
      setMisterioIndex((prev) => prev + 1);
    } else {
      navigate("/");
    }
  };

  const renderPista = (pista: Pista) => {
    const registrar = () =>
      pistaMutation.mutate({
        sessaoMisterioId: misterio.sessaoMisterioId,
        pistaId: pista.id,
        tipoPista: pista.tipo,
        ordem: currentClueIndex + 1,
      });

    switch (pista.tipo) {
      case "AUDIO":
        return (
          <div className="flex justify-center items-center w-full h-full">
            <button
              onClick={() => {
                handlePlayAudio(pista.conteudo, pista.id);
                registrar();
              }}
              className="flex items-center justify-center w-20 h-20 bg-pill-yellow border-2 border-black rounded-lg shadow-md hover:bg-yellow-400 transition"
            >
              <img
                src="/assets/audio_playing.gif"
                alt="Reproduzindo áudio"
                className="h-10 w-10"
              />
            </button>
          </div>
        );
      case "IMAGEM":
        return (
          <div className="flex justify-center items-center w-full h-full">
            <img
              src={getFullUrl(pista.conteudo)}
              alt="Pista visual"
              className="max-h-40 rounded-lg cursor-pointer"
              onClick={registrar}
            />
          </div>
        );
      default:
        return (
          <div
            className="flex justify-center items-center w-full h-full cursor-pointer"
            onClick={registrar}
          >
            <p className="text-pill-text font-bold text-center text-2xl md:text-4xl lg:text-6xl">
              {pista.conteudo}
            </p>
          </div>
        );
    }
  };

  const renderResposta = (r: Resposta) => {
    switch (r.tipo) {
      case "AUDIO":
        return <img src="/assets/audio_playing.gif" alt="Reproduzir áudio" className="h-8 w-8" />;
      case "IMAGEM":
        return (
          <img
            src={getFullUrl(r.descricao)}
            alt="Resposta"
            className="w-full h-full flex justify-center items-center p-[15px]"
          />
        );
      default:
        return <span className="block text-center w-full">{r.descricao}</span>;
    }
  };

  return (
    <div
      className="flex flex-col h-screen bg-cover bg-center bg-no-repeat p-10"
      style={{ backgroundImage: "url('/assets/background_image.png')" }}
    >
      <h1 className="text-3xl font-extrabold text-center bg-white/70 rounded-lg p-2 mb-6">
        Mistério {misterioIndex + 1}: {misterio.titulo}
      </h1>

      <div className="flex flex-grow-[2] bg-white/80 border-2 border-black rounded-2xl p-6 gap-6 w-full">
        {pistas.slice(0, currentClueIndex + 1).map((p) => (
          <div
            key={p.id}
            className="flex-1 border-2 border-gray-300 rounded-lg flex justify-center items-center"
          >
            {renderPista(p)}
          </div>
        ))}
      </div>

      <div className="flex-grow grid grid-cols-2 md:grid-cols-4 gap-6 w-full mt-6 text-lg md:text-xl lg:text-5xl">
        {respostas.map((r) => (
          <button
            key={r.id}
            onClick={() => handleAnswerClick(r)}
            className="flex justify-center items-center border-2 rounded-xl font-bold bg-pill-yellow text-pill-text hover:bg-pill-yellow/90 w-full h-full"
          >
            {renderResposta(r)}
          </button>
        ))}
      </div>

      {showSuccessPopup && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white border-4 border-black rounded-3xl p-8 text-center w-[90%] max-w-4xl">
            <h2 className="text-3xl font-extrabold mb-6 bg-pill-yellow px-6 py-2 rounded-full inline-block">
              PROGRESSO
            </h2>

            <div className="grid grid-cols-2 md:grid-cols-5 gap-6 mb-8">
              {sessao.misterios.map((m, index) => {
                let bgColor = "bg-gray-300";
                if (index < misterioIndex) bgColor = "bg-green-400";
                if (index === misterioIndex) bgColor = "bg-yellow-300";

                const resolvido = mistériosResolvidos[index];

                return (
                  <div
                    key={m.idMisterio}
                    className={`${bgColor} border-2 border-black rounded-2xl p-4 flex flex-col items-center justify-center`}
                  >
                    <span className="font-extrabold mb-2">MISTÉRIO {index + 1}</span>
                    {resolvido && (
                      <span className="text-sm font-bold">⭐ {pontuacoesPorMisterio[index]}</span>
                    )}
                  </div>
                );
              })}
            </div>

            <PillButton onClick={handleNextMystery}>
              {misterioIndex < sessao.misterios.length - 1 ? "CONTINUAR" : "FINALIZAR"}
            </PillButton>
          </div>
        </div>
      )}
    </div>
  );
};

export default Mystery;
