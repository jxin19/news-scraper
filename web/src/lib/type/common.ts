export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

export interface DateRangeParams {
  startDate?: string;
  endDate?: string;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: string;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    unpaged: boolean;
    paged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
