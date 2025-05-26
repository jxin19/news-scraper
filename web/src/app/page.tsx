"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";

export default function Home() {
  return (
    <div className="flex flex-col min-h-screen">
      <header className="border-b p-5">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">뉴스서비스 - 신민준</h1>
          <div className="space-x-4">
            <Link href="/login">
              <Button variant="outline">로그인</Button>
            </Link>
            <Link href="/register">
              <Button>회원가입</Button>
            </Link>
          </div>
        </div>
      </header>

      <main className="flex-1 p-5">
        {/* 히어로 섹션 */}
        <section className="bg-gradient-to-r from-blue-500 to-indigo-600 text-white py-16 px-5 rounded-lg mb-10">
          <div className="container mx-auto text-center">
            <h2 className="text-4xl font-bold mb-4">관심 키워드로 맞춤 뉴스 알림을 받으세요</h2>
            <p className="text-xl max-w-2xl mx-auto mb-8">
              원하는 키워드를 등록하고 관련 뉴스를 자동으로 수집하여 맞춤 정보를 제공합니다. 
              중요한 정보를 놓치지 마세요!
            </p>
            <Link href="/register">
              <Button size="lg" className="bg-white text-blue-600 hover:bg-blue-50">
                지금 시작하기
              </Button>
            </Link>
          </div>
        </section>

        {/* 기능 소개 섹션 */}
        <section className="py-16 bg-gray-50 rounded-lg p-5 mb-10">
          <div className="container mx-auto">
            <h2 className="text-3xl font-bold text-center mb-12">주요 기능</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <Card>
                <CardHeader>
                  <CardTitle>키워드 관리</CardTitle>
                  <CardDescription>
                    원하는 키워드를 등록하고 관리하세요
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p>
                    관심 있는 주제나 키워드를 등록하고 언제든지 수정할 수 있습니다. 
                    키워드를 효율적으로 관리하세요.
                  </p>
                </CardContent>
              </Card>
              
              <Card>
                <CardHeader>
                  <CardTitle>뉴스 제공처 설정</CardTitle>
                  <CardDescription>
                    뉴스를 수집할 사이트를 직접 선택하세요
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p>
                    신뢰할 수 있는 뉴스 사이트를 직접 선택하여 원하는 소스에서만 
                    정보를 수집하도록 설정할 수 있습니다.
                  </p>
                </CardContent>
              </Card>
              
              <Card>
                <CardHeader>
                  <CardTitle>수집 결과 조회</CardTitle>
                  <CardDescription>
                    수집된 뉴스를 한 곳에서 확인하세요
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p>
                    등록한 키워드에 맞게 수집된 최신 뉴스를 한 곳에서 확인할 수 있습니다. 
                    최신 뉴스 정보를 놓치지 마세요.
                  </p>
                </CardContent>
              </Card>
            </div>
          </div>
        </section>

        {/* 사용 방법 섹션 */}
        <section className="py-16 p-5 rounded-lg mb-10">
          <div className="container mx-auto">
            <h2 className="text-3xl font-bold text-center mb-12">이용 방법</h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8 text-center">
              <div>
                <div className="w-16 h-16 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">1</div>
                <h3 className="text-xl font-semibold mb-2">회원가입</h3>
                <p>간단한 정보로 계정을 생성하세요</p>
              </div>
              
              <div>
                <div className="w-16 h-16 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">2</div>
                <h3 className="text-xl font-semibold mb-2">키워드 등록</h3>
                <p>관심 있는 키워드를 등록하세요</p>
              </div>
              
              <div>
                <div className="w-16 h-16 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">3</div>
                <h3 className="text-xl font-semibold mb-2">뉴스 사이트 설정</h3>
                <p>수집할 뉴스 제공처를 선택하세요</p>
              </div>
              
              <div>
                <div className="w-16 h-16 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">4</div>
                <h3 className="text-xl font-semibold mb-2">수집 결과 확인</h3>
                <p>수집된 뉴스를 확인하고 분석하세요</p>
              </div>
            </div>
          </div>
        </section>

        {/* 가입 유도 섹션 */}
        <section className="py-16 bg-gray-900 text-white rounded-lg p-5">
          <div className="container mx-auto text-center">
            <h2 className="text-3xl font-bold mb-6">지금 바로 시작하세요</h2>
            <p className="text-xl max-w-2xl mx-auto mb-8">
              무료로 서비스를 이용해보고, 맞춤형 뉴스 알림의 편리함을 경험하세요.
            </p>
            <div className="flex justify-center space-x-4">
              <Link href="/register">
                <Button size="lg" className="bg-white text-gray-900 hover:bg-gray-200">
                  무료로 시작하기
                </Button>
              </Link>
              <Link href="/login">
                <Button size="lg" variant="outline" className="text-gray-900 border-white hover:bg-gray-800">
                  로그인
                </Button>
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="border-t py-8 p-5">
        <div className="container mx-auto text-center text-gray-600">
          <p>&copy; 2025 뉴스서비스 - 신민준. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
