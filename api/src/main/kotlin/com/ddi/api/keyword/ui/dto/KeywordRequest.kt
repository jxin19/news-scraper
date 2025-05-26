package com.ddi.api.keyword.ui.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class KeywordRequest(
    @field:NotBlank(message = "키워드 이름을 입력해주세요.")
    @field:Size(min = 2, max = 100, message = "키워드 이름은 2-100자 사이여야 합니다.")
    val name: String = ""
)
