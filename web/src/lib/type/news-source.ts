export interface NewsSource {
  id: number;
  name: string;
  url: string;
  code: NewsSourceCode;
  type: NewsSourceType;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface NewsSources {
  list: NewsSource[],
  size: number
}

export enum NewsSourceType {
  RSS_STATIC = "RSS_STATIC",
  RSS_KEYWORD = "RSS_KEYWORD",
  SCRAPING = "SCRAPING"
}

export enum NewsSourceCode {
  YNA = "YNA",
  SBS = "SBS",
  MK = "MK",
  GOOGLE = "GOOGLE",
  NAVER = "NAVER"
}

export interface NewsSourceCreateRequest {
  name: string;
  url: string;
  code: string;
  type: NewsSourceType;
}
