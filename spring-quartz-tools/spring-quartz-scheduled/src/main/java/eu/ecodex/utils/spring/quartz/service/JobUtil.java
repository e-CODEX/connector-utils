/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.service;

import eu.ecodex.utils.spring.quartz.configuration.PersistableCronTriggerFactoryBean;
import java.text.ParseException;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Utility class for creating Quartz jobs and triggers.
 */
@UtilityClass
public class JobUtil {
    /**
     * Create Quartz Job.
     *
     * @param jobClass  Class whose executeInternal() method needs to be called.
     * @param isDurable Job needs to be persisted even after completion. if true, job will be
     *                  persisted, not otherwise.
     * @param context   Spring application context.
     * @param jobName   Job name.
     * @param jobGroup  Job group.
     * @return JobDetail object
     */
    protected static JobDetail createJob(
        Class<? extends QuartzJobBean> jobClass, boolean isDurable,
        ApplicationContext context, String jobName, String jobGroup) {
        var factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);

        // set job data map
        var jobDataMap = new JobDataMap();
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    /**
     * Create cron trigger.
     *
     * @param triggerName        Trigger name.
     * @param startTime          Trigger start time.
     * @param cronExpression     Cron expression.
     * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
     * @return Trigger
     */
    protected static Trigger createCronTrigger(
        String triggerName, Date startTime, String cronExpression, int misFireInstruction) {
        var factoryBean = new PersistableCronTriggerFactoryBean();
        factoryBean.setName(triggerName);
        factoryBean.setStartTime(startTime);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(misFireInstruction);
        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return factoryBean.getObject();
    }

    /**
     * Create a Single trigger.
     *
     * @param triggerName        Trigger name.
     * @param startTime          Trigger start time.
     * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
     * @return Trigger
     */
    protected static Trigger createSingleTrigger(
        String triggerName, Date startTime, int misFireInstruction) {
        var factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setName(triggerName);
        factoryBean.setStartTime(startTime);
        factoryBean.setMisfireInstruction(misFireInstruction);
        factoryBean.setRepeatCount(0);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
