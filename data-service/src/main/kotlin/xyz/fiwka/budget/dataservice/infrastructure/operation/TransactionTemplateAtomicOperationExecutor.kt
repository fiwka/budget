package xyz.fiwka.budget.dataservice.infrastructure.operation

import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor

@Component
class TransactionTemplateAtomicOperationExecutor(
    private val transactionTemplate: TransactionTemplate
) : AtomicOperationExecutor {

    override fun <T> execute(operation: () -> T): T =
        transactionTemplate.execute { _ -> operation() }
}