package com.ddi.api.newsitem.dto

import com.ddi.core.newsitem.NewsItemView
import org.springframework.data.domain.Page

data class NewsItemServiceResponses(
    val content: List<NewsItemServiceResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean
) {
    companion object {
        fun of(newsItems: Page<NewsItemView>): NewsItemServiceResponses {
            return NewsItemServiceResponses(
                content = newsItems.content.map { NewsItemServiceResponse.of(it) },
                totalElements = newsItems.totalElements,
                totalPages = newsItems.totalPages,
                page = newsItems.number,
                size = newsItems.size,
                isFirst = newsItems.isFirst,
                isLast = newsItems.isLast
            )
        }
    }
}
