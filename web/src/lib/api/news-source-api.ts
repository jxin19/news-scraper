import { apiClient } from "@/lib/api/base-api";
import {NewsSource, NewsSourceCreateRequest, NewsSources} from "@/lib/type/news-source";

export const newsSourceApi = {
  // 뉴스 제공처 수 조회
  count: async (): Promise<number> => {
    return await apiClient<number>(`/news-source/count`, {
      method: 'GET',
    });
  },
  
  // 뉴스 제공처 목록 조회
  list: async (): Promise<NewsSources> => {
    return await apiClient<any>(`/news-source`, {
      method: 'GET',
    });
  },
  
  // 뉴스 제공처 생성
  create: async (data: NewsSourceCreateRequest): Promise<NewsSource> => {
    return await apiClient<any>(`/news-source`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },
  
  // 뉴스 제공처 활성화
  activate: async (id: number): Promise<any> => {
    return await apiClient<any>(`/news-source/${id}/activate`, {
      method: 'PATCH',
    });
  },
  
  // 뉴스 제공처 비활성화
  deactivate: async (id: number): Promise<any> => {
    return await apiClient(`/news-source/${id}/deactivate`, {
      method: 'PATCH',
    });
  },
};
