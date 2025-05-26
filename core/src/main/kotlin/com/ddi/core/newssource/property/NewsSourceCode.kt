package com.ddi.core.newssource.property

enum class NewsSourceCode {
    YNA,
    SBS,
    MK,
    GOOGLE,
    NAVER;

    companion object {
        fun fromName(name: String?): NewsSourceCode =
            name?.let { entries.find { it.name == name } }
                ?: throw IllegalArgumentException("올바르지 않은 뉴스 제공처 코드: $name")
    }
}