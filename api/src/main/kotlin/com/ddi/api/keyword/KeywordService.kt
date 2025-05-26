package com.ddi.api.keyword

import com.ddi.api.keyword.dto.KeywordServiceRequest
import com.ddi.api.keyword.dto.KeywordServiceResponse
import com.ddi.api.keyword.dto.KeywordServiceResponses

interface KeywordService {
    fun count(): Long
    fun list(): KeywordServiceResponses
    fun create(keywordServiceRequest: KeywordServiceRequest): KeywordServiceResponse
    fun toggleActiveStatus(id: Long)
}