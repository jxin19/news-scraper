package com.ddi.api.newssource.application.dto

import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceName
import com.ddi.core.newssource.property.NewsSourceType
import com.ddi.core.newssource.property.NewsSourceUrl

data class NewsSourceServiceRequest(
    val name: String,
    val url: String,
    val code: NewsSourceCode,
    val type: NewsSourceType
) {
    fun toEntity() = NewsSource(
        name = NewsSourceName(name),
        url = NewsSourceUrl(url),
        code = code,
        type = type
    )
}
