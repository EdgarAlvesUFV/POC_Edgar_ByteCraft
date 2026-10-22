import { FormEvent, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { PillButton } from "@/components/PillButton.tsx";
import { API_BASE } from "@/api/client";

export default function Login() {
  const { login, isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState(user?.email || "");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const [showCadastro, setShowCadastro] = useState(false);
  const [cadastroNome, setCadastroNome] = useState("");
  const [cadastroEmail, setCadastroEmail] = useState("");
  const [cadastroSenha, setCadastroSenha] = useState("");
  const [cadastroConfirmar, setCadastroConfirmar] = useState("");
  const [cadastroMsg, setCadastroMsg] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErr(null);
    setSubmitting(true);
    try {
      await login(email, password);
      navigate("/professora");
    } catch (error: any) {
      setErr(error?.message || "Falha ao autenticar");
    } finally {
      setSubmitting(false);
    }
  }

  async function cadastrarProfessor() {
    if (!cadastroNome.trim() || !cadastroEmail.trim() || !cadastroSenha.trim()) {
      setCadastroMsg("Todos os campos são obrigatórios.");
      return;
    }
    if (cadastroSenha !== cadastroConfirmar) {
      setCadastroMsg("As senhas não coincidem!");
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/professores/cadastrar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nome: cadastroNome,
          email: cadastroEmail,
          senha: cadastroSenha,
        }),
      });

      const msg = await res.text();

      if (!res.ok) {
        setCadastroMsg(msg || "Erro ao cadastrar.");
        return;
      }

      setCadastroMsg("✅ Cadastro realizado com sucesso!");
      setTimeout(() => {
        setShowCadastro(false);
        setCadastroMsg(null);
        setCadastroNome("");
        setCadastroEmail("");
        setCadastroSenha("");
        setCadastroConfirmar("");
      }, 1500);
    } catch (error) {
      console.error(error);
      setCadastroMsg("Erro de conexão com o servidor.");
    }
  }

  return (
    <div
      className="min-h-screen bg-background flex items-center justify-center p-6 bg-cover bg-center bg-no-repeat"
      style={{ backgroundImage: "url('/assets/background_image.png')" }}
    >
      <div className="w-full max-w-md rounded-3xl border-2 border-content-border bg-content-bg p-8 relative">
        <div className="mb-6 text-center">
          <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-6 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)] inline-block">
            LOGIN DA PROFESSORA
          </div>
        </div>

        {isAuthenticated && (
          <div className="mb-4 text-sm font-semibold text-green-700">
            ✅ Você está logada {user?.email ? `como ${user.email}` : ""}.
          </div>
        )}

        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="block text-pill-text font-bold mb-2">E-MAIL</label>
            <input
              type="email"
              placeholder="ex: prof@escola.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </div>

          <div>
            <label className="block text-pill-text font-bold mb-2">SENHA</label>
            <input
              type="password"
              placeholder="********"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </div>

          {err && <p className="text-red-600 font-semibold text-sm">{err}</p>}

          <div className="flex flex-col gap-3 pt-2">
            <div className="flex gap-3">
              <PillButton type="submit" disabled={submitting}>
                {submitting ? "ENTRANDO..." : "ENTRAR"}
              </PillButton>

              {isAuthenticated ? (
                <PillButton type="button" variant="secondary" onClick={logout}>
                  SAIR
                </PillButton>
              ) : (
                <Link to="/" className="ml-auto">
                  <PillButton type="button" variant="secondary">
                    VOLTAR
                  </PillButton>
                </Link>
              )}
            </div>

            {!isAuthenticated && (
              <button
                type="button"
                onClick={() => setShowCadastro(true)}
                className="text-pill-text font-bold underline text-center mt-4"
              >
                Não tem conta? Cadastre-se
              </button>
            )}
          </div>
        </form>

        {isAuthenticated && (
          <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-3">
            <PillButton onClick={() => navigate("/professora")}>IR AO PAINEL</PillButton>
            <PillButton onClick={() => navigate("/professora/turmas")}>
              GERENCIAR TURMAS
            </PillButton>
          </div>
        )}
      </div>

      {showCadastro && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white border-4 border-black rounded-3xl p-8 w-[380px] text-center shadow-[6px_6px_0_rgba(0,0,0,0.5)]">
            <h2 className="bg-pill-yellow border-2 border-black rounded-full px-6 py-2 font-extrabold text-pill-text text-lg mb-4">
              CADASTRAR PROFESSOR(A)
            </h2>

            <div className="flex flex-col gap-3 text-left text-pill-text">
              <label className="font-bold text-sm">
                Nome:
                <input
                  type="text"
                  value={cadastroNome}
                  onChange={(e) => setCadastroNome(e.target.value)}
                  className="block w-full bg-pill-yellow border-2 border-black rounded-full text-center px-4 py-2 mt-1"
                />
              </label>

              <label className="font-bold text-sm">
                E-mail:
                <input
                  type="email"
                  value={cadastroEmail}
                  onChange={(e) => setCadastroEmail(e.target.value)}
                  className="block w-full bg-pill-yellow border-2 border-black rounded-full text-center px-4 py-2 mt-1"
                />
              </label>

              <label className="font-bold text-sm">
                Senha:
                <input
                  type="password"
                  value={cadastroSenha}
                  onChange={(e) => setCadastroSenha(e.target.value)}
                  className="block w-full bg-pill-yellow border-2 border-black rounded-full text-center px-4 py-2 mt-1"
                />
              </label>

              <label className="font-bold text-sm">
                Confirmar senha:
                <input
                  type="password"
                  value={cadastroConfirmar}
                  onChange={(e) => setCadastroConfirmar(e.target.value)}
                  className="block w-full bg-pill-yellow border-2 border-black rounded-full text-center px-4 py-2 mt-1"
                />
              </label>
            </div>

            {cadastroMsg && (
              <p
                className={`mt-3 text-sm font-bold ${
                  cadastroMsg.startsWith("✅") ? "text-green-600" : "text-red-600"
                }`}
              >
                {cadastroMsg}
              </p>
            )}

            <div className="flex justify-around mt-6">
              <PillButton onClick={cadastrarProfessor}>CONFIRMAR</PillButton>
              <PillButton variant="secondary" onClick={() => setShowCadastro(false)}>
                CANCELAR
              </PillButton>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
