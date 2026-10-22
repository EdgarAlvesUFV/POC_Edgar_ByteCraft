import api from "../../../../api/api";
import { DifficultyResponse } from "../types";

// Você precisará adicionar esse tipo no types/index.ts (veja abaixo)

export const listDifficulties = async (): Promise<DifficultyResponse[]> => {
    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");

    const response = await api.get(
        '/difficult/list',
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};