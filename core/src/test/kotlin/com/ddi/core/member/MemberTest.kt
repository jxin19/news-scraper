package com.ddi.core.member

import com.ddi.core.member.property.Password
import com.ddi.core.member.property.Username
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MemberTest {

    @Test
    fun `Member 생성 테스트`() {
        // given
        val username = Username("testuser")
        val password = Password("Test1234!")
        
        // when
        val member = Member(
            username = username,
            password = password
        )
        
        // then
        assertEquals("testuser", member.usernameValue)
        assertEquals(password._value, member.passwordValue)
        assertNotNull(member.createdAt)
        assertNotNull(member.updatedAt)
    }
    
    @Test
    fun `Member 업데이트 테스트`() {
        // given
        val originalUsername = Username("testuser")
        val originalPassword = Password("Test1234!")
        
        val member = Member(
            id = 1,
            username = originalUsername,
            password = originalPassword
        )
        
        val newUsername = Username("newuser")
        val newPassword = Password("NewTest1234!")
        
        val updatedMember = Member(
            id = 1,
            username = newUsername,
            password = newPassword
        )
        
        // when
        member.update(updatedMember)
        
        // then
        assertEquals("newuser", member.usernameValue)
        assertEquals(newPassword._value, member.passwordValue)
    }
}
