package com.ddi.api.dlqmessage.application.dto

import com.ddi.core.dlqmessage.property.DlqStatus
import org.springframework.data.domain.Pageable
import java.time.Instant

data class DlqMessageSearchServiceRequest(
    val status: DlqStatus? = null,
    val key: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val pageable: Pageable = Pageable.unpaged(),
)