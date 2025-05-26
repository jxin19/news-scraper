package com.ddi.scrapeweb.model

import java.time.Instant

/**
 * 웹 스크래핑으로 수집한 뉴스 아이템을 나타내는 데이터 클래스
 */
data class ScrapeItem(
    val title: String,
    val url: String,
    val content: String?,
    val publishedDate: Instant?,
    val source: String?
)
