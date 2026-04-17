package xyz.fiwka.budget.dataservice.infrastructure.port.out.access

import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.SaveBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetRoleEntity
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRoleRepository
import xyz.fiwka.budget.dataservice.infrastructure.repository.RoleRepository

class SaveBudgetRoleForUserOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
    private val roleRepository: RoleRepository,
) : SaveBudgetRoleForUserOutputPort {

    override fun execute(request: SaveBudgetRoleForUserRequest) {
        val role = roleRepository.findByRoleKey(request.role.key)
            ?: error("Role with key ${request.role.key} not found")

        budgetRoleRepository.save(
            BudgetRoleEntity().apply {
                budgetId = request.budgetId
                userId = request.userId
                roleId = role.id
            }
        )
    }
}


