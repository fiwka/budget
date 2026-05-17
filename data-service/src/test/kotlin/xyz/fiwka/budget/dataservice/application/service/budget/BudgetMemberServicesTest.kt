package xyz.fiwka.budget.dataservice.application.service.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.exception.type.ConflictException
import xyz.fiwka.budget.dataservice.application.exception.type.ForbiddenException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.DeleteBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetMemberRequest
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.UpsertBudgetMemberRoleRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class BudgetMemberServicesTest {

    private val budgetId = UUID.randomUUID()
    private val ownerId = UUID.randomUUID()
    private val adminId = UUID.randomUUID()
    private val viewerId = UUID.randomUUID()
    private val targetId = UUID.randomUUID()

    private val users = mapOf(
        "owner" to User(ownerId, "owner", "owner@example.com", "hash"),
        "admin" to User(adminId, "admin", "admin@example.com", "hash"),
        "viewer" to User(viewerId, "viewer", "viewer@example.com", "hash"),
        "target" to User(targetId, "target", "target@example.com", "hash"),
    )

    @Test
    fun `owner should add new budget member`() {
        val members = mutableMapOf(ownerId to member(ownerId, "owner", BudgetRole.OWNER))
        val upserts = mutableListOf<UpsertBudgetMemberRoleRequest>()
        val service = AddBudgetMemberService(
            findUserByLoginOutputPort = userPort(),
            findBudgetMemberOutputPort = memberPort(members),
            upsertBudgetMemberRoleOutputPort = object : UpsertBudgetMemberRoleOutputPort {
                override fun execute(request: UpsertBudgetMemberRoleRequest): BudgetMember {
                    upserts += request
                    val saved = member(request.userId, "target", request.role)
                    members[request.userId] = saved
                    return saved
                }
            },
            policy = BudgetMemberPolicy(userPort(), memberPort(members)),
            atomicOperationExecutor = inlineAtomic(),
        )

        val response = service.execute(
            AddBudgetMemberCommand(
                budgetId = budgetId,
                login = "target",
                role = BudgetRole.EDITOR,
                actorLogin = "owner",
            )
        )

        assertEquals(BudgetRole.EDITOR, response.member.role)
        assertEquals(listOf(UpsertBudgetMemberRoleRequest(budgetId, targetId, BudgetRole.EDITOR)), upserts)
    }

    @Test
    fun `admin should not assign privileged role`() {
        val members = mutableMapOf(adminId to member(adminId, "admin", BudgetRole.ADMIN))
        val service = AddBudgetMemberService(
            findUserByLoginOutputPort = userPort(),
            findBudgetMemberOutputPort = memberPort(members),
            upsertBudgetMemberRoleOutputPort = object : UpsertBudgetMemberRoleOutputPort {
                override fun execute(request: UpsertBudgetMemberRoleRequest): BudgetMember = error("Should not save")
            },
            policy = BudgetMemberPolicy(userPort(), memberPort(members)),
            atomicOperationExecutor = inlineAtomic(),
        )

        assertThrows(ForbiddenException::class.java) {
            service.execute(AddBudgetMemberCommand(budgetId, "target", BudgetRole.ADMIN, "admin"))
        }
    }

    @Test
    fun `should reject duplicate member`() {
        val members = mutableMapOf(
            ownerId to member(ownerId, "owner", BudgetRole.OWNER),
            targetId to member(targetId, "target", BudgetRole.READER),
        )
        val service = AddBudgetMemberService(
            findUserByLoginOutputPort = userPort(),
            findBudgetMemberOutputPort = memberPort(members),
            upsertBudgetMemberRoleOutputPort = object : UpsertBudgetMemberRoleOutputPort {
                override fun execute(request: UpsertBudgetMemberRoleRequest): BudgetMember = error("Should not save")
            },
            policy = BudgetMemberPolicy(userPort(), memberPort(members)),
            atomicOperationExecutor = inlineAtomic(),
        )

        assertThrows(ConflictException::class.java) {
            service.execute(AddBudgetMemberCommand(budgetId, "target", BudgetRole.EDITOR, "owner"))
        }
    }

    @Test
    fun `owner should update member role`() {
        val members = mutableMapOf(
            ownerId to member(ownerId, "owner", BudgetRole.OWNER),
            targetId to member(targetId, "target", BudgetRole.READER),
        )
        val service = UpdateBudgetMemberRoleService(
            findBudgetMemberOutputPort = memberPort(members),
            upsertBudgetMemberRoleOutputPort = object : UpsertBudgetMemberRoleOutputPort {
                override fun execute(request: UpsertBudgetMemberRoleRequest): BudgetMember {
                    val saved = member(request.userId, "target", request.role)
                    members[request.userId] = saved
                    return saved
                }
            },
            policy = BudgetMemberPolicy(userPort(), memberPort(members)),
            atomicOperationExecutor = inlineAtomic(),
        )

        val response = service.execute(UpdateBudgetMemberRoleCommand(budgetId, targetId, BudgetRole.EDITOR, "owner"))

        assertEquals(BudgetRole.EDITOR, response.member.role)
    }

    @Test
    fun `should reject self removal`() {
        val members = mutableMapOf(ownerId to member(ownerId, "owner", BudgetRole.OWNER))
        val service = RemoveBudgetMemberService(
            findBudgetMemberOutputPort = memberPort(members),
            deleteBudgetMemberOutputPort = object : DeleteBudgetMemberOutputPort {
                override fun execute(request: DeleteBudgetMemberRequest) = error("Should not delete")
            },
            policy = BudgetMemberPolicy(userPort(), memberPort(members)),
            atomicOperationExecutor = inlineAtomic(),
        )

        assertThrows(BadRequestException::class.java) {
            service.execute(RemoveBudgetMemberCommand(budgetId, ownerId, "owner"))
        }
    }

    private fun userPort() = object : FindUserByLoginOutputPort {
        override fun execute(request: String): User? = users[request]
    }

    private fun memberPort(members: Map<UUID, BudgetMember>) = object : FindBudgetMemberOutputPort {
        override fun execute(request: FindBudgetMemberRequest): BudgetMember? = members[request.userId]
    }

    private fun member(userId: UUID, username: String, role: BudgetRole) =
        BudgetMember(userId, username, "$username@example.com", role)

    private fun inlineAtomic() = object : AtomicOperationExecutor {
        override fun <T> execute(operation: () -> T): T = operation()
    }
}
