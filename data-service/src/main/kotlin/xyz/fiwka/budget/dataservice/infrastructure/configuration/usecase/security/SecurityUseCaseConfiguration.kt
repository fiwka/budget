package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.infrastructure.port.out.access.FindBudgetRoleForUserOutputPortImpl

@Configuration
class SecurityUseCaseConfiguration {

    @Bean
    fun budgetAccessGuard(
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        findBudgetRoleForUserOutputPort: FindBudgetRoleForUserOutputPortImpl,
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
    ): BudgetAccessGuard =
        BudgetAccessGuard(
            findUserByLoginOutputPort,
            findBudgetRoleForUserOutputPort,
            findCategoryByIdOutputPort,
            findTransactionByIdOutputPort,
        )
}


