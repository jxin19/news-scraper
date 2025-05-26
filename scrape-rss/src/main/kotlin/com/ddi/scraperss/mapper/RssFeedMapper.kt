package com.ddi.scraperss.mapper

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scraperss.model.RssItem
import com.rometools.rome.feed.synd.SyndFeed

/**
 * RSS 피드를 RssItem 객체 리스트로 변환하는 매퍼 인터페이스
 * 각 뉴스 제공처별로 구현체를 제공합니다.
 */
interface RssFeedMapper {
    /**
     * SyndFeed 객체를 RssItem 객체 리스트로 변환합니다.
     *
     * @param feed 변환할 SyndFeed 객체
     * @return 변환된 RssItem 객체 리스트
     */
    fun mapFeedToItems(feed: SyndFeed): List<RssItem>
    
    /**
     * 현재 매퍼가 지정된 소스 코드를 지원하는지 확인합니다.
     *
     * @param sourceCode 확인할 소스 코드 (예: "YNA", "SBS", "MK")
     * @return 지원 여부
     */
    fun supports(sourceCode: NewsSourceCode): Boolean
}