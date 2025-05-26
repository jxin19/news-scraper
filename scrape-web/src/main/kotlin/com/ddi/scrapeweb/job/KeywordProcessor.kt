package com.ddi.scrapeweb.job

import com.ddi.core.dto.NewsEvent
import com.ddi.core.keyword.Keyword
import com.ddi.core.newssource.NewsSource
import com.ddi.scrapeweb.repository.KeywordRepository
import com.ddi.scrapeweb.scraper.WebScraperFactory
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
 * 키워드 기반 웹 스크래핑 프로세서
 *
 * NewsSource 객체를 받아서 활성화된 키워드 목록을 조회하고,
 * 각 키워드에 대해 웹 스크래핑을 수행하여 뉴스 이벤트를 생성합니다.
 */
@Component
class KeywordProcessor(
    private val keywordRepository: KeywordRepository,
    private val webScraperFactory: WebScraperFactory,
) : ItemProcessor<NewsSource, List<NewsEvent>> {
    
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val MAX_CONCURRENT_REQUESTS = 3
        private const val REQUEST_DELAY_MS = 1000L
        private const val TIMEOUT_SECONDS = 10L
    }

    private val requestSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

    private val executorService = Executors.newFixedThreadPool(
        MAX_CONCURRENT_REQUESTS,
        ThreadFactory { r ->
            Thread(r, "web-scraping-keyword-processor-${System.nanoTime()}").apply {
                isDaemon = true
            }
        }
    )

    override fun process(source: NewsSource): List<NewsEvent>? {
        logger.info("키워드 기반 웹 스크래핑 소스 처리 중: ${source.name} (${source.url})")

        if (!source.isActive) {
            return emptyList()
        }

        try {
            val keywords = keywordRepository.findByActive()
            if (keywords.isEmpty()) {
                return emptyList()
            }

            val newsEvents = processKeywords(source, keywords)

            logger.info("총 ${newsEvents.size}개의 뉴스 이벤트가 생성됨 (키워드 ${keywords.size}개 처리)")
            return newsEvents
            
        } catch (e: Exception) {
            logger.error("키워드 기반 웹 스크래핑 소스 ${source.name} 처리 중 오류 발생: ${e.message}", e)
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
                if (index > 0) {
                    Thread.sleep(REQUEST_DELAY_MS)
                }

                val scraper = webScraperFactory.getScraper(source.code)
                val scrapedItems = scraper.scrapeNews(source.urlValue, 1)

                val newsEvents = scrapedItems.map { item ->
                    NewsEvent.newBuilder()
                        .setTitle(item.title)
                        .setUrl(item.url)
                        .setContent(item.content)
                        .setPublishedDate(item.publishedDate)
                        .setKeywordId(keyword.id)
                        .setSourceId(source.id)
                        .setCollectedAt(Instant.now())
                        .build()
                }

                logger.info("키워드 '${keyword.nameValue}' 처리 완료: ${newsEvents.size}개 뉴스 수집")
                return newsEvents
                
            } finally {
                requestSemaphore.release()
            }
        } catch (e: Exception) {
            logger.error("키워드 '${keyword.nameValue}' 처리 실패: ${e.message}", e)
            emptyList()
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
