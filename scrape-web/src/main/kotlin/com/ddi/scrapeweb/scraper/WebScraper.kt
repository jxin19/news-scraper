package com.ddi.scrapeweb.scraper

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scrapeweb.model.ScrapeItem

/**
 * 웹 스크래핑을 담당하는 인터페이스
 * 각 뉴스 제공처별로 구현체를 제공합니다.
 */
interface WebScraper {
    /**
     * 지정된 URL에서 뉴스 데이터를 스크래핑합니다.
     *
     * @param url 스크래핑할 URL
     * @param pageNo 페이지 번호 (선택사항, 구현체에 따라 사용)
     * @param next 다음 페이지 토큰 (선택사항, 구현체에 따라 사용)
     * @return 스크래핑된 뉴스 아이템 리스트
     */
    fun scrapeNews(
        url: String,
        pageNo: Int? = null,
    ): List<ScrapeItem>

    /**
     * 현재 스크래퍼가 지정된 소스 코드를 지원하는지 확인합니다.
     *
     * @param sourceCode 확인할 소스 코드
     * @return 지원 여부
     */
    fun supports(sourceCode: NewsSourceCode): Boolean
}
