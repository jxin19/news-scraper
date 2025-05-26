package com.ddi.scrapeweb

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EntityScan(basePackages = ["com.ddi.core"])
@ComponentScan(basePackages = ["com.ddi.core", "com.ddi.scrapeweb"])
class ScrapeWebApplication

fun main(args: Array<String>) {
    runApplication<ScrapeWebApplication>(*args)
}
