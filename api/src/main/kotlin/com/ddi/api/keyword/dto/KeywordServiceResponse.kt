package com.ddi.api.keyword.dto

import com.ddi.core.keyword.Keyword
import java.time.Instant
import java.time.LocalDateTime

data class KeywordServiceResponse(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun of(keyword: Keyword) =
            KeywordServiceResponse(
                id = keyword.id,
                name = keyword.nameValue,
                isActive = keyword.isActive,
                createdAt = keyword.createdAt,
                updatedAt = keyword.updatedAt
            )
    }
}
