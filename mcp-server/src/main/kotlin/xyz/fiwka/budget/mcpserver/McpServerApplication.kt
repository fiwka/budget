package xyz.fiwka.budget.mcpserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class McpServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(McpServerApplication::class.java, *args)
}