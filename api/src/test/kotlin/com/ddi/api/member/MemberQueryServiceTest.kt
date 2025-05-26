package com.ddi.api.member

import com.ddi.api.common.component.AuthenticationFacade
import com.ddi.api.common.component.JwtProvider
import com.ddi.api.member.application.impl.MemberQueryServiceImpl
import com.ddi.api.member.repository.MemberRepository
import com.ddi.core.common.config.PasswordConfig
import com.ddi.core.member.Member
import com.ddi.core.member.property.Password
import com.ddi.core.member.property.Username
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import java.util.*

@DisplayName("회원 조회 서비스 테스트")
class MemberQueryServiceTest {

    private val memberRepository = mockk<MemberRepository>()
    private val passwordConfig = mockk<PasswordConfig>()
    private val jwtProvider = mockk<JwtProvider>()
    private val authenticationFacade = mockk<AuthenticationFacade>()

    private val memberQueryService = MemberQueryServiceImpl(
        memberRepository = memberRepository,
        passwordConfig = passwordConfig,
        jwtProvider = jwtProvider,
        authenticationFacade = authenticationFacade
    )

    @Test
    fun `중복되지 않은 사용자명으로 검증 시 정상 처리된다`() {
        // given
        val username = "newuser"
        every { memberRepository.existsByIdAndUsername(null, username) } returns false

        // when & then
        memberQueryService.validateDuplicate(null, username)
        
        verify { memberRepository.existsByIdAndUsername(null, username) }
    }

    @Test
    fun `중복된 사용자명으로 검증 시 예외를 발생시킨다`() {
        // given
        val username = "duplicateuser"
        every { memberRepository.existsByIdAndUsername(null, username) } returns true

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            memberQueryService.validateDuplicate(null, username)
        }
        
        assertThat(exception.message).isEqualTo("아이디가 이미 존재합니다.")
        verify { memberRepository.existsByIdAndUsername(null, username) }
    }

    @Test
    fun `정상적으로 인증 정보를 반환한다`() {
        // given
        val username = "testuser"
        val member = createMember(1L, username, "Test123!@#")
        every { memberRepository.findByUsername(username) } returns Optional.of(member)

        // when
        val result = memberQueryService.loadUserByUsername(username)

        // then
        assertThat(result.memberId).isEqualTo(1L)
        assertThat(result.username).isEqualTo(username)
        verify { memberRepository.findByUsername(username) }
    }

    @Test
    fun `존재하지 않는 사용자명으로 조회 시 예외를 발생시킨다`() {
        // given
        val username = "nonexistentuser"
        every { memberRepository.findByUsername(username) } returns Optional.empty()

        // when & then
        val exception = assertThrows<BadCredentialsException> {
            memberQueryService.loadUserByUsername(username)
        }
        
        assertThat(exception.message).isEqualTo("일치하는 정보가 없습니다. 계정정보 확인 후 다시 시도해 주세요.")
        verify { memberRepository.findByUsername(username) }
    }

    @Test
    fun `로그인된 회원의 상세 정보를 반환한다`() {
        // given
        val memberId = 1L
        val member = createMember(memberId, "testuser", "Test123!@#")
        every { authenticationFacade.getMemberId() } returns memberId
        every { memberRepository.findById(memberId) } returns Optional.of(member)

        // when
        val result = memberQueryService.detail()

        // then
        assertThat(result.id).isEqualTo(memberId)
        assertThat(result.username).isEqualTo("testuser")
        verify { authenticationFacade.getMemberId() }
        verify { memberRepository.findById(memberId) }
    }

    @Test
    fun `ID로 회원을 정상적으로 반환한다`() {
        // given
        val memberId = 1L
        val member = createMember(memberId, "testuser", "Test123!@#")
        every { memberRepository.findById(memberId) } returns Optional.of(member)

        // when
        val result = memberQueryService.fetchById(memberId)

        // then
        assertThat(result.id).isEqualTo(memberId)
        verify { memberRepository.findById(memberId) }
    }

    @Test
    fun `존재하지 않는 ID로 조회 시 예외를 발생시킨다`() {
        // given
        val memberId = 999L
        every { memberRepository.findById(memberId) } returns Optional.empty()

        // when & then
        val exception = assertThrows<NoSuchElementException> {
            memberQueryService.fetchById(memberId)
        }
        
        assertThat(exception.message).isEqualTo("찾을 수 없는 정보입니다.")
        verify { memberRepository.findById(memberId) }
    }

    @Test
    fun `정상적으로 액세스 토큰을 갱신한다`() {
        // given
        val refreshToken = "refresh-token"
        val memberId = 1L
        val username = "testuser"
        val newAccessToken = "new-access-token"
        val newRefreshToken = "new-refresh-token"
        
        every { authenticationFacade.getMemberId() } returns memberId
        every { authenticationFacade.getUsername() } returns username
        every { jwtProvider.validateRefreshToken(memberId, refreshToken) } returns true
        every { jwtProvider.generateAccessToken(any()) } returns newAccessToken
        every { jwtProvider.generateRefreshToken(any()) } returns newRefreshToken

        // when
        val result = memberQueryService.refreshAccessToken(refreshToken)

        // then
        assertThat(result.accessToken).isEqualTo(newAccessToken)
        assertThat(result.refreshToken).isEqualTo(newRefreshToken)
        
        verify { jwtProvider.validateRefreshToken(memberId, refreshToken) }
    }

    @Test
    fun `유효하지 않은 리프레시 토큰으로 갱신 시 예외를 발생시킨다`() {
        // given
        val refreshToken = "invalid-refresh-token"
        val memberId = 1L
        val username = "testuser"
        
        every { authenticationFacade.getMemberId() } returns memberId
        every { authenticationFacade.getUsername() } returns username
        every { jwtProvider.validateRefreshToken(memberId, refreshToken) } returns false

        // when & then
        val exception = assertThrows<BadCredentialsException> {
            memberQueryService.refreshAccessToken(refreshToken)
        }
        
        assertThat(exception.message).isEqualTo("로그아웃되었으니, 다시 로그인하십시오.")
    }

    @Test
    fun `정상적으로 토큰을 무효화한다`() {
        // given
        val accessToken = "Bearer access-token"
        val refreshToken = "refresh-token"
        every { jwtProvider.invalidateRefreshToken("access-token", refreshToken) } returns true

        // when
        memberQueryService.logout(accessToken, refreshToken)

        // then
        verify { jwtProvider.invalidateRefreshToken("access-token", refreshToken) }
    }

    private fun createMember(id: Long, username: String, password: String): Member {
        return Member(
            id = id,
            username = Username(username),
            password = Password(password)
        )
    }
}
