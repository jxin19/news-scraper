import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    reactStrictMode: false,
    output: 'standalone',
    eslint: {
        ignoreDuringBuilds: true,
    },
    typescript: {
        ignoreBuildErrors: true,
    },
    env: {
        API_URL: process.env.API_URL || 'http://ddi-api:8080',
    },
    async rewrites() {
        const apiUrl = process.env.API_URL || 'http://ddi-api:8080';
        console.log('API URL for rewrites:', apiUrl);
        
        return [
            {
                source: '/api/:path*',
                destination: `${apiUrl}/:path*`,
            },
        ];
    },
};

export default nextConfig;