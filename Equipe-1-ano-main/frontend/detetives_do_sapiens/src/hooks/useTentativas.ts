import { useMutation } from "@tanstack/react-query";
import { TentativaPayload } from "@/api/tentativas";
import { API_BASE } from "@/api/client";

export function useTentativas() {
  return useMutation({
    mutationFn: async (data: TentativaPayload) => {
      const res = await fetch(`${API_BASE}/tentativas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error("Falha ao registrar tentativa");
      return res.json();
    },
  });
}
