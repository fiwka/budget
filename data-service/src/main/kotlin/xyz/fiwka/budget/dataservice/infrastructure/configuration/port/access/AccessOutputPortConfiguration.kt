package xyz.fiwka.budget.dataservice.infrastructure.configuration.port.access

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.DeleteBudgetMemberOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.FindBudgetMemberOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.FindBudgetRoleForUserOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.ListBudgetMembersOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.SaveBudgetRoleForUserOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.UpsertBudgetMemberRoleOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRoleRepository
import xyz.fiwka.budget.dataservice.infrastructure.repository.RoleRepository

@Configuration
class AccessOutputPortConfiguration {

    @Bean
    fun findBudgetRoleForUserOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
    ): FindBudgetRoleForUserOutputPortImpl =
        FindBudgetRoleForUserOutputPortImpl(budgetRoleRepository)

    @Bean
    fun saveBudgetRoleForUserOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
        roleRepository: RoleRepository,
    ): SaveBudgetRoleForUserOutputPortImpl =
        SaveBudgetRoleForUserOutputPortImpl(budgetRoleRepository, roleRepository)

    @Bean
    fun listBudgetMembersOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
    ): ListBudgetMembersOutputPortImpl =
        ListBudgetMembersOutputPortImpl(budgetRoleRepository)

    @Bean
    fun findBudgetMemberOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
    ): FindBudgetMemberOutputPortImpl =
        FindBudgetMemberOutputPortImpl(budgetRoleRepository)

    @Bean
    fun upsertBudgetMemberRoleOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
        roleRepository: RoleRepository,
    ): UpsertBudgetMemberRoleOutputPortImpl =
        UpsertBudgetMemberRoleOutputPortImpl(budgetRoleRepository, roleRepository)

    @Bean
    fun deleteBudgetMemberOutputPort(
        budgetRoleRepository: BudgetRoleRepository,
    ): DeleteBudgetMemberOutputPortImpl =
        DeleteBudgetMemberOutputPortImpl(budgetRoleRepository)
}


