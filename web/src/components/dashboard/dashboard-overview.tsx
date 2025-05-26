"use client";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { useState, useEffect } from 'react';
import {keywordApi} from '@/lib/api/keyword-api';
import {newsSourceApi} from '@/lib/api/news-source-api';
import {newsItemApi} from '@/lib/api/news-item-api';
import {dlqMessageApi} from '@/lib/api/dlq-message-api';
import {NewsItem} from '@/lib/type/news-item';

export function DashboardOverview() {
  // 대시보드에 표시할 데이터
  const [stats, setStats] = useState({
    keywordCount: 0,
    sourceCount: 0,
    totalArticles: 0,
    newArticlesToday: 0,
    dlqMessageCount: 0,
  });

  // 최근 뉴스 데이터
  const [recentNews, setRecentNews] = useState<NewsItem[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [keywordCount, sourceCount, totalArticles, newArticlesToday, newsResponse] = await Promise.all([
          keywordApi.count(),
          newsSourceApi.count(),
          newsItemApi.count(),
          newsItemApi.countTodayCollected(),
          newsItemApi.list({ page: 0, size: 5 }) // 최근 5개 뉴스 조회
        ]);

        setStats(prev => ({
          ...prev,
          keywordCount,
          sourceCount,
          totalArticles,
          newArticlesToday
        }));

        setRecentNews(newsResponse.content);
      } catch (error: any) {
        alert(error.message);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">등록된 키워드</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.keywordCount}개</div>
            <p className="text-xs text-muted-foreground mt-1">
              다양한 키워드로 뉴스를 수집하고 있습니다
            </p>
            <Link href="/dashboard/keywords">
              <Button variant="outline" size="sm" className="mt-4">
                키워드 관리
              </Button>
            </Link>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">등록된 뉴스 사이트</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.sourceCount}개</div>
            <p className="text-xs text-muted-foreground mt-1">
              다양한 뉴스 사이트에서 정보를 수집합니다
            </p>
            <Link href="/dashboard/sources">
              <Button variant="outline" size="sm" className="mt-4">
                사이트 관리
              </Button>
            </Link>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">수집된 기사</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalArticles}개</div>
            <p className="text-xs text-muted-foreground mt-1">
              오늘 {stats.newArticlesToday}개의 새 기사가 수집되었습니다
            </p>
            <Link href="/dashboard/results">
              <Button variant="outline" size="sm" className="mt-4">
                결과 보기
              </Button>
            </Link>
          </CardContent>
        </Card>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>최근 수집된 주요 뉴스</CardTitle>
          <CardDescription>
            {recentNews.length > 0 ? `최근 수집된 ${recentNews.length}개의 뉴스` : '뉴스를 불러오는 중...'}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {recentNews.length > 0 ? (
              recentNews.map((news) => (
                <RecentNewsItem 
                  key={news.id}
                  title={news.title}
                  source={news.sourceName}
                  url={news.url}
                  publishedDate={news.publishedDate}
                  collectedAt={news.collectedAt}
                  keyword={news.keywordName}
                />
              ))
            ) : (
              <div className="text-center py-8 text-muted-foreground">
                수집된 뉴스가 없습니다.
              </div>
            )}
          </div>
          {recentNews.length > 0 && (
            <div className="mt-4 text-center">
              <Link href="/dashboard/results">
                <Button variant="outline" size="sm">
                  모든 뉴스 보기
                </Button>
              </Link>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

function RecentNewsItem({ title, source, url, publishedDate, collectedAt, keyword }: {
  title: string;
  source: string;
  url: string;
  publishedDate: string | null;
  collectedAt: string;
  keyword: string;
}) {
  // 날짜 포맷팅 함수
  const formatRelativeTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInHours < 1) {
      const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
      return `${diffInMinutes}분 전`;
    } else if (diffInHours < 24) {
      return `${diffInHours}시간 전`;
    } else {
      return `${diffInDays}일 전`;
    }
  };

  const displayDate = publishedDate 
    ? formatRelativeTime(publishedDate)
    : formatRelativeTime(collectedAt);

  return (
    <div className="flex justify-between items-start border-b pb-3 last:border-b-0">
      <div className="flex-1 pr-4">
        <a 
          href={url} 
          target="_blank" 
          rel="noopener noreferrer"
          className="hover:text-blue-600 transition-colors"
        >
          <h3 className="font-medium leading-tight overflow-hidden" style={{
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical'
          }}>{title}</h3>
        </a>
        <div className="text-sm text-muted-foreground mt-1">
          {source} • {displayDate}
        </div>
      </div>
      <div className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded whitespace-nowrap">
        {keyword}
      </div>
    </div>
  );
}
