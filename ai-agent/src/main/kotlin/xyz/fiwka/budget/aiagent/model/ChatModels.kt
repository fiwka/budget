package xyz.fiwka.budget.aiagent.model

import java.time.Instant
import java.util.UUID

data class ChatSession(
    val sessionId: UUID,
    val budgetId: UUID,
    val userId: UUID,
    val username: String,
    val createdAt: Instant,
)

data class UserInfoResponse(
    val id: UUID,
    val username: String,
    val email: String,
)

data class CreateChatSessionRequest(
    val budgetId: UUID,
)

data class CreateChatSessionResponse(
    val sessionId: UUID,
)

data class ChatMessageRequest(
    val message: String,
)

data class ChatMessageResponse(
    val response: String,
)

data class ChatStreamChunk(
    val content: String,
)
