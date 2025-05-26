package com.ddi.core.common.exception

class BadRequestException : RuntimeException {
    constructor() : super("잘못된 요청입니다.")

    constructor(message: String?) : super(message)
}
