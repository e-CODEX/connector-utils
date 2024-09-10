package eu.ecodex.utils.spring.quartz.testbeans;

import eu.ecodex.utils.spring.quartz.annotation.CronStringProvider;
import eu.ecodex.utils.spring.quartz.annotation.IntervalProvider;
import java.time.Duration;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for scheduling beans with intervals and cron expressions.
 *
 * <p>This class implements both the {@link IntervalProvider} and {@link CronStringProvider}
 * interfaces. It is designed to be used with the
 * {@link eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled} annotation for configuring
 * scheduled tasks.
 */
@Setter
@Component
@ConfigurationProperties(prefix = "test")
public class ScheduledBeanConfigurationProperties implements IntervalProvider, CronStringProvider {
    private String cronString;
    private Duration interval;

    @Override
    public String getCronString() {
        return cronString;
    }

    @Override
    public Duration getInterval() {
        return interval;
    }
}
