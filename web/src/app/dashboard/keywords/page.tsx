import { KeywordManager } from "@/components/dashboard/keyword-manager";

export default function KeywordsPage() {
  return (
    <div className="container mx-auto py-6">
      <h1 className="text-2xl font-bold mb-6">키워드 관리</h1>
      <KeywordManager />
    </div>
  );
}
