package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort

class DeleteTransactionService(
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
    private val deleteTransactionByIdOutputPort: DeleteTransactionByIdOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : DeleteTransactionUseCase {
    override fun execute(request: DeleteTransactionCommand) {
        atomicOperationExecutor.execute {
            findTransactionByIdOutputPort.execute(request.id)
                ?: throw TransactionNotFoundException(request.id)

            deleteTransactionByIdOutputPort.execute(request.id)
        }
    }
}

