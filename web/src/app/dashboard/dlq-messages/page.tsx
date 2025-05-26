"use client";

import { DlqMessageManager } from "@/components/dashboard/dlq-message-manager";

export default function DlqMessagesPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">DLQ 메시지 관리</h1>
        <p className="text-muted-foreground">
          처리 실패한 메시지들을 조회하고 관리할 수 있습니다.
        </p>
      </div>
      <DlqMessageManager />
    </div>
  );
}
