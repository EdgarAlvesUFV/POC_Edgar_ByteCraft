import axios from "axios";
import { API_BASE } from "@/config/api";

export interface PistaUsoPayload {
  sessaoMisterioId: number;
  pistaId: number;
  tipoPista: string;
  ordem: number;
}

export const registrarPistaUso = async (data: PistaUsoPayload, token?: string) => {
  const res = await axios.post(`${API_BASE}/pistauso`, data, {
    headers: { Authorization: token ? `Bearer ${token}` : undefined },
  });
  return res.data;
};
