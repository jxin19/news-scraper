package com.ddi.api.newssource.repository

import com.ddi.core.newssource.NewsSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface NewsSourceRepository : CrudRepository<NewsSource, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<NewsSource>
    
    @Query("""
        SELECT COUNT(ns) > 0 
        FROM NewsSource ns 
        WHERE ns.url._value = :url
    """)
    fun existsByUrl(url: String): Boolean
}
