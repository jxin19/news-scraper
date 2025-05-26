package com.ddi.core.member.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordTest {

    @Test
    fun `유효한 비밀번호 생성 테스트`() {
        // given, when
        val password = Password("Test1234!")
        
        // then
        assertTrue(BCryptPasswordEncoder().matches("Test1234!", password._value))
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `빈 비밀번호 예외 테스트`(input: String) {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            Password(input)
        }
        
        assertEquals("비밀번호 입력해주세요.", exception.message)
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["Test123", "testtest", "12345678", "Test!@#", "test1234"])
    fun `규칙에 맞지 않는 비밀번호 예외 테스트`(input: String) {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            Password(input)
        }
        
        assertEquals("올바른 비밀번호 입력해주세요.", exception.message)
    }
    
    @Test
    fun `BCrypt 인코딩 검증 테스트`() {
        // given
        val rawPassword = "Test1234!"
        
        // when
        val password = Password(rawPassword)
        
        // then
        assertTrue(password._value.startsWith("$2a$"))
        assertTrue(BCryptPasswordEncoder().matches(rawPassword, password._value))
    }
}
