package com.ddi.scrapeweb.scraper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 공통 스크래핑 로직을 제공하는 기본 WebScraper 구현체입니다.
 * 각 제공처별 스크래퍼는 이 클래스를 상속받아 구현할 수 있습니다.
 */
abstract class BaseWebScraper : WebScraper {
    
    protected val logger = LoggerFactory.getLogger(javaClass)
    protected val objectMapper = ObjectMapper()
    
    protected val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    /**
     * HTTP 요청을 실행하고 응답을 받습니다.
     */
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    protected fun executeHttpRequest(url: String, headers: Map<String, String> = emptyMap()): String {
        logger.info("HTTP 요청 실행: $url")
        
        try {
            val requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
            
            // 기본 헤더 추가
            requestBuilder.header("User-Agent", "Mozilla/5.0 (compatible; WebScraper/1.0)")
            
            // 커스텀 헤더 추가
            headers.forEach { (key, value) ->
                requestBuilder.header(key, value)
            }
            
            val request = requestBuilder.build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            
            if (response.statusCode() != 200) {
                throw RuntimeException("HTTP 요청 실패: 상태 코드 ${response.statusCode()}")
            }
            
            return response.body()
            
        } catch (e: Exception) {
            logger.error("HTTP 요청 실행 중 오류 발생: ${e.message}", e)
            throw e
        }
    }

    /**
     * JSON 응답을 파싱합니다.
     */
    protected fun parseJsonResponse(responseBody: String): JsonNode {
        try {
            return objectMapper.readTree(responseBody)
        } catch (e: Exception) {
            logger.error("JSON 파싱 중 오류 발생: ${e.message}", e)
            throw RuntimeException("JSON 파싱 실패", e)
        }
    }

    /**
     * HTML 태그를 제거하고 텍스트를 정리합니다.
     */
    protected fun cleanText(text: String?): String? {
        if (text.isNullOrBlank()) return null
        
        return text
            .replace(Regex("<[^>]+>"), "") // HTML 태그 제거
            .replace("&nbsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ") // 연속된 공백을 하나로
            .trim()
    }

    /**
     * 다양한 날짜 형식을 파싱하여 Instant로 변환합니다.
     */
    protected open fun parseDate(dateStr: String?): Instant? {
        if (dateStr.isNullOrBlank()) return null
        
        val cleanDateStr = dateStr.trim()
        
        // 상대적 시간 표현 처리 (예: "1시간 전", "30분 전")
        if (cleanDateStr.contains("전")) {
            return parseRelativeTime(cleanDateStr)
        }
        
        // 절대적 시간 표현 처리
        val formatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("MM.dd HH:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        )
        
        for (formatter in formatters) {
            try {
                val localDateTime = parseWithFormatter(cleanDateStr, formatter)
                if (localDateTime != null) {
                    return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
                }
            } catch (e: DateTimeParseException) {
                // 다음 포맷터 시도
            }
        }
        
        logger.warn("날짜 파싱 실패: $dateStr")
        return null
    }

    /**
     * 특정 포맷터로 날짜 파싱을 시도합니다.
     */
    private fun parseWithFormatter(dateStr: String, formatter: DateTimeFormatter): LocalDateTime? {
        return try {
            when {
                dateStr.matches(Regex("\\d{2}\\.\\d{2} \\d{2}:\\d{2}")) -> {
                    // MM.dd HH:mm 형식
                    val withYear = "${LocalDateTime.now().year}-${dateStr.replace(".", "-")}"
                    LocalDateTime.parse(withYear, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                }
                dateStr.matches(Regex("\\d{2}:\\d{2}")) -> {
                    // HH:mm 형식
                    val now = LocalDateTime.now()
                    val withDate = "${now.year}-${String.format("%02d", now.monthValue)}-${String.format("%02d", now.dayOfMonth)} $dateStr"
                    LocalDateTime.parse(withDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                }
                else -> LocalDateTime.parse(dateStr, formatter)
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * 상대적 시간 표현을 파싱합니다 (예: "1시간 전", "30분 전").
     */
    private fun parseRelativeTime(relativeTimeStr: String): Instant? {
        val now = LocalDateTime.now()
        
        return try {
            when {
                relativeTimeStr.contains("분 전") -> {
                    val minutes = extractNumber(relativeTimeStr, "분 전")
                    now.minusMinutes(minutes.toLong()).atZone(ZoneId.of("Asia/Seoul")).toInstant()
                }
                relativeTimeStr.contains("시간 전") -> {
                    val hours = extractNumber(relativeTimeStr, "시간 전")
                    now.minusHours(hours.toLong()).atZone(ZoneId.of("Asia/Seoul")).toInstant()
                }
                relativeTimeStr.contains("일 전") -> {
                    val days = extractNumber(relativeTimeStr, "일 전")
                    now.minusDays(days.toLong()).atZone(ZoneId.of("Asia/Seoul")).toInstant()
                }
                else -> null
            }
        } catch (e: Exception) {
            logger.warn("상대적 시간 파싱 실패: $relativeTimeStr")
            null
        }
    }

    /**
     * 문자열에서 숫자를 추출합니다.
     */
    private fun extractNumber(text: String, suffix: String): Int {
        val numberStr = text.replace(suffix, "").trim()
        return numberStr.toInt()
    }
}
