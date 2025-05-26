"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { keywordApi } from '@/lib/api/keyword-api';
import {Keyword} from "@/lib/type/keyword";

export function KeywordManager() {
  const [keywords, setKeywords] = useState<Keyword[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [newKeyword, setNewKeyword] = useState({ keyword: "" });
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // API에서 키워드 목록 가져오기
  const fetchKeywords = async () => {
    try {
      setLoading(true);
      const response = await keywordApi.list();
      setKeywords(response.list);
    } catch (error: any) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 키워드 목록 로드
  useEffect(() => {
    fetchKeywords();
  }, []);

  // 키워드 검색 기능
  const filteredKeywords = keywords.filter(
    (keyword) =>
      keyword.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // 키워드 추가 함수
  const handleAddKeyword = async () => {
    if (!newKeyword.keyword) {
      alert("키워드를 입력해주세요.");
      return;
    }

    try {
      await keywordApi.create({ name: newKeyword.keyword });
      setNewKeyword({ keyword: "" });
      setIsAddDialogOpen(false);
      // 키워드 목록 새로고침
      await fetchKeywords();
    } catch (error: any) {
      alert(error.message);
    }
  };

  // 키워드 상태 토글 함수
  const handleToggleStatus = async (id: number, currentActive: boolean) => {
    try {
      await keywordApi.toggle(id);
      // 키워드 목록 새로고침
      await fetchKeywords();
    } catch (error: any) {
      alert(error.message);
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
              <CardTitle>키워드 관리</CardTitle>
              <CardDescription>
                뉴스를 수집할 키워드를 추가하거나 관리하세요.
              </CardDescription>
            </div>
            
            <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
              <DialogTrigger asChild>
                <Button>키워드 추가</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>키워드 추가</DialogTitle>
                  <DialogDescription>
                    새로운 키워드를 입력하세요.
                  </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                  <div className="space-y-2">
                    <label htmlFor="keyword" className="text-sm font-medium">
                      키워드
                    </label>
                    <Input
                      id="keyword"
                      placeholder="예: 인공지능"
                      value={newKeyword.keyword}
                      onChange={(e) =>
                        setNewKeyword({ keyword: e.target.value })
                      }
                    />
                  </div>
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                    취소
                  </Button>
                  <Button onClick={handleAddKeyword}>저장</Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        </CardHeader>
        <CardContent>
          <div className="mb-4">
            <Input
              placeholder="키워드 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="max-w-sm"
            />
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>키워드</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>등록일</TableHead>
                <TableHead>수정일</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredKeywords.map((keyword) => (
                <TableRow key={keyword.id}>
                  <TableCell>{keyword.id}</TableCell>
                  <TableCell className="font-medium">{keyword.name}</TableCell>
                  <TableCell>
                    <span
                      className={`px-2 py-1 rounded-full text-xs ${
                        keyword.isActive
                          ? "bg-green-100 text-green-800"
                          : "bg-red-100 text-red-800"
                      }`}
                    >
                      {keyword.isActive ? "활성" : "비활성"}
                    </span>
                  </TableCell>
                  <TableCell>{new Date(keyword.createdAt).toLocaleDateString()}</TableCell>
                  <TableCell>{new Date(keyword.updatedAt).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <div className="flex space-x-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleToggleStatus(keyword.id, keyword.isActive)}
                      >
                        {keyword.isActive ? "비활성화" : "활성화"}
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
              {filteredKeywords.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-6">
                    {searchTerm ? "검색 결과가 없습니다." : "등록된 키워드가 없습니다."}
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
        <CardFooter className="flex justify-between border-t px-6 py-4">
          <div className="text-sm text-muted-foreground">
            총 {filteredKeywords.length}개의 키워드
          </div>
        </CardFooter>
      </Card>
    </div>
  );
}