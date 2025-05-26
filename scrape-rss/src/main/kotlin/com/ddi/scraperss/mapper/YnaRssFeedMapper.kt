package com.ddi.scraperss.mapper

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scraperss.model.RssItem
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import org.springframework.stereotype.Component

/**
 * 연합뉴스(YNA) RSS 피드를 위한 매퍼 구현체
 */
@Component
class YnaRssFeedMapper : BaseRssFeedMapper() {
    
    override fun mapEntryToItem(feed: SyndFeed, entry: SyndEntry): RssItem {
        return RssItem(
            title = entry.title ?: "",
            url = entry.link ?: "",
            content = entry.description?.value,
            publishedDate = safeToInstant(entry.publishedDate),
        )
    }

    override fun supports(sourceCode: NewsSourceCode): Boolean {
        return "YNA" == sourceCode.name
    }
}