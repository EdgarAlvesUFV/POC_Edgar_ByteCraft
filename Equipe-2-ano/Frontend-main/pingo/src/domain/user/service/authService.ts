import { UserAuthRequest, UserAuthResponse } from '../types';
import api from "../../../../api/api";

export const authenticateUser = async (data: UserAuthRequest): Promise<UserAuthResponse> => {
    const response = await api.post('/auth/authenticate', data);
    return response.data;
};
