package com.ddi.api.member

import com.ddi.api.common.component.AuthenticationFacade
import com.ddi.api.common.component.JwtProvider
import com.ddi.api.member.application.MemberQueryService
import com.ddi.api.member.dto.MemberServiceRequest
import com.ddi.api.member.application.impl.MemberCommandServiceImpl
import com.ddi.api.member.repository.MemberRepository
import com.ddi.core.member.Member
import com.ddi.core.member.property.Password
import com.ddi.core.member.property.Username
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException

@DisplayName("회원 명령 서비스 테스트")
class MemberCommandServiceTest {

    private val memberRepository = mockk<MemberRepository>()
    private val memberQueryService = mockk<MemberQueryService>()
    private val authenticationFacade = mockk<AuthenticationFacade>()
    private val jwtProvider = mockk<JwtProvider>()
    
    private val memberCommandService = MemberCommandServiceImpl(
        memberRepository = memberRepository,
        memberQueryService = memberQueryService,
        authenticationFacade = authenticationFacade,
        jwtProvider = jwtProvider
    )

    @Test
    fun `정상적으로 회원을 생성하고 인증 토큰을 반환한다`() {
        // given
        val request = MemberServiceRequest("testuser", "Test123!@#")
        val savedMember = createMember(1L, "testuser", "Test456!@#")
        val accessToken = "access-token"
        val refreshToken = "refresh-token"
        
        justRun { memberQueryService.validateDuplicate(username = "testuser") }
        every { memberRepository.save(any()) } returns savedMember
        every { jwtProvider.generateAccessToken(any()) } returns accessToken
        every { jwtProvider.generateRefreshToken(any()) } returns refreshToken

        // when
        val result = memberCommandService.create(request)

        // then
        assertThat(result.username).isEqualTo("testuser")
        assertThat(result.accessToken).isEqualTo(accessToken)
        assertThat(result.refreshToken).isEqualTo(refreshToken)
        
        verify { memberQueryService.validateDuplicate(username = "testuser") }
        verify { memberRepository.save(any()) }
        verify { jwtProvider.generateAccessToken(any()) }
        verify { jwtProvider.generateRefreshToken(any()) }
    }

    @Test
    fun `정상적으로 회원 정보를 수정한다`() {
        // given
        val memberId = 1L
        val request = MemberServiceRequest("updateduser", "Test123!@#")
        val existingMember = mockk<Member>()

        every { authenticationFacade.getMemberId() } returns memberId
        every { memberQueryService.fetchById(memberId) } returns existingMember
        every { memberQueryService.validateDuplicate(id = memberId, username = "updateduser") } just Runs
        every { existingMember.update(any()) } just Runs
        every { existingMember.id } returns memberId
        every { existingMember.usernameValue } returns "updateduser"

        // when
        val result = memberCommandService.update(request)

        // then
        assertThat(result.id).isEqualTo(memberId)

        verify { authenticationFacade.getMemberId() }
        verify { memberQueryService.fetchById(memberId) }
        verify { memberQueryService.validateDuplicate(id = memberId, username = "updateduser") }
        verify { existingMember.update(any()) }
    }

    @Test
    fun `로그인하지 않은 상태에서 수정 시 예외를 발생시킨다`() {
        // given
        val request = MemberServiceRequest("testuser", "Test123!@#")
        every { authenticationFacade.getMemberId() } returns null

        // when & then
        val exception = assertThrows<BadCredentialsException> {
            memberCommandService.update(request)
        }
        
        assertThat(exception.message).isEqualTo("로그인이 필요한 기능입니다.")
        verify { authenticationFacade.getMemberId() }
    }

    private fun createMember(id: Long, username: String, password: String): Member {
        return Member(
            id = id,
            username = Username(username),
            password = Password(password)
        )
    }
}
