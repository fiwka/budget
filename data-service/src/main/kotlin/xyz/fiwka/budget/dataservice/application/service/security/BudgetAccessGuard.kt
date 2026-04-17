package xyz.fiwka.budget.dataservice.application.service.security

import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.type.ForbiddenException
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import java.util.UUID

class BudgetAccessGuard(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val findBudgetRoleForUserOutputPort: FindBudgetRoleForUserOutputPort,
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
) {

    fun requireBudgetPermission(actorLogin: String, budgetId: UUID, permission: BudgetPermission) {
        val user = findUserByLoginOutputPort.execute(actorLogin)
            ?: throw UnauthorizedException("User not found")

        val role = findBudgetRoleForUserOutputPort.execute(
            FindBudgetRoleForUserRequest(
                userId = requireNotNull(user.id),
                budgetId = budgetId,
            )
        )

        if (role == null || !role.hasPermission(permission)) {
            throw ForbiddenException("Insufficient permissions for budget: $budgetId")
        }
    }

    fun requireCategoryPermission(actorLogin: String, categoryId: UUID, permission: BudgetPermission): Category {
        val category = findCategoryByIdOutputPort.execute(categoryId)
            ?: throw CategoryNotFoundException(categoryId)

        requireBudgetPermission(actorLogin, category.budgetId, permission)
        return category
    }

    fun requireTransactionPermission(actorLogin: String, transactionId: UUID, permission: BudgetPermission): Transaction {
        val transaction = findTransactionByIdOutputPort.execute(transactionId)
            ?: throw TransactionNotFoundException(transactionId)

        val category = findCategoryByIdOutputPort.execute(transaction.categoryId)
            ?: throw CategoryNotFoundException(transaction.categoryId)

        requireBudgetPermission(actorLogin, category.budgetId, permission)
        return transaction
    }
}

