package com.ddi.api.newsitem.ui.dto

import com.ddi.api.newsitem.dto.NewsItemSearchServiceRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class NewsItemSearchRequest(
    val title: String? = null,
    val sourceId: Long? = null,
    val keywordId: Long? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sort: String = "collectedAt"
) {
    fun toServiceDto() = NewsItemSearchServiceRequest(
        title = title,
        sourceId = sourceId,
        keywordId = keywordId,
        pageable = PageRequest.of(page, size, Sort.Direction.DESC, sort)
    )
}
