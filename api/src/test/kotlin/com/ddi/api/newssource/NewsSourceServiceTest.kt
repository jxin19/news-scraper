package com.ddi.api.newssource

import com.ddi.api.newssource.application.dto.NewsSourceServiceRequest
import com.ddi.api.newssource.repository.NewsSourceRepository
import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceName
import com.ddi.core.newssource.property.NewsSourceType
import com.ddi.core.newssource.property.NewsSourceUrl
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

@DisplayName("뉴스 소스 서비스 테스트")
class NewsSourceServiceTest {

    private val newsSourceRepository = mockk<NewsSourceRepository>()
    private val newsSourceService = NewsSourceServiceImpl(newsSourceRepository)

    @Test
    fun `뉴스 소스 개수를 반환한다`() {
        // given
        val expectedCount = 20L
        every { newsSourceRepository.count() } returns expectedCount

        // when
        val result = newsSourceService.count()

        // then
        assertThat(result).isEqualTo(expectedCount)
        verify { newsSourceRepository.count() }
    }

    @Test
    fun `모든 뉴스 소스를 생성일시 내림차순으로 조회한다`() {
        // given
        val newsSources = listOf(
            createNewsSource(1L, "뉴스소스1", "https://example1.com"),
            createNewsSource(2L, "뉴스소스2", "https://example2.com")
        )
        every { newsSourceRepository.findAllByOrderByCreatedAtDesc() } returns newsSources

        // when
        val result = newsSourceService.list()

        // then
        assertThat(result.list).hasSize(2)
        verify { newsSourceRepository.findAllByOrderByCreatedAtDesc() }
    }

    @Test
    fun `정상적으로 뉴스 소스를 생성한다`() {
        // given
        val request = NewsSourceServiceRequest(
            name = "새로운뉴스소스",
            url = "https://newnews.com",
            code = NewsSourceCode.GOOGLE,
            type = NewsSourceType.RSS_STATIC
        )
        val savedNewsSource = createNewsSource(1L, "새로운뉴스소스", "https://newnews.com")
        
        every { newsSourceRepository.existsByUrl("https://newnews.com") } returns false
        every { newsSourceRepository.save(any()) } returns savedNewsSource

        // when
        val result = newsSourceService.create(request)

        // then
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("새로운뉴스소스")
        assertThat(result.url).isEqualTo("https://newnews.com")
        verify { newsSourceRepository.existsByUrl("https://newnews.com") }
        verify { newsSourceRepository.save(any()) }
    }

    @Test
    fun `이미 존재하는 URL로 생성 시 예외를 발생시킨다`() {
        // given
        val request = NewsSourceServiceRequest(
            name = "중복뉴스소스",
            url = "https://duplicate.com",
            code = NewsSourceCode.YNA,
            type = NewsSourceType.RSS_STATIC
        )
        every { newsSourceRepository.existsByUrl("https://duplicate.com") } returns true

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            newsSourceService.create(request)
        }
        
        assertThat(exception.message).isEqualTo("이미 존재하는 뉴스 제공처 URL입니다. (https://duplicate.com)")
        verify { newsSourceRepository.existsByUrl("https://duplicate.com") }
    }

    @Test
    fun `뉴스 소스를 활성화한다`() {
        // given
        val newsSourceId = 1L
        val newsSource = mockk<NewsSource>()
        every { newsSourceRepository.findById(newsSourceId) } returns Optional.of(newsSource)
        every { newsSource.activate() } just Runs

        // when
        newsSourceService.activate(newsSourceId)

        // then
        verify { newsSourceRepository.findById(newsSourceId) }
        verify { newsSource.activate() }
    }

    @Test
    fun `존재하지 않는 뉴스 소스 활성화 시 예외를 발생시킨다`() {
        // given
        val newsSourceId = 999L
        every { newsSourceRepository.findById(newsSourceId) } returns Optional.empty()

        // when & then
        val exception = assertThrows<NoSuchElementException> {
            newsSourceService.activate(newsSourceId)
        }
        
        assertThat(exception.message).isEqualTo("찾을 수 없는 뉴스 제공처입니다.")
        verify { newsSourceRepository.findById(newsSourceId) }
    }

    @Test
    fun `뉴스 소스를 비활성화한다`() {
        // given
        val newsSourceId = 1L
        val newsSource = mockk<NewsSource>()
        every { newsSourceRepository.findById(newsSourceId) } returns Optional.of(newsSource)
        every { newsSource.deactivate() } just Runs

        // when
        newsSourceService.deactivate(newsSourceId)

        // then
        verify { newsSourceRepository.findById(newsSourceId) }
        verify { newsSource.deactivate() }
    }

    @Test
    fun `존재하지 않는 뉴스 소스 비활성화 시 예외를 발생시킨다`() {
        // given
        val newsSourceId = 999L
        every { newsSourceRepository.findById(newsSourceId) } returns Optional.empty()

        // when & then
        val exception = assertThrows<NoSuchElementException> {
            newsSourceService.deactivate(newsSourceId)
        }
        
        assertThat(exception.message).isEqualTo("찾을 수 없는 뉴스 제공처입니다.")
        verify { newsSourceRepository.findById(newsSourceId) }
    }

    private fun createNewsSource(id: Long, name: String, url: String): NewsSource {
        return NewsSource(
            id = id,
            name = NewsSourceName(name),
            url = NewsSourceUrl(url),
            code = NewsSourceCode.GOOGLE,
            type = NewsSourceType.RSS_STATIC
        )
    }
}
