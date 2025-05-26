package com.ddi.api.dlqmessage.ui

import com.ddi.api.dlqmessage.application.DlqMessageService
import com.ddi.api.dlqmessage.ui.dto.DlqMessageRequest
import com.ddi.api.dlqmessage.ui.dto.DlqMessageResponse
import com.ddi.api.dlqmessage.ui.dto.DlqMessageResponses
import com.ddi.api.dlqmessage.ui.dto.DlqMessageSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "DLQ 메시지 관리", description = "DLQ 메시지 조회, 재시도, 삭제 등의 기능을 제공합니다")
@RestController
@RequestMapping("/dlq-message")
class DlqMessageController(
    private val dlqMessageService: DlqMessageService
) {
    @Operation(
        summary = "DLQ 메시지 전체 수 조회",
        description = "저장된 모든 DLQ 메시지의 총 개수를 조회합니다."
    )
    @GetMapping("/count")
    fun count(): Long = dlqMessageService.count()

    @Operation(
        summary = "DLQ 메시지 상세 조회",
        description = "지정된 ID의 DLQ 메시지 상세 정보를 조회합니다. 메시지가 존재하지 않을 경우 예외가 발생합니다."
    )
    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): DlqMessageResponse {
        val serviceResponse = dlqMessageService.detail(id)
        return DlqMessageResponse.of(serviceResponse)
    }

    @Operation(
        summary = "DLQ 메시지 목록 조회",
        description = "필터 조건에 맞는 DLQ 메시지 목록을 페이징하여 조회합니다. 상태, 키, 날짜 범위 등으로 필터링할 수 있습니다."
    )
    @GetMapping
    fun list(searchRequest: DlqMessageSearchRequest): ResponseEntity<DlqMessageResponses> {
        val serviceResponse = dlqMessageService.list(searchRequest.toServiceDto())
        return ResponseEntity.ok(DlqMessageResponses.of(serviceResponse))
    }

    @Operation(
        summary = "DLQ 메시지 재시도",
        description = "실패한 DLQ 메시지를 원래 토픽으로 다시 전송합니다. 성공 여부를 반환합니다."
    )
    @PostMapping("/{id}/retry")
    fun retry(@PathVariable id: Long): Boolean =
        dlqMessageService.retry(id)

    @Operation(
        summary = "DLQ 메시지 삭제",
        description = "지정된 ID의 DLQ 메시지를 영구적으로 삭제합니다. 삭제 후에는 해당 메시지를 복구할 수 없습니다."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) =
        dlqMessageService.delete(id)

    @Operation(
        summary = "DLQ 메시지 일괄 처리",
        description = "여러 DLQ 메시지에 대한 일괄 작업을 수행합니다. 상태 변경, 변환 여부 설정, 재시도 등의 작업을 일괄적으로 처리할 수 있습니다."
    )
    @PostMapping("/bulk-action")
    fun bulkAction(@RequestBody request: DlqMessageRequest) =
        dlqMessageService.bulkAction(request.toServiceDto())
}