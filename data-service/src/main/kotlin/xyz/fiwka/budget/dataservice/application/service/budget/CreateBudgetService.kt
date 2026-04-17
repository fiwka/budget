package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.budget.Budget

class CreateBudgetService(
    private val saveBudgetOutputPort: SaveBudgetOutputPort,
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val saveBudgetRoleForUserOutputPort: SaveBudgetRoleForUserOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : CreateBudgetUseCase {
    override fun execute(request: CreateBudgetCommand): CreateBudgetResponse =
        atomicOperationExecutor.execute {
            val actor = findUserByLoginOutputPort.execute(request.actorLogin)
                ?: throw UnauthorizedException("User not found")

            val budget = saveBudgetOutputPort.execute(Budget(null, request.name, request.description))

            saveBudgetRoleForUserOutputPort.execute(
                SaveBudgetRoleForUserRequest(
                    budgetId = requireNotNull(budget.id),
                    userId = requireNotNull(actor.id),
                    role = BudgetRole.OWNER,
                )
            )

            CreateBudgetResponse(budget)
        }
}