"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { newsSourceApi } from '@/lib/api/news-source-api';
import { NewsSource, NewsSourceType, NewsSourceCode } from '@/lib/type/news-source';

export function SourceManager() {
  const [sources, setSources] = useState<NewsSource[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [newSource, setNewSource] = useState({
    name: "",
    url: "",
    code: NewsSourceCode.GOOGLE,
    type: NewsSourceType.RSS_STATIC,
  });
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // API에서 뉴스 제공처 목록 가져오기
  const fetchSources = async () => {
    try {
      setLoading(true);
      const response = await newsSourceApi.list();
      setSources(response.list);
    } catch (error: any) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 뉴스 제공처 목록 로드
  useEffect(() => {
    fetchSources();
  }, []);

  // 소스 검색 기능
  const filteredSources = sources.filter(
    (source) =>
      source.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      source.url.toLowerCase().includes(searchTerm.toLowerCase()) ||
      source.code.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // 소스 추가 함수
  const handleAddSource = async () => {
    if (!newSource.name || !newSource.url || !newSource.code) {
      alert("모든 필드를 입력해주세요.");
      return;
    }

    if (!isValidUrl(newSource.url)) {
      alert("유효한 URL을 입력해주세요.");
      return;
    }

    try {
      await newsSourceApi.create({
        name: newSource.name,
        url: newSource.url,
        code: newSource.code,
        type: newSource.type,
      });
      
      setNewSource({ 
        name: "", 
        url: "", 
        code: NewsSourceCode.GOOGLE,
        type: NewsSourceType.RSS_STATIC 
      });
      setIsAddDialogOpen(false);
      // 뉴스 제공처 목록 새로고침
      await fetchSources();
    } catch (error: any) {
      alert(error.message);
    }
  };

  // URL 유효성 검사 함수
  const isValidUrl = (url: string) => {
    try {
      new URL(url);
      return true;
    } catch (e) {
      return false;
    }
  };

  // 소스 상태 변경 함수
  const handleToggleStatus = async (id: number, currentActive: boolean) => {
    try {
      if (currentActive) {
        await newsSourceApi.deactivate(id);
      } else {
        await newsSourceApi.activate(id);
      }
      // 뉴스 제공처 목록 새로고침
      await fetchSources();
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 뉴스 제공처 코드 한글 표시
  const getCodeLabel = (code: NewsSourceCode) => {
    switch (code) {
      case NewsSourceCode.YNA:
        return "연합뉴스";
      case NewsSourceCode.SBS:
        return "SBS";
      case NewsSourceCode.MK:
        return "매일경제";
      case NewsSourceCode.GOOGLE:
        return "구글";
      case NewsSourceCode.NAVER:
        return "네이버";
      default:
        return code;
    }
  };

  // 뉴스 제공처 타입 한글 표시
  const getTypeLabel = (type: NewsSourceType) => {
    switch (type) {
      case NewsSourceType.RSS_STATIC:
        return "최신 RSS";
      case NewsSourceType.RSS_KEYWORD:
        return "키워드 RSS";
      case NewsSourceType.SCRAPING:
        return "스크래퍼";
      default:
        return type;
    }
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
              <CardTitle>뉴스 사이트 관리</CardTitle>
              <CardDescription>
                뉴스를 수집할 사이트를 추가하거나 관리하세요.
              </CardDescription>
            </div>
            
            <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
              <DialogTrigger asChild>
                <Button>사이트 추가</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>뉴스 사이트 추가</DialogTitle>
                  <DialogDescription>
                    새로운 뉴스 사이트의 정보를 입력하세요.
                  </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <label htmlFor="name" className="text-sm font-medium">
                      사이트 이름
                    </label>
                    <Input
                      id="name"
                      placeholder="예: CNN"
                      value={newSource.name}
                      onChange={(e) =>
                        setNewSource({ ...newSource, name: e.target.value })
                      }
                    />
                  </div>
                  <div className="space-y-2">
                    <label htmlFor="url" className="text-sm font-medium">
                      URL
                    </label>
                    <Input
                      id="url"
                      placeholder="예: https://cnn.com"
                      value={newSource.url}
                      onChange={(e) =>
                        setNewSource({ ...newSource, url: e.target.value })
                      }
                    />
                  </div>
                  <div className="space-y-2">
                    <label htmlFor="type" className="text-sm font-medium">
                      타입
                    </label>
                    <Select
                        value={newSource.type}
                        onValueChange={(value: NewsSourceType) =>
                            setNewSource({ ...newSource, type: value })
                        }
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value={NewsSourceType.RSS_STATIC}>최신 RSS</SelectItem>
                        <SelectItem value={NewsSourceType.RSS_KEYWORD}>키워드 RSS</SelectItem>
                        <SelectItem value={NewsSourceType.SCRAPING}>스크래퍼</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <label htmlFor="code" className="text-sm font-medium">
                      코드
                    </label>
                    <Select
                        value={newSource.code}
                        onValueChange={(value: NewsSourceCode) =>
                            setNewSource({ ...newSource, code: value })
                        }
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value={NewsSourceCode.MK}>매일경제</SelectItem>
                        <SelectItem value={NewsSourceCode.SBS}>SBS</SelectItem>
                        <SelectItem value={NewsSourceCode.YNA}>연합뉴스</SelectItem>
                        <SelectItem value={NewsSourceCode.GOOGLE}>구글</SelectItem>
                        <SelectItem value={NewsSourceCode.NAVER}>네이버</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                    취소
                  </Button>
                  <Button onClick={handleAddSource}>저장</Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        </CardHeader>
        <CardContent>
          <div className="mb-4">
            <Input
              placeholder="사이트 이름, URL, 코드 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="max-w-sm"
            />
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>이름</TableHead>
                <TableHead>URL</TableHead>
                <TableHead>코드</TableHead>
                <TableHead>타입</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>등록일</TableHead>
                <TableHead>수정일</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredSources.map((source) => (
                <TableRow key={source.id}>
                  <TableCell>{source.id}</TableCell>
                  <TableCell className="font-medium">{source.name}</TableCell>
                  <TableCell>
                    <a
                      href={source.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-blue-600 hover:underline"
                    >
                      {source.url}
                    </a>
                  </TableCell>
                  <TableCell>{getCodeLabel(source.code)}</TableCell>
                  <TableCell>{getTypeLabel(source.type)}</TableCell>
                  <TableCell>
                    <span
                      className={`px-2 py-1 rounded-full text-xs ${
                        source.isActive
                          ? "bg-green-100 text-green-800"
                          : "bg-red-100 text-red-800"
                      }`}
                    >
                      {source.isActive ? "활성" : "비활성"}
                    </span>
                  </TableCell>
                  <TableCell>{new Date(source.createdAt).toLocaleDateString()}</TableCell>
                  <TableCell>{new Date(source.updatedAt).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <div className="flex space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleToggleStatus(source.id, source.isActive)}
                      >
                        {source.isActive ? "비활성화" : "활성화"}
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
              {filteredSources.length === 0 && (
                <TableRow>
                  <TableCell colSpan={9} className="text-center py-6">
                    {searchTerm
                      ? "검색 결과가 없습니다."
                      : "등록된 뉴스 사이트가 없습니다."}
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
        <CardFooter className="flex justify-between border-t px-6 py-4">
          <div className="text-sm text-muted-foreground">
            총 {filteredSources.length}개의 뉴스 사이트
          </div>
        </CardFooter>
      </Card>
    </div>
  );
}