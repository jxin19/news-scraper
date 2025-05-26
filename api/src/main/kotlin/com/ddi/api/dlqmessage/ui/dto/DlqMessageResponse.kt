package com.ddi.api.dlqmessage.ui.dto

import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponse
import com.ddi.core.dlqmessage.property.DlqStatus
import java.time.LocalDateTime

data class DlqMessageResponse(
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
        fun of(response: DlqMessageServiceResponse) =
            DlqMessageResponse(
                id = response.id,
                key = response.key,
                originalTopic = response.originalTopic,
                value = response.value,
                exceptionMessage = response.exceptionMessage,
                originalPartition = response.originalPartition,
                originalOffset = response.originalOffset,
                originalTimestamp = response.originalTimestamp,
                status = response.status,
                retryCount = response.retryCount,
                transformed = response.transformed,
                createdAt = response.createdAt,
                retriedAt = response.retriedAt,
                processedAt = response.processedAt,
            )
    }
}