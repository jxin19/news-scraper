package com.ddi.core.member.property

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UsernameTest {

    @Test
    fun `유효한 사용자 이름 생성 테스트`() {
        // given, when
        val username = Username("testuser")
        
        // then
        assertEquals("testuser", username.value)
    }
    
    @Test
    fun `최소 길이 사용자 이름 테스트`() {
        // given, when
        val username = Username("ab")
        
        // then
        assertEquals("ab", username.value)
    }
    
    @Test
    fun `최대 길이 사용자 이름 테스트`() {
        // given, when
        val username = Username("abcdefghijklmnop")
        
        // then
        assertEquals("abcdefghijklmnop", username.value)
    }

    @Test
    fun `너무 짧은 사용자 이름 예외 테스트`() {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            Username("a")
        }
        
        assertEquals("올바른 아이디를 입력해주세요.", exception.message)
    }
    
    @Test
    fun `너무 긴 사용자 이름 예외 테스트`() {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            Username("abcdefghijklmnopq")
        }
        
        assertEquals("올바른 아이디를 입력해주세요.", exception.message)
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["test@user", "test#user", "test\$user", "test%user", "test^user", "test&user", "test*user", "test(user", "test)user"])
    fun `잘못된 문자가 포함된 사용자 이름 예외 테스트`(input: String) {
        // given, when, then
        val exception = assertThrows<IllegalArgumentException> {
            Username(input)
        }
        
        assertEquals("올바른 아이디를 입력해주세요.", exception.message)
    }
}
