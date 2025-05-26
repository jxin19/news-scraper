package com.ddi.core.keyword

import com.ddi.core.common.domain.property.Name
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KeywordTest {

    @Test
    fun `Keyword 생성 테스트`() {
        // given
        val name = Name("테스트 키워드")
        
        // when
        val keyword = Keyword(
            id = 1,
            name = name,
            _isActive = true
        )
        
        // then
        assertEquals(1, keyword.id)
        assertEquals("테스트 키워드", keyword.nameValue)
        assertTrue(keyword.isActive)
        assertNotNull(keyword.createdAt)
        assertNotNull(keyword.updatedAt)
    }
    
    @Test
    fun `Keyword 활성 상태 토글 테스트`() {
        // given
        val keyword = Keyword(
            id = 1,
            name = Name("테스트 키워드"),
            _isActive = true
        )
        
        // when - 비활성화
        keyword.toggle()
        
        // then
        assertFalse(keyword.isActive)
        
        // when - 다시 활성화
        keyword.toggle()
        
        // then
        assertTrue(keyword.isActive)
    }
    
    @Test
    fun `최소 길이 이름으로 Keyword 생성 테스트`() {
        // given
        val minLengthName = "ab" // Name.MIN_NAME_LENGTH = 2
        
        // when
        val keyword = Keyword(
            name = Name(minLengthName),
            _isActive = true
        )
        
        // then
        assertEquals(minLengthName, keyword.nameValue)
        assertTrue(keyword.isActive)
    }
    
    @Test
    fun `최대 길이 이름으로 Keyword 생성 테스트`() {
        // given
        val maxLengthName = "a".repeat(100) // Name.MAX_NAME_LENGTH = 100
        
        // when
        val keyword = Keyword(
            name = Name(maxLengthName),
            _isActive = true
        )
        
        // then
        assertEquals(maxLengthName, keyword.nameValue)
        assertTrue(keyword.isActive)
    }
    
    @Test
    fun `너무 짧은 이름으로 Keyword 생성 시 예외 테스트`() {
        // given
        val tooShortName = "a" // Name.MIN_NAME_LENGTH = 2
        
        // when, then
        val exception = assertThrows<IllegalArgumentException> {
            Keyword(
                name = Name(tooShortName),
                _isActive = true
            )
        }
        
        // then
        assertEquals("이름은 2 글자 이상으로 입력해 주세요.", exception.message)
    }
    
    @Test
    fun `너무 긴 이름으로 Keyword 생성 시 예외 테스트`() {
        // given
        val tooLongName = "a".repeat(101) // Name.MAX_NAME_LENGTH = 100
        
        // when, then
        val exception = assertThrows<IllegalArgumentException> {
            Keyword(
                name = Name(tooLongName),
                _isActive = true
            )
        }
        
        // then
        assertEquals("이름은 100 글자 이내로 입력해 주세요.", exception.message)
    }
}
