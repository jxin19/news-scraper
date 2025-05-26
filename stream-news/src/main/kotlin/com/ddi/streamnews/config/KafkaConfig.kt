package com.ddi.streamnews.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties


@Configuration
class KafkaConfig {
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<Any, Any>
    ): ConcurrentKafkaListenerContainerFactory<Any, Any> {
        return ConcurrentKafkaListenerContainerFactory<Any, Any>().apply {
            this.consumerFactory = consumerFactory
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        }
    }
}