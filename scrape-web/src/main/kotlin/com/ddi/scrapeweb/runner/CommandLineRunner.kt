package com.ddi.scrapeweb.runner

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*

@Component
class CommandLineRunner(
    private val jobLauncher: JobLauncher,
    private val webScrapingJob: Job
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        logger.info("Web 배치 작업을 실행합니다.")

        try {
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("uuid", UUID.randomUUID().toString())
                .toJobParameters()

            val jobExecution = jobLauncher.run(webScrapingJob, jobParameters)

            logger.info("작업 실행 결과: ${jobExecution.status}")

            if (jobExecution.failureExceptions.isNotEmpty()) {
                jobExecution.failureExceptions.forEach {
                    logger.error("- ${it.message}", it)
                }
            }
        } catch (e: Exception) {
            logger.error("배치 작업 실행 중 오류가 발생했습니다", e)
        }
    }
}