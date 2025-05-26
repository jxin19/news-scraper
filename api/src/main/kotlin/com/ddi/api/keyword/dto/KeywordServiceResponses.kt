package com.ddi.api.keyword.dto

import com.ddi.core.keyword.Keyword

data class KeywordServiceResponses(
    val list: List<KeywordServiceResponse> = listOf(),
    val size: Int
) {
    companion object {
        fun of(keywords: List<Keyword>) =
            KeywordServiceResponses(
                list = keywords.map { KeywordServiceResponse.of(it) },
                size = keywords.size
            )
    }
}