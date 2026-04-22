package xyz.fiwka.budget.dataservice.infrastructure.port.out.budget

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsRequest
import xyz.fiwka.budget.dataservice.domain.budget.AccessibleBudget
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRoleRepository

@Component
class ListAccessibleBudgetsOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
) : ListAccessibleBudgetsOutputPort {

    override fun execute(request: ListAccessibleBudgetsRequest): PageResult<AccessibleBudget> {
        val page = budgetRoleRepository.findAccessibleBudgets(
            userId = request.userId,
            budgetId = request.budgetId,
            name = request.name,
            description = request.description,
            roleKey = request.role?.key,
            pageable = PageRequest.of(request.page, request.size),
        )

        return PageResult(
            items = page.content.map {
                AccessibleBudget(
                    budget = Budget(
                        id = it.getBudgetId(),
                        name = it.getBudgetName(),
                        description = it.getBudgetDescription(),
                    ),
                    role = BudgetRole.fromKey(it.getRoleKey()),
                )
            },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
        )
    }
}

