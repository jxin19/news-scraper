package com.ddi.api.common.application

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * Redis를 사용하여 JWT 리프레시 토큰을 관리하는 서비스
 *
 * @property redisTemplate RedisTemplate 인스턴스
 */
@Service
class JwtTokenService(private val redisTemplate: RedisTemplate<String, String>) {

    private val refreshTokenPrefix = "refreshToken:"

    /**
     * 리프레시 토큰을 Redis에 저장하고 만료 시간을 설정
     *
     * @param memberId 리프레시 토큰과 연결된 회원번호
     * @param refreshToken 저장할 리프레시 토큰
     * @param expirationMinutes 리프레시 토큰의 만료 시간
     */
    fun saveRefreshToken(memberId: Long, refreshToken: String, expirationMinutes: Long) {
        val key = "$refreshTokenPrefix$memberId"
        invalidateRefreshToken(memberId)
        redisTemplate.opsForValue().set(key, refreshToken, expirationMinutes, TimeUnit.SECONDS)
    }

    /**
     * 리프레시 토큰이 유효한지 검증
     *
     * @param memberId 리프레시 토큰과 연결된 회원번호
     * @param refreshToken 검증할 리프레시 토큰
     * @return 저장된 토큰과 주어진 토큰이 일치하면 `true`, 그렇지 않으면 `false`
     */
    fun validateRefreshToken(memberId: Long, refreshToken: String): Boolean {
        val key = "$refreshTokenPrefix$memberId"
        val storedToken = redisTemplate.opsForValue().get(key)
        return storedToken == refreshToken
    }

    /**
     * 리프레시 토큰을 무효화합니다
     *
     * @param memberId 리프레시 토큰과 연결된 회원번호
     * @return 토큰이 성공적으로 삭제되면 `true`, 그렇지 않으면 `false`
     */
    fun invalidateRefreshToken(memberId: Long): Boolean {
        val key = "$refreshTokenPrefix$memberId"
        return redisTemplate.delete(key)
    }

}
