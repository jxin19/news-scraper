package com.ddi.core.common.domain.property

/**
 * 문자열 속성에 대한 공통 유효성 검사 메소드를 정의하는 인터페이스.
 */
interface StringValidator {
    /**
     * 주어진 문자열 값이 null이 아니고 비어있지 않은지 검증합니다.
     *
     * @param value 검증할 문자열 값
     * @param message 유효성 검사 실패 시 사용할 에러 메시지
     * @throws IllegalArgumentException 문자열이 null이거나 비어있을 경우 발생
     */
    fun validateLength(value: String?, message: String) {
        require(!value.isNullOrEmpty()) { message }
    }

    /**
     * 주어진 문자열 값이 지정된 정규 표현식 패턴과 일치하는지 검증합니다.
     *
     * @param value 검증할 문자열 값
     * @param regex 문자열이 일치해야 하는 정규 표현식 패턴
     * @param message 유효성 검사 실패 시 사용할 에러 메시지
     * @throws IllegalArgumentException 문자열이 정규 표현식 패턴과 일치하지 않을 경우 발생
     */
    fun validateRule(value: String?, regex: String, message: String) {
        if (value != null && !value.matches(regex.toRegex())) {
            throw IllegalArgumentException(message)
        }
    }
}
