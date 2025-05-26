import { apiClient } from "@/lib/api/base-api";
import {NewsItem, NewsItems, NewsItemSearchParams} from "@/lib/type/news-item";

// 쿼리 스트링 생성 유틸리티 함수
function buildQueryString(params: Record<string, any>): string {
  const query = Object.entries(params)
    .filter(([_, value]) => value !== undefined && value !== null)
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&');
  
  return query ? `?${query}` : '';
}

export const newsItemApi = {
  // 뉴스 아이템 전체 수 조회
  count: async (): Promise<number> => {
    return await apiClient<number>(`/news-items/count`, {
      method: 'GET',
    });
  },
  
  // 오늘 수집된 뉴스 아이템 수 조회
  countTodayCollected: async (): Promise<number> => {
    return await apiClient<number>(`/news-items/count/today`, {
      method: 'GET',
    });
  },
  
  // 뉴스 아이템 목록 조회
  list: async (params: NewsItemSearchParams = {}): Promise<NewsItems> => {
    const queryString = buildQueryString(params);
    return await apiClient<any>(`/news-items${queryString}`, {
      method: 'GET',
    });
  },
  
  // 뉴스 아이템 상세 조회
  detail: async (id: number): Promise<NewsItem> => {
    return await apiClient<any>(`/news-items/${id}`, {
      method: 'GET',
    });
  },
};
