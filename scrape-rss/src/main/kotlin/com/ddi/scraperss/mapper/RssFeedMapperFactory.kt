package com.ddi.scraperss.mapper

import com.ddi.core.newssource.property.NewsSourceCode
import org.springframework.stereotype.Component

/**
 * 소스 코드에 맞는 RssFeedMapper 구현체를 제공하는 팩토리 클래스
 */
@Component
class RssFeedMapperFactory(private val mappers: List<RssFeedMapper>) {
    
    /**
     * 주어진 소스 코드에 맞는 RssFeedMapper 구현체를 반환합니다.
     *
     * @param sourceCode 매퍼를 찾을 소스 코드 (예: "YNA", "SBS", "MK")
     * @return 해당 소스 코드를 지원하는 RssFeedMapper 구현체
     * @throws IllegalArgumentException 지원하지 않는 소스 코드인 경우 발생
     */
    fun getMapper(sourceCode: NewsSourceCode): RssFeedMapper {
        return mappers.find { it.supports(sourceCode) }
            ?: throw IllegalArgumentException("지원하지 않는 소스 코드: $sourceCode")
    }
}