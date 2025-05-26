package com.ddi.api.dlqmessage.ui.dto

import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceRequest
import com.ddi.core.dlqmessage.property.DlqStatus

data class DlqMessageRequest(
    val ids: List<Long>?,
    val status: DlqStatus? = null,
    val transformed: Boolean? = null,
    val retry: Boolean = false
) {
    fun toServiceDto() = DlqMessageServiceRequest(
        ids = ids,
        status = status,
        transformed = transformed,
        retry = retry
    )
}