package com.ddi.api.member.dto

import com.ddi.core.member.property.Username

data class AuthServiceRequest(
    val username: String,
    val password: String
) {
    val usernameValue: String
        get() = Username(username).value
}
