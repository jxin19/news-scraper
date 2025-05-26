import { apiClient } from "@/lib/api/base-api";
import {Keyword, KeywordRequest, Keywords} from "@/lib/type/keyword";

export const keywordApi = {
  // 키워드 수 조회
  count: async (): Promise<number> => {
    return await apiClient<number>(`/keyword/count`, {
      method: 'GET',
    });
  },
  
  // 키워드 목록 조회
  list: async (): Promise<Keywords> => {
    return await apiClient<any>(`/keyword`, {
      method: 'GET',
    });
  },
  
  // 키워드 생성
  create: async (data: KeywordRequest): Promise<Keyword> => {
    return await apiClient<any>(`/keyword`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },
  
  // 키워드 활성화 상태 토글
  toggle: async (id: number) => {
    return await apiClient(`/keyword/${id}/toggle`, {
      method: 'PATCH',
    });
  },
};
