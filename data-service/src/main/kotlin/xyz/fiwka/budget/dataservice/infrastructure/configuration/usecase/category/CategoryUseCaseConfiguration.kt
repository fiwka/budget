package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.category

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.DeleteCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.SaveCategoryOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.UpdateCategoryOutputPort
import xyz.fiwka.budget.dataservice.application.service.category.CreateCategoryService
import xyz.fiwka.budget.dataservice.application.service.category.DeleteCategoryService
import xyz.fiwka.budget.dataservice.application.service.category.ReadCategoryService
import xyz.fiwka.budget.dataservice.application.service.category.UpdateCategoryService
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard

@Configuration
class CategoryUseCaseConfiguration {

    @Bean
    fun createCategoryUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        saveCategoryOutputPort: SaveCategoryOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        atomicOperationExecutor: AtomicOperationExecutor
    ): CreateCategoryUseCase =
        CreateCategoryService(findBudgetByIdOutputPort, saveCategoryOutputPort, budgetAccessGuard, atomicOperationExecutor)

    @Bean
    fun readCategoryUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
    ): ReadCategoryUseCase =
        ReadCategoryService(findCategoryByIdOutputPort, budgetAccessGuard)

    @Bean
    fun updateCategoryUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        updateCategoryOutputPort: UpdateCategoryOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        atomicOperationExecutor: AtomicOperationExecutor
    ): UpdateCategoryUseCase =
        UpdateCategoryService(
            findCategoryByIdOutputPort,
            findBudgetByIdOutputPort,
            updateCategoryOutputPort,
            budgetAccessGuard,
            atomicOperationExecutor
        )

    @Bean
    fun deleteCategoryUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        deleteCategoryByIdOutputPort: DeleteCategoryByIdOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        atomicOperationExecutor: AtomicOperationExecutor
    ): DeleteCategoryUseCase =
        DeleteCategoryService(findCategoryByIdOutputPort, deleteCategoryByIdOutputPort, budgetAccessGuard, atomicOperationExecutor)
}
