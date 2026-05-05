package xyz.fiwka.budget.aiagent.store

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import xyz.fiwka.budget.aiagent.model.ChatSession
import java.time.Duration
import java.util.UUID

@Component
class ChatSessionStore(
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val objectMapper: ObjectMapper,
) {

    fun save(session: ChatSession, ttl: Duration): Mono<ChatSession> {
        val key = sessionKey(session.sessionId)
        val value = objectMapper.writeValueAsString(session)

        return redisTemplate.opsForValue()
            .set(key, value, ttl)
            .flatMap { if (it) Mono.just(session) else Mono.empty() }
    }

    fun findById(sessionId: UUID): Mono<ChatSession> = redisTemplate.opsForValue()
        .get(sessionKey(sessionId))
        .flatMap { value -> Mono.just(objectMapper.readValue(value, ChatSession::class.java)) }

    fun delete(sessionId: UUID): Mono<Boolean> = redisTemplate.delete(sessionKey(sessionId)).map { it > 0 }

    private fun sessionKey(sessionId: UUID): String = "ai:chat:session:$sessionId"
}
