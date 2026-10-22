import { createContext, useContext, useMemo, useState, ReactNode } from "react";
import { API_BASE } from "@/config/api";

type User = { id: number; nome: string; email: string } | null;

type AuthContextType = {
  user: User;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem("auth:token")
  );

  const [user, setUser] = useState<User>(() => {
    const raw = localStorage.getItem("auth:user");
    return raw ? JSON.parse(raw) : null;
  });

  async function login(email: string, password: string) {
    try {
      const res = await fetch(`${API_BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha: password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Falha ao autenticar");
      }

      const data = await res.json().catch(() => ({}));

      if (!data?.token || !data?.email) {
        throw new Error("Resposta inválida do servidor");
      }

      const userData = {
        id: data.id ?? 0,
        nome: data.nome ?? "Usuário",
        email: data.email,
      };

      setToken(data.token);
      setUser(userData);

      localStorage.setItem("auth:token", data.token);
      localStorage.setItem("auth:user", JSON.stringify(userData));

      console.log("Usuário autenticado:", userData);
      console.log("Token armazenado:", data.token);
    } catch (err: any) {
      console.error("Erro no login:", err);
      throw new Error(err?.message || "Erro inesperado ao autenticar.");
    }
  }

  function logout() {
    setToken(null);
    setUser(null);
    localStorage.removeItem("auth:token");
    localStorage.removeItem("auth:user");
  }

  const value = useMemo(
    () => ({
      user,
      token,
      isAuthenticated: !!token,
      login,
      logout,
    }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de <AuthProvider>");
  return ctx;
}
