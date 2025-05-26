package com.ddi.scraperss.config

import com.ddi.core.newssource.NewsSource
import com.ddi.scraperss.job.*
import com.ddi.core.dto.NewsEvent
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
class ScrapeRssJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val staticRssReader: StaticRssReader,
    private val staticRssProcessor: StaticRssProcessor,
    private val keywordRssReader: KeywordRssReader,
    private val keywordRssProcessor: KeywordRssProcessor,
    private val newsEventWriter: NewsEventWriter
) {

    @Bean
    fun rssScrapingJob(): Job =
        JobBuilder("scrapeRssJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(staticRssStep())
            .next(keywordRssStep())
            .build()

    @Bean
    fun staticRssStep(): Step {
        return StepBuilder("staticRssStep", jobRepository)
            .chunk<NewsSource, List<NewsEvent>>(1, transactionManager)
            .reader(staticRssReader)
            .processor(staticRssProcessor)
            .writer(newsEventWriter)
            .build()
    }

    @Bean
    fun keywordRssStep(): Step {
        return StepBuilder("keywordRssStep", jobRepository)
            .chunk<NewsSource, List<NewsEvent>>(10, transactionManager)
            .reader(keywordRssReader)
            .processor(keywordRssProcessor)
            .writer(newsEventWriter)
            .build()
    }

}