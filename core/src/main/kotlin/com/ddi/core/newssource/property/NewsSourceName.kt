package com.ddi.core.newssource.property

import com.ddi.core.common.domain.property.StringValidator
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class NewsSourceName : StringValidator {
    @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
    private var _value: String? = null

    constructor()

    constructor(name: String) {
        validateLength(name, "뉴스 제공처 이름을 입력해주세요.")
        validateRule(name, REGEX, "뉴스 제공처 이름은 $MAX_NAME_LENGTH 자 이내로 입력해주세요.")
        this._value = name
    }

    val value: String
        get() = _value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsSourceName

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "NewsSourceName(_value=$_value)"
    }

    companion object {
        private const val MAX_NAME_LENGTH = 32
        private const val REGEX = "^.{0,$MAX_NAME_LENGTH}$"
    }
}