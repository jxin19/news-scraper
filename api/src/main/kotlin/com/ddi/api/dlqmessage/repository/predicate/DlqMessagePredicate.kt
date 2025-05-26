package com.ddi.api.dlqmessage.repository.predicate

import com.ddi.api.dlqmessage.application.dto.DlqMessageSearchServiceRequest
import com.ddi.core.dlqmessage.QDlqMessage
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import java.time.ZoneId

class DlqMessagePredicate {
    companion object {
        fun search(request: DlqMessageSearchServiceRequest): Predicate {
            val qDlqMessage = QDlqMessage.dlqMessage
            val builder = BooleanBuilder()

            request.status?.let { builder.and(qDlqMessage.status.eq(it)) }
            request.key?.let { builder.and(qDlqMessage.key.eq(it)) }
            request.startDate?.let { startDate ->
                val startDateTime = startDate.atZone(ZoneId.systemDefault()).toLocalDateTime()
                builder.and(qDlqMessage.createdAt.goe(startDateTime))
            }
            request.endDate?.let { endDate ->
                val endDateTime = endDate.atZone(ZoneId.systemDefault()).toLocalDateTime()
                builder.and(qDlqMessage.createdAt.loe(endDateTime))
            }

            return builder
        }
    }
}