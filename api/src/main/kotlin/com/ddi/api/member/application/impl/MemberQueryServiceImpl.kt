package com.ddi.api.member.application.impl

import com.ddi.api.common.authentication.MemberPrincipal
import com.ddi.api.common.component.AuthenticationFacade
import com.ddi.api.common.component.JwtProvider
import com.ddi.api.member.application.MemberQueryService
import com.ddi.api.member.dto.AuthServiceRequest
import com.ddi.api.member.dto.AuthServiceResponse
import com.ddi.api.member.dto.MemberServiceResponse
import com.ddi.api.member.repository.MemberRepository
import com.ddi.core.common.config.PasswordConfig
import com.ddi.core.common.dto.AuthDetails
import com.ddi.core.member.Member
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberQueryServiceImpl(
    private val memberRepository: MemberRepository,
    private val passwordConfig: PasswordConfig,
    private val jwtProvider: JwtProvider,
    private val authenticationFacade: AuthenticationFacade
) : MemberQueryService {

    override fun validateDuplicate(id: Long?, username: String) {
        require(!memberRepository.existsByIdAndUsername(id, username)) {
            "아이디가 이미 존재합니다."
        }
    }

    override fun loadUserByUsername(username: String): AuthDetails {
        val member = fetchMemberByUsername(username)

        return AuthDetails(
            member.id,
            member.usernameValue,
            ArrayList()
        )
    }

    override fun detail(): MemberServiceResponse {
        val memberId = authenticationFacade.getMemberId()
        return MemberServiceResponse.of(fetchById(memberId!!))
    }

    override fun fetchById(id: Long): Member =
        memberRepository.findById(id)
            .orElseThrow { NoSuchElementException("찾을 수 없는 정보입니다.") }

    override fun login(authServiceRequest: AuthServiceRequest): AuthServiceResponse {
        val member = fetchMemberByUsername(authServiceRequest.usernameValue)
        val isValid = passwordConfig.passwordEncoder()
            .matches(authServiceRequest.password, member.passwordValue)

        if (!isValid) {
            throw BadCredentialsException("일치하는 정보가 없습니다. 계정정보 확인 후 다시 시도해 주세요.")
        }

        val memberPrincipal = MemberPrincipal(
            member.id,
            member.usernameValue,
        )

        return AuthServiceResponse(
            username = member.usernameValue,
            accessToken = jwtProvider.generateAccessToken(memberPrincipal),
            refreshToken = jwtProvider.generateRefreshToken(memberPrincipal)
        )
    }

    override fun fetchMemberByUsername(username: String): Member =
        memberRepository.findByUsername(username)
            .orElseThrow { BadCredentialsException("일치하는 정보가 없습니다. 계정정보 확인 후 다시 시도해 주세요.") }

    override fun refreshAccessToken(refreshToken: String): AuthServiceResponse {
        val memberId = authenticationFacade.getMemberId()
            ?: throw BadCredentialsException("로그인이 필요한 기능입니다.")
        val username = authenticationFacade.getUsername()
            ?: throw BadCredentialsException("로그인이 필요한 기능입니다.")

        if (!jwtProvider.validateRefreshToken(memberId, refreshToken)) {
            throw BadCredentialsException("로그아웃되었으니, 다시 로그인하십시오.")
        }

        val memberPrincipal = MemberPrincipal(memberId, username)

        return AuthServiceResponse(
            accessToken = jwtProvider.generateAccessToken(memberPrincipal),
            refreshToken = jwtProvider.generateRefreshToken(memberPrincipal)
        )
    }

    override fun logout(accessToken: String, refreshToken: String) {
        jwtProvider.invalidateRefreshToken(accessToken.removePrefix("Bearer "), refreshToken)
    }

}
