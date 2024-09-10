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

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Service interface for managing Quartz jobs.
 */
public interface QuartzJobService {
    boolean scheduleOneTimeJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date date);

    boolean scheduleCronJob(
        String jobName, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression);

    boolean updateOneTimeJob(String jobName, Date date);

    boolean updateCronJob(String jobName, Date date, String cronExpression);

    boolean unScheduleJob(String jobName);

    boolean deleteJob(String jobName);

    boolean pauseJob(String jobName);

    boolean resumeJob(String jobName);

    boolean startJobNow(String jobName);

    boolean isJobRunning(String jobName);

    List<Map<String, Object>> getAllJobs();

    boolean isJobWithNamePresent(String jobName);

    String getJobState(String jobName);

    boolean stopJob(String jobName);
}
