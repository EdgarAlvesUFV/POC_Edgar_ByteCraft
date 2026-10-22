import { getHeaders } from "@/api/client";
import { API_BASE } from "@/config/api";

export interface Turma {
  id: number;
  nome: string;
  ano: number;
  turno: string;
}

export interface CreateTurmaPayload {
  nome: string;
  ano: number;
  turno: "MANHA" | "TARDE" | "NOITE";
}

export interface UpdateTurmaPayload extends CreateTurmaPayload {}

function authHeaders(token?: string) {
  return {
    ...getHeaders(),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

export async function listTurmas(token?: string): Promise<Turma[]> {
  const res = await fetch(`${API_BASE}/turma/professor/turmas`, {
    method: "GET",
    headers: authHeaders(token),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || "Falha ao carregar turmas");
  }

  return res.json();
}

export async function listAllTurmas(token?: string): Promise<Turma[]> {
  const res = await fetch(`${API_BASE}/turma`, {
    method: "GET",
    headers: authHeaders(token),
  });

  if (!res.ok) throw new Error("Falha ao carregar turmas");
  return res.json();
}

export async function createTurma(
  payload: CreateTurmaPayload,
  token?: string
): Promise<Turma> {
  const res = await fetch(`${API_BASE}/turma`, {
    method: "POST",
    headers: authHeaders(token),
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(`Erro ao criar turma: ${msg}`);
  }

  return res.json();
}

export async function deleteTurma(id: number, token?: string): Promise<void> {
  const res = await fetch(`${API_BASE}/turma/${id}`, {
    method: "DELETE",
    headers: authHeaders(token),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(`Erro ao excluir turma: ${msg}`);
  }
}

export async function updateTurma(
  id: number,
  payload: UpdateTurmaPayload,
  token?: string
): Promise<Turma> {
  const res = await fetch(`${API_BASE}/turma/${id}`, {
    method: "PUT",
    headers: authHeaders(token),
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(`Erro ao atualizar turma: ${msg}`);
  }

  return res.json();
}
