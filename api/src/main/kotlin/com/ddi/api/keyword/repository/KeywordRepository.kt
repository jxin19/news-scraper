package com.ddi.api.keyword.repository

import com.ddi.core.keyword.Keyword
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface KeywordRepository : CrudRepository<Keyword, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Keyword>
    
    @Query("""
        SELECT COUNT(k) > 0 
        FROM Keyword k 
        WHERE k.name._value = :name
    """)
    fun existsByName(name: String): Boolean
}