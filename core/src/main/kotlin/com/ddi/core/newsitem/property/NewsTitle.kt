package com.ddi.core.newsitem.property

import com.ddi.core.common.domain.property.StringValidator
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class NewsTitle : StringValidator {
    @Column(name = "title", nullable = false, length = 255)
    private var _value: String? = null

    constructor()

    constructor(title: String) {
        validateLength(title, "뉴스 제목을 입력해주세요.")
        validateRule(title, REGEX, "뉴스 제목은 255자 이내로 입력해주세요.")
        this._value = title
    }

    val value: String
        get() = _value ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsTitle

        return _value == other._value
    }

    override fun hashCode(): Int {
        return _value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "NewsTitle(_value=$_value)"
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 255
        private const val REGEX = "^.{0,$MAX_TITLE_LENGTH}$"

    }
}