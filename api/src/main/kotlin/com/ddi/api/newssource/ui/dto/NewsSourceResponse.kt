package com.ddi.api.newssource.ui.dto

import com.ddi.api.newssource.application.dto.NewsSourceServiceResponse
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceType
import java.time.Instant

data class NewsSourceResponse(
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
        fun of(serviceResponse: NewsSourceServiceResponse): NewsSourceResponse {
            return NewsSourceResponse(
                id = serviceResponse.id,
                name = serviceResponse.name,
                url = serviceResponse.url,
                code = serviceResponse.code,
                type = serviceResponse.type,
                isActive = serviceResponse.isActive,
                createdAt = serviceResponse.createdAt,
                updatedAt = serviceResponse.updatedAt
            )
        }
    }
}
