package com.ddi.api.member.dto

import com.ddi.core.member.Member

data class MemberServiceResponse(
    val id: Long = 0,
    val username: String = "",
) {
    companion object {
        fun of(member: Member) = MemberServiceResponse(
            id = member.id,
            username = member.usernameValue,
        )
    }
}
