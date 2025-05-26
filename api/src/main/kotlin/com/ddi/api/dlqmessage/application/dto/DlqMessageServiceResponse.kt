package com.ddi.api.dlqmessage.application.dto

import com.ddi.core.dlqmessage.DlqMessage
import com.ddi.core.dlqmessage.property.DlqStatus
import java.time.LocalDateTime

data class DlqMessageServiceResponse(
    val id: Long,
    val key: String?,
    val originalTopic: String,
    val value: String?,
    val exceptionMessage: String?,
    val originalPartition: Int?,
    val originalOffset: Long?,
    val originalTimestamp: Long?,
    val status: DlqStatus,
    val retryCount: Int,
    val transformed: Boolean,
    val createdAt: LocalDateTime,
    val retriedAt: LocalDateTime?,
    val processedAt: LocalDateTime?
) {
    companion object {
        fun of(dlqMessage: DlqMessage) = DlqMessageServiceResponse(
                id = dlqMessage.id,
                key = dlqMessage.key,
                originalTopic = dlqMessage.originalTopic,
                value = dlqMessage.value,
                exceptionMessage = dlqMessage.exceptionMessage,
                originalPartition = dlqMessage.originalPartition,
                originalOffset = dlqMessage.originalOffset,
                originalTimestamp = dlqMessage.originalTimestamp,
                status = dlqMessage.status,
                retryCount = dlqMessage.retryCount,
                transformed = dlqMessage.transformed,
                createdAt = dlqMessage.createdAt,
                retriedAt = dlqMessage.retriedAt,
                processedAt = dlqMessage.processedAt
            )
    }

}