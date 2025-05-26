package com.ddi.api.keyword.impl

import com.ddi.api.keyword.KeywordService
import com.ddi.api.keyword.dto.KeywordServiceRequest
import com.ddi.api.keyword.dto.KeywordServiceResponse
import com.ddi.api.keyword.dto.KeywordServiceResponses
import com.ddi.api.keyword.repository.KeywordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class KeywordServiceImpl(
    private val keywordRepository: KeywordRepository,
) : KeywordService {

    @Transactional(readOnly = true)
    override fun count(): Long =
        keywordRepository.count()

    @Transactional(readOnly = true)
    override fun list(): KeywordServiceResponses =
        KeywordServiceResponses.of(keywordRepository.findAllByOrderByCreatedAtDesc())

    @Transactional
    override fun create(keywordServiceRequest: KeywordServiceRequest): KeywordServiceResponse {
        val keyword = keywordServiceRequest.toEntity()

        require(!keywordRepository.existsByName(keyword.nameValue)) {
            "이미 존재하는 키워드입니다. (${keyword.nameValue})"
        }

        val savedKeyword = keywordRepository.save(keyword)

        return KeywordServiceResponse.of(savedKeyword)
    }

    @Transactional
    override fun toggleActiveStatus(id: Long) {
        val keyword = keywordRepository.findById(id)
            .orElseThrow { NoSuchElementException("찾을 수 없는 키워드입니다.") }

        keyword.toggle()
    }

}