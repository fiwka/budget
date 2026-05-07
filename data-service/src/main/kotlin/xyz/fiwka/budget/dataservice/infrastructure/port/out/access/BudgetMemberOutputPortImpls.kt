package xyz.fiwka.budget.dataservice.infrastructure.port.out.access

import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.ListBudgetMembersOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleRequest
import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetRoleEntity
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetMemberProjection
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRoleRepository
import xyz.fiwka.budget.dataservice.infrastructure.repository.RoleRepository
import java.util.UUID

private fun BudgetMemberProjection.toDomain(): BudgetMember =
    BudgetMember(
        userId = getUserId(),
        username = getUsername(),
        email = getEmail(),
        role = BudgetRole.fromKey(getRoleKey()),
    )

class ListBudgetMembersOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
) : ListBudgetMembersOutputPort {
    override fun execute(request: UUID): List<BudgetMember> =
        budgetRoleRepository.findBudgetMembers(request).map(BudgetMemberProjection::toDomain)
}

class FindBudgetMemberOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
) : FindBudgetMemberOutputPort {
    override fun execute(request: FindBudgetMemberRequest): BudgetMember? =
        budgetRoleRepository.findBudgetMember(request.budgetId, request.userId)?.toDomain()
}

class UpsertBudgetMemberRoleOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
    private val roleRepository: RoleRepository,
) : UpsertBudgetMemberRoleOutputPort {
    override fun execute(request: UpsertBudgetMemberRoleRequest): BudgetMember {
        val role = roleRepository.findByRoleKey(request.role.key)
            ?: error("Role with key ${request.role.key} not found")

        val entity = budgetRoleRepository.findByBudgetIdAndUserId(request.budgetId, request.userId)
            ?: BudgetRoleEntity().apply {
                budgetId = request.budgetId
                userId = request.userId
            }

        entity.roleId = role.id
        budgetRoleRepository.save(entity)

        return budgetRoleRepository.findBudgetMember(request.budgetId, request.userId)?.toDomain()
            ?: error("Budget member was not saved")
    }
}

class DeleteBudgetMemberOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
) : DeleteBudgetMemberOutputPort {
    override fun execute(request: DeleteBudgetMemberRequest) {
        budgetRoleRepository.deleteByBudgetIdAndUserId(request.budgetId, request.userId)
    }
}
