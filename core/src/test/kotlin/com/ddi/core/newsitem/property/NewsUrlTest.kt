package com.ddi.core.newsitem.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * NewsUrl 값 객체에 대한 단위 테스트
 */
class NewsUrlTest {

    @Test
    fun `유효한 뉴스 URL 생성 테스트`() {
        // given, when
        val newsUrl = NewsUrl("https://example.com/news/article/123")
        
        // then
        assertEquals("https://example.com/news/article/123", newsUrl.value)
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `빈 URL 예외 테스트`(input: String) {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            NewsUrl(input)
        }
        
        // 예외 메시지 확인
        assertEquals("URL을 입력해주세요.", exception.message)
    }
    
    @Test
    fun `http URL 생성 테스트`() {
        // given, when
        val newsUrl = NewsUrl("http://example.com/news/article/123")
        
        // then
        assertEquals("http://example.com/news/article/123", newsUrl.value)
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["invalid-url", "file:///home/user/file.txt"])
    fun `유효하지 않은 URL 형식 예외 테스트`(input: String) {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            NewsUrl(input)
        }
        
        // 예외 메시지 확인 
        assertEquals("유효하지 않은 URL 형식입니다.", exception.message)
    }
    
    @Test
    fun `매우 긴 URL 생성 테스트`() {
        // given
        val longUrl = "https://example.com/news/article/" + "a".repeat(200)
        
        // when
        val newsUrl = NewsUrl(longUrl)
        
        // then
        assertEquals(longUrl, newsUrl.value)
    }
}
