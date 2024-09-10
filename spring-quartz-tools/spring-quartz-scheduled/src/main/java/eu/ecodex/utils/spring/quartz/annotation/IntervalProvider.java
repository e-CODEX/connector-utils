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

import java.time.Duration;

/**
 * An interface to provide interval durations for scheduling tasks.
 */
public interface IntervalProvider {
    Duration getInterval();

    default Duration getInitialDelay() {
        return Duration.ZERO;
    }

    /**
     * Default implementation of the {@link IntervalProvider} interface.
     * This implementation returns a null duration interval.
     * It is commonly used as a default value in scheduling annotations
     * such as {@link QuartzScheduled}.
     */
    class DefaultIntervalProvider implements IntervalProvider {
        @Override
        public Duration getInterval() {
            return null;
        }
    }
}
