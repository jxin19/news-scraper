package com.ddi.api.newssource.application

import com.ddi.api.newssource.application.dto.NewsSourceServiceRequest
import com.ddi.api.newssource.application.dto.NewsSourceServiceResponse
import com.ddi.api.newssource.application.dto.NewsSourceServiceResponses

interface NewsSourceService {
    fun count(): Long
    fun list(): NewsSourceServiceResponses
    fun create(newsSourceServiceRequest: NewsSourceServiceRequest): NewsSourceServiceResponse
    fun activate(id: Long)
    fun deactivate(id: Long)
}
