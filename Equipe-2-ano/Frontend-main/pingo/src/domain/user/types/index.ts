// Define a estrutura de dados que enviamos para a API
export interface UserAuthRequest {
    nickname: string;
    password: string;
}

// Define a estrutura de dados que recebemos da API após o sucesso
export interface UserAuthResponse {
    nickname: string;
    accessToken: string;
}

export interface StageRequest {
    name: string;
    number: number;
}

export interface StageResponse {
    name: string;
    number: number;
}

export interface UserProgressRequest {
    nickname: string;
    currentStage: number;
}

export interface UserProgressResponse {
    userId: number;
    nickname: string;
    currentStage: number;
}
export interface ScoreUpdateRequest {
    userId: number;
    stageId: number;
    points: number;
}

export interface ScoreResponse {
    id: number;
    points: number;
    stageId: number;
    userId: number;
}

export interface NetworkDeviceResponse {
    id: number;
    name: string;
    type: number;
    hasDeffect: boolean;
    positionY: number;
    positionX: number;
}

export interface PhaseSetupResponse {
    devices: NetworkDeviceResponse[];
    correctPaths: number[][][];
}
export interface ScoreCreateRequest {
    userId: number;
    stageId: number;
}
export interface DifficultyResponse {
    id: number;
    noiseAmount: number;
    lightAmount: number; // No frontend era pointAmount, no banco é light_amount
    speed: number;       // No frontend era gameSpeed
    level: 'One' | 'Two' | 'Three'; // O backend retorna o Enum como string
}''