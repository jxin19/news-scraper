package com.ddi.api.dlqmessage.application.dto

import com.ddi.core.dlqmessage.property.DlqStatus

data class DlqMessageServiceRequest(
    val ids: List<Long>?,
    val status: DlqStatus? = null,
    val transformed: Boolean? = null,
    val retry: Boolean = false
)