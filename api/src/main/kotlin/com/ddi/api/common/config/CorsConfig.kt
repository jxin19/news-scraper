package com.ddi.api.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig(
    private val environment: Environment
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        val origins = environment.getProperty("cors.allowed-origins", String::class.java)
            ?.split(",")
            ?.map { it.trim() }
        configuration.allowedOrigins = origins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf(
            "Accept-Encoding",
            "Connection",
            "Content-Length",
            "Cookie",
            "Host",
            "Origin",
            "Referer",
            "User-Agent",
            "Accept",
            "Accept-Language",
            "Client-Language",
            "Client-Currency",
            "Content-Type",
            "Authorization",
            "Refresh-Token"
        )
        configuration.allowCredentials = true
        configuration.maxAge = 7200L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}
