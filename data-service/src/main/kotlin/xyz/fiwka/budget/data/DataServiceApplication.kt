package xyz.fiwka.budget.data

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DataServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(DataServiceApplication::class.java, *args)
}