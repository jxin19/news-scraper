package com.ddi.core.common.domain.property

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class Name {

    @Column(nullable = false)
    var _value: String? = null
        private set

    constructor()

    constructor(name: String) {
        require(name.length >= MIN_NAME_LENGTH) { EXIST_ERROR_MESSAGE }
        require(name.length <= MAX_NAME_LENGTH) { LENGTH_ERROR_MESSAGE }
        this._value = name.lowercase()
    }

    val value: String
        get() = _value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Name

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Name(value=$_value)"
    }

    companion object {
        const val MIN_NAME_LENGTH: Int = 2
        const val MAX_NAME_LENGTH: Int = 100
        const val EXIST_ERROR_MESSAGE: String = "이름은 $MIN_NAME_LENGTH 글자 이상으로 입력해 주세요."
        const val LENGTH_ERROR_MESSAGE: String = "이름은 $MAX_NAME_LENGTH 글자 이내로 입력해 주세요."
    }

}
