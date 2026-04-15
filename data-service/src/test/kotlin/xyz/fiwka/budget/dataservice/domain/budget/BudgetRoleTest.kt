package xyz.fiwka.budget.dataservice.domain.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BudgetRoleTest {

    @Test
    fun `should resolve budget role by key`() {
        assertEquals(BudgetRole.READER, BudgetRole.fromKey(0))
        assertEquals(BudgetRole.EDITOR, BudgetRole.fromKey(1))
        assertEquals(BudgetRole.ADMIN, BudgetRole.fromKey(2))
        assertEquals(BudgetRole.OWNER, BudgetRole.fromKey(3))
    }
}

