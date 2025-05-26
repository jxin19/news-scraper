package com.ddi.api.dlqmessage.repository

import com.ddi.core.dlqmessage.DlqMessage
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository

interface DlqMessageRepository: CrudRepository<DlqMessage, Long>, QuerydslPredicateExecutor<DlqMessage> {
    fun findAllByIdIn(ids: List<Long>): List<DlqMessage>
}