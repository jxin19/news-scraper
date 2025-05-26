package com.ddi.api.keyword

import com.ddi.api.keyword.dto.KeywordServiceRequest
import com.ddi.api.keyword.impl.KeywordServiceImpl
import com.ddi.api.keyword.repository.KeywordRepository
import com.ddi.core.common.domain.property.Name
import com.ddi.core.keyword.Keyword
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@DisplayName("키워드 서비스 테스트")
class KeywordServiceTest {

    private val keywordRepository = mockk<KeywordRepository>()
    private val keywordService = KeywordServiceImpl(keywordRepository)

    @Test
    fun `키워드 개수를 조회한다`() {
        // given
        val expectedCount = 10L
        every { keywordRepository.count() } returns expectedCount

        // when
        val result = keywordService.count()

        // then
        assertThat(result).isEqualTo(expectedCount)
        verify { keywordRepository.count() }
    }

    @Test
    fun `모든 키워드를 생성일시 내림차순으로 조회한다`() {
        // given
        val keywords = listOf(
            createKeyword(1L, "키워드1"),
            createKeyword(2L, "키워드2")
        )
        every { keywordRepository.findAllByOrderByCreatedAtDesc() } returns keywords

        // when
        val result = keywordService.list()

        // then
        assertThat(result.list).hasSize(2)
        verify { keywordRepository.findAllByOrderByCreatedAtDesc() }
    }

    @Test
    fun `정상적으로 키워드를 생성한다`() {
        // given
        val request = KeywordServiceRequest("새로운키워드")
        val savedKeyword = createKeyword(1L, "새로운키워드")
        
        every { keywordRepository.existsByName("새로운키워드") } returns false
        every { keywordRepository.save(any()) } returns savedKeyword

        // when
        val result = keywordService.create(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("새로운키워드")
        verify { keywordRepository.existsByName("새로운키워드") }
        verify { keywordRepository.save(any()) }
    }

    @Test
    fun `이미 존재하는 키워드명으로 생성 시 예외를 발생시킨다`() {
        // given
        val request = KeywordServiceRequest("중복키워드")
        every { keywordRepository.existsByName("중복키워드") } returns true

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            keywordService.create(request)
        }
        
        assertThat(exception.message).isEqualTo("이미 존재하는 키워드입니다. (중복키워드)")
        verify { keywordRepository.existsByName("중복키워드") }
    }

    @Test
    fun `키워드의 활성화 상태를 토글한다`() {
        // given
        val keywordId = 1L
        val keyword = mockk<Keyword>()
        every { keywordRepository.findById(keywordId) } returns Optional.of(keyword)
        every { keyword.toggle() } just Runs

        // when
        keywordService.toggleActiveStatus(keywordId)

        // then
        verify { keywordRepository.findById(keywordId) }
        verify { keyword.toggle() }
    }

    @Test
    fun `존재하지 않는 키워드 ID로 토글 시 예외를 발생시킨다`() {
        // given
        val keywordId = 999L
        every { keywordRepository.findById(keywordId) } returns Optional.empty()

        // when & then
        val exception = assertThrows<NoSuchElementException> {
            keywordService.toggleActiveStatus(keywordId)
        }
        
        assertThat(exception.message).isEqualTo("찾을 수 없는 키워드입니다.")
        verify { keywordRepository.findById(keywordId) }
    }

    private fun createKeyword(id: Long, name: String): Keyword {
        return Keyword(
            id = id,
            name = Name(name),
            _isActive = true
        )
    }
}
