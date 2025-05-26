package com.ddi.api.newssource.application.dto

import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceType
import java.time.Instant

data class NewsSourceServiceResponse(
    val id: Long,
    val name: String,
    val url: String,
    val code: NewsSourceCode,
    val type: NewsSourceType,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(newsSource: NewsSource) =
            NewsSourceServiceResponse(
                id = newsSource.id,
                name = newsSource.nameValue,
                url = newsSource.urlValue,
                code = newsSource.code,
                type = newsSource.type,
                isActive = newsSource.isActive,
                createdAt = newsSource.createdAt,
                updatedAt = newsSource.updatedAt
            )
    }
}
