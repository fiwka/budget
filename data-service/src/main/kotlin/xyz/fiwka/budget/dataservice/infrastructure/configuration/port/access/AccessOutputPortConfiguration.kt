package xyz.fiwka.budget.dataservice.infrastructure.configuration.port.access

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.FindBudgetRoleForUserOutputPortImpl
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.SaveBudgetRoleForUserOutputPortImpl
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
}


