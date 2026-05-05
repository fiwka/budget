package xyz.fiwka.budget.aiagent.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "app.ai-agent")
data class AiAgentProperties(
    val sessionTtl: Duration = Duration.ofMinutes(30),
    val maxMessageLength: Int = 4000,
)
