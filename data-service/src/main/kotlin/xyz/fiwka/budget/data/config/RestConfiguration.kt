package xyz.fiwka.budget.data.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry

@Configuration
class RestConfiguration : RepositoryRestConfigurer {

    override fun configureRepositoryRestConfiguration(
        config: RepositoryRestConfiguration,
        cors: CorsRegistry
    ) {
        config.defaultPageSize = 20
        config.maxPageSize = 100
        config.isReturnBodyOnCreate = true
        config.isReturnBodyOnUpdate = true
    }
}

