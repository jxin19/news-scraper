package com.ddi.api.common.handler

import com.ddi.api.common.handler.util.ExceptionHandlerUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class NoAccessHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?,
    ) {
        ExceptionHandlerUtil.handleException(response!!, AccessDeniedException("접근할 수 없습니다."))
    }
}
