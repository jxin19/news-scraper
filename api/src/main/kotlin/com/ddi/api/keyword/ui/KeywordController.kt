package com.ddi.api.keyword.ui

import com.ddi.api.keyword.KeywordService
import com.ddi.api.keyword.dto.KeywordServiceRequest
import com.ddi.api.keyword.ui.dto.KeywordRequest
import com.ddi.api.keyword.ui.dto.KeywordResponse
import com.ddi.api.keyword.ui.dto.KeywordResponses
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "키워드 관련 API", description = "키워드 정보를 관리하는 API")
@RestController
@RequestMapping("/keyword")
class KeywordController(
    private val keywordService: KeywordService
) {
    @Operation(summary = "키워드 수 조회", description = "등록된 키워드 수를 조회합니다.")
    @GetMapping("/count")
    fun count(): Long =
        keywordService.count()

    @Operation(summary = "키워드 목록 조회", description = "등록된 모든 키워드 목록을 조회합니다.")
    @GetMapping
    fun list(): KeywordResponses =
        KeywordResponses.of(keywordService.list())

    @Operation(summary = "키워드 생성", description = "새로운 키워드를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: KeywordRequest): KeywordResponse {
        val serviceDto = KeywordServiceRequest(name = request.name)
        val response = keywordService.create(serviceDto)
        return KeywordResponse.of(response)
    }

    @Operation(summary = "키워드 활성화 상태 토글", description = "키워드의 활성화 상태를 전환합니다.")
    @PatchMapping("/{id}/toggle")
    @ResponseStatus(HttpStatus.OK)
    fun toggleActiveStatus(@PathVariable id: Long) =
        keywordService.toggleActiveStatus(id)
}
