package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.budget

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.service.budget.CreateBudgetService

@Configuration
class BudgetUseCaseConfiguration {
    @Bean
    fun createBudgetUseCase(saveBudgetOutputPort: SaveBudgetOutputPort): CreateBudgetUseCase =
        CreateBudgetService(saveBudgetOutputPort)
}