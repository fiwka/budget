package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.category.CategoryResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface CategoryMapper {

    fun toEntity(category: Category): CategoryEntity
    fun fromEntity(categoryEntity: CategoryEntity): Category
    fun toDto(category: Category): CategoryResponse
}

