import api from "../../../../api/api";
import { PhaseSetupResponse } from "../types";

export const getPhaseSetup = async (): Promise<PhaseSetupResponse> => {
    const token = localStorage.getItem('accessToken');
    if (!token) throw new Error("Usuário não autenticado");
    const response = await api.get(
        '/network-devices/phase-setup',
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    return response.data;
};
