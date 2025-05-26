package com.ddi.scraperss.repository

import com.ddi.core.keyword.Keyword
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface KeywordRepository : CrudRepository<Keyword, Long> {

    @Query("""
        SELECT k
        FROM Keyword k
        WHERE k._isActive = true
    """)
    fun findByActive(pageable: Pageable = Pageable.unpaged()): List<Keyword>
}