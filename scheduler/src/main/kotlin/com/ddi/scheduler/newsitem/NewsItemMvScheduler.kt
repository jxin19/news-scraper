package com.ddi.scheduler.newsitem

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NewsItemMvScheduler(
    private val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 300000) // 5분
    @Transactional
    fun refreshNewsItemMv() {
        try {
            logger.info("news_item_mv 갱신 시작")

            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY news_item_mv")

            logger.info("news_item_mv 갱신 완료")
        } catch (e: Exception) {
            logger.warn("CONCURRENTLY 갱신 실패, 일반 갱신으로 재시도: ${e.message}")
            try {
                jdbcTemplate.execute("REFRESH MATERIALIZED VIEW news_item_mv")
                logger.info("일반 갱신으로 news_item_mv 갱신 완료")
            } catch (fallbackException: Exception) {
                logger.error("news_item_mv 갱신 실패: ${fallbackException.message}", fallbackException)
            }
        }
    }
}