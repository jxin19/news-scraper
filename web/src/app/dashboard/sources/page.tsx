import { SourceManager } from "@/components/dashboard/source-manager";

export default function SourcesPage() {
  return (
    <div className="container mx-auto py-6">
      <h1 className="text-2xl font-bold mb-6">뉴스 수집 사이트 관리</h1>
      <SourceManager />
    </div>
  );
}
