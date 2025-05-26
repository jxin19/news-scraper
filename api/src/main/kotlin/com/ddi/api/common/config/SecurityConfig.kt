package com.ddi.api.common.config

import com.ddi.api.common.component.AuthenticationFilter
import com.ddi.api.common.component.MemberAuthenticationProvider
import com.ddi.api.common.handler.NoAccessHandler
import com.ddi.api.common.handler.UnauthenticationHandler
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security 설정 클래스
 *
 * @property authenticationFilter 커스텀 인증 필터
 * @property memberAuthenticationProvider 인증을 처리하는 인증 제공자
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationFilter: AuthenticationFilter,
    private val memberAuthenticationProvider: MemberAuthenticationProvider,
) {

    /**
     * AuthenticationFilter를 서블릿 필터로 자동 등록되지 않도록 비활성화
     */
    @Bean
    fun authenticationFilterRegistration(): FilterRegistrationBean<AuthenticationFilter> {
        return FilterRegistrationBean<AuthenticationFilter>().apply {
            filter = authenticationFilter
            isEnabled = false
        }
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(HttpMethod.POST, "/member/**")
                .requestMatchers(
                    "/api/public/**",
                    // Swagger 관련 경로
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-resources/**",
                    "/webjars/**"
                )
        }
    }

    /**
     * HTTP 보안 설정을 구성하는 메서드.
     *
     * @param http HttpSecurity 객체로 보안 관련 설정을 정의하는 데 사용됩니다.
     * @return [SecurityFilterChain] 타입의 보안 필터 체인.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.cors { }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authenticationProvider(memberAuthenticationProvider)
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint(UnauthenticationHandler())
                    .accessDeniedHandler(NoAccessHandler())
            }
            .build()

    /**
     * 인증 관리자를 구성하는 메서드
     *
     * @return [AuthenticationManager] 타입의 인증 공급자
     */
    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authManagerBuilder.authenticationProvider(memberAuthenticationProvider)
        return authManagerBuilder.build()
    }
}