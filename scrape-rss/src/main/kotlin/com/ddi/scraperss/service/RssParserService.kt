package com.ddi.scraperss.service

import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.scraperss.mapper.RssFeedMapperFactory
import com.ddi.scraperss.model.RssItem
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.StringReader
import java.net.URI
import java.net.http.*
import java.time.Duration

@Service
class RssParserService(
    private val mapperFactory: RssFeedMapperFactory
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    fun parseRss(newsSourceCode: NewsSourceCode, url: String): List<RssItem> {
        logger.info("URL에서 RSS 피드 파싱 중: $url")

        try {
            // HTTP 요청 생성
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "RSS Feed Reader/1.0")
                .GET()
                .build()

            // HTTP 요청 실행 및 응답 받기
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() != 200) {
                logger.error("RSS 피드 가져오기 실패: HTTP 상태 코드 ${response.statusCode()}")
                return emptyList()
            }

            // RSS 피드 파싱
            val feedContent = response.body()
            val input = SyndFeedInput()
            val feed: SyndFeed = input.build(StringReader(feedContent))

            // 소스에 맞는 매퍼 사용 (제공처별 커스텀 로직)
            val mapper = mapperFactory.getMapper(newsSourceCode)
            return mapper.mapFeedToItems(feed)
        } catch (e: Exception) {
            logger.error("${url}에서 RSS 피드 파싱 중 오류 발생: ${e.message}", e)
            throw e
        }
    }
}