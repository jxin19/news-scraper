package com.ddi.scrapeweb.scraper

import com.ddi.core.newssource.property.NewsSourceCode
import org.springframework.stereotype.Component

/**
 * 소스 코드에 맞는 WebScraper 구현체를 제공하는 팩토리 클래스
 */
@Component
class WebScraperFactory(private val scrapers: List<WebScraper>) {
    
    /**
     * 주어진 소스 코드에 맞는 WebScraper 구현체를 반환합니다.
     *
     * @param sourceCode 스크래퍼를 찾을 소스 코드
     * @return 해당 소스 코드를 지원하는 WebScraper 구현체
     * @throws IllegalArgumentException 지원하지 않는 소스 코드인 경우 발생
     */
    fun getScraper(sourceCode: NewsSourceCode): WebScraper {
        return scrapers.find { it.supports(sourceCode) }
            ?: throw IllegalArgumentException("지원하지 않는 소스 코드: $sourceCode")
    }
}
