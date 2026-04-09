package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository

@Component
class SaveTransactionOutputPortImpl(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
) : SaveTransactionOutputPort {
    override fun execute(request: Transaction): Transaction =
        transactionMapper.fromEntity(
            transactionRepository.save(transactionMapper.toEntity(request))
        )
}

