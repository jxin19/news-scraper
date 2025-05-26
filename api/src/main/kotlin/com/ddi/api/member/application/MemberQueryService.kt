package com.ddi.api.member.application

import com.ddi.api.member.dto.AuthServiceRequest
import com.ddi.core.common.dto.AuthDetails
import com.ddi.api.member.dto.AuthServiceResponse
import com.ddi.api.member.dto.MemberServiceResponse
import com.ddi.core.member.Member

interface MemberQueryService {
    fun validateDuplicate(id: Long? = null, username: String)
    fun loadUserByUsername(username: String): AuthDetails
    fun detail(): MemberServiceResponse
    fun fetchById(id: Long): Member
    fun login(authServiceRequest: AuthServiceRequest): AuthServiceResponse
    fun fetchMemberByUsername(username: String): Member
    fun refreshAccessToken(refreshToken: String): AuthServiceResponse
    fun logout(accessToken: String, refreshToken: String)
}
