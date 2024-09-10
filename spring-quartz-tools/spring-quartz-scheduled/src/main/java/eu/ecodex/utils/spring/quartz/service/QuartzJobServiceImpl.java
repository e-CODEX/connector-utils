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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Implementation of QuartzJobService interface to manage Quartz jobs using a SchedulerFactoryBean.
 */
@SuppressWarnings("checkstyle:LocalVariableName")
public class QuartzJobServiceImpl implements QuartzJobService {
    private static final Logger LOGGER = LogManager.getLogger(QuartzJobServiceImpl.class);
    @Autowired
    @Lazy
    SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private ApplicationContext context;

    /**
     * Schedule a job by jobName at given date.
     */
    @Override
    public boolean scheduleOneTimeJob(
        String jobName, Class<? extends QuartzJobBean> jobClass, Date date) {
        LOGGER.debug("Request received to scheduleJob");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var triggerKey = jobName;

        var jobDetail = JobUtil.createJob(jobClass, false, context, jobKey, groupKey);

        LOGGER.debug("creating trigger for key : {} at date : {}", jobKey, date);
        var cronTriggerBean = JobUtil.createSingleTrigger(
            triggerKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
        );

        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var scheduledDate = scheduler.scheduleJob(jobDetail, cronTriggerBean);
            LOGGER.debug(
                "Job with key jobKey : {} and group : {} scheduled successfully for date : {}",
                jobKey, groupKey, scheduledDate
            );
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while scheduling job with key : {} message : {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Schedule a job by jobName at given date.
     */
    @Override
    public boolean scheduleCronJob(
        String jobName, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression) {
        LOGGER.debug("Request received to scheduleJob");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var triggerKey = jobName;

        var jobDetail = JobUtil.createJob(jobClass, false, context, jobKey, groupKey);

        LOGGER.debug(
            "creating trigger for key : {} at date : {}", jobKey, date
        );
        var cronTriggerBean = JobUtil.createCronTrigger(
            triggerKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
        );

        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var scheduledDate = scheduler.scheduleJob(jobDetail, cronTriggerBean);
            LOGGER.debug(
                "Job with key jobKey : {} and group : {} scheduled successfully for date : {}",
                jobKey, groupKey, scheduledDate
            );
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while scheduling job with key : {} message : {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update one time scheduled job.
     */
    @Override
    public boolean updateOneTimeJob(String jobName, Date date) {
        LOGGER.debug("Request received for updating one time job.");

        var jobKey = jobName;

        LOGGER.debug(
            "Parameters received for updating one time job : jobKey : {}, date: {}", jobKey, date
        );
        try {
            var newTrigger = JobUtil.createSingleTrigger(
                jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
            );

            var scheduledDate = schedulerFactoryBean
                .getScheduler()
                .rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            LOGGER.debug(
                "Trigger associated with jobKey : {} rescheduled successfully for date : {}",
                jobKey, scheduledDate
            );
            return true;
        } catch (Exception e) {
            LOGGER.debug(
                "SchedulerException while updating one time job with key : {} message : {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update scheduled cron job.
     */
    @Override
    public boolean updateCronJob(String jobName, Date date, String cronExpression) {
        LOGGER.debug("Request received for updating cron job.");

        String jobKey = jobName;

        LOGGER.debug(
            "Parameters received for updating cron job : jobKey: {}, date: {}", jobKey, date
        );
        try {
            var newTrigger = JobUtil.createCronTrigger(
                jobKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
            );

            Date dt = schedulerFactoryBean.getScheduler()
                                          .rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            LOGGER.debug(
                "Trigger associated with jobKey: {} rescheduled successfully for date : {}", jobKey,
                dt
            );
            return true;
        } catch (Exception e) {
            LOGGER.debug(
                "SchedulerException while updating cron job with key: {} message: {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove the indicated Trigger from the scheduler. If the related job does not have any other
     * triggers, and the job is not durable, then the job will also be deleted.
     */
    @Override
    public boolean unScheduleJob(String jobName) {
        LOGGER.debug("Request received for Un-scheduling job.");
        var jobKey = jobName;
        var tkey = new TriggerKey(jobKey);
        LOGGER.debug("Parameters received for un-scheduling job : tkey : {}", jobKey);
        try {
            boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
            LOGGER.debug(
                "Trigger associated with jobKey: {} unscheduled with status: {}", jobKey, status
            );
            return status;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while un-scheduling job with key: {} message: {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the identified Job from the Scheduler - and any associated Triggers.
     */
    @Override
    public boolean deleteJob(String jobName) {
        LOGGER.debug("Request received for deleting job.");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var jkey = new JobKey(jobKey, groupKey);

        LOGGER.debug("Parameters received for deleting job : jobKey: {}", jobKey);

        try {
            boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
            LOGGER.debug("Job with jobKey: {} deleted with status: {}", jobKey, status);
            return status;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while deleting job with key: {} message: {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Pause a job.
     */
    @Override
    public boolean pauseJob(String jobName) {
        LOGGER.debug("Request received for pausing job.");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var jkey = new JobKey(jobKey, groupKey);

        LOGGER.debug(
            "Parameters received for pausing job : jobKey: {}, groupKey: {}", jobKey, groupKey
        );

        try {
            schedulerFactoryBean.getScheduler().pauseJob(jkey);
            LOGGER.debug("Job with jobKey: {} paused successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while pausing job with key: {} message: {}",
                jobName, e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Resume paused job.
     */
    @Override
    public boolean resumeJob(String jobName) {
        LOGGER.debug("Request received for resuming job.");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var jKey = new JobKey(jobKey, groupKey);

        LOGGER.debug("Parameters received for resuming job : jobKey: {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jKey);
            LOGGER.debug("Job with jobKey: {} resumed successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while resuming job with key: {} message : {}",
                jobKey, e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Start a job now.
     */
    @Override
    public boolean startJobNow(String jobName) {
        LOGGER.debug("Request received for starting job now.");

        var jobKey = jobName;
        var groupKey = "SampleGroup";
        var jKey = new JobKey(jobKey, groupKey);

        LOGGER.debug("Parameters received for starting job now : jobKey: {}", jobKey);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jKey);
            LOGGER.debug("Job with jobKey: {} started now successfully.", jobKey);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while starting job now with key: {} message: {}", jobKey,
                e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if job is already running.
     */
    @Override
    public boolean isJobRunning(String jobName) {
        LOGGER.debug("Request received to check if job is running");

        var jobKey = jobName;
        var groupKey = "SampleGroup";

        LOGGER.debug("Parameters received for checking job is running now: jobKey: {}", jobKey);
        try {

            var currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    String jobNameDB = jobCtx.getJobDetail().getKey().getName();
                    String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
                    if (jobKey.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(
                        groupNameDB)) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while checking job with key: {} is running. error message: {}",
                jobKey, e.getMessage()
            );
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Get all jobs.
     */
    @Override
    public List<Map<String, Object>> getAllJobs() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    // get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date scheduleTime = triggers.getFirst().getStartTime();
                    Date nextFireTime = triggers.getFirst().getNextFireTime();
                    Date lastFiredTime = triggers.getFirst().getPreviousFireTime();

                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobName);
                    map.put("groupName", jobGroup);
                    map.put("scheduleTime", scheduleTime);
                    map.put("lastFiredTime", lastFiredTime);
                    map.put("nextFireTime", nextFireTime);

                    if (isJobRunning(jobName)) {
                        map.put("jobStatus", "RUNNING");
                    } else {
                        String jobState = getJobState(jobName);
                        map.put("jobStatus", jobState);
                    }

                    list.add(map);
                    LOGGER.debug("Job details:");
                    LOGGER.debug(
                        "Job Name: {}, Group Name: {}, Schedule Time: {}", jobName, groupName,
                        scheduleTime
                    );
                }
            }
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while fetching all jobs. error message: {}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Check job exist with given name.
     */
    @Override
    public boolean isJobWithNamePresent(String jobName) {
        try {
            var groupKey = "SampleGroup";
            var jobKey = new JobKey(jobName, groupKey);
            var scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while checking job with name and group exist: {}",
                e.getMessage()
            );
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the current state of job.
     */
    public String getJobState(String jobName) {
        LOGGER.debug("JobServiceImpl.getJobState()");

        try {
            var groupKey = "SampleGroup";
            var jobKey = new JobKey(jobName, groupKey);

            var scheduler = schedulerFactoryBean.getScheduler();
            var jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (triggers != null && !triggers.isEmpty()) {
                for (Trigger trigger : triggers) {
                    var triggerState = scheduler.getTriggerState(trigger.getKey());

                    if (TriggerState.PAUSED.equals(triggerState)) {
                        return "PAUSED";
                    } else if (TriggerState.BLOCKED.equals(triggerState)) {
                        return "BLOCKED";
                    } else if (TriggerState.COMPLETE.equals(triggerState)) {
                        return "COMPLETE";
                    } else if (TriggerState.ERROR.equals(triggerState)) {
                        return "ERROR";
                    } else if (TriggerState.NONE.equals(triggerState)) {
                        return "NONE";
                    } else if (TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                }
            }
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while checking job with name and group exist: {}",
                e.getMessage()
            );
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stop a job.
     */
    @Override
    public boolean stopJob(String jobName) {
        LOGGER.debug("JobServiceImpl.stopJob()");
        try {
            var jobKey = jobName;
            var groupKey = "SampleGroup";

            var scheduler = schedulerFactoryBean.getScheduler();
            var jkey = new JobKey(jobKey, groupKey);

            return scheduler.interrupt(jkey);
        } catch (SchedulerException e) {
            LOGGER.debug(
                "SchedulerException while stopping job. error message : {}", e.getMessage()
            );
            e.printStackTrace();
        }
        return false;
    }
}
