package com.ddi.api.newssource.ui.dto

import com.ddi.api.newssource.application.dto.NewsSourceServiceResponses

data class NewsSourceResponses(
    val list: List<NewsSourceResponse>,
    val size: Int
) {
    companion object {
        fun of(responses: NewsSourceServiceResponses) =
            NewsSourceResponses(
                list = responses.list.map { NewsSourceResponse.of(it) },
                size = responses.size
            )
    }
}
