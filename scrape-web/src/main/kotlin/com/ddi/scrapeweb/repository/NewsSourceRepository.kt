package com.ddi.scrapeweb.repository

import com.ddi.core.newssource.NewsSource
import com.ddi.core.newssource.property.NewsSourceType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface NewsSourceRepository : CrudRepository<NewsSource, Long> {

    @Query("""
        SELECT ns
        FROM NewsSource ns
        WHERE ns._isActive = true
        AND ns.type = :type
    """)
    fun findByActiveKeyword(type: NewsSourceType, pageable: Pageable = Pageable.unpaged()): List<NewsSource>
}