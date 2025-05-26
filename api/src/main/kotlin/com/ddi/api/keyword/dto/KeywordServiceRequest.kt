package com.ddi.api.keyword.dto

import com.ddi.core.common.domain.property.Name
import com.ddi.core.keyword.Keyword

data class KeywordServiceRequest(
    val name: String
) {
    fun toEntity() =
        Keyword(
            name = Name(name),
        )
}
