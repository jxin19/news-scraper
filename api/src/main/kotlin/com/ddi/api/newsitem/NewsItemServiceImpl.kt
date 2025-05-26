package com.ddi.api.newsitem

import com.ddi.api.newsitem.dto.NewsItemSearchServiceRequest
import com.ddi.api.newsitem.dto.NewsItemServiceResponse
import com.ddi.api.newsitem.dto.NewsItemServiceResponses
import com.ddi.api.newsitem.repository.NewsItemRepository
import com.ddi.api.newsitem.repository.predicate.NewsItemPredicate
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class NewsItemServiceImpl(
    private val newsItemRepository: NewsItemRepository
) : NewsItemService {

    override fun count(): Long {
        return newsItemRepository.count()
    }

    override fun countTodayCollected(): Long {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        return newsItemRepository.countTodayCollected(startOfDay)
    }

    override fun list(request: NewsItemSearchServiceRequest): NewsItemServiceResponses {
        val newsItems = newsItemRepository.findAll(NewsItemPredicate.search(request), request.pageable)
        return NewsItemServiceResponses.of(newsItems)
    }

    @Cacheable(value = ["newsItem"], key = "#id")
    override fun detail(id: Long): NewsItemServiceResponse =
        newsItemRepository.findById(id)
            .orElseThrow { NoSuchElementException("찾을 수 없는 뉴스입니다.") }
            .let { NewsItemServiceResponse.of(it) }
}
