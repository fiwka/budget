package xyz.fiwka.budget.dataservice.infrastructure.scheduling.job

import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import xyz.fiwka.budget.dataservice.application.port.`in`.outbox.PublishOutboxMessagesUseCase

@DisallowConcurrentExecution
class OutboxPublishJob : Job {

    @Autowired
    lateinit var publishOutboxMessagesUseCase: PublishOutboxMessagesUseCase

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(context: JobExecutionContext) {
        val publishedMessagesCount = publishOutboxMessagesUseCase.execute(Unit)
        if (publishedMessagesCount > 0) {
            log.info("Published {} outbox messages", publishedMessagesCount)
        }
    }
}

