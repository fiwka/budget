package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsRequest

class ListAccessibleBudgetsService(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val listAccessibleBudgetsOutputPort: ListAccessibleBudgetsOutputPort,
) : ListAccessibleBudgetsUseCase {

    override fun execute(request: ListAccessibleBudgetsCommand): ListAccessibleBudgetsResponse {
        val actor = findUserByLoginOutputPort.execute(request.actorLogin)
            ?: throw UnauthorizedException("User not found")

        return ListAccessibleBudgetsResponse(
            listAccessibleBudgetsOutputPort.execute(
                ListAccessibleBudgetsRequest(
                    userId = requireNotNull(actor.id),
                    page = request.page,
                    size = request.size,
                    budgetId = request.budgetId,
                    name = request.name,
                    description = request.description,
                    role = request.role,
                )
            )
        )
    }
}

