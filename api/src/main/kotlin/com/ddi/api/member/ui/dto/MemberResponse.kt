package com.ddi.api.member.ui.dto

import com.ddi.api.member.dto.MemberServiceResponse

data class MemberResponse(
    val id: Long,
    val username: String,
) {
    companion object {
        fun of(memberServiceResponse: MemberServiceResponse) = MemberResponse(
            id = memberServiceResponse.id,
            username = memberServiceResponse.username,
        )
    }
}
