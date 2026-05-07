package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.exception.type.ConflictException
import xyz.fiwka.budget.dataservice.application.exception.type.ForbiddenException
import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.BudgetMemberResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleUseCase
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.ListBudgetMembersOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import java.util.UUID

private fun BudgetRole.isPrivileged(): Boolean = this == BudgetRole.OWNER || this == BudgetRole.ADMIN

class BudgetMemberPolicy(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
) {
    fun requireActorMember(actorLogin: String, budgetId: UUID): BudgetMember {
        val actor = findUserByLoginOutputPort.execute(actorLogin)
            ?: throw UnauthorizedException("User not found")

        return findBudgetMemberOutputPort.execute(
            FindBudgetMemberRequest(
                budgetId = budgetId,
                userId = requireNotNull(actor.id),
            )
        ) ?: throw ForbiddenException("Insufficient permissions for budget: $budgetId")
    }

    fun requireCanManage(actor: BudgetMember, currentTargetRole: BudgetRole?, requestedRole: BudgetRole? = null) {
        if (!actor.role.canManage) {
            throw ForbiddenException("Insufficient permissions for budget members")
        }

        if (requestedRole == BudgetRole.OWNER) {
            throw BadRequestException("Budget owner role cannot be transferred")
        }

        if (currentTargetRole == BudgetRole.OWNER) {
            throw BadRequestException("Budget owner cannot be managed")
        }

        if (actor.role != BudgetRole.OWNER && currentTargetRole?.isPrivileged() == true) {
            throw ForbiddenException("Only budget owner can manage administrators")
        }

        if (actor.role != BudgetRole.OWNER && requestedRole?.isPrivileged() == true) {
            throw ForbiddenException("Only budget owner can assign administrator role")
        }
    }
}

class ListBudgetMembersService(
    private val listBudgetMembersOutputPort: ListBudgetMembersOutputPort,
    private val policy: BudgetMemberPolicy,
) : ListBudgetMembersUseCase {
    override fun execute(request: ListBudgetMembersCommand): ListBudgetMembersResponse {
        val actor = policy.requireActorMember(request.actorLogin, request.budgetId)
        policy.requireCanManage(actor, currentTargetRole = null)
        return ListBudgetMembersResponse(listBudgetMembersOutputPort.execute(request.budgetId))
    }
}

class AddBudgetMemberService(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
    private val upsertBudgetMemberRoleOutputPort: UpsertBudgetMemberRoleOutputPort,
    private val policy: BudgetMemberPolicy,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : AddBudgetMemberUseCase {
    override fun execute(request: AddBudgetMemberCommand): BudgetMemberResponse =
        atomicOperationExecutor.execute {
            val actor = policy.requireActorMember(request.actorLogin, request.budgetId)
            policy.requireCanManage(actor, currentTargetRole = null, requestedRole = request.role)

            val target = findUserByLoginOutputPort.execute(request.login)
                ?: throw NotFoundException("User not found: ${request.login}")
            val targetId = requireNotNull(target.id)

            if (actor.userId == targetId) {
                throw BadRequestException("Cannot manage yourself")
            }

            val existing = findBudgetMemberOutputPort.execute(FindBudgetMemberRequest(request.budgetId, targetId))
            if (existing != null) {
                throw ConflictException("User is already a budget member")
            }

            BudgetMemberResponse(
                upsertBudgetMemberRoleOutputPort.execute(
                    UpsertBudgetMemberRoleRequest(request.budgetId, targetId, request.role)
                )
            )
        }
}

class UpdateBudgetMemberRoleService(
    private val findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
    private val upsertBudgetMemberRoleOutputPort: UpsertBudgetMemberRoleOutputPort,
    private val policy: BudgetMemberPolicy,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : UpdateBudgetMemberRoleUseCase {
    override fun execute(request: UpdateBudgetMemberRoleCommand): BudgetMemberResponse =
        atomicOperationExecutor.execute {
            val actor = policy.requireActorMember(request.actorLogin, request.budgetId)
            val existing = findBudgetMemberOutputPort.execute(FindBudgetMemberRequest(request.budgetId, request.userId))
                ?: throw NotFoundException("Budget member not found")

            if (actor.userId == request.userId) {
                throw BadRequestException("Cannot manage yourself")
            }

            policy.requireCanManage(actor, currentTargetRole = existing.role, requestedRole = request.role)

            BudgetMemberResponse(
                upsertBudgetMemberRoleOutputPort.execute(
                    UpsertBudgetMemberRoleRequest(request.budgetId, request.userId, request.role)
                )
            )
        }
}

class RemoveBudgetMemberService(
    private val findBudgetMemberOutputPort: FindBudgetMemberOutputPort,
    private val deleteBudgetMemberOutputPort: DeleteBudgetMemberOutputPort,
    private val policy: BudgetMemberPolicy,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : RemoveBudgetMemberUseCase {
    override fun execute(request: RemoveBudgetMemberCommand): Unit =
        atomicOperationExecutor.execute {
            val actor = policy.requireActorMember(request.actorLogin, request.budgetId)
            val existing = findBudgetMemberOutputPort.execute(FindBudgetMemberRequest(request.budgetId, request.userId))
                ?: throw NotFoundException("Budget member not found")

            if (actor.userId == request.userId) {
                throw BadRequestException("Cannot remove yourself")
            }

            policy.requireCanManage(actor, currentTargetRole = existing.role)
            deleteBudgetMemberOutputPort.execute(DeleteBudgetMemberRequest(request.budgetId, request.userId))
        }
}
