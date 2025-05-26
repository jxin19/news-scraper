package com.ddi.streamnews.repository

import com.ddi.core.dlqmessage.DlqMessage
import org.springframework.data.repository.CrudRepository

interface DlqMessageRepository: CrudRepository<DlqMessage, Long> {
}