package com.ddi.scrapeweb.scraper

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scrapeweb.model.ScrapeItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 네이버 뉴스 스크래퍼 구현체
 */
@Component
class NaverNewsScraper : BaseWebScraper() {

    override fun scrapeNews(
        url: String,
        pageNo: Int?
    ): List<ScrapeItem> {
        try {
            val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            val nextParam = LocalDateTime.now().format(timeFormatter)
            val apiUrl = "$url&pageNo=${pageNo ?: 1}&date=&next=$nextParam&_=${System.currentTimeMillis()}"
            val headers = createNaverHeaders()
            val responseBody = executeHttpRequest(apiUrl, headers)

            return parseNewsResponse(responseBody)

        } catch (e: Exception) {
            logger.error("네이버 뉴스 스크래핑 실패: ${e.message}", e)
            return emptyList()
        }
    }

    override fun supports(sourceCode: NewsSourceCode): Boolean {
        return sourceCode == NewsSourceCode.NAVER
    }

    /**
     * 네이버 뉴스 API용 HTTP 헤더를 생성합니다.
     */
    private fun createNaverHeaders(): Map<String, String> {
        return mapOf(
            "Accept" to "*/*",
            "Accept-Language" to "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "Referer" to "https://news.naver.com/section/100",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "same-origin",
            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1",
            "X-Requested-With" to "XMLHttpRequest"
        )
    }

    /**
     * 네이버 뉴스 API 응답을 파싱합니다.
     */
    private fun parseNewsResponse(responseBody: String): List<ScrapeItem> {
        val newsItems = mutableListOf<ScrapeItem>()

        try {
            val rootNode = parseJsonResponse(responseBody)

            // renderedComponent > SECTION_ARTICLE_LIST에서 HTML 문자열 추출
            val renderedComponent = rootNode.path("renderedComponent")
            val sectionArticleList = renderedComponent.path("SECTION_ARTICLE_LIST")

            if (sectionArticleList.isTextual) {
                val htmlContent = sectionArticleList.asText()
                val doc = Jsoup.parse(htmlContent)
                val articles = doc.select("li.sa_item")

                for (article in articles) {
                    val newsItem = parseArticle(article)
                    if (newsItem != null) {
                        newsItems.add(newsItem)
                    }
                }
            }

        } catch (e: Exception) {
            logger.error("뉴스 응답 파싱 실패: ${e.message}", e)
        }

        return newsItems
    }

    /**
     * 개별 뉴스 기사를 파싱합니다.
     */
    private fun parseArticle(articleElement: Element): ScrapeItem? {
        return try {
            // 제목 추출
            val titleElement = articleElement.selectFirst("strong.sa_text_strong")
            val title = cleanText(titleElement?.text())

            // URL 추출
            val linkElement = articleElement.selectFirst("a.sa_text_title")
            val url = linkElement?.attr("href") ?: ""

            // 요약 내용 추출
            val summaryElement = articleElement.selectFirst("div.sa_text_lede")
            val content = cleanText(summaryElement?.text()) // 요약 내용

            // 발행시간 추출
            val timeElement = articleElement.selectFirst("div.sa_text_datetime b")
            val publishedDateStr = timeElement?.text() ?: ""

            // 언론사명 추출
            val pressElement = articleElement.selectFirst("div.sa_text_press")
            val source = cleanText(pressElement?.text()) // 언론사명

            if (title.isNullOrBlank() || url.isBlank()) {
                return null
            }

            ScrapeItem(
                title = title,
                url = url,
                content = content,
                publishedDate = parseDate(publishedDateStr),
                source = source
            )
        } catch (e: Exception) {
            logger.warn("기사 파싱 실패: ${e.message}")
            null
        }
    }

    /**
     * 상대시간을 절대시간으로 변환합니다.
     */
    override fun parseDate(dateStr: String?): Instant? {
        if (dateStr.isNullOrBlank()) return null

        return when {
            dateStr.contains("시간전") -> {
                val hours = dateStr.replace("시간전", "").trim()
                val hoursAgo = hours.toIntOrNull() ?: 0
                val now = System.currentTimeMillis()
                val timestamp = now - (hoursAgo * 60 * 60 * 1000)
                Instant.ofEpochMilli(timestamp)
            }
            dateStr.contains("분전") -> {
                val minutes = dateStr.replace("분전", "").trim()
                val minutesAgo = minutes.toIntOrNull() ?: 0
                val now = System.currentTimeMillis()
                val timestamp = now - (minutesAgo * 60 * 1000)
                Instant.ofEpochMilli(timestamp)
            }
            else -> {
                // 부모 클래스의 기본 파싱 로직 사용
                super.parseDate(dateStr)
            }
        }
    }
}
