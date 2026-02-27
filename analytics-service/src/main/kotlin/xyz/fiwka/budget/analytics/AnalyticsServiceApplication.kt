package xyz.fiwka.budget.analytics

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class AnalyticsServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(AnalyticsServiceApplication::class.java, *args)
}