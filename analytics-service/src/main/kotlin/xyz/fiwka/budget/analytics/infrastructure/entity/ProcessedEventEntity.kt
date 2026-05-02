package xyz.fiwka.budget.analytics.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "analytics_processed_events")
class ProcessedEventEntity(
    @Id
    @Column(name = "event_id", nullable = false)
    var eventId: String,
    @Column(name = "processed_at", nullable = false)
    var processedAt: Instant = Instant.now(),
) {
    constructor() : this("", Instant.now())
}
