package xyz.fiwka.budget.dataservice.infrastructure.controller.budget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.AddBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.BudgetMemberResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListBudgetMembersUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.RemoveBudgetMemberUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetMemberRoleUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.domain.budget.AccessibleBudget
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.AccessibleBudgetsQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.AddBudgetMemberRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.BudgetFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.UpdateBudgetMemberRoleRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget.BudgetResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetEntity
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import java.util.UUID

class BudgetControllerTest {

    @Test
    fun `should create read update delete and list budgets`() {
        val budgetId = UUID.randomUUID()
        var created: CreateBudgetCommand? = null
        var read: ReadBudgetCommand? = null
        var updated: UpdateBudgetCommand? = null
        var deleted: DeleteBudgetCommand? = null
        var listed: ListAccessibleBudgetsCommand? = null
        val controller = controller(
            createBudgetUseCase = object : CreateBudgetUseCase {
                override fun execute(request: CreateBudgetCommand): CreateBudgetResponse {
                    created = request
                    return CreateBudgetResponse(Budget(budgetId, request.name, request.description))
                }
            },
            readBudgetUseCase = object : ReadBudgetUseCase {
                override fun execute(request: ReadBudgetCommand): ReadBudgetResponse {
                    read = request
                    return ReadBudgetResponse(Budget(request.id, "Personal", "Main"))
                }
            },
            updateBudgetUseCase = object : UpdateBudgetUseCase {
                override fun execute(request: UpdateBudgetCommand): UpdateBudgetResponse {
                    updated = request
                    return UpdateBudgetResponse(Budget(request.id, request.name, request.description))
                }
            },
            deleteBudgetUseCase = object : DeleteBudgetUseCase {
                override fun execute(request: DeleteBudgetCommand) {
                    deleted = request
                }
            },
            listAccessibleBudgetsUseCase = object : ListAccessibleBudgetsUseCase {
                override fun execute(request: ListAccessibleBudgetsCommand): ListAccessibleBudgetsResponse {
                    listed = request
                    return ListAccessibleBudgetsResponse(
                        PageResult(
                            items = listOf(AccessibleBudget(Budget(budgetId, "Personal", "Main"), BudgetRole.OWNER)),
                            page = request.page,
                            size = request.size,
                            totalElements = 1,
                            totalPages = 1,
                        )
                    )
                }
            },
        )
        val auth = TestingAuthenticationToken("alex", "credentials")

        assertEquals(budgetId, controller.createBudget(BudgetFieldsRequest("Personal", "Main"), auth).id)
        assertEquals("Personal", controller.readBudget(budgetId, auth).name)
        assertEquals("Updated", controller.updateBudget(budgetId, BudgetFieldsRequest("Updated", "Next"), auth).name)
        controller.deleteBudget(budgetId, auth)
        val page = controller.listAccessibleBudgets(AccessibleBudgetsQueryRequest(name = "Per", page = 1, size = 10), auth)

        assertEquals(CreateBudgetCommand("Personal", "Main", "alex"), created)
        assertEquals(ReadBudgetCommand(budgetId, "alex"), read)
        assertEquals(UpdateBudgetCommand(budgetId, "Updated", "Next", "alex"), updated)
        assertEquals(DeleteBudgetCommand(budgetId, "alex"), deleted)
        assertEquals("alex", listed?.actorLogin)
        assertEquals(1, page.items.size)
        assertEquals(BudgetRole.OWNER, page.items.first().role)
    }

    @Test
    fun `should manage budget members`() {
        val budgetId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        var listCommand: ListBudgetMembersCommand? = null
        var addCommand: AddBudgetMemberCommand? = null
        var updateCommand: UpdateBudgetMemberRoleCommand? = null
        var removeCommand: RemoveBudgetMemberCommand? = null
        val member = BudgetMember(userId, "maria", "maria@example.com", BudgetRole.EDITOR)
        val controller = controller(
            listBudgetMembersUseCase = object : ListBudgetMembersUseCase {
                override fun execute(request: ListBudgetMembersCommand): ListBudgetMembersResponse {
                    listCommand = request
                    return ListBudgetMembersResponse(listOf(member))
                }
            },
            addBudgetMemberUseCase = object : AddBudgetMemberUseCase {
                override fun execute(request: AddBudgetMemberCommand): BudgetMemberResponse {
                    addCommand = request
                    return BudgetMemberResponse(member.copy(role = request.role))
                }
            },
            updateBudgetMemberRoleUseCase = object : UpdateBudgetMemberRoleUseCase {
                override fun execute(request: UpdateBudgetMemberRoleCommand): BudgetMemberResponse {
                    updateCommand = request
                    return BudgetMemberResponse(member.copy(role = request.role))
                }
            },
            removeBudgetMemberUseCase = object : RemoveBudgetMemberUseCase {
                override fun execute(request: RemoveBudgetMemberCommand) {
                    removeCommand = request
                }
            },
        )
        val auth = TestingAuthenticationToken("alex", "credentials")

        val members = controller.listMembers(budgetId, auth)
        val added = controller.addMember(budgetId, AddBudgetMemberRequest("maria", BudgetRole.ADMIN), auth)
        val updated = controller.updateMemberRole(budgetId, userId, UpdateBudgetMemberRoleRequest(BudgetRole.READER), auth)
        controller.removeMember(budgetId, userId, auth)

        assertEquals(ListBudgetMembersCommand(budgetId, "alex"), listCommand)
        assertEquals(AddBudgetMemberCommand(budgetId, "maria", BudgetRole.ADMIN, "alex"), addCommand)
        assertEquals(UpdateBudgetMemberRoleCommand(budgetId, userId, BudgetRole.READER, "alex"), updateCommand)
        assertEquals(RemoveBudgetMemberCommand(budgetId, userId, "alex"), removeCommand)
        assertEquals("maria", members.first().username)
        assertEquals(BudgetRole.ADMIN, added.role)
        assertEquals(BudgetRole.READER, updated.role)
    }

