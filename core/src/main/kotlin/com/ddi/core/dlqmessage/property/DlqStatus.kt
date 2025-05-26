package com.ddi.core.dlqmessage.property

enum class DlqStatus {
    PENDING, // 처리 대기 중
    RETRIED, // 재시도 성공
    FAILED, // 재시도 실패
    IGNORED // 무시 (처리 불필요)
}