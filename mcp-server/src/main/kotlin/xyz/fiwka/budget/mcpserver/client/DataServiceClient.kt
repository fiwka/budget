package xyz.fiwka.budget.mcpserver.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import xyz.fiwka.budget.mcpserver.model.CategoryResponse
import xyz.fiwka.budget.mcpserver.model.PageResponse
import xyz.fiwka.budget.mcpserver.model.TransactionResponse
import xyz.fiwka.budget.mcpserver.model.UserInfoResponse
import java.util.UUID

@Component
class DataServiceClient(
    webClientBuilder: WebClient.Builder,
    @Value("\${app.services.data-service-url}") dataServiceUrl: String,
) {
    private val webClient: WebClient = webClientBuilder.baseUrl(dataServiceUrl).build()

    fun getCurrentUser(accessToken: String): UserInfoResponse =
        webClient.get()
            .uri("/api/user/info")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .bodyToMono<UserInfoResponse>()
            .block()!!

    fun listBudgetTransactions(
        budgetId: UUID,
        accessToken: String,
        page: Int,
        size: Int,
    ): PageResponse<TransactionResponse> =
        webClient.get()
            .uri {
                it.path("/api/transaction/budget/{budgetId}")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(budgetId)
            }
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<PageResponse<TransactionResponse>>() {})
            .block()!!

    fun listBudgetCategories(
        budgetId: UUID,
        accessToken: String,
        page: Int,
        size: Int,
    ): PageResponse<CategoryResponse> =
        webClient.get()
            .uri {
                it.path("/api/category/budget/{budgetId}")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(budgetId)
            }
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<PageResponse<CategoryResponse>>() {})
            .block()!!

    fun readCategory(categoryId: UUID, accessToken: String): CategoryResponse =
        webClient.get()
            .uri("/api/category/{categoryId}", categoryId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .bodyToMono<CategoryResponse>()
            .block()!!
}
