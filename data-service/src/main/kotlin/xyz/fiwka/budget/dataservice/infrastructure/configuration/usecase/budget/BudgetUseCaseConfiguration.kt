package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.budget

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleUseCase
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.ListBudgetMembersOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.application.service.budget.AddBudgetMemberService
import xyz.fiwka.budget.dataservice.application.service.budget.BudgetMemberPolicy
import xyz.fiwka.budget.dataservice.application.service.budget.CreateBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.DeleteBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.ListBudgetMembersService
import xyz.fiwka.budget.dataservice.application.service.budget.ListAccessibleBudgetsService
import xyz.fiwka.budget.dataservice.application.service.budget.ReadBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.RemoveBudgetMemberService
import xyz.fiwka.budget.dataservice.application.service.budget.UpdateBudgetService
import xyz.fiwka.budget.dataservice.application.service.budget.UpdateBudgetMemberRoleService

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
    fun listAccessibleBudgetsUseCase(
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        listAccessibleBudgetsOutputPort: ListAccessibleBudgetsOutputPort,
    ): ListAccessibleBudgetsUseCase =
        ListAccessibleBudgetsService(findUserByLoginOutputPort, listAccessibleBudgetsOutputPort)


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

    @Bean
    fun budgetMemberPolicy(
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
    ): BudgetMemberPolicy =
        BudgetMemberPolicy(findUserByLoginOutputPort, findBudgetMemberOutputPort)

    @Bean
    fun listBudgetMembersUseCase(
        listBudgetMembersOutputPort: ListBudgetMembersOutputPort,
        budgetMemberPolicy: BudgetMemberPolicy,
    ): ListBudgetMembersUseCase =
        ListBudgetMembersService(listBudgetMembersOutputPort, budgetMemberPolicy)

    @Bean
    fun addBudgetMemberUseCase(
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
        upsertBudgetMemberRoleOutputPort: UpsertBudgetMemberRoleOutputPort,
        budgetMemberPolicy: BudgetMemberPolicy,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): AddBudgetMemberUseCase =
        AddBudgetMemberService(
            findUserByLoginOutputPort,
            findBudgetMemberOutputPort,
            upsertBudgetMemberRoleOutputPort,
            budgetMemberPolicy,
            atomicOperationExecutor,
        )

    @Bean
    fun updateBudgetMemberRoleUseCase(
        findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
        upsertBudgetMemberRoleOutputPort: UpsertBudgetMemberRoleOutputPort,
        budgetMemberPolicy: BudgetMemberPolicy,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): UpdateBudgetMemberRoleUseCase =
        UpdateBudgetMemberRoleService(
            findBudgetMemberOutputPort,
            upsertBudgetMemberRoleOutputPort,
            budgetMemberPolicy,
            atomicOperationExecutor,
        )

    @Bean
    fun removeBudgetMemberUseCase(
        findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
        deleteBudgetMemberOutputPort: DeleteBudgetMemberOutputPort,
        budgetMemberPolicy: BudgetMemberPolicy,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): RemoveBudgetMemberUseCase =
        RemoveBudgetMemberService(
            findBudgetMemberOutputPort,
            deleteBudgetMemberOutputPort,
            budgetMemberPolicy,
            atomicOperationExecutor,
        )
}
