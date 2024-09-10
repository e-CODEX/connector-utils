package eu.ecodex.utils.spring.quartz;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;
import static eu.ecodex.utils.spring.quartz.testbeans.ScheduledBean.SCHEDULED_CRON_JOB_STRING;
import static eu.ecodex.utils.spring.quartz.testbeans.ScheduledBean.SCHEDULED_FIXED_RATE_JOB_STRING;
import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.test.logging.MemoryAppender;
import eu.domibus.connector.test.logging.MemoryAppenderAssert;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {"debug=true",
        "test.cron-string=* * * * * ?",
        "test.interval=1s"
    }
)
@ActiveProfiles("test")
class QuartzQuartzScheduledJobTest {
    @SpringBootApplication(scanBasePackages = "eu.ecodex.utils.spring.quartz")
    static class QuartzScheduledContext {
    }

    @Autowired(required = false)
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;

    @Test
    void testRegisteredScheduled() {
        assertThat(triggerAndJobDefinitionList).hasSize(2);
    }

    /*
     * Ensure that the beans are scheduled
     */
    @Test
    void testJobsRunning() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        MemoryAppenderAssert.assertThat(MemoryAppender.getAppender())
                            .containsLogLine(SCHEDULED_CRON_JOB_STRING);
        MemoryAppenderAssert.assertThat(MemoryAppender.getAppender())
                            .containsLogLine(SCHEDULED_FIXED_RATE_JOB_STRING);
    }
}
