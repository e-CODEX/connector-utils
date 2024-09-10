/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.spring.quartz.annotation;

import static eu.ecodex.utils.spring.quartz.annotation.IntervalProvider.DefaultIntervalProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The QuartzScheduled annotation is used to declare a scheduled task to be executed by the Quartz
 * Scheduler. It supports two types of scheduling: fixed-rate execution and cron-based execution.
 *
 * <p>Usage of this annotation inherently requires that either fixedRate() or cron() are defined,
 * but not both simultaneously.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(QuartzSchedules.class)
@Documented
public @interface QuartzScheduled {
    /**
     * Specifies a class that provides the interval duration for fixed-rate execution. The class
     * must implement the {@link IntervalProvider} interface. By default, the
     * {@link DefaultIntervalProvider} class is used, which returns a null interval.
     *
     * @return the class that provides the interval duration for fixed-rate execution
     */
    Class<? extends IntervalProvider> fixedRate() default DefaultIntervalProvider.class;

    /**
     * Specifies a class that provides a cron expression for cron-based scheduling execution. The
     * class must implement the {@link CronStringProvider} interface. By default, the
     * {@link CronStringProvider.DefaultCronStringProvider} class is used, which returns a null cron
     * string.
     *
     * @return the class that provides the cron expression for scheduling
     */
    Class<? extends CronStringProvider> cron()
        default CronStringProvider.DefaultCronStringProvider.class;

    /**
     * Specifies an optional string to qualify the scheduled task. This qualifier can be used to
     * identify or distinguish different scheduled tasks.
     *
     * @return the qualifier string for the scheduled task
     */
    String qualifier() default "";
}
