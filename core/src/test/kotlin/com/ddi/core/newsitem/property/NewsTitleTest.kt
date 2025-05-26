package com.ddi.core.newsitem.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NewsTitleTest {

    @Test
    fun `유효한 뉴스 제목 생성 테스트`() {
        // given, when
        val newsTitle = NewsTitle("유효한 뉴스 제목")
        
        // then
        assertEquals("유효한 뉴스 제목", newsTitle.value)
    }

    @Test
    fun `매우 긴 뉴스 제목 예외 테스트`() {
        // given
        val longTitle = "a".repeat(256) // MAX_TITLE_LENGTH = 255
        
        // when, then
        val exception = assertThrows<IllegalArgumentException> {
            NewsTitle(longTitle)
        }
        
        // 예외 메시지 확인
        assertEquals("뉴스 제목은 255자 이내로 입력해주세요.", exception.message)
    }
    
    @Test
    fun `최대 길이 뉴스 제목 생성 테스트`() {
        // given
        val maxLengthTitle = "a".repeat(255) // MAX_TITLE_LENGTH = 255
        
        // when
        val newsTitle = NewsTitle(maxLengthTitle)
        
        // then
        assertEquals(maxLengthTitle, newsTitle.value)
    }
}
