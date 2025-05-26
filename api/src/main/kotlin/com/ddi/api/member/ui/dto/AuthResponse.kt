package com.ddi.api.member.ui.dto

import com.ddi.api.member.dto.AuthServiceResponse

data class AuthResponse(
    val username: String? = null,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(authServiceResponse: AuthServiceResponse) =
            AuthResponse(
                username = authServiceResponse.username,
                accessToken = authServiceResponse.accessToken,
                refreshToken = authServiceResponse.refreshToken
            )
    }
}
