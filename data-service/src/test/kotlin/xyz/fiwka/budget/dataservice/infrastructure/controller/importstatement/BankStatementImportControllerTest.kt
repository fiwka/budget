package xyz.fiwka.budget.dataservice.infrastructure.controller.importstatement

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.TestingAuthenticationToken
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementFormat
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportedBankStatementTransaction
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class BankStatementImportControllerTest {

    @Test
    fun `should import bank statement`() {
        val budgetId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val content = byteArrayOf(1, 2, 3)
        var command: ImportBankStatementCommand? = null
        val controller = BankStatementImportController(
            importBankStatementUseCase = object : ImportBankStatementUseCase {
                override fun execute(request: ImportBankStatementCommand): ImportBankStatementResponse {
                    command = request
                    return ImportBankStatementResponse(
                        bank = request.bank,
                        importedCount = 1,
                        skippedCount = 2,
                        transactions = listOf(
                            ImportedBankStatementTransaction(
                                transactionId = transactionId,
                                categoryId = categoryId,
                                completedDate = Instant.parse("2026-01-05T10:00:00Z"),
                                amount = BigDecimal("10.00"),
                                merchantName = "Shop",
                                description = "Purchase",
                            )
                        ),
                    )
                }
            }
        )

        val response = controller.importBankStatement(
            budgetId = budgetId,
            file = MockMultipartFile("file", "statement.pdf", "application/pdf", content),
            bank = BankStatementBank.VTB,
            format = BankStatementFormat.PDF,
            zoneId = "UTC",
            authentication = TestingAuthenticationToken("alex", "credentials"),
        )

        val captured = requireNotNull(command)
        assertEquals(budgetId, captured.budgetId)
        assertArrayEquals(content, captured.content)
        assertEquals("statement.pdf", captured.fileName)
        assertEquals("alex", captured.actorLogin)
        assertEquals(ZoneId.of("UTC"), captured.zoneId)
        assertEquals(1, response.importedCount)
        assertEquals(2, response.skippedCount)
        assertEquals(transactionId, response.transactions.first().transactionId)
    }

    @Test
    fun `should reject empty file and invalid zone id`() {
        val controller = BankStatementImportController(
            importBankStatementUseCase = object : ImportBankStatementUseCase {
                override fun execute(request: ImportBankStatementCommand): ImportBankStatementResponse =
                    ImportBankStatementResponse(request.bank, 0, 0, emptyList())
            }
        )
        val budgetId = UUID.randomUUID()
        val auth = TestingAuthenticationToken("alex", "credentials")

        assertThrows(BadRequestException::class.java) {
            controller.importBankStatement(
                budgetId,
                MockMultipartFile("file", "empty.pdf", "application/pdf", byteArrayOf()),
                BankStatementBank.VTB,
                BankStatementFormat.PDF,
                "UTC",
                auth,
            )
        }
        assertThrows(BadRequestException::class.java) {
            controller.importBankStatement(
                budgetId,
                MockMultipartFile("file", "statement.pdf", "application/pdf", byteArrayOf(1)),
                BankStatementBank.VTB,
                BankStatementFormat.PDF,
                "bad-zone",
                auth,
            )
        }
    }
}
