export interface Member {
    id: number | null;
    username: string;
    password: string | undefined;
}

export interface AuthResponse {
    username: string | null;
    accessToken: string;
    refreshToken: string;
}