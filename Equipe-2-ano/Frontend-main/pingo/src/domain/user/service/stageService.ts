import api from "../../../../api/api";
import { StageResponse } from "../types";

export const listStages = async (): Promise<StageResponse[]> => {
    const token = localStorage.getItem('accessToken'); // pega o token armazenado
    if (!token) throw new Error("Usuário não autenticado");
    const response = await api.get(
        '/stage/list',
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};
