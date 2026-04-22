package xyz.fiwka.budget.dataservice.application.service.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesRequest
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class ListBudgetCategoriesServiceTest {

    @Test
    fun `should return paged categories`() {
        val budgetId = UUID.randomUUID()

        val service = ListBudgetCategoriesService(
            budgetAccessGuard = budgetAccessGuardWithRole(BudgetRole.READER, budgetId),
            listBudgetCategoriesOutputPort = object : ListBudgetCategoriesOutputPort {
                override fun execute(request: ListBudgetCategoriesRequest): PageResult<Category> =
                    PageResult(
                        items = listOf(Category(UUID.randomUUID(), budgetId, "Food", true)),
                        page = request.page,
                        size = request.size,
                        totalElements = 1,
                        totalPages = 1,
                    )
            },
        )

        val response = service.execute(
            ListBudgetCategoriesCommand(
                budgetId = budgetId,
                actorLogin = "alex",
                page = 0,
                size = 20,
                name = "Food",
            )
        )

        assertEquals(1, response.categories.items.size)
        assertEquals("Food", response.categories.items.first().name)
    }

    private fun budgetAccessGuardWithRole(role: BudgetRole, budgetId: UUID): BudgetAccessGuard {
        val userId = UUID.randomUUID()

        val findUser = object : FindUserByLoginOutputPort {
            override fun execute(request: String): User? =
                if (request == "alex") User(userId, "alex", "alex@example.com", "hash") else null
        }

        val findRole = object : FindBudgetRoleForUserOutputPort {
            override fun execute(request: FindBudgetRoleForUserRequest): BudgetRole? =
                if (request.userId == userId && request.budgetId == budgetId) role else null
        }

        return BudgetAccessGuard(
            findUserByLoginOutputPort = findUser,
            findBudgetRoleForUserOutputPort = findRole,
            findCategoryByIdOutputPort = object : FindCategoryByIdOutputPort {
                override fun execute(request: UUID): Category? = null
            },
            findTransactionByIdOutputPort = object : FindTransactionByIdOutputPort {
                override fun execute(request: UUID): Transaction? = null
            },
        )
    }
}

