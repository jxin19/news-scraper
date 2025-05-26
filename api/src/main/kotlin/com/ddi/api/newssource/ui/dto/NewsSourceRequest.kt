package com.ddi.api.newssource.ui.dto

import com.ddi.api.newssource.application.dto.NewsSourceServiceRequest
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class NewsSourceRequest(
    @field:NotBlank(message = "뉴스 제공처 이름을 입력해주세요.")
    @field:Size(min = 2, max = 32, message = "뉴스 제공처 이름은 2-32자 사이여야 합니다.")
    val name: String = "",

    @field:NotBlank(message = "뉴스 제공처 URL을 입력해주세요.")
    @field:Size(max = 255, message = "URL은 255자를 초과할 수 없습니다.")
    val url: String = "",

    @field:NotNull(message = "뉴스 제공처 코드를 선택해주세요.")
    val code: String = "",

    @field:NotNull(message = "뉴스 제공처 타입을 선택해주세요.")
    val type: String = ""
) {
    fun toServiceDto() = NewsSourceServiceRequest(
        name = name,
        url = url,
        code = NewsSourceCode.fromName(code),
        type = NewsSourceType.fromName(type),
    )
}