import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { DesempenhoHeader } from "@/components/DesempenhoHeader";
import { TurmaCard } from "@/components/TurmaCard";
import { PillButton } from "@/components/PillButton";
import { TurmaDetalheModal } from "@/components/TurmaDetalheModal";
import { getDesempenhoTurmas, DesempenhoTurma } from "@/api/desempenho";
import { useAuth } from "@/contexts/AuthContext";

const AcompanharDesempenho = () => {
  const [ordem, setOrdem] = useState<"melhor" | "pior">("melhor");
  const [turmas, setTurmas] = useState<DesempenhoTurma[]>([]);
  const [turmaSelecionada, setTurmaSelecionada] = useState<DesempenhoTurma | null>(null);
  const [carregando, setCarregando] = useState(true);

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const carregarTurmas = async () => {
      try {
        setCarregando(true);
        const dados = await getDesempenhoTurmas(user.id);

        // 💡 Calcula o índice no frontend (ponderando pontuação e taxa de resolução)
        const turmasComIndice = dados.map((t) => ({
          ...t,
          indiceDesempenho: Math.round(
            (t.mediaPontuacao * 20) + ((t.totalResolvidos / Math.max(t.totalMisterios, 1)) * 50)
          ),
        }));

        setTurmas(turmasComIndice);
      } catch (error) {
        console.error("Erro ao buscar desempenho das turmas:", error);
      } finally {
        setCarregando(false);
      }
    };

    if (user?.id) carregarTurmas();
  }, [user]);

  const turmasOrdenadas = [...turmas].sort((a, b) =>
    ordem === "melhor"
      ? b.indiceDesempenho - a.indiceDesempenho
      : a.indiceDesempenho - b.indiceDesempenho
  );

  if (carregando) {
    return (
      <div className="h-screen flex items-center justify-center text-xl font-bold text-gray-700">
        Carregando desempenho das turmas...
      </div>
    );
  }

  return (
    <div
      className="min-h-screen flex flex-col items-center justify-between bg-cover bg-center bg-no-repeat p-6"
      style={{ backgroundImage: "url('/assets/background_image.png')" }}
    >
      <div className="w-full max-w-5xl bg-white/70 rounded-3xl border-4 border-black p-6 shadow-lg">
        <DesempenhoHeader />

        <div className="grid grid-cols-5 gap-4 justify-items-center">
          {turmasOrdenadas.map((turma, idx) => (
            <div
              key={turma.turmaId}
              onClick={() => setTurmaSelecionada(turma)}
              className="cursor-pointer"
            >
              <TurmaCard
                turma={turma}
                posicao={idx + 1}
              />
            </div>
          ))}
        </div>

        <div className="flex justify-center items-center gap-4 mt-6">
          <PillButton
            variant={ordem === "pior" ? "primary" : "secondary"}
            onClick={() => setOrdem("pior")}
          >
            PIOR
          </PillButton>

          <span className="font-bold text-black text-lg">↔ ORDENAR ↔</span>

          <PillButton
            variant={ordem === "melhor" ? "primary" : "secondary"}
            onClick={() => setOrdem("melhor")}
          >
            MELHOR
          </PillButton>
        </div>
      </div>

      <div className="mt-8">
        <PillButton variant="secondary" onClick={() => navigate(-1)}>
          VOLTAR
        </PillButton>
      </div>

      {turmaSelecionada && (
        <TurmaDetalheModal
          open={!!turmaSelecionada}
          turmaNome={turmaSelecionada.nomeTurma}
          dados={turmaSelecionada}
          onClose={() => setTurmaSelecionada(null)}
        />
      )}
    </div>
  );
};

export default AcompanharDesempenho;
