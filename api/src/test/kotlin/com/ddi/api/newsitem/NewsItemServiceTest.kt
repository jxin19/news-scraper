package com.ddi.api.newsitem

import com.ddi.api.newsitem.dto.NewsItemSearchServiceRequest
import com.ddi.api.newsitem.repository.NewsItemRepository
import com.ddi.api.newsitem.repository.predicate.NewsItemPredicate
import com.ddi.core.newsitem.NewsItem
import com.ddi.core.newsitem.NewsItemView
import com.ddi.core.newsitem.property.NewsTitle
import com.ddi.core.newsitem.property.NewsUrl
import com.querydsl.core.types.Predicate
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.util.*

@DisplayName("뉴스 아이템 서비스 테스트")
class NewsItemServiceTest {

    private val newsItemRepository = mockk<NewsItemRepository>()
    private val newsItemService = NewsItemServiceImpl(newsItemRepository)

    @Test
    fun `전체 뉴스 아이템 개수를 반환한다`() {
        // given
        val expectedCount = 100L
        every { newsItemRepository.count() } returns expectedCount

        // when
        val result = newsItemService.count()

        // then
        assertThat(result).isEqualTo(expectedCount)
        verify { newsItemRepository.count() }
    }

    @Test
    fun `오늘 수집된 뉴스 개수를 반환한다`() {
        // given
        val expectedCount = 50L
        every { newsItemRepository.countTodayCollected(any()) } returns expectedCount

        // when
        val result = newsItemService.countTodayCollected()

        // then
        assertThat(result).isEqualTo(expectedCount)
        verify { newsItemRepository.countTodayCollected(any()) }
    }

    @Test
    fun `검색 조건에 맞는 뉴스 아이템 목록을 반환한다`() {
        // given
        val pageable = PageRequest.of(0, 10)
        val request = NewsItemSearchServiceRequest(
            title = "테스트",
            pageable = pageable
        )

        val newsItemView1 = mockk<NewsItemView>()
        val newsItemView2 = mockk<NewsItemView>()

        // newsItemView1의 모든 필요한 속성 설정
        every { newsItemView1.id } returns 1L
        every { newsItemView1.title } returns "테스트 뉴스 1"
        every { newsItemView1.keywordId } returns 1L
        every { newsItemView1.keywordName } returns "키워드1"
        every { newsItemView1.sourceId } returns 1L
        every { newsItemView1.sourceName } returns "소스1"
        every { newsItemView1.url } returns "https://test1.com"
        every { newsItemView1.content } returns "내용1"
        every { newsItemView1.publishedDate } returns null
        every { newsItemView1.collectedAt } returns Instant.now()

        // newsItemView2의 모든 필요한 속성 설정
        every { newsItemView2.id } returns 2L
        every { newsItemView2.title } returns "테스트 뉴스 2"
        every { newsItemView2.keywordId } returns 2L
        every { newsItemView2.keywordName } returns "키워드2"
        every { newsItemView2.sourceId } returns 2L
        every { newsItemView2.sourceName } returns "소스2"
        every { newsItemView2.url } returns "https://test2.com"
        every { newsItemView2.content } returns "내용2"
        every { newsItemView2.publishedDate } returns null
        every { newsItemView2.collectedAt } returns Instant.now()

        val page = PageImpl(listOf(newsItemView1, newsItemView2), pageable, 2L)
        val predicate = mockk<Predicate>()

        mockkObject(NewsItemPredicate)
        every { NewsItemPredicate.search(request) } returns predicate
        every { newsItemRepository.findAll(predicate, pageable) } returns page

        // when
        val result = newsItemService.list(request)

        // then
        assertThat(result.content).hasSize(2)
        verify { NewsItemPredicate.search(request) }
        verify { newsItemRepository.findAll(predicate, pageable) }
    }

    @Test
    fun `뉴스 아이템 상세 정보를 반환한다`() {
        // given
        val newsItemId = 1L
        val newsItemView = mockk<NewsItemView>()
        every { newsItemView.id } returns newsItemId
        every { newsItemView.title } returns "테스트 뉴스 1"
        every { newsItemView.keywordId } returns 1L
        every { newsItemView.keywordName } returns "키워드1"
        every { newsItemView.sourceId } returns 1L
        every { newsItemView.sourceName } returns "소스1"
        every { newsItemView.url } returns "https://test1.com"
        every { newsItemView.content } returns "내용1"
        every { newsItemView.publishedDate } returns null
        every { newsItemView.collectedAt } returns Instant.now()
        every { newsItemRepository.findById(newsItemId) } returns Optional.of(newsItemView)

        // when
        val result = newsItemService.detail(newsItemId)

        // then
        assertThat(result.id).isEqualTo(newsItemId)
        assertThat(result.title).isEqualTo("테스트 뉴스 1")
        verify { newsItemRepository.findById(newsItemId) }
    }

    @Test
    fun `존재하지 않는 뉴스 아이템 조회 시 예외를 발생시킨다`() {
        // given
        val newsItemId = 999L
        every { newsItemRepository.findById(newsItemId) } returns Optional.empty()

        // when & then
        val exception = assertThrows<NoSuchElementException> {
            newsItemService.detail(newsItemId)
        }
        
        assertThat(exception.message).isEqualTo("찾을 수 없는 뉴스입니다.")
        verify { newsItemRepository.findById(newsItemId) }
    }

    private fun createNewsItem(id: Long, title: String): NewsItem {
        return NewsItem(
            id = id,
            keywordId = 1L,
            sourceId = 1L,
            title = NewsTitle(title),
            url = NewsUrl("https://example.com/news/$id"),
            content = "테스트 내용",
            collectedAt = Instant.now()
        )
    }
}
