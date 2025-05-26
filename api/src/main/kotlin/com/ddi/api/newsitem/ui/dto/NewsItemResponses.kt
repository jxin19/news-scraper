package com.ddi.api.newsitem.ui.dto

import com.ddi.api.newsitem.dto.NewsItemServiceResponses

data class NewsItemResponses(
    val content: List<NewsItemResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean
) {
    companion object {
        fun of(responses: NewsItemServiceResponses) = NewsItemResponses(
            content = responses.content.map { NewsItemResponse.of(it) },
            totalElements = responses.totalElements,
            totalPages = responses.totalPages,
            page = responses.page,
            size = responses.size,
            isFirst = responses.isFirst,
            isLast = responses.isLast
        )
    }
}
