package com.ddi.api.dlqmessage.application.impl

import com.ddi.api.dlqmessage.application.DlqMessageService
import com.ddi.api.dlqmessage.application.dto.DlqMessageSearchServiceRequest
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceRequest
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponse
import com.ddi.api.dlqmessage.application.dto.DlqMessageServiceResponses
import com.ddi.api.dlqmessage.repository.DlqMessageRepository
import com.ddi.api.dlqmessage.repository.predicate.DlqMessagePredicate
import com.ddi.core.dto.NewsEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DlqMessageServiceImpl(
    private val dlqMessageRepository: DlqMessageRepository,
    private val kafkaTemplate: KafkaTemplate<String, NewsEvent>
) : DlqMessageService {

    @Transactional(readOnly = true)
    override fun count(): Long = dlqMessageRepository.count()

    @Transactional(readOnly = true)
    override fun detail(id: Long): DlqMessageServiceResponse {
        val dlqMessage = dlqMessageRepository.findById(id)
            .orElseThrow { NoSuchElementException("ID가 ${id}인 DLQ 메시지를 찾을 수 없습니다") }

        return DlqMessageServiceResponse.of(dlqMessage)
    }

    @Transactional(readOnly = true)
    override fun list(searchRequest: DlqMessageSearchServiceRequest): DlqMessageServiceResponses {
        val dlqMessages = dlqMessageRepository.findAll(DlqMessagePredicate.search(searchRequest), searchRequest.pageable)
        return DlqMessageServiceResponses.of(dlqMessages)
    }

    @Transactional
    override fun retry(id: Long): Boolean {
        val dlqMessage = dlqMessageRepository.findById(id)
            .orElseThrow { NoSuchElementException("ID가 ${id}인 DLQ 메시지를 찾을 수 없습니다") }

        try {
            kafkaTemplate.send(dlqMessage.originalTopic, dlqMessage.key, dlqMessage.value as NewsEvent?)

            dlqMessage.markAsRetried()

            return true
        } catch (e: Exception) {
            return false
        }
    }

    @Transactional
    override fun delete(id: Long) {
        dlqMessageRepository.deleteById(id)
    }

    @Transactional
    override fun bulkAction(request: DlqMessageServiceRequest) {
        val messages = request.ids?.let { dlqMessageRepository.findAllByIdIn(it) }

        messages?.forEach { dlqMessage ->
            if (request.retry) {
                kafkaTemplate.send(dlqMessage.originalTopic, dlqMessage.key, dlqMessage.value as NewsEvent?)
                dlqMessage.markAsRetried()
            }

            request.status?.let { dlqMessage.status = it }
            request.transformed?.let { dlqMessage.transformed = it }
        }
    }
}