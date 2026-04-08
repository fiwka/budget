package xyz.fiwka.budget.analytics.configuration.openapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(title = "Budget Analytics Service", version = "1.0.0"),
    servers = [Server(
        url = "http://localhost:8080",
        description = "localhost"
    )]
)
class OpenApiConfiguration