export enum DlqStatus {
  PENDING = "처리 대기중",
  PROCESSED = "재시도 성공",
  FAILED = "재시도 실패",
  RETRYING = "처리 불필요(무시)"
}

export interface DlqMessage {
  id: number;
  key: string | null;
  originalTopic: string;
  value: string | null;
  exceptionMessage: string | null;
  originalPartition: number | null;
  originalOffset: number | null;
  originalTimestamp: number | null;
  status: DlqStatus;
  retryCount: number;
  transformed: boolean;
  createdAt: string;
  retriedAt: string | null;
  processedAt: string | null;
}

export interface DlqMessages {
  content: DlqMessage[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  size: number;
  isFirst: boolean;
  isLast: boolean;
}

export interface DlqMessageSearchParams {
  status?: DlqStatus | null;
  key?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  page?: number;
  size?: number;
  sort?: string;
}

export interface DlqMessageBulkActionRequest {
  ids: number[];
  status?: DlqStatus;
  transformed?: boolean;
  retry?: boolean;
}
