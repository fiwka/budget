package xyz.fiwka.budget.dataservice.infrastructure.configuration.jackson

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper

@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper = JsonMapper.builder()
        .findAndAddModules()
        .build()

    @Bean
    fun jsonMapper(): JsonMapper = objectMapper() as JsonMapper
}