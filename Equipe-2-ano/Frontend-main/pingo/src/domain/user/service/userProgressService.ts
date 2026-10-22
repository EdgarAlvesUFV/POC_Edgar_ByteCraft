import api from "../../../../api/api";
import { UserProgressRequest, UserProgressResponse } from "../types";

export const getUserProgress = async (data: string | null): Promise<UserProgressResponse> => {
    if (data == null) throw new Error("Usuário não autenticado");
    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");
    const response = await api.get(
        '/progress/get', 
        {   
            headers: {
                Authorization: `Bearer ${token}`,
            },
            params: {nickname: data}
        }
    );
    return response.data;
};

export const saveProgress = async (data: UserProgressRequest): Promise<UserProgressResponse> => {
    if (!data) throw new Error("Usuário não autenticado");

    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");

    const response = await api.post(
        '/progress/save',
        data,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};
