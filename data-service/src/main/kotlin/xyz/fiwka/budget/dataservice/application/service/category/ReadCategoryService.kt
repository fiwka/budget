package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort

class ReadCategoryService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort
) : ReadCategoryUseCase {
    override fun execute(request: ReadCategoryCommand): ReadCategoryResponse =
        ReadCategoryResponse(
            findCategoryByIdOutputPort.execute(request.id)
                ?: throw CategoryNotFoundException(request.id)
        )
}

