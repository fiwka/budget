package xyz.fiwka.budget.dataservice.infrastructure.port.out.budget

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRepository
import java.util.UUID

@Component
class FindBudgetByIdOutputPortImpl(
    private val budgetRepository: BudgetRepository,
    private val budgetMapper: BudgetMapper
) : FindBudgetByIdOutputPort {
    override fun execute(request: UUID): Budget? =
        budgetRepository.findById(request)
            .map(budgetMapper::fromEntity)
            .orElse(null)
}

