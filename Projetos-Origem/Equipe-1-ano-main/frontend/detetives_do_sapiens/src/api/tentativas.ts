import axios from "axios";
import { API_BASE } from "@/config/api";

export interface TentativaPayload {
  sessaoId: number;
  misterioId: number;
  respostaId: number;
  correta: boolean;
  tempoSegundos?: number;
}

export const registrarTentativa = async (data: TentativaPayload, token?: string) => {
  const res = await axios.post(`${API_BASE}/tentativas`, data, {
    headers: { Authorization: token ? `Bearer ${token}` : undefined },
  });
  return res.data;
};
