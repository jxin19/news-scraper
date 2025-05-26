package com.ddi.streamnews.component

import com.ddi.core.newsitem.NewsItem
import com.ddi.core.newsitem.property.NewsTitle
import com.ddi.core.newsitem.property.NewsUrl
import com.ddi.core.dto.NewsEvent
import com.ddi.streamnews.repository.NewsItemRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class NewsEventConsumer(
    private val newsItemRepository: NewsItemRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${spring.kafka.topic.news:news-events}"],
        groupId = "\${spring.kafka.consumer.group-id:stream-news}",
        batch = "true",
        concurrency = "3",
    )
    fun consume(events: List<ConsumerRecord<*, *>>, ack: Acknowledgment) {
        try {
            if (events.isEmpty()) {
                ack.acknowledge()
                return
            }

            val newsEvents = events.mapNotNull {
                try {
                    val value = it.value()
                    value as? NewsEvent
                } catch (e: Exception) {
                    logger.warn("메시지 변환 실패: ${e.message}")
                    null
                }
            }

            if (newsEvents.isEmpty()) {
                logger.info("처리 가능한 이벤트가 없습니다.")
                ack.acknowledge()
                return
            }

            var savedCount = 0
            var duplicateCount = 0

            newsEvents.forEach { event ->
                try {
                    val newsItem = NewsItem(
                        keywordId = event.keywordId,
                        sourceId = event.sourceId,
                        title = NewsTitle(event.title),
                        url = NewsUrl(event.url),
                        content = event.content,
                        publishedDate = event.publishedDate,
                        collectedAt = event.collectedAt
                    )

                    newsItemRepository.save(newsItem)
                    savedCount++
                } catch (e: DataIntegrityViolationException) {
                    if (e.message?.contains("duplicate key value violates unique constraint") == true) {
                        duplicateCount++
                        logger.debug("중복 URL 무시: ${event.url}")
                    } else {
                        logger.warn("데이터 무결성 오류: ${e.message}")
                        throw e
                    }
                } catch (e: Exception) {
                    logger.error("개별 아이템 저장 실패: ${e.message}")
                    throw e
                }
            }

            logger.info("전체 ${newsEvents.size}개 이벤트 처리 완료 - 저장: ${savedCount}개, 중복: ${duplicateCount}개")
            ack.acknowledge()

        } catch (e: Exception) {
            logger.error("뉴스 이벤트 처리 중 오류 발생: ${e.message}", e)
            throw e
        }
    }
}