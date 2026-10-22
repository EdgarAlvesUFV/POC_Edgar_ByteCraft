import { useMutation } from "@tanstack/react-query";
import { ProgressoPayload } from "@/api/progresso";
import { API_BASE } from "@/config/api";

export function useProgresso() {
  return useMutation({
    mutationFn: async (data: ProgressoPayload) => {
      const res = await fetch(`${API_BASE}/progresso`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error("Falha ao registrar progresso");
      return res.json();
    },
  });
}
