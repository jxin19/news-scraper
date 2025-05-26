package com.ddi.scraperss.mapper

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scraperss.model.RssItem
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import org.springframework.stereotype.Component

/**
 * Google News RSS 피드를 위한 매퍼 구현체
 * 
 * Google News RSS 피드 구조:
 * - 기본 RSS 2.0 형식
 * - URL: https://news.google.com/rss/search?q={keyword}&hl=ko&gl=KR&ceid=KR:ko
 * - 각 item에는 title, link, description, pubDate, source, guid 포함
 * - description에는 HTML 링크와 출처 정보가 포함됨
 */
@Component
class GoogleRssFeedMapper : BaseRssFeedMapper() {
    
    override fun mapEntryToItem(feed: SyndFeed, entry: SyndEntry): RssItem {
        return RssItem(
            title = cleanTitle(entry.title ?: ""),
            url = entry.link ?: "",
            content = extractContent(entry),
            publishedDate = safeToInstant(entry.publishedDate),
        )
    }

    override fun supports(sourceCode: NewsSourceCode): Boolean {
        return NewsSourceCode.GOOGLE == sourceCode
    }
    
    /**
     * Google News의 제목을 정리합니다.
     */
    private fun cleanTitle(title: String): String {
        // " - 출처명" 형태로 끝나는 부분을 제거
        val cleanedTitle = title.replace(Regex("\\s*-\\s*[가-힣A-Za-z\\s]+$"), "")
        return cleanedTitle.trim()
    }
    
    /**
     * Google News RSS 항목에서 컨텐츠를 추출합니다.
     * 이 중에서 순수 텍스트만 추출합니다.
     */
    private fun extractContent(entry: SyndEntry): String? {
        val description = entry.description?.value
        
        return description?.let { content ->
            // HTML 태그 완전 제거 및 HTML 엔티티 변환
            content
                .replace(Regex("<[^>]*>"), "") // 모든 HTML 태그 제거
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&nbsp;", " ")
                .replace("&apos;", "'")
                .replace(Regex("\\s+"), " ") // 연속된 공백을 하나로
                .trim()
                .takeIf { it.isNotEmpty() }
        }
    }
}
