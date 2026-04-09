package xyz.fiwka.budget.dataservice.infrastructure.configuration.scheduling

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.infrastructure.scheduling.job.OutboxPublishJob

@Configuration
class SchedulingConfiguration {

    companion object {
        const val OUTBOX_PUBLISH_JOB_NAME = "outbox-publish-job"
        const val OUTBOX_PUBLISH_JOB_GROUP = "outbox-jobs"
        const val OUTBOX_PUBLISH_TRIGGER_NAME = "outbox-publish-trigger"
        const val OUTBOX_PUBLISH_TRIGGER_GROUP = "outbox-triggers"
    }

    @Bean
    fun outboxPublishJobDetail(): JobDetail {
        return JobBuilder.newJob(OutboxPublishJob::class.java)
            .withIdentity(OUTBOX_PUBLISH_JOB_NAME, OUTBOX_PUBLISH_JOB_GROUP)
            .storeDurably()
            .build()
    }

    @Bean
    fun outboxPublishJobTrigger(
        @Qualifier("outboxPublishJobDetail") jobDetail: JobDetail,
        @Value("\${app.outbox.scheduler.cron:0 */30 * * * ?}") cron: String,
    ): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(OUTBOX_PUBLISH_TRIGGER_NAME, OUTBOX_PUBLISH_TRIGGER_GROUP)
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build()
    }
}
