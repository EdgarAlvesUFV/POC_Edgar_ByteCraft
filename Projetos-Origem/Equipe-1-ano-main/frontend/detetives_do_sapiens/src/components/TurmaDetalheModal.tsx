import { useEffect, useMemo } from "react";
import { PieChart, Pie, Cell, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from "recharts";
import { PillButton } from "@/components/PillButton";
import { useGraficosTurma } from "@/hooks/useGraficosTurma";

interface TurmaDetalheModalProps {
  open: boolean;
  onClose: () => void;
  turmaNome: string;
  dados: { turmaId: number; mediaPontuacao: number; mediaTempo: number; totalResolvidos: number; totalMisterios: number };
}

const COLORS = ["#facc15", "#fbbf24", "#f59e0b", "#d97706", "#92400e"];

export const TurmaDetalheModal = ({ open, onClose, turmaNome, dados }: TurmaDetalheModalProps) => {
  const { data: graficos, isLoading } = useGraficosTurma(dados.turmaId);

  const estrelasAgrupadas = useMemo(() => {
    if (!graficos) return {};
    return graficos.distribuicaoEstrelas.reduce((acc, item) => {
      const tipo = item.tipo_pista;
      if (!acc[tipo]) acc[tipo] = [];
      acc[tipo].push(item);
      return acc;
    }, {} as Record<string, { tipo_pista: string; estrelas: number; total: number }[]>);
  }, [graficos]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-6">
      <div className="bg-yellow-100 border-4 border-black rounded-3xl p-8 w-full max-w-5xl overflow-y-auto max-h-[90vh] shadow-[6px_6px_0px_rgba(0,0,0,0.6)]">
        <div className="text-center mb-4">
          <div className="bg-yellow-300 border-2 border-black rounded-full px-6 py-2 inline-block">
            <h2 className="text-2xl font-extrabold uppercase text-black">
              Desempenho da {turmaNome}
            </h2>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-6 text-center text-black font-bold">
          <div className="bg-yellow-200 border-2 border-black rounded-xl p-4">
            <p>Média de Pontuação</p>
            <p className="text-3xl">{dados.mediaPontuacao.toFixed(2)} ⭐</p>
          </div>
          <div className="bg-yellow-200 border-2 border-black rounded-xl p-4">
            <p>Tempo Médio</p>
            <p className="text-3xl">{Math.round(dados.mediaTempo / 1000)}s ⏱️</p>
          </div>
          <div className="bg-yellow-200 border-2 border-black rounded-xl p-4">
            <p>Taxa de Resolução</p>
            <p className="text-3xl">
              {Math.round((dados.totalResolvidos / Math.max(dados.totalMisterios, 1)) * 100)}%
            </p>
          </div>
        </div>

        {isLoading && <p className="text-center text-lg font-bold mt-6">Carregando gráficos...</p>}

        {graficos && (
          <>
            {/* 🎯 Gráficos de pizza: distribuição de estrelas */}
            <h3 className="text-xl font-bold mt-8 mb-4 text-center">Distribuição de Estrelas por Tipo de Pista</h3>
            <div className="flex flex-wrap justify-center gap-10">
              {Object.entries(estrelasAgrupadas).map(([tipo, data]) => (
                <div key={tipo} className="text-center">
                  <p className="font-bold mb-2">{tipo}</p>
                  <ResponsiveContainer width={200} height={200}>
                    <PieChart>
                      <Pie data={data} dataKey="total" nameKey="estrelas" outerRadius={80}>
                        {data.map((_, i) => (
                          <Cell key={i} fill={COLORS[i % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip formatter={(v, n, e) => [`${v} tentativas`, `${e.payload.estrelas} ⭐`]} />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              ))}
            </div>

            {/* 🕒 Gráfico de barras: tempo médio por tipo */}
            <h3 className="text-xl font-bold mt-10 mb-4 text-center">Tempo Médio por Tipo de Pista</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={graficos.tempoPorPista}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="tipo_pista" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="tempoMedioSegundos" fill="#f59e0b" />
              </BarChart>
            </ResponsiveContainer>
          </>
        )}

        <div className="mt-8 flex justify-center">
          <PillButton variant="secondary" onClick={onClose}>
            FECHAR
          </PillButton>
        </div>
      </div>
    </div>
  );
};
