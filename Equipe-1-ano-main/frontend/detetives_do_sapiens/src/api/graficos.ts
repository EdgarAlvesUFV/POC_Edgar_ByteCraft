import axios from "axios";
import { API_BASE } from "@/config/api";
import { getHeaders } from "@/api/client";

export interface DistribuicaoEstrelas {
  tipo_pista: string;
  estrelas: number;
  total: number;
}

export interface TempoPorPista {
  tipo_pista: string;
  tempoMedioSegundos: number;
}

export interface GraficosTurma {
  distribuicaoEstrelas: DistribuicaoEstrelas[];
  tempoPorPista: TempoPorPista[];
}

export async function getGraficosTurma(turmaId: number): Promise<GraficosTurma> {
  const res = await axios.get(`${API_BASE}/metricas/turma/${turmaId}/graficos`);
  return res.data;
}
