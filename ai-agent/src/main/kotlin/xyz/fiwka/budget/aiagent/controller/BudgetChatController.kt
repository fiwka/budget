package xyz.fiwka.budget.aiagent.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import xyz.fiwka.budget.aiagent.model.ChatMessageRequest
import xyz.fiwka.budget.aiagent.model.ChatMessageResponse
import xyz.fiwka.budget.aiagent.model.ChatStreamChunk
import xyz.fiwka.budget.aiagent.model.CreateChatSessionRequest
import xyz.fiwka.budget.aiagent.model.CreateChatSessionResponse
import xyz.fiwka.budget.aiagent.service.BudgetChatService
import xyz.fiwka.budget.aiagent.service.UserContextService
import java.util.UUID

@RestController
@RequestMapping("/api/ai/chat")
class BudgetChatController(
    private val budgetChatService: BudgetChatService,
    private val userContextService: UserContextService,
) {

    @PostMapping("/sessions")
    fun createSession(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String,
        @RequestBody request: CreateChatSessionRequest,
    ): Mono<CreateChatSessionResponse> = userContextService.resolveUser(authorizationHeader)
        .flatMap { user ->
            budgetChatService.createSession(request.budgetId, user.id, user.username)
                .map { session -> CreateChatSessionResponse(session.sessionId) }
        }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSession(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String,
        @PathVariable sessionId: UUID,
    ): Mono<Void> = userContextService.resolveUser(authorizationHeader)
        .flatMap { user ->
            budgetChatService.deleteSession(sessionId, user.id)
                .then()
        }

    @PostMapping("/sessions/{sessionId}/messages")
    fun chat(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String,
        @PathVariable sessionId: UUID,
        @RequestBody request: ChatMessageRequest,
    ): Mono<ChatMessageResponse> = userContextService.resolveUser(authorizationHeader)
        .flatMap { user ->
            budgetChatService.chat(sessionId, user.id, authorizationHeader, request.message)
        }

    @PostMapping("/sessions/{sessionId}/messages/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String,
        @PathVariable sessionId: UUID,
        @RequestBody request: ChatMessageRequest,
    ): Flux<ServerSentEvent<ChatStreamChunk>> = userContextService.resolveUser(authorizationHeader)
        .flatMapMany { user ->
            budgetChatService.stream(sessionId, user.id, authorizationHeader, request.message)
        }
        .map { chunk ->
            ServerSentEvent.builder(ChatStreamChunk(chunk))
                .event("chunk")
                .build()
        }
        .concatWith(Flux.just(ServerSentEvent.builder(ChatStreamChunk(""))
            .event("done")
            .build()))
}
