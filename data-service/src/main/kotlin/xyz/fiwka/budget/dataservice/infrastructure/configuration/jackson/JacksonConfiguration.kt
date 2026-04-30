package xyz.fiwka.budget.dataservice.infrastructure.configuration.jackson

import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): JsonMapper = JsonMapper.builder()
        .findAndAddModules()
        .build()
}