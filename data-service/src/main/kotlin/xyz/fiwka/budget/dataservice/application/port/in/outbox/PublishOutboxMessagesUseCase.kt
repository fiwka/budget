package xyz.fiwka.budget.dataservice.application.port.`in`.outbox

import xyz.fiwka.budget.port.Port

interface PublishOutboxMessagesUseCase : Port<Unit, Int>

