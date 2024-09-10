/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.domain;

import java.lang.reflect.Method;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Represents a cron job that is scheduled and executed by Quartz. This class extends the
 * {@link QuartzJobBean} to leverage Quartz's job scheduling capabilities.
 *
 * <p>The core functionality of the CronJob includes setting the method to be invoked and the bean
 * on which the method is to be invoked. It also provides a mechanism to execute the given method on
 * the provided bean when the job is triggered.
 *
 * <p>The CronJob maintains a logger to offer trace level logging during the method invocation.
 * This helps in understanding what method on which bean is being invoked at runtime.
 */
@Data
public class CronJob extends QuartzJobBean {
    public static final String MDC_ACTIVE_QUARTZ_JOB = "quartzJobName";
    private static final Logger LOGGER = LogManager.getLogger(CronJob.class);
    private final boolean toStopFlag = true;
    private Method method;
    private Object bean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
        throws JobExecutionException {

        MDC.put(MDC_ACTIVE_QUARTZ_JOB, convertJobToGroupName(jobExecutionContext.getJobDetail()));
        try {
            LOGGER.trace("Invoking method [{}] on bean [{}] to run cron job", method, bean);
            method.invoke(bean);
        } catch (Exception e) {

            throw new JobExecutionException(e);
        } finally {
            MDC.remove(MDC_ACTIVE_QUARTZ_JOB);
        }
    }

    private String convertJobToGroupName(JobDetail jobDetail) {
        return jobDetail.getKey().getGroup() + "_" + jobDetail.getKey().getName();
    }
}
