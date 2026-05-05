package xyz.fiwka.budget.aiagent.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

@Configuration(proxyBeanMethods = false)
class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(
        @Value("\${spring.data.redis.host}") host: String,
        @Value("\${spring.data.redis.port}") port: Int,
        @Value("\${spring.data.redis.password:}") password: String,
    ): ReactiveRedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration(host, port)

        if (password.isNotBlank()) {
            configuration.password = RedisPassword.of(password)
        }

        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun reactiveStringRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveStringRedisTemplate = ReactiveStringRedisTemplate(connectionFactory)
}


