/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.utils.monitor.gw;

import eu.ecodex.utils.monitor.gw.config.GatewayMonitorConfigurationProperties;
import eu.ecodex.utils.monitor.gw.service.ConfiguredGatewaysService;
import eu.ecodex.utils.monitor.gw.service.GatewayHealthIndicator;
import eu.ecodex.utils.monitor.gw.service.GatewayReachableEndpoint;
import eu.ecodex.utils.monitor.gw.service.PModeDownloader;
import eu.ecodex.utils.monitor.gw.service.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * GatewayMonitorAutoConfiguration provides the configuration for the Gateway Monitor module in a
 * Spring Boot application. This class sets up the necessary beans and dependencies based on the
 * application's external configuration properties. It is activated only if the property
 * 'monitor.gw.enabled' is set to true in the configuration.
 */
@Configuration
@ConditionalOnProperty(
    prefix = GatewayMonitorConfigurationProperties.GATEWAY_MONITOR_PREFIX, name = "enabled",
    havingValue = "true"
)
@EnableConfigurationProperties(GatewayMonitorConfigurationProperties.class)
@Import(ServiceConfiguration.class)
public class GatewayMonitorAutoConfiguration {
    @Autowired
    private GatewayMonitorConfigurationProperties gatewayRestInterfaceConfiguration;

    @SuppressWarnings("checkstyle:MethodName")
    @Bean
    public PModeDownloader pModeDownloader() {
        return new PModeDownloader(gatewayRestInterfaceConfiguration.getRest());
    }

    @Bean
    public ConfiguredGatewaysService configuredGatewaysService() {
        return new ConfiguredGatewaysService();
    }

    @Bean
    public GatewayHealthIndicator gatewayHealthIndicator() {
        return new GatewayHealthIndicator();
    }

    @Bean
    public GatewayReachableEndpoint gatewayReachableEndpoint() {
        return new GatewayReachableEndpoint();
    }
}
