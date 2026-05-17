package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.PageRequest
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.infrastructure.PostgresIntegrationTest
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetEntity
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetRoleEntity
import xyz.fiwka.budget.dataservice.infrastructure.entity.RoleEntity
import xyz.fiwka.budget.dataservice.infrastructure.entity.UserEntity

@DataJpaTest
class BudgetRoleRepositoryIntegrationTest @Autowired constructor(
    private val budgetRepository: BudgetRepository,
    private val budgetRoleRepository: BudgetRoleRepository,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `should find accessible budgets with filters and role projection`() {
        val ownerRole = role(BudgetRole.OWNER)
        val readerRole = role(BudgetRole.READER)
        roleRepository.saveAll(listOf(ownerRole, readerRole))
        val user = userRepository.save(user("alex", "alex@example.com"))
        val home = budgetRepository.save(budget("Home", "Family budget"))
        val travel = budgetRepository.save(budget("Travel", "Vacation"))
        budgetRoleRepository.saveAll(
            listOf(
                budgetRole(home.id, user.id, ownerRole.id),
                budgetRole(travel.id, user.id, readerRole.id),
            )
        )

        val result = budgetRoleRepository.findAccessibleBudgets(
            userId = user.id,
            budgetId = null,
            name = "ho",
            description = null,
            roleKey = BudgetRole.OWNER.key,
            pageable = PageRequest.of(0, 10),
        )

        assertEquals(1, result.totalElements)
        assertEquals(home.id, result.content.single().getBudgetId())
        assertEquals(BudgetRole.OWNER.key, result.content.single().getRoleKey())
    }

    @Test
    fun `should list budget members ordered by role and username`() {
        val ownerRole = roleRepository.save(role(BudgetRole.OWNER))
        val editorRole = roleRepository.save(role(BudgetRole.EDITOR))
        val budget = budgetRepository.save(budget("Shared", "Team budget"))
        val alex = userRepository.save(user("alex", "alex@example.com"))
        val zoe = userRepository.save(user("zoe", "zoe@example.com"))
        budgetRoleRepository.saveAll(
            listOf(
                budgetRole(budget.id, zoe.id, editorRole.id),
                budgetRole(budget.id, alex.id, ownerRole.id),
            )
        )

        val members = budgetRoleRepository.findBudgetMembers(budget.id)

        assertEquals(listOf("alex", "zoe"), members.map { it.getUsername() })
        assertEquals(BudgetRole.OWNER.key, members.first().getRoleKey())
    }

    private fun budget(name: String, description: String) = BudgetEntity().apply {
        this.name = name
        this.description = description
    }

    private fun user(username: String, email: String) = UserEntity().apply {
        this.username = username
        this.email = email
        this.passwordHash = "hash"
    }

    private fun role(role: BudgetRole) = RoleEntity().apply {
        roleKey = role.key
        canView = role.canView
        canEdit = role.canEdit
        canManage = role.canManage
    }

    private fun budgetRole(budgetId: java.util.UUID, userId: java.util.UUID, roleId: java.util.UUID) =
        BudgetRoleEntity().apply {
            this.budgetId = budgetId
            this.userId = userId
            this.roleId = roleId
        }
}
