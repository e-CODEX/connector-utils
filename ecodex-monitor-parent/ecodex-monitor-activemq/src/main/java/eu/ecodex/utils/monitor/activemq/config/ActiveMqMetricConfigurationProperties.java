/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.activemq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for ActiveMQ metrics monitoring.
 *
 * <p>This class holds the configuration settings specific for enabling or disabling metrics
 * collection for ActiveMQ. Metrics provide insights into the behavior and performance of ActiveMQ
 * brokers and destinations.
 */
@Data
@ConfigurationProperties(prefix = ActiveMqMetricConfigurationProperties.PREFIX)
public class ActiveMqMetricConfigurationProperties {
    public static final String PREFIX =
        ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX + ".metrics";
    boolean enabled = true;
}
