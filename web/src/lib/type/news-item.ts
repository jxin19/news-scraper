export interface NewsItem {
  id: number;
  keywordId: number;
  keywordName: string;
  sourceId: number;
  sourceName: string;
  title: string;
  url: string;
  content: string | null;
  publishedDate: string | null
  collectedAt: string
}

export interface NewsItems {
  content: NewsItem[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  isFirst: boolean;
  isLast: boolean;
}

export interface NewsItemSearchParams {
  title?: string | null;
  sourceId?: number | null;
  keywordId?: number | null;
  page?: number;
  size?: number;
}
