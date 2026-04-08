package xyz.fiwka.budget.analytics.configuration.jackson

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper

@Configuration
class JacksonConfiguration {

    @Bean
    fun jsonMapper(): JsonMapper = JsonMapper.builder()
        .findAndAddModules()
        .build()
}