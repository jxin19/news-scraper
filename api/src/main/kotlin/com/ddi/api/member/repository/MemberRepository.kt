package com.ddi.api.member.repository

import com.ddi.core.member.Member
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface MemberRepository : CrudRepository<Member, Long> {
    @Query("""
       SELECT EXISTS (
           SELECT 1 FROM Member m 
           WHERE (:id IS NULL OR m.id != :id) 
           AND m.username._value = :username
       )
    """)
    fun existsByIdAndUsername(id: Long?, username: String): Boolean

    @Query("""
        SELECT m
        FROM Member m 
        WHERE (:username IS NOT NULL AND m.username._value = :username)
    """)
    fun findByUsername(username: String): Optional<Member>
}
