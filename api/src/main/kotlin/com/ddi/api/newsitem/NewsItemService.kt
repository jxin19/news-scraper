package com.ddi.api.newsitem

import com.ddi.api.newsitem.dto.NewsItemSearchServiceRequest
import com.ddi.api.newsitem.dto.NewsItemServiceResponse
import com.ddi.api.newsitem.dto.NewsItemServiceResponses

interface NewsItemService {
    fun count(): Long
    fun countTodayCollected(): Long
    fun list(request: NewsItemSearchServiceRequest): NewsItemServiceResponses
    fun detail(id: Long): NewsItemServiceResponse
}
