package xyz.fiwka.budget.dataservice.application.service.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class CreateBudgetServiceTest {

    @Test
    fun `should assign owner role to budget creator`() {
        val actorId = UUID.randomUUID()
        val budgetId = UUID.randomUUID()

        val findUserPort = object : FindUserByLoginOutputPort {
            override fun execute(request: String): User? =
                if (request == "alex") User(actorId, "alex", "alex@example.com", "hash") else null
        }

        val saveBudgetPort = object : SaveBudgetOutputPort {
            override fun execute(request: Budget): Budget =
                Budget(budgetId, request.name, request.description)
        }

        var savedRoleRequest: SaveBudgetRoleForUserRequest? = null
        val saveBudgetRolePort = object : SaveBudgetRoleForUserOutputPort {
            override fun execute(request: SaveBudgetRoleForUserRequest) {
                savedRoleRequest = request
            }
        }

        val service = CreateBudgetService(
            saveBudgetOutputPort = saveBudgetPort,
            findUserByLoginOutputPort = findUserPort,
            saveBudgetRoleForUserOutputPort = saveBudgetRolePort,
            atomicOperationExecutor = object : AtomicOperationExecutor {
                override fun <T> execute(operation: () -> T): T = operation()
            },
        )

        val response = service.execute(
            CreateBudgetCommand(
                name = "Personal",
                description = "Main budget",
                actorLogin = "alex",
            )
        )

        assertEquals(budgetId, response.budget.id)
        assertNotNull(savedRoleRequest)
        val roleRequest = requireNotNull(savedRoleRequest)
        assertEquals(budgetId, roleRequest.budgetId)
        assertEquals(actorId, roleRequest.userId)
        assertEquals(BudgetRole.OWNER, roleRequest.role)
    }
}


