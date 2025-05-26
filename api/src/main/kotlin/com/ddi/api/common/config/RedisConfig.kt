package com.ddi.api.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Redis 설정 구성 클래스
 */
@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}") private val redisHost: String,
    @Value("\${spring.data.redis.port}") private val redisPort: Int
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration(redisHost, redisPort))

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        createRedisTemplate(connectionFactory, StringRedisSerializer())

    @Bean
    fun clinicRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> =
        createRedisTemplate(connectionFactory, Jackson2JsonRedisSerializer(Any::class.java))

    @Bean
    fun exchangeRateRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Double> =
        createRedisTemplate(connectionFactory, Jackson2JsonRedisSerializer(Double::class.java))

    private fun <T> createRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        valueSerializer: RedisSerializer<T>
    ) = RedisTemplate<String, T>().apply {
        this.connectionFactory = connectionFactory
        this.keySerializer = StringRedisSerializer()
        this.valueSerializer = valueSerializer
        this.isEnableDefaultSerializer = false
        this.setEnableTransactionSupport(true)
    }
}