package xyz.fiwka.budget.application.operation

interface AtomicOperationExecutor {

    fun <T> execute(operation: () -> T): T
}