package com.ddi.api.common.component

import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class AuthenticationFacade(private val jwtProvider: JwtProvider) {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val REFRESH_TOKEN_HEADER = "Refresh-Token"
    }

    fun getMemberId(): Long? = executeTokenOperationWithFallback { getMemberId(it) }

    fun getUsername(): String? = executeTokenOperationWithFallback { getUsername(it) }

    private fun <T> executeTokenOperationWithFallback(operation: JwtProvider.(String) -> T): T? {
        val accessToken = getAccessToken()

        if (accessToken != null) {
            try {
                return jwtProvider.operation(accessToken)
            } catch (e: JwtException) {
                // Access token is invalid, try refresh token
            }
        }

        return getRefreshToken()?.let { refreshToken ->
            try {
                jwtProvider.operation(refreshToken)
            } catch (e: JwtException) {
                null
            }
        }
    }

    private fun getAccessToken(): String? =
        getCurrentRequest().getHeader(AUTHORIZATION_HEADER)?.takeIf { it.isNotBlank() }

    private fun getRefreshToken(): String? =
        getCurrentRequest().getHeader(REFRESH_TOKEN_HEADER)?.takeIf { it.isNotBlank() }

    private fun getCurrentRequest(): HttpServletRequest =
        (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
}
