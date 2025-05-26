package com.ddi.api.keyword.ui.dto

import com.ddi.api.keyword.dto.KeywordServiceResponses

data class KeywordResponses(
    val list: List<KeywordResponse>,
    val size: Int
) {
    companion object {
        fun of(responses: KeywordServiceResponses) =
            KeywordResponses(
                list = responses.list.map { KeywordResponse.of(it) },
                size = responses.size
            )
    }
}