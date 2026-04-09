package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository
import java.util.UUID

@Component
class DeleteTransactionByIdOutputPortImpl(
    private val transactionRepository: TransactionRepository,
) : DeleteTransactionByIdOutputPort {
    override fun execute(request: UUID) {
        transactionRepository.deleteById(request)
    }
}

