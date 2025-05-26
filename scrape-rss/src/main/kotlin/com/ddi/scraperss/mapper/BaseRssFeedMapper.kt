package com.ddi.scraperss.mapper

import com.ddi.scraperss.model.RssItem
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.Date

/**
 * 공통 매핑 로직을 제공하는 기본 RssFeedMapper 구현체입니다.
 * 각 제공처별 매퍼는 이 클래스를 상속받아 구현할 수 있습니다.
 */
abstract class BaseRssFeedMapper : RssFeedMapper {
    
    protected val logger = LoggerFactory.getLogger(javaClass)
    
    override fun mapFeedToItems(feed: SyndFeed): List<RssItem> {
        return feed.entries.mapNotNull { entry ->
            try {
                mapEntryToItem(feed, entry)
            } catch (e: Exception) {
                logger.error("항목 매핑 중 오류 발생: ${e.message}", e)
                null // 오류 발생 시 해당 항목 무시
            }
        }
    }
    
    /**
     * 개별 SyndEntry를 RssItem으로 변환합니다.
     * 각 제공처별 구현체에서 오버라이드해야 합니다.
     */
    protected abstract fun mapEntryToItem(feed: SyndFeed, entry: SyndEntry): RssItem
    
    /**
     * 안전하게 날짜 문자열을 Instant로 변환합니다.
     */
    protected fun safeToInstant(date: Date?): Instant? =
        date?.toInstant()
}