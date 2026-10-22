import axios from "axios";
import { API_BASE } from "@/config/api";

export interface ProgressoPayload {
  sessaoMisterioId: number;
  pontuacaoObtida: number;
  tempoResolucaoMs: number;
  resolvido: boolean;
}

export const atualizarProgresso = async (data: ProgressoPayload, token?: string) => {
  const res = await axios.post(`${API_BASE}/progresso`, data, {
    headers: { Authorization: token ? `Bearer ${token}` : undefined },
  });
  return res.data;
};
