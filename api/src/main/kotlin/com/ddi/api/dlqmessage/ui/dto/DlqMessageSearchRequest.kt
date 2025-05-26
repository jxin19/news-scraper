package com.ddi.api.dlqmessage.ui.dto

import com.ddi.api.dlqmessage.application.dto.DlqMessageSearchServiceRequest
import com.ddi.core.dlqmessage.property.DlqStatus
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.Instant

data class DlqMessageSearchRequest(
    val status: DlqStatus? = null,
    val key: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sort: String = "createdAt"
) {
    fun toServiceDto(): DlqMessageSearchServiceRequest {
        return DlqMessageSearchServiceRequest(
            status = status,
            key = key,
            startDate = startDate,
            endDate = endDate,
            pageable = PageRequest.of(page, size, Sort.Direction.DESC, sort)
        )
    }
}