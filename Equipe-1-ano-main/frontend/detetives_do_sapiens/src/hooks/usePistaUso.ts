import { API_BASE } from "@/config/api";
import { useMutation } from "@tanstack/react-query";
import { PistaUsoPayload } from "@/api/pistauso";

export function usePistaUso() {
  return useMutation({
    mutationFn: async (data: PistaUsoPayload) => {
      const res = await fetch(`${API_BASE}/pistauso`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error("Falha ao registrar uso de pista");
      return res.json();
    },
  });
}
