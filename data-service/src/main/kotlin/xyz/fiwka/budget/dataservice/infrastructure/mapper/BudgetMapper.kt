package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.BudgetFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget.BudgetResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetEntity

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface BudgetMapper {

    fun toEntity(budget: Budget): BudgetEntity
    fun fromEntity(budgetEntity: BudgetEntity): Budget
    fun toDto(budget: Budget): BudgetResponse
    fun toCommand(request: BudgetFieldsRequest): CreateBudgetCommand
}