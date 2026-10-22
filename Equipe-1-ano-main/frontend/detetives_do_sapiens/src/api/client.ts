import axios, { AxiosError } from 'axios';
import { API_BASE } from '@/config/api';


export const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
    // Para casos onde o backend usa sessão/cookie
    withCredentials: false,
});

// Função Para buscar o header de autenticação
export function getHeaders(extra?: HeadersInit) {
    const token = localStorage.getItem("auth:token");
    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(extra || {}),
    } as HeadersInit;
}

// Interceptor para tratamento de erros
api.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
        // Trata erros específicos do backend
        if (error.response?.status === 400) {
            // Erro de validação - retorna os erros de campo
            return Promise.reject({
                type: 'validation',
                errors: error.response.data as Record<string, string>,
            });
        }

        if (error.response?.status === 409) {
            // Conflito de integridade
            return Promise.reject({
                type: 'conflict',
                message: (error.response.data as any)?.error || 'Erro de integridade',
            });
        }

        if (error.response?.status >= 500) {
            // Erro interno do servidor
            return Promise.reject({
                type: 'server',
                message: 'Erro interno do servidor',
            });
        }

        return Promise.reject(error);
    }
);

export default api;
export { API_BASE };