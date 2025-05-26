package com.ddi.api.dlqmessage.application

import com.ddi.api.dlqmessage.application.dto.DlqMessageSearchServiceRequest
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceRequest
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponse
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponses

interface DlqMessageService {
    fun count(): Long
    fun detail(id: Long): DlqMessageServiceResponse
    fun list(searchRequest: DlqMessageSearchServiceRequest): DlqMessageServiceResponses
    fun retry(id: Long): Boolean
    fun delete(id: Long)
    fun bulkAction(request: DlqMessageServiceRequest)
}