package com.ddi.api.newssource.ui

import com.ddi.api.newssource.application.NewsSourceService
import com.ddi.api.newssource.application.dto.NewsSourceServiceRequest
import com.ddi.api.newssource.application.dto.NewsSourceServiceResponses
import com.ddi.api.newssource.ui.dto.NewsSourceRequest
import com.ddi.api.newssource.ui.dto.NewsSourceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "뉴스 제공처 관련 API", description = "뉴스 제공처 정보를 관리하는 API")
@RestController
@RequestMapping("/news-source")
class NewsSourceController(
    private val newsSourceService: NewsSourceService
) {
    @Operation(summary = "뉴스 제공처 수 조회", description = "등록된 뉴스 제공처 수를 조회합니다.")
    @GetMapping("/count")
    fun count(): Long =
        newsSourceService.count()

    @Operation(summary = "뉴스 제공처 목록 조회", description = "등록된 모든 뉴스 제공처 목록을 조회합니다.")
    @GetMapping
    fun findAll(): NewsSourceServiceResponses =
        newsSourceService.list()

    @Operation(summary = "뉴스 제공처 생성", description = "새로운 뉴스 제공처를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: NewsSourceRequest): NewsSourceResponse {
        val response = newsSourceService.create(request.toServiceDto())
        return NewsSourceResponse.of(response)
    }

    @Operation(summary = "뉴스 제공처 활성화", description = "뉴스 제공처를 활성화합니다.")
    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.OK)
    fun activate(@PathVariable id: Long) =
        newsSourceService.activate(id)

    @Operation(summary = "뉴스 제공처 비활성화", description = "뉴스 제공처를 비활성화합니다.")
    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    fun deactivate(@PathVariable id: Long) =
        newsSourceService.deactivate(id)
}
