package com.ddi.api.member.application.impl

import com.ddi.api.common.authentication.MemberPrincipal
import com.ddi.api.common.component.AuthenticationFacade
import com.ddi.api.common.component.JwtProvider
import com.ddi.api.member.application.MemberCommandService
import com.ddi.api.member.application.MemberQueryService
import com.ddi.api.member.dto.AuthServiceResponse
import com.ddi.api.member.dto.MemberServiceRequest
import com.ddi.api.member.dto.MemberServiceResponse
import com.ddi.api.member.repository.MemberRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandServiceImpl(
    private val memberRepository: MemberRepository,
    private val memberQueryService: MemberQueryService,
    private val authenticationFacade: AuthenticationFacade,
    private val jwtProvider: JwtProvider,
) : MemberCommandService {

    override fun create(memberServiceRequest: MemberServiceRequest): AuthServiceResponse {
        val newMember = memberServiceRequest.toEntity()

        memberQueryService.validateDuplicate(
            username = newMember.usernameValue,
        )

        val savedMember = memberRepository.save(newMember)

        val memberPrincipal = MemberPrincipal(
            savedMember.id,
            savedMember.usernameValue,
        )

        return AuthServiceResponse(
            username = savedMember.usernameValue,
            accessToken = jwtProvider.generateAccessToken(memberPrincipal),
            refreshToken = jwtProvider.generateRefreshToken(memberPrincipal)
        )
    }

    override fun update(memberServiceRequest: MemberServiceRequest): MemberServiceResponse {
        val memberId = authenticationFacade.getMemberId()
            ?: throw BadCredentialsException("로그인이 필요한 기능입니다.")
        val member = memberQueryService.fetchById(memberId)
        val updateMember = memberServiceRequest.toEntity()

        memberQueryService.validateDuplicate(
            id = member.id,
            username = updateMember.usernameValue,
        )

        member.update(updateMember)

        return MemberServiceResponse.of(member)
    }

}
