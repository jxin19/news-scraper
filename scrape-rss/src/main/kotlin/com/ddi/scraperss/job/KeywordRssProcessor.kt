package com.ddi.scraperss.job

import com.ddi.core.dto.NewsEvent
import com.ddi.core.keyword.Keyword
import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scraperss.model.RssItem
import com.ddi.scraperss.repository.KeywordRepository
import com.ddi.scraperss.service.RssParserService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 키워드 기반 RSS 프로세서
 *
 * NewsSource 객체를 받아서 활성화된 키워드 목록을 조회하고,
 * 각 키워드에 대해 Google News RSS 피드를 조회하여 뉴스 이벤트를 생성합니다.
 */
@Component
class KeywordRssProcessor(
    private val keywordRepository: KeywordRepository,
    private val rssParserService: RssParserService
) : ItemProcessor<NewsSource, List<NewsEvent>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val MAX_CONCURRENT_REQUESTS = 5
        private const val REQUEST_DELAY_MS = 200L // 요청 간격 단축
        private const val TIMEOUT_SECONDS = 5L
    }

    private val requestSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

    // 비동기 처리용 스레드 풀 (별도 관리)
    private val executorService = Executors.newFixedThreadPool(
        MAX_CONCURRENT_REQUESTS,
        ThreadFactory { r ->
            Thread(r, "rss-keyword-processor-${System.nanoTime()}").apply {
                isDaemon = true
            }
        }
    )

    override fun process(source: NewsSource): List<NewsEvent>? {
        logger.info("키워드 기반 RSS 소스 처리 중: ${source.name} (${source.url})")

        if (!source.isActive) {
            return emptyList()
        }

        try {
            val keywords = keywordRepository.findByActive()
            if (keywords.isEmpty()) {
                return emptyList()
            }

            val allNewsEvents = processKeywords(source, keywords)

            logger.info("총 ${allNewsEvents.size}개의 뉴스 이벤트가 생성됨 (키워드 ${keywords.size}개 처리)")
            return allNewsEvents
        } catch (e: Exception) {
            logger.error("키워드 기반 RSS 소스 ${source.name} 처리 중 오류 발생: ${e.message}", e)
            return emptyList()
        }
    }

    /**
     * 키워드들을 비동기 병렬로 처리
     */
    private fun processKeywords(source: NewsSource, keywords: List<Keyword>): List<NewsEvent> {
        val futures = keywords.mapIndexed { index, keyword ->
            CompletableFuture.supplyAsync({
                processKeyword(source, keyword, index)
            }, executorService)
                .orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally { throwable ->
                    logger.error("키워드 '${keyword.nameValue}' 처리 중 오류 발생: ${throwable.message}")
                    emptyList<NewsEvent>()
                }
        }

        // 모든 비동기 작업 완료 대기
        return try {
            CompletableFuture.allOf(*futures.toTypedArray())
                .get(TIMEOUT_SECONDS * keywords.size, TimeUnit.SECONDS)

            // 결과 수집
            futures.flatMap { it.get() }
        } catch (e: TimeoutException) {
            logger.error("키워드 처리 시간 초과")
            futures.filter { it.isDone && !it.isCompletedExceptionally }
                .flatMap { it.get() }
        } catch (e: Exception) {
            logger.error("키워드 병렬 처리 중 오류 발생: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Rate Limiting을 적용하여 개별 키워드 처리
     */
    private fun processKeyword(
        source: NewsSource,
        keyword: Keyword,
        index: Int
    ): List<NewsEvent> {
        return try {
            requestSemaphore.acquire()

            try {
                // 첫 번째 요청이 아닌 경우에만 지연 적용
                if (index > 0) {
                    Thread.sleep(REQUEST_DELAY_MS)
                }

                val url = source.urlValue.replace("[keyword]", keyword.nameValue)
                val rssItems = rssParserService.parseRss(NewsSourceCode.GOOGLE, url)

                convertRssItemsToNewsEvents(keyword, rssItems, source.id)
            } finally {
                requestSemaphore.release()
            }
        } catch (e: Exception) {
            logger.error("키워드 '${keyword.nameValue}' 처리 실패: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * RSS 아이템들을 뉴스 이벤트로 변환
     */
    private fun convertRssItemsToNewsEvents(
        keyword: Keyword,
        rssItems: List<RssItem>,
        sourceId: Long
    ): List<NewsEvent> {
        if (rssItems.isEmpty()) {
            return emptyList()
        }

        return rssItems.map { item ->
            NewsEvent(
                item.title,
                item.url,
                item.content,
                item.publishedDate,
                keyword.id,
                sourceId,
                Instant.now()
            )
        }
    }

    /**
     * 리소스 정리
     */
    @PreDestroy
    fun cleanup() {
        executorService.shutdown()

        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
