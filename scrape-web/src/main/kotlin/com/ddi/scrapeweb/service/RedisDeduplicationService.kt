package com.ddi.scrapeweb.service

import com.ddi.core.dto.NewsEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class RedisDeduplicationService(
    private val stringRedisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${redis.deduplication.ttl-hours:12}")
    private var ttlHours: Long = 12

    companion object {
        private const val SENT_PREFIX = "news:sent:"
        private const val BATCH_SIZE = 100
    }

    /**
     * 중복 체크 및 전송 기록 (Pipeline 최적화 버전)
     */
    fun filterAndMarkUnique(newsEvents: List<NewsEvent>): List<NewsEvent> {
        if (newsEvents.isEmpty()) return emptyList()

        val startTime = System.currentTimeMillis()

        // 모든 청크의 결과를 수집
        val result = newsEvents.chunked(BATCH_SIZE)
            .flatMap { chunk -> filterAndMarkUniqueInternal(chunk) }

        val endTime = System.currentTimeMillis()
        logger.info("중복 체크 완료: 전체 ${newsEvents.size}개 중 ${result.size}개가 새로운 이벤트 (처리시간: ${endTime - startTime}ms)")

        return result
    }

    /**
     * Pipeline을 활용한 실제 중복 체크 및 마킹 로직
     */
    private fun filterAndMarkUniqueInternal(newsEvents: List<NewsEvent>): List<NewsEvent> {
        val keyValuePairs = newsEvents.associateWith { newsEvent ->
            generateKey(newsEvent) to generateValue(newsEvent)
        }

        val keys = keyValuePairs.values.map { it.first }

        val existsResults = try {
            stringRedisTemplate.opsForValue().multiGet(keys)
                ?.map { it != null }
                ?: List(keys.size) { false }
        } catch (e: Exception) {
            List(keys.size) { false }
        }

        if (existsResults.size != keys.size) {
            logger.warn("Pipeline 결과 크기 불일치: 예상 ${keys.size}, 실제 ${existsResults.size}")
            return filterAndMarkUniqueFallback(newsEvents)
        }

        val newEvents = mutableListOf<NewsEvent>()
        val keysToStore = mutableMapOf<String, String>()

        keyValuePairs.entries.forEachIndexed { index, (newsEvent, keyValue) ->
            if (!existsResults[index]) {
                newEvents.add(newsEvent)
                keysToStore[keyValue.first] = keyValue.second
            }
        }

        // 새로운 키들 저장
        if (keysToStore.isNotEmpty()) {
            storeNewKeys(keysToStore)
        }

        logger.debug("Pipeline 처리 결과: ${newsEvents.size}개 중 ${newEvents.size}개가 새로운 이벤트")
        return newEvents
    }

    // Fallback 메서드 추가
    private fun filterAndMarkUniqueFallback(newsEvents: List<NewsEvent>): List<NewsEvent> {
        logger.info("Fallback 모드로 개별 체크 실행")

        return newsEvents.filter { newsEvent ->
            val key = generateKey(newsEvent)
            val exists = stringRedisTemplate.hasKey(key)

            if (!exists) {
                // 새로운 키 저장
                val value = generateValue(newsEvent)
                stringRedisTemplate.opsForValue().set(key, value, Duration.ofHours(ttlHours))
                true
            } else {
                false
            }
        }
    }

    /**
     * Pipeline으로 새로운 키들을 일괄 저장
     */
    private fun storeNewKeys(keysToStore: Map<String, String>) {
        try {
            stringRedisTemplate.executePipelined {
                keysToStore.forEach { (key, value) ->
                    stringRedisTemplate.opsForValue().set(key, value, Duration.ofHours(ttlHours))
                }
                null
            }
            logger.debug("${keysToStore.size}개 키 저장 완료")
        } catch (e: Exception) {
            logger.error("키 저장 중 오류 발생", e)
            throw e
        }
    }

    /**
     * Redis 키 생성 (URL 기반)
     */
    private fun generateKey(newsEvent: NewsEvent): String =
        "$SENT_PREFIX${newsEvent.url}"

    /**
     * Redis 값 생성 (메타데이터 포함)
     */
    private fun generateValue(newsEvent: NewsEvent): String {
        val timestamp = Instant.now().toString()
        return "$timestamp|${newsEvent.title}|${newsEvent.url}"
    }

}