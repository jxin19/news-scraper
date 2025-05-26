package com.ddi.scraperss.job

import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceType
import com.ddi.scraperss.repository.NewsSourceRepository
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class StaticRssReader(
    private val newsSourceRepository: NewsSourceRepository
) : ItemReader<NewsSource> {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var sources: List<NewsSource>? = null
    private var index = 0

    override fun read(): NewsSource? {
        try {

            if (sources == null) {
                sources = newsSourceRepository.findByActiveKeyword(
                    NewsSourceType.RSS_STATIC,
                    PageRequest.of(0, 100, Sort.by("id").ascending())
                )
            }

            if (index < sources!!.size) {
                val source = sources!![index++]
                return source
            } else {
                sources = null
                index = 0
                return null
            }
        } catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }
}