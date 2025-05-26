package com.ddi.api.newssource

import com.ddi.api.newssource.application.NewsSourceService
import com.ddi.api.newssource.application.dto.NewsSourceServiceRequest
import com.ddi.api.newssource.application.dto.NewsSourceServiceResponse
import com.ddi.api.newssource.application.dto.NewsSourceServiceResponses
import com.ddi.api.newssource.repository.NewsSourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewsSourceServiceImpl(
    private val newsSourceRepository: NewsSourceRepository,
) : NewsSourceService {

    @Transactional(readOnly = true)
    override fun count(): Long =
        newsSourceRepository.count()

    @Transactional(readOnly = true)
    override fun list(): NewsSourceServiceResponses =
        NewsSourceServiceResponses.of(newsSourceRepository.findAllByOrderByCreatedAtDesc())

    @Transactional
    override fun create(newsSourceServiceRequest: NewsSourceServiceRequest): NewsSourceServiceResponse {
        val newsSource = newsSourceServiceRequest.toEntity()

        require(!newsSourceRepository.existsByUrl(newsSource.urlValue)) {
            "이미 존재하는 뉴스 제공처 URL입니다. (${newsSource.urlValue})"
        }

        val savedNewsSource = newsSourceRepository.save(newsSource)

        return NewsSourceServiceResponse.of(savedNewsSource)
    }

    @Transactional
    override fun activate(id: Long) {
        val newsSource = newsSourceRepository.findById(id)
            .orElseThrow { NoSuchElementException("찾을 수 없는 뉴스 제공처입니다.") }

        newsSource.activate()
    }

    @Transactional
    override fun deactivate(id: Long) {
        val newsSource = newsSourceRepository.findById(id)
            .orElseThrow { NoSuchElementException("찾을 수 없는 뉴스 제공처입니다.") }

        newsSource.deactivate()
    }
}
