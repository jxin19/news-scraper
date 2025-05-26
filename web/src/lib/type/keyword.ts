export interface Keyword {
  id: number;
  name: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Keywords {
  list: Keyword[],
  size: number
}

export interface KeywordRequest {
  name: string;
}
