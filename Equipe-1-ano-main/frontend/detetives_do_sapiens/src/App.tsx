import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "@/contexts/AuthContext";

import Index from "./pages/Index";
import NotFound from "./pages/NotFound";
import SelecionarTurma from "./pages/SelecionarTurma";
import ProfessoraDashboard from "./pages/professora/Index";
import TurmasList from "./pages/professora/turmas/Index";
import CadastrarTurma from "./pages/professora/turmas/Cadastrar";
import Login from "./pages/Login";
import Mystery from "./pages/game/Mystery";
import AcompanharDesempenho from "./pages/professora/acompanhar-desempenho/Index";

const queryClient = new QueryClient();

// Guard para rotas protegidas (apenas para gerenciar turmas)
function RequireAuth({ children }: { children: JSX.Element }) {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? children : <Navigate to="/professora/login" replace />;
}

export default function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <TooltipProvider>
                <Toaster />
                <Sonner />
                <AuthProvider>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/" element={<Index />} />
                            <Route path="/selecionar-turma" element={<SelecionarTurma />} />

                            {/* Login público da professora */}
                            <Route path="/professora/login" element={<Login />} />

                            {/* Área da professora – protegida */}
                            <Route
                                path="/professora"
                                element={
                                    <RequireAuth>
                                        <ProfessoraDashboard />
                                    </RequireAuth>
                                }
                            />
                            <Route
                                path="/professora/turmas"
                                element={
                                    <RequireAuth>
                                        <TurmasList />
                                    </RequireAuth>
                                }
                            />
                            <Route
                                path="/jogo/misterio"
                                element={<Mystery />}
                            />
                            <Route
                                path="/professora/turmas/cadastrar"
                                element={
                                    <RequireAuth>
                                        <CadastrarTurma />
                                    </RequireAuth>
                                }
                            />
                            <Route
                                path="/professora/acompanhar-desempenho"
                                element={
                                    <RequireAuth>
                                        <AcompanharDesempenho />
                                    </RequireAuth>
                                }
                            />

                            <Route path="*" element={<NotFound />} />
                        </Routes>
                    </BrowserRouter>
                </AuthProvider>
            </TooltipProvider>
        </QueryClientProvider>
    );
}
