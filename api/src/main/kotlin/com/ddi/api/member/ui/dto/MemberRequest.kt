package com.ddi.api.member.ui.dto

import com.ddi.api.member.dto.MemberServiceRequest
import jakarta.validation.constraints.NotBlank

data class MemberRequest(
    @field:NotBlank(message = "아이디를 입력해주세요.")
    val username: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String
) {
    fun toServiceDto() = MemberServiceRequest(
        username = username,
        password = password
    )
}
