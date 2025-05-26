"use client";

import { RegisterForm } from "@/components/auth/register-form";
import Link from "next/link";

export default function RegisterPage() {
  return (
    <div className="container mx-auto p-5">
      <div className="flex justify-between items-center mb-8">
        <Link href="/" className="text-2xl font-bold">뉴스서비스 - 신민준</Link>
      </div>
      <div className="max-w-md mx-auto">
        <h1 className="text-2xl font-bold mb-6">회원가입</h1>
        <RegisterForm />
      </div>
    </div>
  );
}
