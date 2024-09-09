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
 * Configuration properties for ActiveMQ health checks.
 *
 * <p>This class holds the configuration settings for monitoring the health of ActiveMQ queues. It
 * provides options to enable or disable the health check feature and to configure the thresholds
 * for warning and error states based on queue size usage.
 */
@Data
@ConfigurationProperties(prefix = ActiveMqHealthChecksConfigurationProperties.PREFIX)
public class ActiveMqHealthChecksConfigurationProperties {
    public static final String PREFIX =
        ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX + ".health";
    private boolean enabled = true;
    private float queueSizeWarnThreshold = 0.6f;
    private float queueSizeErrorThreshold = 0.8f;
}
