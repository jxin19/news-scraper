package com.ddi.api.dlqmessage.ui.dto

import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponses

data class DlqMessageResponses(
    val content: List<DlqMessageResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val currentPage: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean
) {
    companion object {
        fun of(responses: DlqMessageServiceResponses) = DlqMessageResponses(
                content = responses.content.map { DlqMessageResponse.of(it) },
                totalPages = responses.totalPages,
                totalElements = responses.totalElements,
                currentPage = responses.currentPage,
                size = responses.size,
                isFirst = responses.isFirst,
                isLast = responses.isLast
            )
    }

}