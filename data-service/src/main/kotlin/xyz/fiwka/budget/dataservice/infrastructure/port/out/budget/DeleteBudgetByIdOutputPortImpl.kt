package xyz.fiwka.budget.dataservice.infrastructure.port.out.budget

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRepository
import java.util.UUID

@Component
class DeleteBudgetByIdOutputPortImpl(
    private val budgetRepository: BudgetRepository
) : DeleteBudgetByIdOutputPort {
    override fun execute(request: UUID) {
        budgetRepository.deleteById(request)
    }
}

