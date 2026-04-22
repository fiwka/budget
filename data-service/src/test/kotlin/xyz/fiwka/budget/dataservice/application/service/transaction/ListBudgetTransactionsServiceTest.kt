package xyz.fiwka.budget.dataservice.application.service.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsRequest
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.domain.user.User
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class ListBudgetTransactionsServiceTest {

    @Test
    fun `should fail when amount range is invalid`() {
        val service = ListBudgetTransactionsService(
            budgetAccessGuard = budgetAccessGuardWithRole(BudgetRole.READER),
            listBudgetTransactionsOutputPort = object : ListBudgetTransactionsOutputPort {
                override fun execute(request: ListBudgetTransactionsRequest): PageResult<Transaction> =
                    PageResult(emptyList(), 0, 20, 0, 0)
            },
        )

        assertThrows(BadRequestException::class.java) {
            service.execute(
                ListBudgetTransactionsCommand(
                    budgetId = UUID.randomUUID(),
                    actorLogin = "alex",
                    page = 0,
                    size = 20,
                    amountFrom = BigDecimal("100"),
                    amountTo = BigDecimal("10"),
                )
            )
        }
    }

    @Test
    fun `should return paged transactions for user with view permission`() {
        val budgetId = UUID.randomUUID()
        var capturedRequest: ListBudgetTransactionsRequest? = null

        val service = ListBudgetTransactionsService(
            budgetAccessGuard = budgetAccessGuardWithRole(BudgetRole.READER, budgetId),
            listBudgetTransactionsOutputPort = object : ListBudgetTransactionsOutputPort {
                override fun execute(request: ListBudgetTransactionsRequest): PageResult<Transaction> {
                    capturedRequest = request
                    return PageResult(
                        items = listOf(
                            Transaction(
                                id = UUID.randomUUID(),
                                categoryId = UUID.randomUUID(),
                                completedDate = Instant.parse("2026-01-01T00:00:00Z"),
                                amount = BigDecimal("50.00"),
                                appendix = null,
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

        val response = service.execute(
            ListBudgetTransactionsCommand(
                budgetId = budgetId,
                actorLogin = "alex",
                page = 0,
                size = 20,
            )
        )

        assertEquals(budgetId, capturedRequest!!.budgetId)
        assertEquals(1, response.transactions.items.size)
        assertEquals(1, response.transactions.totalElements)
    }

    private fun budgetAccessGuardWithRole(role: BudgetRole, budgetId: UUID = UUID.randomUUID()): BudgetAccessGuard {
        val userId = UUID.randomUUID()

        val findUser = object : FindUserByLoginOutputPort {
            override fun execute(request: String): User? =
                if (request == "alex") User(userId, "alex", "alex@example.com", "hash") else null
        }

        val findRole = object : FindBudgetRoleForUserOutputPort {
            override fun execute(request: FindBudgetRoleForUserRequest): BudgetRole? =
                if (request.userId == userId && request.budgetId == budgetId) role else null
        }

        val findCategory = object : FindCategoryByIdOutputPort {
            override fun execute(request: UUID): Category? = null
        }

        val findTransaction = object : FindTransactionByIdOutputPort {
            override fun execute(request: UUID): Transaction? = null
        }

        return BudgetAccessGuard(findUser, findRole, findCategory, findTransaction)
    }
}

