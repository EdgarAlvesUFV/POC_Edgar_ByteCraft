import { useNavigate } from "react-router-dom";
import { PillButton } from "@/components/PillButton";
import { useListAllTurmas } from "@/hooks/useTurmas";
import { useState } from "react";
import { Volume2 } from "lucide-react";
import {Sessao} from "@/api/sessao.ts";
import {useCreateSession} from "@/hooks/useSessao.ts";

const SelecionarTurma = () => {
    const navigate = useNavigate();
    const { data: turmas, isLoading, error } = useListAllTurmas();
    const [selectedTurma, setSelectedTurma] = useState<number | null>(null);
    const { startSession, sessao, loading: creating, error: createError } = useCreateSession();

    const getTurnoDisplay = (turno: string) => {
        switch (turno) {
            case 'MANHA': return 'MANHÃ';
            case 'TARDE': return 'TARDE';
            case 'NOITE': return 'NOITE';
            default: return turno;
        }
    };

    const handleIniciar = async () => {
        if (selectedTurma) {
            const turma = turmas?.find((t) => t.id === selectedTurma);
            if (turma) {
                try {
                    const novaSessao = await startSession(turma);
                    navigate("/jogo/misterio", { state: {sessao: novaSessao } });
                } catch (e) {
                    console.error("Erro ao criar sessão:", e);
                }
            }
        }
    };

    return (
        <div className="relative min-h-screen flex flex-col items-center p-8 overflow-hidden">
            {/* Background em vídeo */}
            <video
                autoPlay
                loop
                muted
                playsInline
                className="absolute top-0 left-0 w-full h-full object-cover -z-10"
            >
                <source src="/assets/video_background2.mp4" type="video/mp4" />
            </video>

            {/* Overlay leve para contraste */}
            <div className="absolute top-0 left-0 w-full h-full bg-white/30 -z-10"></div>

            {/* Título */}
            <div className="mb-8 z-10">
                <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-8 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)]">
                    SELECIONE SUA TURMA
                </div>
            </div>

            {/* Área de conteúdo */}
            <div className="flex-1 w-full max-w-3xl bg-content-bg border-2 border-content-border rounded-3xl p-8 mb-8 z-10">
                {isLoading && (
                    <div className="flex items-center justify-center h-64">
                        <div className="text-center">
                            <div className="w-8 h-8 border-4 border-pill-yellow border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                            <p className="text-pill-text font-bold">Carregando turmas...</p>
                        </div>
                    </div>
                )}

                {error && (
                    <div className="flex items-center justify-center h-64">
                        <div className="text-center">
                            <p className="text-red-600 font-bold mb-4">Erro ao carregar turmas</p>
                            <PillButton onClick={() => window.location.reload()}>
                                TENTAR NOVAMENTE
                            </PillButton>
                        </div>
                    </div>
                )}

                {!isLoading && !error && (!turmas || turmas.length === 0) && (
                    <div className="flex items-center justify-center h-64">
                        <div className="text-center">
                            <p className="text-pill-text font-bold mb-6 text-lg">
                                Nenhuma turma disponível
                            </p>
                        </div>
                    </div>
                )}

                {!isLoading && !error && turmas && turmas.length > 0 && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-h-96 overflow-y-auto">
                        {turmas.map((turma) => (
                            <div
                                key={turma.id}
                                className={`bg-white border-2 rounded-3xl p-6 cursor-pointer transition-all duration-200 ${
                                    selectedTurma === turma.id
                                        ? 'border-pill-border bg-pill-yellow/10 shadow-[2px_4px_0px_rgba(0,0,0,0.35)]'
                                        : 'border-content-border hover:border-pill-border'
                                }`}
                                onClick={() => setSelectedTurma(turma.id)}
                            >
                                <div className="text-center">
                                    <div className={`inline-block px-4 py-2 rounded-full border-2 font-extrabold mb-4 ${
                                        selectedTurma === turma.id
                                            ? 'bg-pill-yellow text-pill-text border-pill-border'
                                            : 'bg-background text-pill-text border-content-border'
                                    }`}>
                                        {turma.nome}
                                    </div>
                                    <p className="text-pill-text font-semibold">
                                        {turma.ano}º Ano
                                    </p>
                                    <p className="text-pill-text">
                                        {getTurnoDisplay(turma.turno)}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Botões inferiores */}
            <div className="flex items-center justify-between w-full max-w-3xl z-10">
                <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-2xl p-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)] cursor-pointer hover:bg-pill-yellow/90 transition-colors">
                    <Volume2 size={24} />
                </div>

                <PillButton
                    size="large"
                    onClick={handleIniciar}
                    disabled={!selectedTurma}
                >
                    INICIAR
                </PillButton>

                <PillButton
                    variant="secondary"
                    onClick={() => navigate("/")}
                >
                    VOLTAR
                </PillButton>
            </div>
        </div>
    );
};

export default SelecionarTurma;
