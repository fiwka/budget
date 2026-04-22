package xyz.fiwka.budget.gateway.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableRedisWebSession(redisNamespace = "budget:api-gateway:session")
class SessionConfiguration {

    @Bean
    @LoadBalanced
    fun loadBalancedWebClientBuilder(): WebClient.Builder = WebClient.builder()
}

