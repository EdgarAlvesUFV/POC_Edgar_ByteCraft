// src/hooks/useTurmas.ts
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  listTurmas,
  createTurma,
  listAllTurmas,
  deleteTurma,
  updateTurma,
  type UpdateTurmaPayload,
  type CreateTurmaPayload,
  type Turma,
} from "@/api/turmas";
import { useToast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";

export type { Turma };

// ====================================================
// GET /turma/professor/:id/turmas
// ====================================================
export function useListTurmas() {
  const { token } = useAuth() ?? {};

  return useQuery({
    queryKey: ["turmas"],
    queryFn: () => listTurmas(token ?? undefined),
    enabled: !!token,
    retry: 0,
    refetchOnWindowFocus: false,
  });
}


// ====================================================
// GET /turma
// ====================================================
export function useListAllTurmas() {
  const { token } = useAuth() ?? {};

  return useQuery({
    queryKey: ["all-turmas"],
    queryFn: () => listAllTurmas(token ?? undefined),
    retry: 0,
    refetchOnWindowFocus: false,
    staleTime: 60_000,
  });
}

// ====================================================
// POST /turma
// ====================================================
export function useCreateTurma(professorIdForRefetch?: number) {
  const qc = useQueryClient();
  const { toast } = useToast();
  const { token } = useAuth() ?? {};

  return useMutation({
    mutationFn: (p: CreateTurmaPayload) => createTurma(p, token ?? undefined),
    onSuccess: () => {
      qc.invalidateQueries({
        queryKey:
          professorIdForRefetch != null
            ? ["turmas", professorIdForRefetch]
            : ["turmas"],
      });
      toast({ title: "Sucesso", description: "Turma cadastrada!" });
    },
    onError: (error: any) => {
      if (error?.type === "server" || error?.type === "conflict") {
        toast({
          title: "Erro ao cadastrar",
          description: error.message || "Tente novamente.",
          variant: "destructive",
        });
      }
    },
  });
}

// ====================================================
// DELETE /turma/:id
// ====================================================
export function useDeleteTurma(professorIdForRefetch?: number) {
  const qc = useQueryClient();
  const { toast } = useToast();
  const { token } = useAuth() ?? {};

  return useMutation({
    mutationFn: (id: number) => deleteTurma(id, token ?? undefined),
    onSuccess: () => {
      qc.invalidateQueries({
        queryKey:
          professorIdForRefetch != null
            ? ["turmas", professorIdForRefetch]
            : ["turmas"],
      });
      toast({ title: "Turma excluída com sucesso!" });
    },
    onError: (err: any) => {
      const msg =
        err?.message ||
        (err?.type === "conflict"
          ? "Não é possível excluir: há registros vinculados."
          : "Falha ao excluir turma.");
      toast({ title: "Erro", description: msg, variant: "destructive" });
    },
  });
}

// ====================================================
// PUT /turma/:id
// ====================================================
export function useUpdateTurma(professorIdForRefetch: number) {
  const qc = useQueryClient();
  const { toast } = useToast();
  const { token } = useAuth() ?? {};

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CreateTurmaPayload }) =>
      updateTurma(id, data, token ?? undefined),
    onMutate: (p) => console.log("[HOOK] updateTurma -> onMutate", p),
    onSuccess: () => {
      console.log("[HOOK] updateTurma -> onSuccess");
      qc.invalidateQueries({ queryKey: ["turmas", professorIdForRefetch] });
      toast({ title: "Sucesso", description: "Turma atualizada!" });
    },
    onError: (error: any) => {
      console.error("[HOOK] updateTurma -> onError", error);
      toast({
        title: "Falha ao atualizar",
        description: error?.message || "Não foi possível atualizar a turma.",
        variant: "destructive",
      });
    },
  });
}
