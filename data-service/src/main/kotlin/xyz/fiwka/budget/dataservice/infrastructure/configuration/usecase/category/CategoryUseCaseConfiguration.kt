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

@Configuration
class CategoryUseCaseConfiguration {

    @Bean
    fun createCategoryUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        saveCategoryOutputPort: SaveCategoryOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor
    ): CreateCategoryUseCase =
        CreateCategoryService(findBudgetByIdOutputPort, saveCategoryOutputPort, atomicOperationExecutor)

    @Bean
    fun readCategoryUseCase(findCategoryByIdOutputPort: FindCategoryByIdOutputPort): ReadCategoryUseCase =
        ReadCategoryService(findCategoryByIdOutputPort)

    @Bean
    fun updateCategoryUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        updateCategoryOutputPort: UpdateCategoryOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor
    ): UpdateCategoryUseCase =
        UpdateCategoryService(
            findCategoryByIdOutputPort,
            findBudgetByIdOutputPort,
            updateCategoryOutputPort,
            atomicOperationExecutor
        )

    @Bean
    fun deleteCategoryUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        deleteCategoryByIdOutputPort: DeleteCategoryByIdOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor
    ): DeleteCategoryUseCase =
        DeleteCategoryService(findCategoryByIdOutputPort, deleteCategoryByIdOutputPort, atomicOperationExecutor)
}
