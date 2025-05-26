package com.ddi.api.member.dto

data class AuthServiceResponse(
    val username: String? = null,
    val accessToken: String,
    val refreshToken: String
)
