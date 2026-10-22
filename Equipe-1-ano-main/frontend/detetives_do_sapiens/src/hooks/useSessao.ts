import {useState} from "react";
import {createSession, Sessao} from "@/api/sessao.ts";
import {Turma} from "@/api/turmas.ts";

export function useCreateSession() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [sessao, setSessao] = useState<Sessao | null>(null);

    const startSession = async (turma: Turma) => {
        setLoading(true);
        setError(null);
        try {
            const result = await createSession(turma.id, 5);
            setSessao(result);
            return result;
        } catch (err: any) {
            setError(err.message || "Erro ao criar sessão");
            throw err;
        } finally {
            setLoading(false);
        }
    };

    return { startSession, sessao, loading, error };
}