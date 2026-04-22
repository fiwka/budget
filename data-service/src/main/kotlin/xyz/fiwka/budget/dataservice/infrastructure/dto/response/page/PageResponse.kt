package xyz.fiwka.budget.dataservice.infrastructure.dto.response.page

data class PageResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

