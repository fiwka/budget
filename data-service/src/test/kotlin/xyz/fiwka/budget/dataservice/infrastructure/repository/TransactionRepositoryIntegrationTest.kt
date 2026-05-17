package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import xyz.fiwka.budget.dataservice.infrastructure.PostgresIntegrationTest
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetEntity
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity
import xyz.fiwka.budget.dataservice.infrastructure.entity.TransactionEntity
import java.math.BigDecimal
import java.time.Instant

@DataJpaTest
class TransactionRepositoryIntegrationTest @Autowired constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `should filter budget transactions by category amount date and appendix`() {
        val budget = budgetRepository.save(budget("Home"))
        val food = categoryRepository.save(category(budget.id, "Food", true))
        val salary = categoryRepository.save(category(budget.id, "Salary", false))
        val grocery = transactionRepository.save(
            transaction(food.id, "2026-01-10T10:00:00Z", "42.50", mapOf("merchant" to "Market"))
        )
        transactionRepository.save(transaction(food.id, "2026-01-11T10:00:00Z", "12.00", mapOf("merchant" to "Cafe")))
        transactionRepository.save(transaction(salary.id, "2026-01-12T10:00:00Z", "1000.00", null))

        val result = transactionRepository.findBudgetTransactions(
            budgetId = budget.id,
            id = null,
            categoryId = food.id,
            completedDateFrom = Instant.parse("2026-01-01T00:00:00Z"),
            completedDateTo = Instant.parse("2026-01-31T23:59:59Z"),
            amountFrom = BigDecimal("40.00"),
            amountTo = BigDecimal("50.00"),
            appendixContains = "market",
            pageable = PageRequest.of(0, 10),
        )

        assertEquals(1, result.totalElements)
        assertEquals(grocery.id, result.content.single().id)
    }

    @Test
    fun `should filter budget categories by name and consumption flag`() {
        val budget = budgetRepository.save(budget("Home"))
        categoryRepository.save(category(budget.id, "Food", true))
        categoryRepository.save(category(budget.id, "Fuel", true))
        categoryRepository.save(category(budget.id, "Salary", false))

        val result = categoryRepository.findBudgetCategories(
            budgetId = budget.id,
            id = null,
            name = "f",
            isConsumption = true,
            pageable = PageRequest.of(0, 10),
        )

        assertEquals(listOf("Food", "Fuel"), result.content.map { it.name })
    }

    private fun budget(name: String) = BudgetEntity().apply {
        this.name = name
        description = "$name budget"
    }

    private fun category(budgetId: java.util.UUID, name: String, isConsumption: Boolean) = CategoryEntity().apply {
        this.budgetId = budgetId
        this.name = name
        this.isConsumption = isConsumption
    }

    private fun transaction(
        categoryId: java.util.UUID,
        completedDate: String,
        amount: String,
        appendix: Map<String, Any>?,
    ) = TransactionEntity().apply {
        this.categoryId = categoryId
        this.completedDate = Instant.parse(completedDate)
        this.amount = BigDecimal(amount)
        this.appendix = appendix
    }
}
