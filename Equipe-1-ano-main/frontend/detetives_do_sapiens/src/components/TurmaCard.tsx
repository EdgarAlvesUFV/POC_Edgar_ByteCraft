import { DesempenhoTurma } from "@/api/desempenho";

interface TurmaCardProps {
  turma: DesempenhoTurma;
  posicao?: number;
}

export const TurmaCard = ({ turma, posicao }: TurmaCardProps) => {
  return (
    <div className="bg-pill-yellow border-2 border-black rounded-xl shadow-[3px_3px_0px_rgba(0,0,0,0.6)] p-3 text-center w-[150px] h-[160px] flex flex-col justify-between">
      <div className="text-xs font-bold text-black">
        {posicao ? `${posicao}°` : "[Turma]"}
      </div>

      <div className="text-lg font-extrabold text-black">{turma.nomeTurma}</div>

      <div className="text-3xl font-black text-black">
        {turma.indiceDesempenho ?? 0}
        <span className="text-base font-bold"> pts</span>
      </div>

      <div className="text-sm font-semibold text-black">
        ⭐ {turma.mediaPontuacao.toFixed(1)} | 🎯 {turma.totalResolvidos}/{turma.totalMisterios}
      </div>
    </div>
  );
};
