import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

interface MemberState {
    isAuthenticated: () => boolean;
    destroy: () => void;
}

export const useMemberStore = create<MemberState>()(
    persist(
        () => ({
            isAuthenticated: () => {
                // Check if we're on the client side
                if (typeof window !== 'undefined') {
                    // Get all cookies
                    const cookieString = document.cookie;
                    const cookies = cookieString.split(';');

                    // Check for refresh token
                    const refreshTokenCookie = cookies.find(cookie =>
                        cookie.trim().startsWith('Refresh-token=')
                    );

                    if (refreshTokenCookie) {
                        // Check if we have an access token
                        const accessTokenCookie = cookies.find(cookie =>
                            cookie.trim().startsWith('Authorization=')
                        );

                        if (accessTokenCookie) {
                            return true;
                        }
                    }
                }

                return false;
            },

            destroy: () => {
                // Remove cookies
                if (typeof window !== 'undefined') {
                    document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                    document.cookie = 'Refresh-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                }
            },
        }),
        {
            name: 'member-storage',
            storage: createJSONStorage(() => localStorage),
        }
    )
);
