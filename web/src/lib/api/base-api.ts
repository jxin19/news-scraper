'use client'

/**
 * API 요청을 처리하는 유틸리티 함수
 * /api 경로로 시작하는 모든 요청은 API_URL로 프록시됩니다.
 */
export async function apiClient<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const { headers, ...rest } = options;

    // 쿠키에서 인증 토큰 추출
    const authToken = getTokenFromCookie('Authorization');
    const refreshToken = getTokenFromCookie('Refresh-token');

    // 기본 헤더 설정
    const defaultHeaders: HeadersInit = {
        'Content-Type': 'application/json',
    };

    // 인증 토큰이 있는 경우 헤더에 추가
    if (authToken) {
        defaultHeaders['Authorization'] = `Bearer ${authToken}`;
    }

    // 리프레시 토큰이 있는 경우 헤더에 추가
    if (refreshToken) {
        defaultHeaders['Refresh-token'] = refreshToken;
    }

    try {
        const response = await fetch(`/api${endpoint}`, {
            headers: {
                ...defaultHeaders,
                ...headers,
            },
            ...rest,
        });

        // 401 에러인 경우 로그인 페이지로 리다이렉트
        if (response.status === 401) {
            // 인증 관련 쿠키 삭제
            document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            document.cookie = 'Refresh-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

            // 로그인 페이지로 리다이렉트
            window.location.href = '/login';
            return Promise.reject(new Error('Unauthorized'));
        }

        // 응답이 성공이 아닌 경우 에러 처리
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.meta?.message || 'Failed request API');
        }

        // 응답 헤더의 Content-Type을 확인하여 처리 방식 결정
        const contentType = response.headers.get('Content-Type') || '';

        if (contentType.includes('application/json')) {
            // JSON 응답인 경우
            const data = await response.json();
            return data as T;
        } else {
            // JSON이 아닌 경우 - 텍스트, 바이너리 등
            // 반환 타입에 따라 적절한 처리 필요
            if (options.headers && (options.headers as Record<string, string>)['Accept'] === 'text/plain') {
                const text = await response.text();
                return text as unknown as T;
            }

            // 바이너리 데이터 또는 기타 형식의 경우
            // 기본적으로 response 객체 자체를 반환하고 호출자가 처리하도록 함
            return response as unknown as T;
        }
    } catch (error) {
        console.error('Occurred request error:', error);
        throw error;
    }
}

/**
 * 쿠키에서 특정 토큰 값을 추출하는 함수
 */
function getTokenFromCookie(tokenName: string): string | null {
    if (typeof document === 'undefined') return null;

    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === tokenName) {
            return decodeURIComponent(value);
        }
    }
    return null;
}