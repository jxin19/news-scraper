package com.ddi.core.common.domain.property

import jakarta.persistence.*

/**
 * URL 관련 값 객체의 추상 기본 클래스.
 * 모든 URL 타입의 공통 기능을 제공합니다.
 */
@MappedSuperclass
@Access(AccessType.FIELD)
abstract class Url : StringValidator {

    @Column(nullable = false)
    var _value: String? = null
        protected set

    protected constructor() {
        this._value = ""
    }

    protected constructor(url: String) {
        val convertedUrl = url.trim()
        validateLength(convertedUrl, "URL을 입력해주세요.")
        validateRule(convertedUrl, DEFAULT_REGEX, DEFAULT_ERROR_MESSAGE)
        this._value = convertedUrl
    }

    val value: String
        get() = _value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Url
        return _value == other._value
    }

    override fun hashCode(): Int = _value?.hashCode() ?: 0

    override fun toString(): String = "Url(_value='$_value')"

    companion object {
        private const val DEFAULT_REGEX = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*(?:[?&=\\w%-+]*)?$"
        private const val DEFAULT_ERROR_MESSAGE = "유효하지 않은 URL 형식입니다."
    }
}