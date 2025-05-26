package com.ddi.streamnews

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EntityScan(basePackages = ["com.ddi.core"])
@ComponentScan(basePackages = ["com.ddi.core", "com.ddi.streamnews"])
class StreamNewsApplication

fun main(args: Array<String>) {
    runApplication<StreamNewsApplication>(*args)
}
