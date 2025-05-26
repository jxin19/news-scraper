package com.ddi.api.keyword.ui.dto

import com.ddi.api.keyword.dto.KeywordServiceResponse
import java.time.Instant

data class KeywordResponse(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(serviceResponse: KeywordServiceResponse): KeywordResponse {
            return KeywordResponse(
                id = serviceResponse.id,
                name = serviceResponse.name,
                isActive = serviceResponse.isActive,
                createdAt = serviceResponse.createdAt,
                updatedAt = serviceResponse.updatedAt
            )
        }
    }
}
