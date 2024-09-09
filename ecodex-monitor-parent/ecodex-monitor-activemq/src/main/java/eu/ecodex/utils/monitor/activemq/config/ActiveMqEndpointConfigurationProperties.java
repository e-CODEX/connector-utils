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

import java.util.ArrayList;
import java.util.List;
import javax.management.remote.JMXServiceURL;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for ActiveMQ endpoint monitoring.
 *
 * <p>This class holds the configuration settings for monitoring ActiveMQ brokers using JMX. It
 * provides options to enable or disable monitoring, configure the JMX connection details, and
 * specify the broker name.
 */
@ConfigurationProperties(prefix = ActiveMqEndpointConfigurationProperties.ACTIVEMQ_MONITOR_PREFIX)
@Data
public class ActiveMqEndpointConfigurationProperties {
    public static final String ACTIVEMQ_MONITOR_PREFIX = "monitor.activemq";
    private boolean enabled = false;
    private boolean localJmx = true;
    private List<JMXServiceURL> jmxUrl = new ArrayList<>();
    private String brokerName;
    private String jmxUser;
    private String jmxPassword;
}
