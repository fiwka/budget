package xyz.fiwka.budget.dataservice.application.service.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsCommand
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.ListAccessibleBudgetsRequest
import xyz.fiwka.budget.dataservice.domain.budget.AccessibleBudget
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class ListAccessibleBudgetsServiceTest {

    @Test
    fun `should throw unauthorized when actor is unknown`() {
        val service = ListAccessibleBudgetsService(
            findUserByLoginOutputPort = object : FindUserByLoginOutputPort {
                override fun execute(request: String): User? = null
            },
            listAccessibleBudgetsOutputPort = object : ListAccessibleBudgetsOutputPort {
                override fun execute(request: ListAccessibleBudgetsRequest): PageResult<AccessibleBudget> =
                    PageResult(emptyList(), 0, 20, 0, 0)
            },
        )

        assertThrows(UnauthorizedException::class.java) {
            service.execute(ListAccessibleBudgetsCommand(actorLogin = "missing", page = 0, size = 20))
        }
    }

    @Test
    fun `should return accessible budgets with roles`() {
        val userId = UUID.randomUUID()
        val budgetId = UUID.randomUUID()

        val service = ListAccessibleBudgetsService(
            findUserByLoginOutputPort = object : FindUserByLoginOutputPort {
                override fun execute(request: String): User? =
                    if (request == "alex") User(userId, "alex", "alex@example.com", "hash") else null
            },
            listAccessibleBudgetsOutputPort = object : ListAccessibleBudgetsOutputPort {
                override fun execute(request: ListAccessibleBudgetsRequest): PageResult<AccessibleBudget> {
                    assertEquals(userId, request.userId)
                    return PageResult(
                        items = listOf(
                            AccessibleBudget(
                                budget = Budget(budgetId, "Home", "Main family budget"),
                                role = BudgetRole.OWNER,
                            )
                        ),
                        page = 0,
                        size = 20,
                        totalElements = 1,
                        totalPages = 1,
                    )
                }
            },
        )

        val response = service.execute(ListAccessibleBudgetsCommand(actorLogin = "alex", page = 0, size = 20))

        assertEquals(1, response.budgets.items.size)
        assertEquals(BudgetRole.OWNER, response.budgets.items.first().role)
    }
}

