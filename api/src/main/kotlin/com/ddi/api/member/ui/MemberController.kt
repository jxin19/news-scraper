package com.ddi.api.member.ui

import com.ddi.api.member.application.MemberCommandService
import com.ddi.api.member.application.MemberQueryService
import com.ddi.api.member.ui.dto.AuthRequest
import com.ddi.api.member.ui.dto.AuthResponse
import com.ddi.api.member.ui.dto.MemberRequest
import com.ddi.api.member.ui.dto.MemberResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "회원 관련 API", description = "회원 정보를 관리하는 API")
@RestController
@RequestMapping("/member")
class MemberController(
    private val memberQueryService: MemberQueryService,
    private val memberCommandService: MemberCommandService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "회원 상세 조회", description = "회원의 상세 정보를 조회합니다.")
    @GetMapping
    fun detail(): MemberResponse {
        val response = memberQueryService.detail()
        return MemberResponse.of(response)
    }

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: MemberRequest, response: HttpServletResponse): String? {
        val authServiceResponse = memberCommandService.create(request.toServiceDto())

        // Set Access Token Cookie
        response.addCookie(
            Cookie("Authorization", authServiceResponse.accessToken).apply {
                secure = true
                path = "/"
                maxAge = 300
            }
        )

        // Set Refresh Token Cookie
        response.addCookie(
            Cookie("Refresh-token", authServiceResponse.refreshToken).apply {
                secure = true
                path = "/"
                maxAge = 3600
            }
        )

        return authServiceResponse.username
    }

    @Operation(summary = "회원 수정", description = "기존 회원 정보를 수정합니다.")
    @PutMapping
    fun update(@Valid @RequestBody request: MemberRequest): MemberResponse {
        val response = memberCommandService.update(request.toServiceDto())
        return MemberResponse.of(response)
    }

    @Operation(summary = "로그인", description = "로그인 처리 및 토큰 발급합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest, response: HttpServletResponse): String? {
        val authServiceResponse = memberQueryService.login(request.toServiceDto())

        // Set Access Token Cookie
        response.addCookie(
            Cookie("Authorization", authServiceResponse.accessToken).apply {
                secure = true
                path = "/"
                maxAge = 300
            }
        )

        // Set Refresh Token Cookie
        response.addCookie(
            Cookie("Refresh-token", authServiceResponse.refreshToken).apply {
                secure = true
                path = "/"
                maxAge = 3600
            }
        )

        return authServiceResponse.username
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰 발급합니다.")
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestHeader("Refresh-Token") refreshToken: String): AuthResponse {
        val authServiceResponse = memberQueryService.refreshAccessToken(refreshToken)
        return AuthResponse.of(authServiceResponse)
    }

    @Operation(summary = "로그아웃", description = "로그아웃 처리 및 토큰 무효화합니다.")
    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("Refresh-Token") refreshToken: String,
    ) {
        memberQueryService.logout(accessToken, refreshToken)
    }

}
