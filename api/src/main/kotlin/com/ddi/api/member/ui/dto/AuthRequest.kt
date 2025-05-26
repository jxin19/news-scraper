package com.ddi.api.member.ui.dto

import com.ddi.api.member.dto.AuthServiceRequest
import jakarta.validation.constraints.NotBlank

data class AuthRequest(
    @field:NotBlank(message = "아이디를 입력해 주세요.")
    val username: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String
) {
    fun toServiceDto() = AuthServiceRequest(
        username = username,
        password = password
    )
}
