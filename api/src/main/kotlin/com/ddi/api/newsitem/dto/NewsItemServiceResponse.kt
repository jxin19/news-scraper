package com.ddi.api.newsitem.dto

import com.ddi.core.newsitem.NewsItemView
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
data class NewsItemServiceResponse(
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
        fun of(newsItem: NewsItemView): NewsItemServiceResponse {
            return NewsItemServiceResponse(
                id = newsItem.id,
                keywordId = newsItem.keywordId,
                keywordName = newsItem.keywordName,
                sourceId = newsItem.sourceId,
                sourceName = newsItem.sourceName,
                title = newsItem.title,
                url = newsItem.url,
                content = newsItem.content,
                publishedDate = newsItem.publishedDate,
                collectedAt = newsItem.collectedAt
            )
        }
    }
}
