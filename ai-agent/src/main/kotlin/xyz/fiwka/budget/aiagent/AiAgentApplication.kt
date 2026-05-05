package xyz.fiwka.budget.aiagent

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import xyz.fiwka.budget.aiagent.configuration.AiAgentProperties

@SpringBootApplication
@EnableConfigurationProperties(AiAgentProperties::class)
class AiAgentApplication

fun main(args: Array<String>) {
    SpringApplication.run(AiAgentApplication::class.java, *args)
}
