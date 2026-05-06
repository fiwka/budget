package xyz.fiwka.budget.mcpserver.tool

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import xyz.fiwka.budget.mcpserver.client.AnalyticsServiceClient
import xyz.fiwka.budget.mcpserver.client.DataServiceClient
import xyz.fiwka.budget.mcpserver.model.BudgetAnalyticsSummary
import xyz.fiwka.budget.mcpserver.model.CategoryResponse
import xyz.fiwka.budget.mcpserver.model.PageResponse
import xyz.fiwka.budget.mcpserver.model.TransactionResponse
import xyz.fiwka.budget.mcpserver.model.UserInfoResponse
import java.util.UUID

@Service
class BudgetMcpTools(
    private val dataServiceClient: DataServiceClient,
    private val analyticsServiceClient: AnalyticsServiceClient,
) {

    @Tool(description = "Validate access token via data-service and return current user info")
    fun getCurrentUser(accessToken: String): UserInfoResponse =
        dataServiceClient.getCurrentUser(accessToken)

    @Tool(description = "Get transactions page for a budget. Requires access token")
    fun listBudgetTransactions(
        budgetId: UUID,
        accessToken: String,
        page: Int = 0,
        size: Int = 20,
    ): PageResponse<TransactionResponse> =
        dataServiceClient.listBudgetTransactions(
            budgetId = budgetId,
            accessToken = accessToken,
            page = page,
            size = size,
        )

    @Tool(description = "Get categories page for a budget. Requires access token")
    fun listBudgetCategories(
        budgetId: UUID,
        accessToken: String,
        page: Int = 0,
        size: Int = 20,
    ): PageResponse<CategoryResponse> =
        dataServiceClient.listBudgetCategories(
            budgetId = budgetId,
            accessToken = accessToken,
            page = page,
            size = size,
        )

    @Tool(description = "Get one budget category by category id. Use this to resolve categoryId values to human-readable category names. Requires access token")
    fun getCategoryById(
        categoryId: UUID,
        accessToken: String,
    ): CategoryResponse =
        dataServiceClient.readCategory(
            categoryId = categoryId,
            accessToken = accessToken,
        )

    @Tool(description = "Get monthly budget analytics from analytics-service. Optional period format: YYYY-MM")
    fun getBudgetMonthlySummary(
        budgetId: UUID,
        period: String? = null,
    ): BudgetAnalyticsSummary = analyticsServiceClient.getMonthlySummary(budgetId, period)
}
