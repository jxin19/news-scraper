package com.ddi.streamnews.repository

import com.ddi.core.newsitem.NewsItem
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface NewsItemRepository : CrudRepository<NewsItem, Long> {
    @Query("""
        SELECT ni.url._value
        FROM NewsItem ni
        WHERE ni.url._value IN :urls
    """)
    fun findAllUrlsByUrlIn(urls: Collection<String>): List<String>
}
