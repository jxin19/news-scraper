"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { dlqMessageApi } from "@/lib/api/dlq-message-api";
import { DlqMessage, DlqMessages, DlqStatus, DlqMessageSearchParams } from "@/lib/type/dlq-message";
import { Badge } from "@/components/ui/badge";
import { RefreshCw, Search, Eye, RotateCcw, Trash2 } from "lucide-react";

export function DlqMessageManager() {
  const [dlqMessages, setDlqMessages] = useState<DlqMessages | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedMessage, setSelectedMessage] = useState<DlqMessage | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

  // 검색 파라미터
  const [searchParams, setSearchParams] = useState<DlqMessageSearchParams>({
    page: 0,
    size: 20,
    status: null,
    key: null,
    startDate: null,
    endDate: null
  });

  // 검색 키 입력값을 별도로 관리
  const [searchKey, setSearchKey] = useState("");

  // DLQ 메시지 목록 조회
  const fetchDlqMessages = async (params: DlqMessageSearchParams = searchParams) => {
    try {
      setLoading(true);
      const result = await dlqMessageApi.list(params);
      setDlqMessages(result);
    } catch (error: any) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchDlqMessages();
  }, []);

  // 검색 파라미터 변경 시 DLQ 메시지 새로고침 (key 제외)
  useEffect(() => {
    fetchDlqMessages(searchParams);
  }, [searchParams]);

  // 검색 실행 핸들러
  const handleSearch = () => {
    setSearchParams(prev => ({
      ...prev,
      key: searchKey || null,
      page: 0, // 검색 시 첫 페이지로 이동
    }));
  };

  // 엔터 키 핸들러
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  // 상태 필터 변경 핸들러
  const handleStatusChange = (status: string) => {
    setSearchParams(prev => ({
      ...prev,
      status: status === "all" ? null : status as DlqStatus,
      page: 0,
    }));
  };

  // 페이지 변경 핸들러
  const handlePageChange = (newPage: number) => {
    setSearchParams(prev => ({ ...prev, page: newPage }));
  };

  // 필터 초기화
  const resetFilters = () => {
    setSearchKey(""); // 검색 입력값도 초기화
    setSearchParams({
      page: 0,
      size: 20,
      status: null,
      key: null,
      startDate: null,
      endDate: null
    });
  };

  // 메시지 재시도 핸들러
  const handleRetry = async (id: number) => {
    try {
      await dlqMessageApi.retry(id);
      await fetchDlqMessages(); // 목록 새로고침
      alert('메시지 재시도가 완료되었습니다.');
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 메시지 삭제 핸들러
  const handleDelete = async (id: number) => {
    if (!confirm('정말로 이 메시지를 삭제하시겠습니까?')) {
      return;
    }

    try {
      await dlqMessageApi.delete(id);
      await fetchDlqMessages(); // 목록 새로고침
      alert('메시지가 삭제되었습니다.');
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 상세 정보 보기
  const handleViewDetail = async (id: number) => {
    try {
      const message = await dlqMessageApi.detail(id);
      setSelectedMessage(message);
      setIsDetailModalOpen(true);
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 상태 배지 컴포넌트
  const StatusBadge = ({ status }: { status: DlqStatus }) => {
    const variants: Record<DlqStatus, "default" | "secondary" | "destructive" | "outline"> = {
      [DlqStatus.PENDING]: "outline",
      [DlqStatus.PROCESSED]: "default",
      [DlqStatus.FAILED]: "destructive",
      [DlqStatus.RETRYING]: "secondary"
    };

    return <Badge variant={variants[status]}>{status}</Badge>;
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  // 페이지네이션 렌더링
  const renderPagination = () => {
    if (!dlqMessages) return null;

    const pages = [];
    const maxPagesToShow = 5;
    const startPage = Math.max(0, dlqMessages.currentPage - Math.floor(maxPagesToShow / 2));
    const endPage = Math.min(dlqMessages.totalPages - 1, startPage + maxPagesToShow - 1);

    // 이전 페이지 버튼
    if (!dlqMessages.isFirst) {
      pages.push(
        <Button
          key="prev"
          variant="outline"
          size="sm"
          onClick={() => handlePageChange(dlqMessages.currentPage - 1)}
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
          variant={i === dlqMessages.currentPage ? "default" : "outline"}
          size="sm"
          onClick={() => handlePageChange(i)}
        >
          {i + 1}
        </Button>
      );
    }

    // 다음 페이지 버튼
    if (!dlqMessages.isLast) {
      pages.push(
        <Button
          key="next"
          variant="outline"
          size="sm"
          onClick={() => handlePageChange(dlqMessages.currentPage + 1)}
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
              <CardTitle>DLQ 메시지 관리</CardTitle>
              <CardDescription>
                Dead Letter Queue에 쌓인 메시지들을 관리합니다. 실패한 메시지들을 재시도하거나 삭제할 수 있습니다.
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
                placeholder="메시지 키 검색... (엔터 또는 검색 버튼 클릭)"
                value={searchKey}
                onChange={(e) => setSearchKey(e.target.value)}
                onKeyPress={handleKeyPress}
              />
              <Button onClick={handleSearch} variant="outline">
                검색
              </Button>
            </div>
            <div className="flex flex-col md:flex-row gap-2 w-full md:w-1/2">
              <div>
                <Select
                  value={searchParams.status || "all"}
                  onValueChange={handleStatusChange}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="상태 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">모든 상태</SelectItem>
                    {Object.values(DlqStatus).map((status) => (
                      <SelectItem key={status} value={status}>
                        {status}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Input
                  type="date"
                  value={searchParams.startDate || ""}
                  onChange={(e) =>
                    setSearchParams(prev => ({
                      ...prev,
                      startDate: e.target.value || null,
                      page: 0,
                    }))
                  }
                  placeholder="시작 날짜"
                />
              </div>
              <div>
                <Input
                  type="date"
                  value={searchParams.endDate || ""}
                  onChange={(e) =>
                    setSearchParams(prev => ({
                      ...prev,
                      endDate: e.target.value || null,
                      page: 0,
                    }))
                  }
                  placeholder="종료 날짜"
                />
              </div>
            </div>
          </div>

          {/* 현재 검색 조건 표시 */}
          {(searchParams.key || searchParams.status || searchParams.startDate || searchParams.endDate) && (
            <div className="bg-blue-50 p-3 rounded-lg">
              <div className="text-sm text-blue-800">
                <strong>현재 검색 조건:</strong>
                {searchParams.key && <span className="ml-2">키: "{searchParams.key}"</span>}
                {searchParams.status && <span className="ml-2">상태: {searchParams.status}</span>}
                {searchParams.startDate && <span className="ml-2">시작일: {searchParams.startDate}</span>}
                {searchParams.endDate && <span className="ml-2">종료일: {searchParams.endDate}</span>}
              </div>
            </div>
          )}

          {/* 결과 테이블 */}
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>키</TableHead>
                <TableHead>원본 토픽</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>재시도 횟수</TableHead>
                <TableHead>생성일시</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {dlqMessages && dlqMessages.content.map((message) => (
                <TableRow key={message.id}>
                  <TableCell className="font-medium">{message.id}</TableCell>
                  <TableCell className="max-w-xs">
                    <div className="truncate" title={message.key || '-'}>
                      {message.key || '-'}
                    </div>
                  </TableCell>
                  <TableCell>{message.originalTopic}</TableCell>
                  <TableCell>
                    <StatusBadge status={message.status} />
                  </TableCell>
                  <TableCell>{message.retryCount}</TableCell>
                  <TableCell>{formatDate(message.createdAt)}</TableCell>
                  <TableCell>
                    <div className="flex gap-1">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleViewDetail(message.id)}
                        title="상세보기"
                      >
                        <Eye className="h-3 w-3" />
                      </Button>
                      {message.status === DlqStatus.PENDING && (
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => handleRetry(message.id)}
                          title="재시도"
                        >
                          <RotateCcw className="h-3 w-3" />
                        </Button>
                      )}
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleDelete(message.id)}
                        className="text-red-600 hover:text-red-700"
                        title="삭제"
                      >
                        <Trash2 className="h-3 w-3" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
              {dlqMessages && dlqMessages.content.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-6">
                    검색 결과가 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          {/* 페이지네이션 */}
          {dlqMessages && dlqMessages.totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-4">
              {renderPagination()}
            </div>
          )}
        </CardContent>
        <CardFooter className="flex justify-between border-t px-6 py-4">
          <div className="text-sm text-muted-foreground">
            총 {dlqMessages?.totalElements || 0}개의 메시지
          </div>
          <div className="text-sm text-muted-foreground">
            페이지 {(dlqMessages?.currentPage || 0) + 1} / {dlqMessages?.totalPages || 1}
          </div>
        </CardFooter>
      </Card>

      {/* 메시지 상세 보기 모달 */}
      {selectedMessage && (
        <Dialog open={isDetailModalOpen} onOpenChange={setIsDetailModalOpen}>
          <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle className="text-lg">DLQ 메시지 상세 정보</DialogTitle>
              <DialogDescription>
                메시지 ID: {selectedMessage.id} • 
                상태: {selectedMessage.status} • 
                재시도 횟수: {selectedMessage.retryCount}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium">키</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.key || '-'}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">원본 토픽</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.originalTopic}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">상태</label>
                  <div className="pt-2">
                    <StatusBadge status={selectedMessage.status} />
                  </div>
                </div>
                <div>
                  <label className="text-sm font-medium">재시도 횟수</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.retryCount}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">파티션</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.originalPartition ?? '-'}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">오프셋</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.originalOffset ?? '-'}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">생성일시</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {formatDate(selectedMessage.createdAt)}
                  </p>
                </div>
                <div>
                  <label className="text-sm font-medium">재시도일시</label>
                  <p className="text-sm bg-gray-50 p-2 rounded">
                    {selectedMessage.retriedAt ? formatDate(selectedMessage.retriedAt) : '-'}
                  </p>
                </div>
              </div>

              {selectedMessage.exceptionMessage && (
                <div>
                  <label className="text-sm font-medium">예외 메시지</label>
                  <pre className="text-sm bg-red-50 p-3 rounded border text-red-700 whitespace-pre-wrap overflow-x-auto">
                    {selectedMessage.exceptionMessage}
                  </pre>
                </div>
              )}

              {selectedMessage.value && (
                <div>
                  <label className="text-sm font-medium">메시지 내용</label>
                  <pre className="text-sm bg-gray-50 p-3 rounded border whitespace-pre-wrap overflow-x-auto max-h-64">
                    {selectedMessage.value}
                  </pre>
                </div>
              )}

              <div className="bg-gray-100 p-4 rounded">
                <h4 className="font-bold mb-2">메시지 정보</h4>
                <div className="space-y-1 text-sm">
                  <p><strong>원본 토픽:</strong> {selectedMessage.originalTopic}</p>
                  <p><strong>파티션:</strong> {selectedMessage.originalPartition ?? '-'}</p>
                  <p><strong>오프셋:</strong> {selectedMessage.originalOffset ?? '-'}</p>
                  <p><strong>생성일:</strong> {formatDate(selectedMessage.createdAt)}</p>
                  {selectedMessage.retriedAt && (
                    <p><strong>재시도일:</strong> {formatDate(selectedMessage.retriedAt)}</p>
                  )}
                </div>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}