import { useQuery } from "@tanstack/react-query";
import { getGraficosTurma } from "@/api/graficos";

export const useGraficosTurma = (turmaId?: number) => {
  return useQuery({
    queryKey: ["graficos-turma", turmaId],
    queryFn: () => getGraficosTurma(turmaId!),
    enabled: !!turmaId, // só busca se o id existir
  });
};
