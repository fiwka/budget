package xyz.fiwka.budget.dataservice.infrastructure.port.out.budget

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRepository

@Component
class UpdateBudgetOutputPortImpl(
    private val budgetRepository: BudgetRepository,
    private val budgetMapper: BudgetMapper
) : UpdateBudgetOutputPort {
    override fun execute(request: Budget): Budget {
        val id = requireNotNull(request.id)
        val budgetEntity = budgetRepository.findById(id)
            .orElseThrow { BudgetNotFoundException(id) }

        budgetEntity.name = request.name
        budgetEntity.description = request.description

        return budgetMapper.fromEntity(budgetRepository.save(budgetEntity))
    }
}

