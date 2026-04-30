package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.transaction.TransactionResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.TransactionEntity

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface TransactionMapper {

    @Mapping(target = "version", ignore = true)
    fun toEntity(transaction: Transaction): TransactionEntity
    fun fromEntity(transactionEntity: TransactionEntity): Transaction
    fun toDto(transaction: Transaction): TransactionResponse
}

