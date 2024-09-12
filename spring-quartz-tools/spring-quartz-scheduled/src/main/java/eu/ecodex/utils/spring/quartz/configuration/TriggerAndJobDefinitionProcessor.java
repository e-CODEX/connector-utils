/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.configuration;

import static eu.ecodex.utils.spring.quartz.configuration.ScheduledWithQuartzConfiguration.TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME;
import static java.time.temporal.ChronoUnit.SECONDS;

import eu.ecodex.utils.spring.quartz.annotation.CronStringProvider;
import eu.ecodex.utils.spring.quartz.annotation.IntervalProvider;
import eu.ecodex.utils.spring.quartz.annotation.QuartzScheduled;
import eu.ecodex.utils.spring.quartz.domain.CronJob;
import eu.ecodex.utils.spring.quartz.domain.TriggerAndJobDefinition;
import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Processes all created TriggerAndJobDefinitions must be called after the
 * ScheduledBeanPostProcessor has been run.
 */
public class TriggerAndJobDefinitionProcessor {
    private static final Logger LOGGER =
        LogManager.getLogger(TriggerAndJobDefinitionProcessor.class);
    @Autowired
    @Qualifier(TRIGGER_AND_JOB_DEFINITION_LIST_BEAN_NAME)
    private List<TriggerAndJobDefinition> triggerAndJobDefinitionList;
    @Autowired
    Scheduler scheduler;
    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        triggerAndJobDefinitionList.forEach(this::processTriggerAndJobDefinition);
    }

    private void processTriggerAndJobDefinition(TriggerAndJobDefinition triggerAndJobDefinition) {
        LOGGER.trace("Creating trigger and job for {}", triggerAndJobDefinition);
        var beanName = triggerAndJobDefinition.getBeanName();
        var methodName = triggerAndJobDefinition.getMethod().getName();

        var jobName = String.format("%s_%s", beanName, methodName);

        var factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(CronJob.class);
        factoryBean.setDurability(true);
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setName(jobName);
        factoryBean.setGroup("defaultGroup");

        // set job data map
        var jobDataMap = new JobDataMap();
        jobDataMap.put("method", triggerAndJobDefinition.getMethod());
        jobDataMap.put("bean", triggerAndJobDefinition.getBean());
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();

        FactoryBean<? extends Trigger> triggerFactoryBean = null;

        var quartzScheduled = triggerAndJobDefinition.getScheduled();
        // check at least one defined
        if (isCronProviderDefined(quartzScheduled) && isFixedRateProviderDefined(quartzScheduled)
        ) {
            var error =
                "Neither fixedRate() or cron() are defined within @QuartzScheduled annotation!";
            throw new TriggerBeanCreationException(error, quartzScheduled);
        }

        // check both defined
        if (!isCronProviderDefined(quartzScheduled) && !isFixedRateProviderDefined(quartzScheduled)
        ) {
            var error = "Both fixedRate() and cron() are defined within @QuartzScheduled "
                + "annotation - you cannot use both!";
            throw new TriggerBeanCreationException(error, quartzScheduled);
        }

        if (isCronProviderDefined(quartzScheduled)) {
            triggerFactoryBean = processFixedRate(quartzScheduled);
        }

        if (isFixedRateProviderDefined(quartzScheduled)) {
            triggerFactoryBean = processCronTrigger(quartzScheduled);
        }

        if (triggerFactoryBean == null) {
            throw new IllegalStateException("triggerFactoryBean must not be null!");
        }

        try {
            ((InitializingBean) triggerFactoryBean).afterPropertiesSet();
            scheduler.scheduleJob(factoryBean.getObject(), triggerFactoryBean.getObject());
        } catch (ParseException e) {
            var error = "Unable to parse cron string";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        } catch (SchedulerException e) {
            var error = "Scheduler exception";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        } catch (Exception e) {
            LOGGER.error(e);
            var error = "Exception";
            throw new TriggerBeanCreationException(error, e, quartzScheduled);
        }
    }

    private boolean isFixedRateProviderDefined(QuartzScheduled quartzScheduled) {
        return quartzScheduled.fixedRate() == IntervalProvider.DefaultIntervalProvider.class;
    }

    private boolean isCronProviderDefined(QuartzScheduled quartzScheduled) {
        return quartzScheduled.cron() == CronStringProvider.DefaultCronStringProvider.class;
    }

    private CronTriggerFactoryBean processCronTrigger(QuartzScheduled quartzScheduled) {
        Class<? extends CronStringProvider> cronProviderClass = quartzScheduled.cron();
        if (isCronProviderDefined(quartzScheduled)) {
            throw new IllegalArgumentException("quartzScheduled.cron() must be defined!");
        }
        CronStringProvider bean = getBean(quartzScheduled.qualifier(), cronProviderClass);
        var cronString = bean.getCronString();
        if (!StringUtils.hasLength(cronString)) {
            throw new TriggerBeanCreationException("Cron string is empty!", quartzScheduled);
        }
        var cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setCronExpression(cronString);
        return cronTriggerFactoryBean;
    }

    private SimpleTriggerFactoryBean processFixedRate(QuartzScheduled quartzScheduled) {
        Class<? extends IntervalProvider> intervalProviderClass = quartzScheduled.fixedRate();
        if (isFixedRateProviderDefined(quartzScheduled)) {
            throw new IllegalArgumentException("quartzScheduled.fixedRate() must be defined!");
        }
        IntervalProvider bean = getBean(quartzScheduled.qualifier(), intervalProviderClass);
        Duration interval = bean.getInterval();
        if (interval == null) {
            throw new TriggerBeanCreationException("Interval is null!", quartzScheduled);
        }
        var simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setRepeatInterval(interval.get(SECONDS) * 1000);
        if (bean.getInitialDelay() != null) {
            simpleTriggerFactoryBean.setStartDelay(interval.get(SECONDS) * 1000);
        }
        return simpleTriggerFactoryBean;
    }

    private <T> T getBean(String qualifier, Class<T> intervalProviderClass) {
        if (!StringUtils.hasLength(qualifier)) {
            return applicationContext.getBean(intervalProviderClass);
        } else {
            return applicationContext.getBean(qualifier, intervalProviderClass);
        }
    }

    /**
     * Exception thrown when there is an error in creating a bean for a scheduled Quartz trigger.
     * This exception typically indicates issues with the QuartzScheduled annotation related to the
     * interval or cron expression definitions.
     *
     * <p>Provides information about the QuartzScheduled annotation that caused the error.
     */
    public static class TriggerBeanCreationException extends BeanCreationException {
        private final QuartzScheduled quartzScheduledAnnotation;

        TriggerBeanCreationException(
            String message, Throwable exc, QuartzScheduled quartzScheduledAnnotation) {
            super(message, exc);
            this.quartzScheduledAnnotation = quartzScheduledAnnotation;
        }

        TriggerBeanCreationException(String message, QuartzScheduled quartzScheduledAnnotation) {
            super(message);
            this.quartzScheduledAnnotation = quartzScheduledAnnotation;
        }

        public QuartzScheduled getScheduledAnnotation() {
            return quartzScheduledAnnotation;
        }
    }
}
