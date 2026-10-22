import axios from "axios";
import { Turma } from "@/api/turmas.ts";
import { Misterio } from "@/api/misterios.ts";
import { getHeaders } from "@/api/client.ts";
import { API_BASE } from "@/config/api";

export const api = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
  withCredentials: false,
});

export interface Sessao {
  idSessao: number;
  turma: Turma;
  misterios: Misterio[];
  statusSessao: string;
}

export async function createSession(idTurma: number, n_misterios: number): Promise<Sessao> {
  try {
    const res = await fetch(`${API_BASE}/sessao/${idTurma}`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify({ n_misterios }),
    });

    if (!res.ok) {
      throw new Error(`Erro ao criar sessão: ${res.status}`);
    }

    return await res.json();
  } catch (err) {
    console.error("Erro ao criar sessão:", err);
    throw err;
  }
}
