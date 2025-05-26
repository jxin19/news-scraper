"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {useRouter} from 'next/navigation';
import { Button } from "@/components/ui/button";
import {useMemberStore} from "@/lib/store/member-store";

export function DashboardNavigation() {
  const pathname = usePathname();
  const router = useRouter();
  const memberStore = useMemberStore();

  const navItems = [
    { name: "개요", path: "/dashboard" },
    { name: "키워드 관리", path: "/dashboard/keywords" },
    { name: "뉴스 사이트 관리", path: "/dashboard/sources" },
    { name: "수집 결과", path: "/dashboard/results" },
    { name: "DLQ 메시지", path: "/dashboard/dlq-messages" },
  ];

  const logout = () => {
    memberStore.destroy();
    router.push('/');
  };

  return (
    <div className="w-full border-b bg-gray-50 dark:bg-gray-900 px-4 py-3">
      <div className="flex justify-between items-center container mx-auto">
        <div className="flex items-center space-x-2">
          <div className="text-xl font-bold">뉴스서비스 - 신민준</div>
          <div className="hidden md:flex ml-6 space-x-2">
            {navItems.map((item) => (
              <Link key={item.path} href={item.path}>
                <Button
                  variant={pathname === item.path ? "default" : "ghost"}
                  className="h-9"
                >
                  {item.name}
                </Button>
              </Link>
            ))}
          </div>
        </div>
        
        <div className="flex items-center">
          <Button
              variant="ghost"
              className="text-red-500 hover:text-red-700 hover:bg-red-50"
              onClick={logout}
          >
            로그아웃
          </Button>
        </div>
      </div>
      
      {/* 모바일 화면에서 메뉴 표시 */}
      <div className="md:hidden flex overflow-x-auto space-x-2 pt-2 pb-1">
        {navItems.map((item) => (
          <Link key={item.path} href={item.path}>
            <Button
              variant={pathname === item.path ? "default" : "ghost"}
              className="h-9 whitespace-nowrap"
              size="sm"
            >
              {item.name}
            </Button>
          </Link>
        ))}
      </div>
    </div>
  );
}
