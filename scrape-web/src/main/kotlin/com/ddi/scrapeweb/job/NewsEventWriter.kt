package com.ddi.scrapeweb.job

import com.ddi.core.dto.NewsEvent
import com.ddi.scrapeweb.service.RedisDeduplicationService
import org.slf4j.LoggerFactory
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class NewsEventWriter(
    private val kafkaTemplate: KafkaTemplate<String, NewsEvent>,
    private val deduplicationService: RedisDeduplicationService
) : ItemWriter<List<NewsEvent>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${kafka.topics.news-events:news-events}")
    private lateinit var topic: String

    override fun write(items: Chunk<out List<NewsEvent>?>) {
        val allEvents = items.flatMap { it.orEmpty() }

        if (allEvents.isEmpty()) {
            logger.debug("Kafka로 전송할 이벤트가 없습니다")
            return
        }

        // Redis를 사용한 중복 체크 및 마킹
        val uniqueEvents = deduplicationService.filterAndMarkUnique(allEvents)
        
        if (uniqueEvents.isEmpty()) {
            logger.info("${allEvents.size}개 이벤트가 이미 전송되었습니다")
            return
        }

        // 새로운 이벤트만 Kafka로 전송
        val futures = uniqueEvents.map { sendEvent(it) }
        
        try {
            CompletableFuture.allOf(*futures.toTypedArray()).join()
            logger.info("총 ${uniqueEvents.size}개 이벤트를 Kafka로 전송 완료")
        } catch (e: Exception) {
            logger.error("Kafka 전송 중 오류 발생", e)
            throw e
        }
    }

    private fun sendEvent(newsEvent: NewsEvent): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()

        try {
            val result = kafkaTemplate.send(topic, newsEvent.url, newsEvent)

            result.whenComplete { sendResult, exception ->
                if (exception != null) {
                    logger.error("이벤트 '${newsEvent.title}'를 Kafka로 전송하는데 실패했습니다: ${exception.message}")
                    future.completeExceptionally(exception)
                } else {
                    logger.debug("이벤트 '${newsEvent.title}'를 Kafka로 전송했습니다: ${sendResult.recordMetadata.partition()}:${sendResult.recordMetadata.offset()}")
                    future.complete(null)
                }
            }
        } catch (e: Exception) {
            logger.error("이벤트 '${newsEvent.title}'를 Kafka 전송용으로 준비하는 중 오류 발생: ${e.message}")
            future.completeExceptionally(e)
        }

        return future
    }
}