import { NewsResults } from "@/components/dashboard/news-results";

export default function ResultsPage() {
  return (
    <div className="container mx-auto py-6">
      <h1 className="text-2xl font-bold mb-6">뉴스 수집 결과</h1>
      <NewsResults />
    </div>
  );
}
