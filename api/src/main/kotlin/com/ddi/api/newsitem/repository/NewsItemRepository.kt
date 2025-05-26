package com.ddi.api.newsitem.repository

import com.ddi.core.newsitem.NewsItemView
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import java.time.Instant

interface NewsItemRepository : CrudRepository<NewsItemView, Long>, QuerydslPredicateExecutor<NewsItemView> {
    
    @Query("""
        SELECT COUNT(n) 
        FROM NewsItemView n 
        WHERE n.collectedAt >= :startOfDay
    """)
    fun countTodayCollected(startOfDay: Instant): Long

}
