package com.ddi.core.member.property

import com.ddi.core.common.domain.property.StringValidator
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Embeddable
class Password : StringValidator {
    @Column(name = "password", nullable = false)
    lateinit var _value: String
        private set

    constructor()

    constructor(password: String) {
        val convertedPassword = password.trim()
        validateLength(convertedPassword, "비밀번호 입력해주세요.")
        validateRule(convertedPassword, PASSWORD_RULE_REGEX, "비밀번호는 최소 8자 이상이어야 하며, 영문자, 숫자, 특수문자(@, \$, !, %, *, #, ?, &)를 각각 하나 이상 포함해야 합니다")
        this._value = BCryptPasswordEncoder().encode(convertedPassword)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Password

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value.hashCode()
    }

    override fun toString(): String {
        return "Password(_value='$_value')"
    }

    companion object {
        const val PASSWORD_RULE_REGEX: String = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    }

}
