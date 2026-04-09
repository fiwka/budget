package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.out.transaction.UpdateTransactionOutputPort
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository

@Component
class UpdateTransactionOutputPortImpl(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
) : UpdateTransactionOutputPort {
    override fun execute(request: Transaction): Transaction {
        val id = requireNotNull(request.id)
        val transactionEntity = transactionRepository.findById(id)
            .orElseThrow { TransactionNotFoundException(id) }

        transactionEntity.categoryId = request.categoryId
        transactionEntity.completedDate = request.completedDate
        transactionEntity.amount = request.amount
        transactionEntity.appendix = request.appendix

        return transactionMapper.fromEntity(transactionRepository.save(transactionEntity))
    }
}

