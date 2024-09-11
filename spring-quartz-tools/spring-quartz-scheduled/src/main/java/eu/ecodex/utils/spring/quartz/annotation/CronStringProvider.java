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

/**
 * Provides a cron expression string for scheduling purposes.
 */
public interface CronStringProvider {
    String getCronString();

    default String zone() {
        return "";
    }

    /**
     * Default implementation of the {@link CronStringProvider} interface.
     * This implementation returns a null cron expression string.
     */
    class DefaultCronStringProvider implements CronStringProvider {
        @Override
        public String getCronString() {
            return null;
        }
    }
}
