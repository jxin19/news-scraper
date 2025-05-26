package com.ddi.api.common.component

import com.ddi.api.common.authentication.AuthenticationToken
import com.ddi.api.common.authentication.MemberPrincipal
import com.ddi.api.common.handler.util.ExceptionHandlerUtil
import com.ddi.api.member.application.MemberQueryService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val memberQueryService: MemberQueryService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.getHeader("Authorization")?.removePrefix("Bearer ")
        val refreshToken = request.getHeader("Refresh-Token")

        try {
            if (!accessToken.isNullOrBlank()) {
                processAccessToken(accessToken, request)
            } else {
                processTokenRefresh(refreshToken, response, request)
            }
        } catch (e: ExpiredJwtException) {
            processTokenRefresh(refreshToken, response, request)
        } catch (e: Exception) {
            ExceptionHandlerUtil.Companion.handleException(response, e)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun processAccessToken(token: String, request: HttpServletRequest) {
        if (jwtProvider.validateToken(token)) {
            val memberPrincipal = MemberPrincipal(
                memberId = jwtProvider.getMemberId(token),
                username = jwtProvider.getUsername(token),
            )
            AuthenticationToken(memberPrincipal, ArrayList()).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = this
            }
        }
    }

    private fun processTokenRefresh(refreshToken: String?, response: HttpServletResponse, request: HttpServletRequest) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw JwtException("로그아웃되었으니, 다시 로그인하십시오.")
        }

        val authServiceResponse = memberQueryService.refreshAccessToken(refreshToken)

        response.addCookie(
            Cookie("Authorization", authServiceResponse.accessToken).apply {
                secure = true
                path = "/"
                maxAge = 300
            }
        )

        response.addCookie(
            Cookie("Refresh-token", authServiceResponse.refreshToken).apply {
                secure = true
                path = "/"
                maxAge = 3600
            }
        )

        processAccessToken(authServiceResponse.accessToken, request)
    }
}