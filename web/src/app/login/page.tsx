"use client";

import { LoginForm } from "@/components/auth/login-form";
import Link from "next/link";

export default function LoginPage() {
  return (
    <div className="container mx-auto p-5">
      <div className="flex justify-between items-center mb-8">
        <Link href="/" className="text-2xl font-bold">뉴스서비스 - 신민준</Link>
      </div>
      <div className="max-w-md mx-auto">
        <h1 className="text-2xl font-bold mb-6">로그인</h1>
        <LoginForm />
      </div>
    </div>
  );
}
