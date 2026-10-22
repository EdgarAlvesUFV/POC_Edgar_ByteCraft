import axios, { AxiosError } from "axios";
import { Turma } from "@/api/turmas";
import { getHeaders } from "@/api/client";
import { API_BASE } from "@/config/api";

export const api = axios.create({
  baseURL: API_BASE,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

export interface Pista {
  id: number;
  conteudo: string;
  tipo: string;
}

export interface Resposta {
  id: number;
  descricao: string;
  ordem: number;
  tipo: string;
  status: boolean;
}

export interface Misterio {
  id: number;
  titulo: string;
  pistas: Pista[];
  respostas: Resposta[];
  sessaoMisterioId: number;
}
