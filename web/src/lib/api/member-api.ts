import {apiClient} from "@/lib/api/base-api";
import {Member} from "@/lib/type/member";

export const memberApi = {
    // 회원 상세 조회 API
    detail: async (): Promise<Member> => {
        return await apiClient<any>(`/member`, {
            method: 'GET',
        });
    },

    // 회원가입 API
    signup: async (data: {
        username: string;
        password: string;
    }) => {
        return apiClient('/member', {
            method: 'POST',
            body: JSON.stringify(data),
        });
    },

    // 로그인 API
    login: async (data: { username?: string; password: string }) => {
        return apiClient('/member/login', {
            method: 'POST',
            headers: {
                'Accept': 'text/plain'
            },
            body: JSON.stringify(data),
        });
    },

    // 리프레시 토큰 API
    refreshToken: async () => {
        return apiClient('/member/refresh-token', {
            method: 'POST',
        });
    }
};