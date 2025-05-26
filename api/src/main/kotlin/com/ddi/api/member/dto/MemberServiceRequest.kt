package com.ddi.api.member.dto

import com.ddi.core.member.Member
import com.ddi.core.member.property.Password
import com.ddi.core.member.property.Username

data class MemberServiceRequest(
    val username: String,
    val password: String
) {
    fun toEntity(): Member = Member(
        username = Username(username),
        password = Password(password),
    )
}
