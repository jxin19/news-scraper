import { NextRequest, NextResponse } from 'next/server';

// 로그인이 필요한 경로들
const authRequiredPaths = ['/dashboard'];

export default async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // 로그인이 필요한 경로인지 확인
  const isAuthRequired = authRequiredPaths.some(path => pathname.startsWith(path));

  if (isAuthRequired) {
    // 쿠키에서 인증 토큰 가져오기
    const refreshTokenCookie = request.cookies.get('Refresh-token')?.value;

    // 리프레시 토큰이 있는 경우 토큰 재발급 시도
    if (refreshTokenCookie) {
      return NextResponse.next();
    } else {
      // 리프레시 토큰이 없거나 재발급 실패 시 로그인 페이지로 리다이렉트
      const loginUrl = new URL('/login', request.url);
      loginUrl.searchParams.set('returnUrl', request.nextUrl.pathname + request.nextUrl.search);

      const response = NextResponse.redirect(loginUrl);

      // 인증 관련 쿠키 무효화
      response.cookies.set({
        name: 'Authorization',
        value: '',
        path: '/',
        expires: new Date(0),
        secure: true,
      });

      response.cookies.set({
        name: 'Refresh-token',
        value: '',
        path: '/',
        expires: new Date(0),
        secure: true,
      });

      return response;
    }
  }
  
  return NextResponse.next()
}

export const config = {
  matcher: ['/((?!api|_next|_vercel|.*\\..*).*)']
};