    private fun controller(
        createBudgetUseCase: CreateBudgetUseCase = object : CreateBudgetUseCase {
            override fun execute(request: CreateBudgetCommand): CreateBudgetResponse =
                CreateBudgetResponse(Budget(UUID.randomUUID(), request.name, request.description))
        },
        readBudgetUseCase: ReadBudgetUseCase = object : ReadBudgetUseCase {
            override fun execute(request: ReadBudgetCommand): ReadBudgetResponse =
                ReadBudgetResponse(Budget(request.id, "Personal", "Main"))
        },
        updateBudgetUseCase: UpdateBudgetUseCase = object : UpdateBudgetUseCase {
            override fun execute(request: UpdateBudgetCommand): UpdateBudgetResponse =
                UpdateBudgetResponse(Budget(request.id, request.name, request.description))
        },
        deleteBudgetUseCase: DeleteBudgetUseCase = object : DeleteBudgetUseCase {
            override fun execute(request: DeleteBudgetCommand) = Unit
        },
        listAccessibleBudgetsUseCase: ListAccessibleBudgetsUseCase = object : ListAccessibleBudgetsUseCase {
            override fun execute(request: ListAccessibleBudgetsCommand): ListAccessibleBudgetsResponse =
                ListAccessibleBudgetsResponse(PageResult(emptyList(), request.page, request.size, 0, 0))
        },
        listBudgetMembersUseCase: ListBudgetMembersUseCase = object : ListBudgetMembersUseCase {
            override fun execute(request: ListBudgetMembersCommand): ListBudgetMembersResponse =
                ListBudgetMembersResponse(emptyList())
        },
        addBudgetMemberUseCase: AddBudgetMemberUseCase = object : AddBudgetMemberUseCase {
            override fun execute(request: AddBudgetMemberCommand): BudgetMemberResponse =
                BudgetMemberResponse(BudgetMember(UUID.randomUUID(), request.login, "${request.login}@example.com", request.role))
        },
        updateBudgetMemberRoleUseCase: UpdateBudgetMemberRoleUseCase = object : UpdateBudgetMemberRoleUseCase {
            override fun execute(request: UpdateBudgetMemberRoleCommand): BudgetMemberResponse =
                BudgetMemberResponse(BudgetMember(request.userId, "maria", "maria@example.com", request.role))
        },
        removeBudgetMemberUseCase: RemoveBudgetMemberUseCase = object : RemoveBudgetMemberUseCase {
            override fun execute(request: RemoveBudgetMemberCommand) = Unit
        },
    ): BudgetController =
        BudgetController(
            budgetMapper = object : BudgetMapper {
                override fun toEntity(budget: Budget): BudgetEntity = BudgetEntity()
                override fun fromEntity(budgetEntity: BudgetEntity): Budget =
                    Budget(budgetEntity.id, budgetEntity.name, budgetEntity.description)
                override fun toDto(budget: Budget): BudgetResponse =
                    BudgetResponse(requireNotNull(budget.id), budget.name, budget.description)
            },
            createBudgetUseCase = createBudgetUseCase,
            readBudgetUseCase = readBudgetUseCase,
            updateBudgetUseCase = updateBudgetUseCase,
            deleteBudgetUseCase = deleteBudgetUseCase,
            listAccessibleBudgetsUseCase = listAccessibleBudgetsUseCase,
            listBudgetMembersUseCase = listBudgetMembersUseCase,
            addBudgetMemberUseCase = addBudgetMemberUseCase,
            updateBudgetMemberRoleUseCase = updateBudgetMemberRoleUseCase,
            removeBudgetMemberUseCase = removeBudgetMemberUseCase,
        )
}
