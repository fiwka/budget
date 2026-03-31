package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.budget

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.service.budget.CreateBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.DeleteBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.ReadBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.UpdateBudgetService

@Configuration
class BudgetUseCaseConfiguration {
    @Bean
    fun createBudgetUseCase(saveBudgetOutputPort: SaveBudgetOutputPort): CreateBudgetUseCase =
        CreateBudgetService(saveBudgetOutputPort)

    @Bean
    fun readBudgetUseCase(findBudgetByIdOutputPort: FindBudgetByIdOutputPort): ReadBudgetUseCase =
        ReadBudgetService(findBudgetByIdOutputPort)

    @Bean
    fun updateBudgetUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        updateBudgetOutputPort: UpdateBudgetOutputPort
    ): UpdateBudgetUseCase =
        UpdateBudgetService(findBudgetByIdOutputPort, updateBudgetOutputPort)

    @Bean
    fun deleteBudgetUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        deleteBudgetByIdOutputPort: DeleteBudgetByIdOutputPort
    ): DeleteBudgetUseCase =
        DeleteBudgetService(findBudgetByIdOutputPort, deleteBudgetByIdOutputPort)
}