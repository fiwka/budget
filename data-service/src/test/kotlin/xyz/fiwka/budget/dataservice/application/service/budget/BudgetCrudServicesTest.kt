package xyz.fiwka.budget.dataservice.application.service.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class BudgetCrudServicesTest {

    @Test
    fun `should read budget when actor can view it`() {
        val budgetId = UUID.randomUUID()
        val budget = Budget(budgetId, "Personal", "Main")
        val service = ReadBudgetService(
            findBudgetByIdOutputPort = findBudgetPort(budget),
            budgetAccessGuard = budgetAccessGuard(BudgetRole.READER, budgetId),
        )

        val response = service.execute(ReadBudgetCommand(budgetId, "alex"))

        assertEquals(budgetId, response.budget.id)
        assertEquals("Personal", response.budget.name)
    }

    @Test
    fun `should update budget after edit permission check`() {
        val budgetId = UUID.randomUUID()
        var updatedBudget: Budget? = null
        val service = UpdateBudgetService(
            findBudgetByIdOutputPort = findBudgetPort(Budget(budgetId, "Old", "Old description")),
            updateBudgetOutputPort = object : UpdateBudgetOutputPort {
                override fun execute(request: Budget): Budget {
                    updatedBudget = request
                    return request
                }
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, budgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        val response = service.execute(UpdateBudgetCommand(budgetId, "New", "New description", "alex"))

        assertEquals(budgetId, response.budget.id)
        assertEquals("New", response.budget.name)
        assertEquals("New description", updatedBudget?.description)
    }

    @Test
    fun `should delete budget after edit permission check`() {
        val budgetId = UUID.randomUUID()
        var deletedBudgetId: UUID? = null
        val service = DeleteBudgetService(
            findBudgetByIdOutputPort = findBudgetPort(Budget(budgetId, "Personal", "Main")),
            deleteBudgetByIdOutputPort = object : DeleteBudgetByIdOutputPort {
                override fun execute(request: UUID) {
                    deletedBudgetId = request
                }
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, budgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        service.execute(DeleteBudgetCommand(budgetId, "alex"))

        assertEquals(budgetId, deletedBudgetId)
    }

    @Test
    fun `should throw when budget is missing during update`() {
        val budgetId = UUID.randomUUID()
        val service = UpdateBudgetService(
            findBudgetByIdOutputPort = findBudgetPort(null),
            updateBudgetOutputPort = object : UpdateBudgetOutputPort {
                override fun execute(request: Budget): Budget = request
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, budgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        assertThrows(BudgetNotFoundException::class.java) {
            service.execute(UpdateBudgetCommand(budgetId, "New", "New description", "alex"))
        }
    }

    private fun findBudgetPort(budget: Budget?): FindBudgetByIdOutputPort =
        object : FindBudgetByIdOutputPort {
            override fun execute(request: UUID): Budget? = budget?.takeIf { it.id == request }
        }

    private fun budgetAccessGuard(role: BudgetRole, budgetId: UUID): BudgetAccessGuard {
        val userId = UUID.randomUUID()
        return BudgetAccessGuard(
            findUserByLoginOutputPort = object : FindUserByLoginOutputPort {
                override fun execute(request: String): User? =
                    if (request == "alex") User(userId, "alex", "alex@example.com", "hash") else null
            },
            findBudgetRoleForUserOutputPort = object : FindBudgetRoleForUserOutputPort {
                override fun execute(request: FindBudgetRoleForUserRequest): BudgetRole? =
                    if (request.userId == userId && request.budgetId == budgetId) role else null
            },
            findCategoryByIdOutputPort = object : FindCategoryByIdOutputPort {
                override fun execute(request: UUID): Category? = null
            },
            findTransactionByIdOutputPort = object : FindTransactionByIdOutputPort {
                override fun execute(request: UUID): Transaction? = null
            },
        )
    }

    private fun atomicOperationExecutor(): AtomicOperationExecutor =
        object : AtomicOperationExecutor {
            override fun <T> execute(operation: () -> T): T = operation()
        }
}
