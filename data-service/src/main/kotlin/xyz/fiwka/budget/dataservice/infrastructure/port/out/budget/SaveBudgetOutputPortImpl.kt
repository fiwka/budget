package xyz.fiwka.budget.dataservice.infrastructure.port.out.budget

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRepository

@Component
class SaveBudgetOutputPortImpl(
    private val budgetRepository: BudgetRepository,
    private val budgetMapper: BudgetMapper
) : SaveBudgetOutputPort {
    override fun execute(request: Budget): Budget =
        budgetMapper.fromEntity(
            budgetRepository.save(budgetMapper.toEntity(request))
        )
}