package xyz.fiwka.budget.dataservice.application.service.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryBelongsToAnotherBudgetException
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.DeleteCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.UpdateCategoryOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class CategoryCrudServicesTest {

    @Test
    fun `should read category when actor can view budget`() {
        val budgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val category = Category(categoryId, budgetId, "Food", true)
        val service = ReadCategoryService(
            findCategoryByIdOutputPort = findCategoryPort(category),
            budgetAccessGuard = budgetAccessGuard(BudgetRole.READER, budgetId),
        )

        val response = service.execute(ReadCategoryCommand(categoryId, "alex"))

        assertEquals(categoryId, response.category.id)
        assertEquals("Food", response.category.name)
    }

    @Test
    fun `should update category inside its budget`() {
        val budgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        var updatedCategory: Category? = null
        val service = UpdateCategoryService(
            findCategoryByIdOutputPort = findCategoryPort(Category(categoryId, budgetId, "Food", true)),
            findBudgetByIdOutputPort = findBudgetPort(Budget(budgetId, "Personal", "Main")),
            updateCategoryOutputPort = object : UpdateCategoryOutputPort {
                override fun execute(request: Category): Category {
                    updatedCategory = request
                    return request
                }
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, budgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        val response = service.execute(
            UpdateCategoryCommand(
                id = categoryId,
                budgetId = budgetId,
                name = "Groceries",
                isConsumption = false,
                actorLogin = "alex",
            )
        )

        assertEquals("Groceries", response.category.name)
        assertEquals(false, updatedCategory?.isConsumption)
    }

    @Test
    fun `should delete category after edit permission check`() {
        val budgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        var deletedCategoryId: UUID? = null
        val service = DeleteCategoryService(
            findCategoryByIdOutputPort = findCategoryPort(Category(categoryId, budgetId, "Food", true)),
            deleteCategoryByIdOutputPort = object : DeleteCategoryByIdOutputPort {
                override fun execute(request: UUID) {
                    deletedCategoryId = request
                }
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, budgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        service.execute(DeleteCategoryCommand(categoryId, "alex"))

        assertEquals(categoryId, deletedCategoryId)
    }

    @Test
    fun `should throw when category is missing during read`() {
        val service = ReadCategoryService(
            findCategoryByIdOutputPort = findCategoryPort(null),
            budgetAccessGuard = budgetAccessGuard(BudgetRole.READER, UUID.randomUUID()),
        )

        assertThrows(CategoryNotFoundException::class.java) {
            service.execute(ReadCategoryCommand(UUID.randomUUID(), "alex"))
        }
    }

    @Test
    fun `should throw when category belongs to another budget during update`() {
        val actualBudgetId = UUID.randomUUID()
        val requestedBudgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val service = UpdateCategoryService(
            findCategoryByIdOutputPort = findCategoryPort(Category(categoryId, actualBudgetId, "Food", true)),
            findBudgetByIdOutputPort = findBudgetPort(Budget(requestedBudgetId, "Other", "Other")),
            updateCategoryOutputPort = object : UpdateCategoryOutputPort {
                override fun execute(request: Category): Category = request
            },
            budgetAccessGuard = budgetAccessGuard(BudgetRole.EDITOR, actualBudgetId),
            atomicOperationExecutor = atomicOperationExecutor(),
        )

        assertThrows(CategoryBelongsToAnotherBudgetException::class.java) {
            service.execute(UpdateCategoryCommand(categoryId, requestedBudgetId, "Other", true, "alex"))
        }
    }

    private fun findCategoryPort(category: Category?): FindCategoryByIdOutputPort =
        object : FindCategoryByIdOutputPort {
            override fun execute(request: UUID): Category? = category?.takeIf { it.id == request }
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
