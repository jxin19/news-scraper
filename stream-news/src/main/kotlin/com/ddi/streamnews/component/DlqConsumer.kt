package com.ddi.streamnews.component

import com.ddi.core.dlqmessage.DlqMessage
import com.ddi.streamnews.repository.DlqMessageRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class DlqConsumer(
    private val dlqMessageRepository: DlqMessageRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${kafka.topic.news-dlq:news-events.dlq}"],
        groupId = "stream-news-dlq",
        concurrency = "1"
    )
    fun processDlq(record: ConsumerRecord<*, *>, ack: Acknowledgment) {
        try {
            logger.error("DLQ 메시지 수신: topic=${record.topic()}, offset=${record.offset()}, key=${record.key()}")

            val exception = record.headers().lastHeader("kafka_dlt-exception-message")?.value()
                ?.let { String(it, StandardCharsets.UTF_8) } ?: "Unknown error"

            val originalTopic = record.headers().lastHeader("kafka_dlt-original-topic")?.value()
                ?.let { String(it, StandardCharsets.UTF_8) } ?: "Unknown"

            val originalPartition = record.headers().lastHeader("kafka_dlt-original-partition")?.value()
                ?.let { String(it, StandardCharsets.UTF_8).toIntOrNull() }

            val originalOffset = record.headers().lastHeader("kafka_dlt-original-offset")?.value()
                ?.let { String(it, StandardCharsets.UTF_8).toLongOrNull() }

            val dlqMessage = DlqMessage(
                originalTopic = originalTopic,
                exceptionMessage = exception,
                key = record.key()?.toString(),
                value = record.value()?.toString(),
                originalPartition = originalPartition,
                originalOffset = originalOffset,
                originalTimestamp = record.timestamp()
            )

            dlqMessageRepository.save(dlqMessage)
            logger.info("DLQ 메시지를 DB에 저장: id=${dlqMessage.id}")

            ack.acknowledge()
        } catch (e: Exception) {
            logger.error("DLQ 메시지 처리 중 오류 발생: ${e.message}", e)
            throw e
        }
    }
}