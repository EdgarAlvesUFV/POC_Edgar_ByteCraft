import axios from "axios";
import { API_BASE } from '@/config/api';

export interface DesempenhoTurma {
  turmaId: number;
  nomeTurma: string;
  mediaPontuacao: number;
  mediaTempo: number;
  totalResolvidos: number;
  totalMisterios: number;
  indiceDesempenho?: number; // calculado no front
}

export const getDesempenhoTurmas = async (
  professorId: number
): Promise<DesempenhoTurma[]> => {
  const res = await axios.get(
    `${API_BASE}/metricas/professora/${professorId}/turmas`
  );
  return res.data;
};
