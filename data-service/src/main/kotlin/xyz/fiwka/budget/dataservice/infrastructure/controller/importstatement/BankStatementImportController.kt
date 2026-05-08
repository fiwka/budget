package xyz.fiwka.budget.dataservice.infrastructure.controller.importstatement

import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementFormat
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportedBankStatementTransaction
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.importstatement.ImportBankStatementResponseDto
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.importstatement.ImportedBankStatementTransactionDto
import java.time.DateTimeException
import java.time.ZoneId
import java.util.UUID

@RestController
@RequestMapping("/api/bank-statement-import")
class BankStatementImportController(
    private val importBankStatementUseCase: ImportBankStatementUseCase,
) {

    @PostMapping("/budget/{budgetId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importBankStatement(
        @PathVariable budgetId: UUID,
        @RequestParam file: MultipartFile,
        @RequestParam(defaultValue = "VTB") bank: BankStatementBank,
        @RequestParam(defaultValue = "PDF") format: BankStatementFormat,
        @RequestParam(required = false) zoneId: String?,
        authentication: Authentication,
    ): ImportBankStatementResponseDto {
        if (file.isEmpty) {
            throw BadRequestException("Bank statement file is empty")
        }

        val response = importBankStatementUseCase.execute(
            ImportBankStatementCommand(
                budgetId = budgetId,
                bank = bank,
                format = format,
                fileName = file.originalFilename,
                content = file.bytes,
                actorLogin = authentication.name,
                zoneId = parseZoneId(zoneId),
            )
        )

        return ImportBankStatementResponseDto(
            bank = response.bank,
            importedCount = response.importedCount,
            skippedCount = response.skippedCount,
            transactions = response.transactions.map { it.toDto() },
        )
    }

    private fun ImportedBankStatementTransaction.toDto(): ImportedBankStatementTransactionDto =
        ImportedBankStatementTransactionDto(
            transactionId = transactionId,
            categoryId = categoryId,
            completedDate = completedDate,
            amount = amount,
            merchantName = merchantName,
            description = description,
        )

    private fun parseZoneId(zoneId: String?): ZoneId =
        try {
            zoneId?.let { ZoneId.of(it) } ?: ZoneId.systemDefault()
        } catch (exception: DateTimeException) {
            throw BadRequestException("Invalid zoneId: $zoneId", exception)
        }
}
