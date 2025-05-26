package com.ddi.core.member.property

import com.ddi.core.common.domain.property.StringValidator
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Username: StringValidator {

    @Column(name = "username", nullable = false, length = MAX_NAME_LENGTH)
    private var _value: String? = null

    constructor(username: String) {
        validateLength(username, "아이디를 입력해주세요.")
        validateRule(username, REGEX, "올바른 아이디를 입력해주세요.")
        this._value = username
    }

    val value: String
        get() = _value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Username

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Username(_value=$_value)"
    }

    companion object {
        private const val MIN_NAME_LENGTH = 2
        private const val MAX_NAME_LENGTH = 16
        private const val REGEX = "^[^!@#$%^&*()\\-_=+`~]{$MIN_NAME_LENGTH,$MAX_NAME_LENGTH}$"
    }

}
