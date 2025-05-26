package com.ddi.api.newssource.application.dto

import com.ddi.core.newssource.NewsSource

data class NewsSourceServiceResponses(
    val list: List<NewsSourceServiceResponse> = listOf(),
    val size: Int
) {
    companion object {
        fun of(newsSources: List<NewsSource>) =
            NewsSourceServiceResponses(
                list = newsSources.map { NewsSourceServiceResponse.of(it) },
                size = newsSources.size
            )
    }
}
