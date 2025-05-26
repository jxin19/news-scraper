package com.ddi.core.newsitem

import com.ddi.core.newsitem.property.NewsTitle
import com.ddi.core.newsitem.property.NewsUrl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NewsItemTest {

    @Test
    fun `NewsItem 생성 테스트`() {
        // given
        val keywordId = 1L
        val sourceId = 2L
        val title = NewsTitle("뉴스 제목")
        val url = NewsUrl("https://example.com/news/123")
        val content = "뉴스 내용"
        val publishedDate = Instant.now()
        
        // when
        val newsItem = NewsItem(
            keywordId = keywordId,
            sourceId = sourceId,
            title = title,
            url = url,
            content = content,
            publishedDate = publishedDate
        )
        
        // then
        assertEquals(keywordId, newsItem.keywordId)
        assertEquals(sourceId, newsItem.sourceId)
        assertEquals("뉴스 제목", newsItem.titleValue)
        assertEquals("https://example.com/news/123", newsItem.urlValue)
        assertEquals(content, newsItem.content)
        assertEquals(publishedDate, newsItem.getPublishedDate())
        assertNotNull(newsItem.collectedAt)
    }
    
    @Test
    fun `NewsItem 게터 테스트`() {
        // given
        val title = NewsTitle("뉴스 제목")
        val url = NewsUrl("https://example.com/news/123")
        val publishedDate = LocalDateTime.of(2025, 5, 1, 12, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
        
        // when
        val newsItem = NewsItem(
            keywordId = 1,
            sourceId = 2,
            title = title,
            url = url,
            publishedDate = publishedDate
        )
        
        // then
        assertEquals("뉴스 제목", newsItem.titleValue)
        assertEquals("https://example.com/news/123", newsItem.urlValue)
        assertEquals(publishedDate, newsItem.getPublishedDate())
    }

}
