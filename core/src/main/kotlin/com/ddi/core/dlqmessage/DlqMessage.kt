package com.ddi.core.dlqmessage

import com.ddi.core.dlqmessage.property.DlqStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "dlq_message",
    indexes = [
        Index(name = "idx_dlq_message_status", columnList = "status"),
        Index(name = "idx_dlq_message_created_at", columnList = "created_at"),
        Index(name = "idx_dlq_message_original_topic", columnList = "original_topic")
    ]
)
class DlqMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Column(name = "original_topic", nullable = false)
    val originalTopic: String = "",

    @Column(name = "exception_message", columnDefinition = "TEXT")
    val exceptionMessage: String? = null,

    @Column(name = "key")
    val key: String? = null,

    @Column(columnDefinition = "TEXT")
    val value: String? = null,

    @Column(name = "original_partition")
    val originalPartition: Int? = null,

    @Column(name = "original_offset")
    val originalOffset: Long? = null,

    @Column(name = "original_timestamp")
    val originalTimestamp: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    var status: DlqStatus = DlqStatus.PENDING,

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0,

    @Column(name = "transformed", nullable = false)
    var transformed: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "retried_at")
    var retriedAt: LocalDateTime? = null,

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null
) {
    fun markAsRetried() {
        this.status = DlqStatus.RETRIED
        this.retriedAt = LocalDateTime.now()
        this.retryCount++
    }

    fun markAsFailed() {
        this.status = DlqStatus.FAILED
        this.processedAt = LocalDateTime.now()
    }

    fun markAsIgnored() {
        this.status = DlqStatus.IGNORED
        this.processedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DlqMessage

        if (id != other.id) return false
        if (originalPartition != other.originalPartition) return false
        if (originalOffset != other.originalOffset) return false
        if (originalTimestamp != other.originalTimestamp) return false
        if (retryCount != other.retryCount) return false
        if (transformed != other.transformed) return false
        if (originalTopic != other.originalTopic) return false
        if (exceptionMessage != other.exceptionMessage) return false
        if (key != other.key) return false
        if (value != other.value) return false
        if (status != other.status) return false
        if (createdAt != other.createdAt) return false
        if (retriedAt != other.retriedAt) return false
        if (processedAt != other.processedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (originalPartition ?: 0)
        result = 31 * result + (originalOffset?.hashCode() ?: 0)
        result = 31 * result + (originalTimestamp?.hashCode() ?: 0)
        result = 31 * result + retryCount
        result = 31 * result + transformed.hashCode()
        result = 31 * result + originalTopic.hashCode()
        result = 31 * result + (exceptionMessage?.hashCode() ?: 0)
        result = 31 * result + (key?.hashCode() ?: 0)
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (retriedAt?.hashCode() ?: 0)
        result = 31 * result + (processedAt?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "DlqMessage(id=$id, originalTopic='$originalTopic', exceptionMessage=$exceptionMessage, key=$key, value=$value, originalPartition=$originalPartition, originalOffset=$originalOffset, originalTimestamp=$originalTimestamp, status=$status, retryCount=$retryCount, transformed=$transformed, createdAt=$createdAt, retriedAt=$retriedAt, processedAt=$processedAt)"
    }
}