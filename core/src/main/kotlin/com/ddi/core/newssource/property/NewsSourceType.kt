package com.ddi.core.newssource.property

enum class NewsSourceType {
    RSS_STATIC, // 고정 URL (예: "https://www.yna.co.kr/rss/news.xml")
    RSS_KEYWORD, // 키워드 기반 URL (예: "https://news.google.com/rss/search?q=[keyword]&hl=ko&gl=KR&ceid=KR:ko")
    SCRAPING;

    companion object {
        fun fromName(name: String?): NewsSourceType =
            name?.let { entries.find { it.name == name } }
                ?: throw IllegalArgumentException("올바르지 않은 뉴스 제공처 타입: $name")
    }
}