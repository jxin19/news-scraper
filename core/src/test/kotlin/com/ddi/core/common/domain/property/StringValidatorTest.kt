package com.ddi.core.common.domain.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class StringValidatorTest {

    // StringValidator를 구현한 테스트 클래스
    private class TestStringValidator : StringValidator

    @Test
    fun `문자열 길이 유효성 검사 성공 테스트`() {
        // given
        val validator = TestStringValidator()
        val value = "test"
        
        // when, then
        assertDoesNotThrow { validator.validateLength(value, "오류 메시지") }
    }

    @Test
    fun `null 문자열 길이 유효성 검사 실패 테스트`() {
        // given
        val validator = TestStringValidator()
        val errorMessage = "문자열이 null입니다."
        
        // when, then
        val exception = assertThrows<IllegalArgumentException> {
            validator.validateLength(null, errorMessage)
        }
        
        assertEquals(errorMessage, exception.message)
    }
    
    @Test
    fun `정규 표현식 유효성 검사 성공 테스트`() {
        // given
        val validator = TestStringValidator()
        val value = "abc123"
        val regex = "^[a-z0-9]+$"
        
        // when, then
        assertDoesNotThrow { validator.validateRule(value, regex, "오류 메시지") }
    }
    
    @Test
    fun `정규 표현식 유효성 검사 실패 테스트`() {
        // given
        val validator = TestStringValidator()
        val value = "ABC123"
        val regex = "^[a-z0-9]+$"
        val errorMessage = "소문자와 숫자만 허용됩니다."
        
        // when, then
        val exception = assertThrows<IllegalArgumentException> {
            validator.validateRule(value, regex, errorMessage)
        }
        
        assertEquals(errorMessage, exception.message)
    }
    
    @Test
    fun `null 값에 대한 정규 표현식 유효성 검사 테스트`() {
        // given
        val validator = TestStringValidator()
        val regex = "^[a-z0-9]+$"
        
        // when, then
        assertDoesNotThrow { validator.validateRule(null, regex, "오류 메시지") }
    }
}
