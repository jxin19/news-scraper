"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { newsItemApi } from '@/lib/api/news-item-api';
import { keywordApi } from '@/lib/api/keyword-api';
import { newsSourceApi } from '@/lib/api/news-source-api';
import { NewsItem, NewsItemSearchParams } from '@/lib/type/news-item';
import { Keyword } from '@/lib/type/keyword';
import { NewsSource } from '@/lib/type/news-source';

export function NewsResults() {
  const [newsItems, setNewsItems] = useState<NewsItem[]>([]);
  const [keywords, setKeywords] = useState<Keyword[]>([]);
  const [sources, setSources] = useState<NewsSource[]>([]);
  const [searchParams, setSearchParams] = useState<NewsItemSearchParams>({
    page: 0,
    size: 10,
  });
  const [searchTitle, setSearchTitle] = useState(""); // 검색 입력값을 별도로 관리
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [isFirst, setIsFirst] = useState(true);
  const [isLast, setIsLast] = useState(true);
  const [selectedArticle, setSelectedArticle] = useState<NewsItem | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // 뉴스 아이템 목록 가져오기
  const fetchNewsItems = async (params: NewsItemSearchParams = searchParams) => {
    try {
      setLoading(true);
      const response = await newsItemApi.list(params);
      setNewsItems(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setCurrentPage(response.page);
      setIsFirst(response.isFirst);
      setIsLast(response.isLast);
    } catch (error: any) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 키워드 목록 가져오기
  const fetchKeywords = async () => {
    try {
      const response = await keywordApi.list();
      setKeywords(response.list);
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 뉴스 제공처 목록 가져오기
  const fetchSources = async () => {
    try {
      const response = await newsSourceApi.list();
      setSources(response.list);
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchNewsItems();
    fetchKeywords();
    fetchSources();
  }, []);

  // 검색 파라미터 변경 시 뉴스 아이템 새로고침 (title 제외)
  useEffect(() => {
    fetchNewsItems(searchParams);
  }, [searchParams]);

  // 검색 실행 핸들러
  const handleSearch = () => {
    setSearchParams(prev => ({
      ...prev,
      title: searchTitle || null,
      page: 0, // 검색 시 첫 페이지로 이동
    }));
  };

  // 엔터 키 핸들러
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  // 키워드 필터 변경 핸들러
  const handleKeywordChange = (keywordId: string) => {
    setSearchParams(prev => ({
      ...prev,
      keywordId: keywordId === "all" ? null : parseInt(keywordId),
      page: 0,
    }));
  };

  // 소스 필터 변경 핸들러
  const handleSourceChange = (sourceId: string) => {
    setSearchParams(prev => ({
      ...prev,
      sourceId: sourceId === "all" ? null : parseInt(sourceId),
      page: 0,
    }));
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setSearchParams(prev => ({
      ...prev,
      page,
    }));
  };

  // 필터 초기화
  const resetFilters = () => {
    setSearchTitle(""); // 검색 입력값도 초기화
    setSearchParams({
      page: 0,
      size: 10,
    });
  };

  // 상세 보기 열기
  const openDetail = async (newsItem: NewsItem) => {
    try {
      const detailData = await newsItemApi.detail(newsItem.id);
      setSelectedArticle(detailData);
      setIsDetailModalOpen(true);
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 페이지네이션 렌더링
  const renderPagination = () => {
    const pages = [];
    const maxPagesToShow = 5;
    const startPage = Math.max(0, currentPage - Math.floor(maxPagesToShow / 2));
    const endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);

    // 이전 페이지 버튼
    if (!isFirst) {
      pages.push(
        <Button
          key="prev"
          variant="outline"
          size="sm"
          onClick={() => handlePageChange(currentPage - 1)}
        >
          이전
        </Button>
      );
    }

    // 페이지 번호 버튼들
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <Button
          key={i}
          variant={i === currentPage ? "default" : "outline"}
          size="sm"
          onClick={() => handlePageChange(i)}
        >
          {i + 1}
        </Button>
      );
    }

    // 다음 페이지 버튼
    if (!isLast) {
      pages.push(
        <Button
          key="next"
          variant="outline"
          size="sm"
          onClick={() => handlePageChange(currentPage + 1)}
        >
          다음
        </Button>
      );
    }

    return pages;
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="p-6">
            <div className="text-center">로딩 중...</div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <div>
              <CardTitle>뉴스 수집 결과</CardTitle>
              <CardDescription>
                키워드에 맞게 수집된 최신 뉴스 기사들입니다. 매 5분마다 새로운 뉴스 기사가 업데이트됩니다.
              </CardDescription>
            </div>
            <Button variant="outline" onClick={resetFilters}>
              필터 초기화
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 검색 및 필터 */}
          <div className="flex flex-col md:flex-row gap-4">
            <div className="w-full md:w-1/2 flex gap-2">
              <Input
                placeholder="기사 제목 검색... (엔터 또는 검색 버튼 클릭)"
                value={searchTitle}
                onChange={(e) => setSearchTitle(e.target.value)}
                onKeyPress={handleKeyPress}
              />
              <Button onClick={handleSearch} variant="outline">
                검색
              </Button>
            </div>
            <div className="flex flex-col md:flex-row gap-2 w-full md:w-1/2">
              <div>
                <Select
                  value={searchParams.keywordId?.toString() || "all"}
                  onValueChange={handleKeywordChange}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="키워드 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">모든 키워드</SelectItem>
                    {keywords.map((keyword) => (
                      <SelectItem key={keyword.id} value={keyword.id.toString()}>
                        {keyword.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Select
                  value={searchParams.sourceId?.toString() || "all"}
                  onValueChange={handleSourceChange}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="제공처 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">모든 제공처</SelectItem>
                    {sources.map((source) => (
                      <SelectItem key={source.id} value={source.id.toString()}>
                        {source.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>

          {/* 현재 검색 조건 표시 */}
          {(searchParams.title || searchParams.keywordId || searchParams.sourceId) && (
            <div className="bg-blue-50 p-3 rounded-lg">
              <div className="text-sm text-blue-800">
                <strong>현재 검색 조건:</strong>
                {searchParams.title && <span className="ml-2">제목: "{searchParams.title}"</span>}
                {searchParams.keywordId && (
                  <span className="ml-2">
                    키워드: {keywords.find(k => k.id === searchParams.keywordId)?.name}
                  </span>
                )}
                {searchParams.sourceId && (
                  <span className="ml-2">
                    제공처: {sources.find(s => s.id === searchParams.sourceId)?.name}
                  </span>
                )}
              </div>
            </div>
          )}

          {/* 결과 테이블 */}
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>제목</TableHead>
                <TableHead>소스</TableHead>
                <TableHead>키워드</TableHead>
                <TableHead>발행일</TableHead>
                <TableHead>수집일</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {newsItems.map((item) => (
                <TableRow key={item.id}>
                  <TableCell className="font-medium max-w-md">
                    <button
                      className="text-left hover:underline truncate block w-full"
                      onClick={() => openDetail(item)}
                      title={item.title}
                    >
                      {item.title}
                    </button>
                  </TableCell>
                  <TableCell>{item.sourceName}</TableCell>
                  <TableCell>
                    <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs">
                      {item.keywordName}
                    </span>
                  </TableCell>
                  <TableCell>
                    {item.publishedDate 
                      ? new Date(item.publishedDate).toLocaleDateString()
                      : "-"
                    }
                  </TableCell>
                  <TableCell>
                    {new Date(item.collectedAt).toLocaleDateString()}
                  </TableCell>
                  <TableCell>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => openDetail(item)}
                    >
                      상세보기
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
              {newsItems.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-6">
                    검색 결과가 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-4">
              {renderPagination()}
            </div>
          )}
        </CardContent>
        <CardFooter className="flex justify-between border-t px-6 py-4">
          <div className="text-sm text-muted-foreground">
            총 {totalElements}개의 기사
          </div>
          <div className="text-sm text-muted-foreground">
            페이지 {currentPage + 1} / {totalPages}
          </div>
        </CardFooter>
      </Card>

      {/* 뉴스 상세 보기 모달 */}
      {selectedArticle && (
        <Dialog open={isDetailModalOpen} onOpenChange={setIsDetailModalOpen}>
          <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle className="text-lg">{selectedArticle.title}</DialogTitle>
              <DialogDescription>
                {selectedArticle.sourceName} • 
                {selectedArticle.publishedDate 
                  ? new Date(selectedArticle.publishedDate).toLocaleDateString()
                  : "발행일 미상"
                } • 
                키워드: {selectedArticle.keywordName}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              {selectedArticle.content ? (
                <div 
                  className="prose max-w-none"
                  dangerouslySetInnerHTML={{ __html: selectedArticle.content }}
                />
              ) : (
                <p className="text-muted-foreground">
                  본문 내용이 없습니다. 원문 링크를 통해 확인해주세요.
                </p>
              )}
              
              <div className="bg-gray-100 p-4 rounded">
                <h4 className="font-bold mb-2">기사 정보</h4>
                <div className="space-y-1 text-sm">
                  <p><strong>소스:</strong> {selectedArticle.sourceName}</p>
                  <p><strong>키워드:</strong> {selectedArticle.keywordName}</p>
                  <p><strong>수집일:</strong> {new Date(selectedArticle.collectedAt).toLocaleString()}</p>
                  {selectedArticle.publishedDate && (
                    <p><strong>발행일:</strong> {new Date(selectedArticle.publishedDate).toLocaleString()}</p>
                  )}
                </div>
              </div>
            </div>
            <DialogFooter>
              <a
                href={selectedArticle.url}
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button>원문 보기</Button>
              </a>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}