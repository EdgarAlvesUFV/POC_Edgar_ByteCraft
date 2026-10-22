import api from "../../../../api/api";
import { ScoreUpdateRequest, ScoreResponse, ScoreCreateRequest } from "../types";

export const updateScore = async (data: ScoreUpdateRequest): Promise<ScoreResponse> => {
    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");

    const response = await api.patch(
        '/score/update',
        data,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};

export const createScore = async (data: ScoreCreateRequest): Promise<ScoreResponse> => {
    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");

    const response = await api.post(
        '/score/create',
        data,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};