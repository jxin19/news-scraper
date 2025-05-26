package com.ddi.api.newsitem.dto

import org.springframework.data.domain.Pageable

data class NewsItemSearchServiceRequest(
    val title: String? = null,
    val sourceId: Long? = null,
    val keywordId: Long? = null,
    val pageable: Pageable = Pageable.unpaged(),
)
