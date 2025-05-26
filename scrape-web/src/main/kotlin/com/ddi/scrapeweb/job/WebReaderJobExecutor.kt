package com.ddi.scrapeweb.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WebReaderJobExecutor(
    private val jobLauncher: JobLauncher,
    private val webScrapingJob: Job,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "1 */10 * * * *")
    fun launchJob() {
        log.info("Web 뉴스 수집 작업 시작")

        val jobParameters = JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()

        try {
            val jobExecution = jobLauncher.run(webScrapingJob, jobParameters)
            log.info("작업 완료: ${jobExecution.status}")

            if (jobExecution.status.isRunning) return
        } catch (e: Exception) {
            log.error("작업 실행 중 오류 발생", e)
        }
    }
}