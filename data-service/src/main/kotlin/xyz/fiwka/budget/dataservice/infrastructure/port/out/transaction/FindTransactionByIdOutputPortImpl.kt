package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository
import java.util.UUID

@Component
class FindTransactionByIdOutputPortImpl(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
) : FindTransactionByIdOutputPort {
    override fun execute(request: UUID): Transaction? =
        transactionRepository.findById(request)
            .map(transactionMapper::fromEntity)
            .orElse(null)
}

