package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.budget

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.application.service.budget.CreateBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.DeleteBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.ReadBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.UpdateBudgetService

@Configuration
class BudgetUseCaseConfiguration {
    @Bean
    fun createBudgetUseCase(
        saveBudgetOutputPort: SaveBudgetOutputPort,
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        saveBudgetRoleForUserOutputPort: SaveBudgetRoleForUserOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): CreateBudgetUseCase =
        CreateBudgetService(
            saveBudgetOutputPort,
            findUserByLoginOutputPort,
            saveBudgetRoleForUserOutputPort,
            atomicOperationExecutor,
        )

    @Bean
    fun readBudgetUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
    ): ReadBudgetUseCase =
        ReadBudgetService(findBudgetByIdOutputPort, budgetAccessGuard)

    @Bean
    fun updateBudgetUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        updateBudgetOutputPort: UpdateBudgetOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        atomicOperationExecutor: AtomicOperationExecutor
    ): UpdateBudgetUseCase =
        UpdateBudgetService(findBudgetByIdOutputPort, updateBudgetOutputPort, budgetAccessGuard, atomicOperationExecutor)

    @Bean
    fun deleteBudgetUseCase(
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        deleteBudgetByIdOutputPort: DeleteBudgetByIdOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        atomicOperationExecutor: AtomicOperationExecutor
    ): DeleteBudgetUseCase =
        DeleteBudgetService(findBudgetByIdOutputPort, deleteBudgetByIdOutputPort, budgetAccessGuard, atomicOperationExecutor)
}