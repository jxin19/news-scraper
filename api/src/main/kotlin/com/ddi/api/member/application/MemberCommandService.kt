package com.ddi.api.member.application

import com.ddi.api.member.dto.AuthServiceResponse
import com.ddi.api.member.dto.MemberServiceRequest
import com.ddi.api.member.dto.MemberServiceResponse

interface MemberCommandService {
    fun create(memberServiceRequest: MemberServiceRequest): AuthServiceResponse
    fun update(memberServiceRequest: MemberServiceRequest): MemberServiceResponse
}
