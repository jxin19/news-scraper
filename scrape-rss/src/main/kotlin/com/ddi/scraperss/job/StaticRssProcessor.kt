package com.ddi.scraperss.job

import com.ddi.core.dto.NewsEvent
import com.ddi.core.keyword.Keyword
import com.ddi.core.newssource.NewsSource
import com.ddi.scraperss.model.RssItem
import com.ddi.scraperss.repository.KeywordRepository
import com.ddi.scraperss.service.RssParserService
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * 정적 RSS 프로세서
 *
 * NewsSource의 RSS 피드를 파싱하고, 활성화된 키워드와 매칭되는
 * 뉴스 아이템들을 필터링하여 뉴스 이벤트로 변환합니다.
 */
@Component
class StaticRssProcessor(
    private val keywordRepository: KeywordRepository,
    private val rssParserService: RssParserService
) : ItemProcessor<NewsSource, List<NewsEvent>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(source: NewsSource): List<NewsEvent>? {
        logger.info("RSS 소스 처리 시작: ${source.name}")

        if (!source.isActive) {
            return emptyList()
        }

        try {
            val keywords = keywordRepository.findByActive()
            if (keywords.isEmpty()) {
                logger.warn("활성화된 키워드가 없음")
                return emptyList()
            }

            val rssItems = rssParserService.parseRss(source.code, source.url.value)
            val newsEvents = extractNewsEventsByKeywords(source.id, keywords, rssItems)

            logger.info("RSS 처리 완료: ${newsEvents.size}개 뉴스 수집 (전체 ${rssItems.size}개 중)")
            return newsEvents
        } catch (e: Exception) {
            logger.error("RSS 소스 처리 실패: ${source.name} - ${e.message}")
            return emptyList()
        }
    }

    /**
     * RSS 아이템들을 키워드로 필터링하여 뉴스 이벤트로 변환
     */
    private fun extractNewsEventsByKeywords(
        sourceId: Long,
        keywords: List<Keyword>,
        rssItems: List<RssItem>
    ): List<NewsEvent> {
        if (rssItems.isEmpty()) {
            return emptyList()
        }

        return rssItems
            .mapNotNull { item ->
                findMatchingKeyword(item, keywords)?.let { keyword ->
                    convertToNewsEvent(item, keyword, sourceId)
                }
            }
            .also { newsEvents ->
                if (logger.isDebugEnabled) {
                    logger.debug("키워드 매칭 결과: ${newsEvents.size}/${rssItems.size}개 아이템")
                }
            }
    }

    /**
     * RSS 아이템에서 매칭되는 키워드 찾기
     */
    private fun findMatchingKeyword(item: RssItem, keywords: List<Keyword>): Keyword? {
        val title = item.title.lowercase()
        val content = item.content?.lowercase() ?: ""

        return keywords.find { keyword ->
            val keywordValue = keyword.nameValue.lowercase()
            title.contains(keywordValue) || content.contains(keywordValue)
        }
    }

    /**
     * RSS 아이템을 뉴스 이벤트로 변환
     */
    private fun convertToNewsEvent(
        item: RssItem,
        keyword: Keyword,
        sourceId: Long
    ): NewsEvent {
        return NewsEvent(
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