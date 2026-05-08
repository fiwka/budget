package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ExistsImportedTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ExistsImportedTransactionRequest
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository

@Component
class ExistsImportedTransactionOutputPortImpl(
    private val transactionRepository: TransactionRepository,
) : ExistsImportedTransactionOutputPort {

    override fun execute(request: ExistsImportedTransactionRequest): Boolean =
        transactionRepository.existsImportedTransaction(
            budgetId = request.budgetId,
            importFingerprint = request.importFingerprint,
        )
}
