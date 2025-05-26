package com.ddi.api.dlqmessage.application.dto

import com.ddi.core.dlqmessage.DlqMessage
import org.springframework.data.domain.Page

data class DlqMessageServiceResponses(
    val content: List<DlqMessageServiceResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val currentPage: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean
) {
    companion object {
        fun of(responses: Page<DlqMessage>) = DlqMessageServiceResponses(
                content = responses.content.map { DlqMessageServiceResponse.of(it) },
                totalPages = responses.totalPages,
                totalElements = responses.totalElements,
                currentPage = responses.number,
                size = responses.size,
                isFirst = responses.isFirst,
                isLast = responses.isLast
            )
    }

}