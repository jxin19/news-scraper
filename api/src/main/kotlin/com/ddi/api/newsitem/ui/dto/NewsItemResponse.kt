package com.ddi.api.newsitem.ui.dto

import com.ddi.api.newsitem.dto.NewsItemServiceResponse
import java.time.Instant

data class NewsItemResponse(
    val id: Long,
    val keywordId: Long,
    val keywordName: String,
    val sourceId: Long,
    val sourceName: String,
    val title: String,
    val url: String,
    val content: String?,
    val publishedDate: Instant?,
    val collectedAt: Instant
) {
    companion object {
        fun of(response: NewsItemServiceResponse) = NewsItemResponse(
            id = response.id,
            keywordId = response.keywordId,
            keywordName = response.keywordName,
            sourceId = response.sourceId,
            sourceName = response.sourceName,
            title = response.title,
            url = response.url,
            content = response.content,
            publishedDate = response.publishedDate,
            collectedAt = response.collectedAt
        )
    }
}
