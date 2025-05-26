import { apiClient } from "@/lib/api/base-api";
import {DlqMessage, DlqMessageBulkActionRequest, DlqMessageSearchParams, DlqMessages} from "@/lib/type/dlq-message";

// 쿼리 스트링 생성 유틸리티 함수
function buildQueryString(params: Record<string, any>): string {
  const query = Object.entries(params)
    .filter(([_, value]) => value !== undefined && value !== null && value !== '')
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&');
  
  return query ? `?${query}` : '';
}

export const dlqMessageApi = {
  // DLQ 메시지 전체 수 조회
  count: async (): Promise<number> => {
    return await apiClient<number>(`/dlq-message/count`, {
      method: 'GET',
    });
  },

  // DLQ 메시지 상세 조회
  detail: async (id: number): Promise<DlqMessage> => {
    return await apiClient<any>(`/dlq-message/${id}`, {
      method: 'GET',
    });
  },
  
  // DLQ 메시지 목록 조회
  list: async (params: DlqMessageSearchParams = {}): Promise<DlqMessages> => {
    const queryParams: Record<string, any> = {
      page: params.page ?? 0,
      size: params.size ?? 20,
    };

    if (params.status) {
      queryParams.status = params.status;
    }

    if (params.key) {
      queryParams.key = params.key;
    }

    if (params.startDate) {
      queryParams.startDate = params.startDate;
    }

    if (params.endDate) {
      queryParams.endDate = params.endDate;
    }

    if (params.sort) {
      queryParams.sort = params.sort;
    }

    const queryString = buildQueryString(queryParams);
    return await apiClient<any>(`/dlq-message${queryString}`, {
      method: 'GET',
    });
  },
  
  // DLQ 메시지 재시도
  retry: async (id: number): Promise<DlqMessage> => {
    return await apiClient<any>(`/dlq-message/${id}/retry`, {
      method: 'POST',
    });
  },
  
  // DLQ 메시지 삭제
  delete: async (id: number) => {
    return await apiClient(`/dlq-message/${id}`, {
      method: 'DELETE',
    });
  },
  
  // DLQ 메시지 일괄 처리
  bulkAction: async (data: DlqMessageBulkActionRequest) => {
    return await apiClient(`/dlq-message/bulk-action`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },
};
