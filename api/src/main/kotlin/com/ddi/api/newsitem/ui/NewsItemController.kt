package com.ddi.api.newsitem.ui

import com.ddi.api.newsitem.NewsItemService
import com.ddi.api.newsitem.ui.dto.NewsItemResponse
import com.ddi.api.newsitem.ui.dto.NewsItemResponses
import com.ddi.api.newsitem.ui.dto.NewsItemSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "뉴스 아이템 API", description = "뉴스 아이템 정보를 관리하는 API")
@RestController
@RequestMapping("/news-items")
class NewsItemController(
    private val newsItemService: NewsItemService
) {
    
    @Operation(summary = "뉴스 아이템 전체 수 조회", description = "등록된 뉴스 아이템 전체 수를 조회합니다.")
    @GetMapping("/count")
    fun count(): Long = 
        newsItemService.count()
    
    @Operation(summary = "오늘 수집된 뉴스 아이템 수 조회", description = "오늘 수집된 뉴스 아이템 수를 조회합니다.")
    @GetMapping("/count/today")
    fun countTodayCollected(): Long = 
        newsItemService.countTodayCollected()
    
    @Operation(summary = "뉴스 아이템 목록 조회", description = "필터링 조건에 맞는 뉴스 아이템 목록을 조회합니다.")
    @GetMapping
    fun list(@Parameter(description = "검색 조건") request: NewsItemSearchRequest): NewsItemResponses {
        val responses = newsItemService.list(request.toServiceDto())
        return NewsItemResponses.of(responses)
    }
    
    @Operation(summary = "뉴스 아이템 상세 조회", description = "지정된 ID의 뉴스 아이템 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): NewsItemResponse {
        val newsItem = newsItemService.detail(id)
        return NewsItemResponse.of(newsItem)
    }

}
