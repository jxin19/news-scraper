package com.ddi.scrapeweb.config

import com.ddi.core.newssource.NewsSource
import com.ddi.core.dto.NewsEvent
import com.ddi.scrapeweb.job.KeywordProcessor
import com.ddi.scrapeweb.job.KeywordReader
import com.ddi.scrapeweb.job.NewsEventWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ScrapeWebJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val keywordReader: KeywordReader,
    private val keywordProcessor: KeywordProcessor,
    private val newsEventWriter: NewsEventWriter
) {

    @Bean
    fun webScrapingJob(): Job =
        JobBuilder("scrapeWebJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(webStep())
            .build()

    @Bean
    fun webStep(): Step {
        return StepBuilder("webStep", jobRepository)
            .chunk<NewsSource, List<NewsEvent>>(1, transactionManager)
            .reader(keywordReader)
            .processor(keywordProcessor)
            .writer(newsEventWriter)
            .build()
    }

}