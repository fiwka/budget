package xyz.fiwka.budget.mcpserver.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import xyz.fiwka.budget.mcpserver.model.BudgetAnalyticsSummary
import java.util.UUID

@Component
class AnalyticsServiceClient(
    webClientBuilder: WebClient.Builder,
    @Value("\${app.services.analytics-service-url}") analyticsServiceUrl: String,
) {
    private val webClient: WebClient = webClientBuilder.baseUrl(analyticsServiceUrl).build()

    fun getMonthlySummary(budgetId: UUID, period: String?): BudgetAnalyticsSummary =
        webClient.get()
            .uri {
                val builder = it.path("/api/analytics/budget/monthly-summary")
                    .queryParam("budgetId", budgetId)
                if (!period.isNullOrBlank()) {
                    builder.queryParam("period", period)
                }
                builder.build()
            }
            .retrieve()
            .bodyToMono<BudgetAnalyticsSummary>()
            .block()!!
}

